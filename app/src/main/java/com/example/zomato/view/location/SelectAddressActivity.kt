package com.test.zomato.view.location

import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.test.zomato.R
import com.test.zomato.databinding.ActivitySelectAddressBinding
import com.test.zomato.utils.AppSharedPreferences
import com.test.zomato.utils.EnableAppLocationPermissionDialogFragment
import com.test.zomato.utils.MyHelper
import com.test.zomato.utils.PrefKeys
import com.test.zomato.view.location.adapter.ShowAllSavedAddressAdapter
import com.test.zomato.view.location.adapter.ViewNearbyLocationsAdapter
import com.test.zomato.view.location.interfaces.AddressMenuClickListener
import com.test.zomato.view.location.models.UserSavedAddress
import com.test.zomato.view.login.repository.UserViewModel
import com.test.zomato.viewModels.MainViewModel

class SelectAddressActivity : AppCompatActivity(), AddressMenuClickListener {

    private lateinit var binding: ActivitySelectAddressBinding

    private lateinit var mainViewModel: MainViewModel

    private val myHelper: MyHelper by lazy { MyHelper(this) }

    private lateinit var viewNearbyLocationsAdapter: ViewNearbyLocationsAdapter
    private lateinit var resultLauncher: ActivityResultLauncher<IntentSenderRequest>
    private lateinit var addressAdapter: ShowAllSavedAddressAdapter
    private lateinit var userViewModel: UserViewModel

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val appSharedPreferences by lazy { AppSharedPreferences.getInstance(this) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // enableEdgeToEdge()
        binding = ActivitySelectAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        window.statusBarColor = Color.parseColor("#F3F4FA")

        resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    recreate()
                } else {
                    Toast.makeText(
                        this,
                        "Location permission is required to find nearby shops",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

        mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]


        // initilize the sharedpresrence class to get saved value in sharedprefrence
        val isSkipBtnClick = appSharedPreferences?.getBoolean(PrefKeys.SKIP_BTN_CLICK)

        if (isSkipBtnClick == true) {
            binding.blinketCard.visibility = View.GONE
            binding.savedAddressRecyclerViewLayout.visibility = View.GONE
            binding.dividerView4.visibility = View.GONE
        }else{
            binding.dividerView4.visibility = View.VISIBLE
            binding.savedAddressRecyclerViewLayout.visibility = View.VISIBLE
            binding.blinketCard.visibility = View.VISIBLE
        }

        updateTheNearbyLocationAdapter()

        binding.backButton.setOnClickListener {
            finish()
        }

        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]

       // Toast.makeText(this, "${myHelper.numberIs()}", Toast.LENGTH_SHORT).show()

        showSavedAddressInRecyclerView()

        binding.addAddress.setOnClickListener {
            val intent = Intent(this, AddLocationFromMapActivity::class.java)
            startActivity(intent)
        }

        if (!myHelper.checkLocationPermission() || !myHelper.isLocationEnable()) {
            binding.text1.text = "Device location not\nenabled"
            binding.text5.text = "Tab here to enable your device\nlocation for a better"
            binding.enableLocationBtn.visibility = View.VISIBLE
            binding.selectCurrentLocation.visibility = View.GONE

            binding.enableLocationBtn.setOnClickListener {
                if (myHelper.checkLocationPermission()) {
                    if (!myHelper.isLocationEnable()) {
                        myHelper.onGPS(resultLauncher)
                    }
                } else {
                    myHelper.requestLocationPermission(this)
                }
            }
        } else {
            if (myHelper.checkLocationPermission() && myHelper.isLocationEnable()) {
                    fusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
                        val location: Location? = task.result
                        if (location != null) {
                            val locationData =
                                myHelper.extractAddressDetails(location.latitude, location.longitude)

                            binding.text1.text = "Use current location"

                            binding.text5.text =
                                "${locationData?.block},${locationData?.locality},${locationData?.state}"

                        } else {
                            setLocationOnToolbarFromSharedprefrence()
                        }
                    }

            } else {
                setLocationOnToolbarFromSharedprefrence()
            }

            binding.enableLocationBtn.visibility = View.GONE
            binding.selectCurrentLocation.visibility = View.VISIBLE

            binding.selectCurrentLocation.setOnClickListener {
                finish()
            }
        }

        addressAdapter = ShowAllSavedAddressAdapter(this, "selectedActivity")
        binding.savedAddressRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.savedAddressRecyclerView.adapter = addressAdapter


    }

    private fun setLocationOnToolbarFromSharedprefrence() {
        val locationData =
            myHelper.extractAddressDetails(myHelper.getLatitude(), myHelper.getLongitude())
        binding.text1.text = "Use current location"

        binding.text5.text =
            "${locationData?.block},${locationData?.locality},${locationData?.state}"

    }

    // fetch saved address in roomdb
    private fun showSavedAddressInRecyclerView() {
        binding.savedAddressProgressBar.visibility = View.VISIBLE
        binding.savedAddressRecyclerView.visibility = View.GONE

        mainViewModel.getAllAddresses(myHelper.numberIs())
        mainViewModel.addresses.observe(this) { addresses ->

            if (addresses.isEmpty()) {
                binding.savedAddressRecyclerViewLayout.visibility = View.GONE
                binding.dividerView4.visibility = View.GONE
            } else {
                binding.savedAddressRecyclerViewLayout.visibility = View.VISIBLE
                binding.savedAddressRecyclerView.visibility = View.VISIBLE
                binding.dividerView4.visibility = View.VISIBLE
                binding.savedAddressProgressBar.visibility = View.GONE
                addressAdapter.updateAddresses(addresses)
                addressAdapter.notifyDataSetChanged()
            }

        }
    }

    // get nearby location from geoapify
    private fun updateTheNearbyLocationAdapter() {
        viewNearbyLocationsAdapter = ViewNearbyLocationsAdapter()
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = viewNearbyLocationsAdapter

        binding.progressBar.visibility = View.VISIBLE
        binding.recyclerView.visibility = View.GONE

        mainViewModel.getNearByLocation(myHelper.getLatitude(), myHelper.getLongitude())

        mainViewModel.nearLocationList.observe(this, Observer { validPlaces ->
            binding.progressBar.visibility = View.GONE
            binding.recyclerView.visibility = View.VISIBLE

            viewNearbyLocationsAdapter.updateList(validPlaces as MutableList)
            viewNearbyLocationsAdapter.notifyDataSetChanged()
            //  nearByLocationList.addAll(validPlaces)

            for (place in validPlaces) {
                Log.d(
                    "SelectAddressActivity",
                    "Name: ${place.properties.name}, Address: ${place.properties.formatted}"
                )
            }
        })
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 10 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            recreate()
            myHelper.onGPS(resultLauncher)
        } else {
            if (!shouldShowRequestPermissionRationale(permissions[0])) {
                val progressDialog = EnableAppLocationPermissionDialogFragment()
                progressDialog.show(supportFragmentManager, "requestLocationPermission")
            }
        }
    }


    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_bottom)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_bottom)
    }


    override fun menuClick(address: UserSavedAddress, action: String) {
        when (action) {
            "edit" -> {
                // when user click on Edit click
                val intent = Intent(this, AddLocationFromMapActivity::class.java)
                intent.putExtra("selectedLocation", address.selectedLocation)
                intent.putExtra("addressId", address.id)
                startActivity(intent)
            }

            "delete" -> {
                showDeleteConfirmationDialog(address)
            }
        }
    }

    private fun showDeleteConfirmationDialog(address: UserSavedAddress) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Are you sure you want to delete this address?")

        builder.setPositiveButton("Yes") { dialog, _ ->
            mainViewModel.deleteAddress(address.id)
            showSavedAddressInRecyclerView()

            dialog.dismiss()
        }

        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }

        builder.create().show()
    }


    override fun addressClick(userSavedAddress: UserSavedAddress) {
        mainViewModel.getAllAddresses(myHelper.numberIs())

        mainViewModel.addresses.observe(this, Observer { addresses->

          //  val selectedAddress = address.find { it.addressSelected }

            val updatedAddresses = addresses.map { address ->
                // If this is the previously selected address, set it to false
                if (address.addressSelected) {
                    address.copy(addressSelected = false)
                } else if (address.id == userSavedAddress.id) {
                    // If this is the clicked address, set it to true
                    address.copy(addressSelected = true)
                } else {
                    // Leave all other addresses unchanged
                    address
                }
            }

            mainViewModel.saveAddressesForUser(updatedAddresses)

            finish()

        })

    }
}