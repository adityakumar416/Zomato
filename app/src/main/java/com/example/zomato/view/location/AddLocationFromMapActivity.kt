package com.test.zomato.view.location

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Geocoder
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.test.zomato.R
import com.test.zomato.databinding.ActivityAddLocationFromMapBinding
import com.test.zomato.utils.EnableAppLocationPermissionDialogFragment
import com.test.zomato.utils.MyHelper
import com.test.zomato.view.location.bottomSheets.EnterCompleteAddressBottomSheetFragment
import com.test.zomato.view.location.interfaces.SavedAddressClickListener
import com.test.zomato.view.main.MainActivity
import java.io.IOException
import java.util.Locale

class AddLocationFromMapActivity : AppCompatActivity(), OnMapReadyCallback,
    SavedAddressClickListener {

    private lateinit var googleMap: GoogleMap
    private lateinit var binding: ActivityAddLocationFromMapBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var markerPosition: LatLng? = null
    private var selectedAddress: String? = null
    private val myHelper by lazy { MyHelper(this) }
    private lateinit var resultLauncher: ActivityResultLauncher<IntentSenderRequest>
    private var updateAddress: String? = null
    private var addressId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddLocationFromMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        updateAddress = intent?.getStringExtra("selectedLocation") ?: ""
        addressId = intent?.getIntExtra("addressId", -1)  // Ensure this is being passed correctly
        // Toast.makeText(this, "Address ID: $addressId", Toast.LENGTH_SHORT).show()


        resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    getCurrentLocation()
                } else {
                    Toast.makeText(
                        this,
                        "Location permission is required to add address.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

        window.statusBarColor = Color.parseColor("#F3F4FA")

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Initialize the map
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        binding.backButton.setOnClickListener { finish() }


        binding.userCurrentLocation.setOnClickListener {
            if (myHelper.checkLocationPermission()) {
                if (myHelper.isLocationEnable()) {
                    if (updateAddress.isNullOrEmpty()) {
                        getCurrentLocation()
                    }

                } else {
                    myHelper.onGPS(resultLauncher)
                }
            } else {
                myHelper.requestLocationPermission(this)  // Request location permission
            }
           // getCurrentLocation()
        }

        //Toast.makeText(this, "${myHelper.numberIs()}", Toast.LENGTH_SHORT).show()

        //  Toast.makeText(this, "$addressId", Toast.LENGTH_SHORT).show()

        if (addressId != null && addressId != -1) {
            //  editing the address using the addressId
            // Set up Add More Details button to open bottom sheet and pass the address
            binding.addMoreDetails.setOnClickListener {
                val enterCompleteAddressBottomSheetFragment =
                    EnterCompleteAddressBottomSheetFragment()
                val bundle = Bundle()
                bundle.putString("address", selectedAddress)
                bundle.putInt("addressId", addressId!!)
                enterCompleteAddressBottomSheetFragment.arguments = bundle
                enterCompleteAddressBottomSheetFragment.show(
                    supportFragmentManager,
                    "enterCompleteAddressBottomSheetFragment"
                )
            }
        } else {
            // Set up Add More Details button to open bottom sheet and pass the address
            binding.addMoreDetails.setOnClickListener {
                val enterCompleteAddressBottomSheetFragment =
                    EnterCompleteAddressBottomSheetFragment()
                val bundle = Bundle()
                bundle.putString("address", selectedAddress)  // Pass address to the bottom sheet
                enterCompleteAddressBottomSheetFragment.arguments = bundle
                enterCompleteAddressBottomSheetFragment.show(
                    supportFragmentManager,
                    "enterCompleteAddressBottomSheetFragment"
                )
            }
        }

        binding.search.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                charSequence: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(
                charSequence: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                s?.let {
                    if (it.isNotEmpty()) {
                        Handler().postDelayed({
                            getLocationFromAddress(it.toString())
                        },1000)

                    }else{
                        getCurrentLocation()
                    }
                }
            }
        })

        val locationData =
            myHelper.extractAddressDetails(myHelper.getLatitude(), myHelper.getLongitude())
        locationData?.let {  updateLocationUI(it.block, "${it.locality},${it.state}") }

        if (myHelper.checkLocationPermission()) {
            if (myHelper.isLocationEnable()) {
                if (updateAddress.isNullOrEmpty()) {
                    getCurrentLocation()
                }
            } else {
                myHelper.onGPS(resultLauncher)
            }
        } else {
            myHelper.requestLocationPermission(this)  // Request location permission
        }


    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap

        // Set a click listener for the map
        googleMap.setOnMapClickListener { latLng ->
            // Clear existing markers and place a new marker at the clicked location
            googleMap.clear()  // Clear any existing markers
            googleMap.addMarker(MarkerOptions().position(latLng))

            //  markerPosition = latLng

            // Extract address details from the clicked location
            extractAddressDetails(latLng.latitude, latLng.longitude)

            updateAddress?.let {
                getLocationFromAddress(it)
                updateAddress = null
            }

        }
        updateAddress?.let {
            getLocationFromAddress(it)
        }

    }

    // Function to fetch the address details from the given location
    private fun extractAddressDetails(latitude: Double, longitude: Double) {
        val location = myHelper.extractAddressDetails(latitude, longitude)
        Log.d("selectedMapAddress", location?.fullAddress.toString())
        Log.d("selectedMapAddress", "${location?.block},${location?.street},${location?.locality},${location?.state},${location?.subState},${location?.postalCode},${location?.country}")

        location?.let {
            updateLocationUI(it.block, "${it.locality},${it.state}")

            selectedAddress = "${it.block},${it.locality},${it.state}"
        }

    }

    // Function to update UI with block and full address
    private fun updateLocationUI(block: String, fullAddress: String) {
        binding.userBlockLocation.text = block
        binding.address.text = fullAddress
    }

    // Function to get current location
    private fun getCurrentLocation() {
        if (myHelper.checkLocationPermission() && myHelper.isLocationEnable()) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    val currentLatLng = LatLng(location.latitude, location.longitude)

                    // Move camera to current location and set a marker
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))

                    googleMap.clear()
                    googleMap.addMarker(MarkerOptions().position(currentLatLng))

                    extractAddressDetails(currentLatLng.latitude, currentLatLng.longitude)
                }
            }
        } else {
            myHelper.requestLocationPermission(this)
        }
    }

    // show addres on map using search
    private fun getLocationFromAddress(address: String) {
        try {
            val geocoder = Geocoder(this, Locale.getDefault())
            val addresses = geocoder.getFromLocationName(address, 1)

            if (!addresses.isNullOrEmpty()) {
                val location = addresses[0]
                val latLng = LatLng(location.latitude, location.longitude)

                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))

                googleMap.clear()
                googleMap.addMarker(MarkerOptions().position(latLng))

                // Extract and display address details for the new location
                extractAddressDetails(latLng.latitude, latLng.longitude)

            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 10 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
          //  recreate()
            if (myHelper.isLocationEnable()) {
                getCurrentLocation()
            } else {
                myHelper.onGPS(resultLauncher)
            }
        } else {
            if (!shouldShowRequestPermissionRationale(permissions[0])) {
                val progressDialog = EnableAppLocationPermissionDialogFragment()
                progressDialog.show(supportFragmentManager, "requestLocationPermission")
            }
        }
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }


    override fun addressSave() {

        if (intent.getStringExtra("fromMyAddressBook") == "fromMyAddressBook"){
            finish()
        }else{
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finishAffinity()
        }


    }
}
