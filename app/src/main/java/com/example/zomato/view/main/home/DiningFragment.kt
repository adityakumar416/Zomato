package com.test.zomato.view.main.home

import android.app.Activity.RESULT_OK
import android.content.ActivityNotFoundException
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.speech.RecognizerIntent
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.test.zomato.R
import com.test.zomato.databinding.FragmentDiningBinding
import com.test.zomato.utils.AppSharedPreferences
import com.test.zomato.utils.MyHelper
import com.test.zomato.utils.PrefKeys
import com.test.zomato.view.location.SelectAddressActivity
import com.test.zomato.view.location.models.UserSavedAddress
import com.test.zomato.view.login.repository.UserViewModel
import com.test.zomato.view.main.home.adapter.BarAdapter
import com.test.zomato.view.main.home.models.BarData
import com.test.zomato.view.profile.ProfileActivity
import com.test.zomato.viewModels.MainViewModel
import java.util.Locale

class DiningFragment : Fragment() {

    private lateinit var binding: FragmentDiningBinding
    private val myHelper by lazy { MyHelper(requireActivity()) }
    private lateinit var barAdapter: BarAdapter

    private lateinit var mainViewModel: MainViewModel
    private lateinit var userViewModel: UserViewModel
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var savedAddresses: List<UserSavedAddress>
    private val REQUEST_CODE_SPEECH_INPUT = 10
    private val appSharedPreferences by lazy { activity?.let { AppSharedPreferences.getInstance(it) } }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDiningBinding.inflate(inflater, container, false)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())


        myHelper.setStatusBarIconColor(requireActivity(), false)
        mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]
        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]

        //fetchUserData(myHelper.numberIs())

        // Observe the LiveData for saved addresses
        mainViewModel.addresses.observe(viewLifecycleOwner) { addresses ->

            if (addresses.isNotEmpty()) {
                savedAddresses = addresses
                updateToolbarLocation()
            }else{
                setLocationOnToolbar()
            }
        }


        val appPreferences = AppSharedPreferences
        val isSkipBtnClick = appSharedPreferences?.getBoolean(PrefKeys.SKIP_BTN_CLICK)

        if (isSkipBtnClick == true) {
            binding.profile.visibility = View.GONE
            binding.menuIcon.visibility = View.VISIBLE

            binding.menuIcon.setOnClickListener {
                val intent = Intent(context, ProfileActivity::class.java)
                activity?.startActivity(intent)
            }

        }else{
            binding.profile.visibility = View.VISIBLE
            binding.menuIcon.visibility = View.GONE
        }

        val dummyBarList = listOf(
            BarData(
                restaurantName = "Noida Social",
                rating = 4.5f,
                address = "DLF Mall of India, Sector 18, Noida",
                distance = "4.5 km",
                cuisines1 = "North Indian",
                cuisines2 = "Chinese",
                costForTwo = "₹1500 for two",
                bankBenefits = "Bank benefits + 1 more",
                sliderImages = listOf(
                    "https://b.zmtcdn.com/data/pictures/6/18767086/5703dac63e1d4deab23e40fb932091c9.jpg?fit=around|960:500&crop=960:500;*,*",
                    "https://content.jdmagicbox.com/comp/noida/x3/011pxx11.xx11.211221112804.i3x3/catalogue/toy-boy-noida-sector-38-noida-pubs-xx2q1226ph.jpg",
                    "https://d24l7ypac8dw56.cloudfront.net/MenuImages/IMG20230211WA0008-90966-102675.jpg",
                    "https://ik.imagekit.io/pu0hxo64d/uploads/gallery/tr:e-sharpen,l-text,ie-U2xvc2hvdXQuY29t,lfo-center,co-FFFFFF50,fs-20,tg-b,l-end/ambience-of-clinque-264.jpg"
                )
            ),
            BarData(
                restaurantName = "The Barbeque Nation",
                rating = 4.2f,
                address = "Sector 32, Noida",
                distance = "3.5 km",
                cuisines1 = "Barbeque",
                cuisines2 = "Desserts",
                costForTwo = "₹1800 for two",
                bankBenefits = "Special Offers",
                sliderImages = listOf(
                    "https://ik.imagekit.io/pu0hxo64d/uploads/gallery/tr:e-sharpen,l-text,ie-U2xvc2hvdXQuY29t,lfo-center,co-FFFFFF50,fs-20,tg-b,l-end/interior-decor-of-clinque-144.jpg",
                    "https://content.jdmagicbox.com/comp/noida/i1/011pxx11.xx11.220623163025.h6i1/catalogue/clinque-noida-sector-38-noida-lounge-bars-jv9vdo0tav.jpg",
                    "https://ik.imagekit.io/pu0hxo64d/uploads/gallery/seating-area-of-the-bar-company-885.JPG",
                    "https://b.zmtcdn.com/data/pictures/6/18767086/5a9f5a35a9c727e341bedc1b3ea753f5.jpeg"
                )
            ),
            BarData(
                restaurantName = "The Whiskey Bar",
                rating = 4.8f,
                address = "Connaught Place, New Delhi",
                distance = "2.1 km",
                cuisines1 = "Continental",
                cuisines2 = "Drinks",
                costForTwo = "₹2500 for two",
                bankBenefits = "10% OFF with Credit Card",
                sliderImages = listOf(
                    "https://content.jdmagicbox.com/comp/delhi/81/011pxx11.xx11.220303094658.0qqp/catalogue/the-whiskey-bar-connaught-place-new-delhi.jpg",
                    "https://b.zmtcdn.com/data/pictures/1/18767086/5703dac63e1d4deab23e40fb932091c9.jpg?fit=around|960:500&crop=960:500;*,*",
                    "https://ik.imagekit.io/pu0hxo64d/uploads/gallery/seating-area-of-the-bar-company-885.JPG",
                    "https://d24l7ypac8dw56.cloudfront.net/MenuImages/IMG20230211WA0008-90966-102675.jpg"
                )
            ),
            BarData(
                restaurantName = "The Royal Lounge",
                rating = 4.3f,
                address = "Sector 47, Gurgaon",
                distance = "7.8 km",
                cuisines1 = "Italian",
                cuisines2 = "European",
                costForTwo = "₹2200 for two",
                bankBenefits = "20% OFF with Debit Card",
                sliderImages = listOf(
                    "https://ik.imagekit.io/pu0hxo64d/uploads/gallery/interior-decor-of-clinque-144.jpg",
                    "https://d24l7ypac8dw56.cloudfront.net/MenuImages/IMG20230211WA0008-90966-102675.jpg",
                    "https://b.zmtcdn.com/data/pictures/6/18767086/5703dac63e1d4deab23e40fb932091c9.jpg?fit=around|960:500&crop=960:500;*,*",
                    "https://content.jdmagicbox.com/comp/noida/x3/011pxx11.xx11.211221112804.i3x3/catalogue/toy-boy-noida-sector-38-noida-pubs-xx2q1226ph.jpg"
                )
            ),
            BarData(
                restaurantName = "The Beer Cafe",
                rating = 4.1f,
                address = "Saket, New Delhi",
                distance = "5.2 km",
                cuisines1 = "Beer",
                cuisines2 = "Bar Snacks",
                costForTwo = "₹1400 for two",
                bankBenefits = "Exclusive Bank Offers",
                sliderImages = listOf(
                    "https://content.jdmagicbox.com/comp/delhi/81/011pxx11.xx11.220303094658.0qqp/catalogue/the-whiskey-bar-connaught-place-new-delhi.jpg",
                    "https://ik.imagekit.io/pu0hxo64d/uploads/gallery/seating-area-of-the-bar-company-885.JPG",
                    "https://b.zmtcdn.com/data/pictures/6/18767086/5a9f5a35a9c727e341bedc1b3ea753f5.jpeg",
                    "https://ik.imagekit.io/pu0hxo64d/uploads/gallery/tr:e-sharpen,l-text,ie-U2xvc2hvdXQuY29t,lfo-center,co-FFFFFF50,fs-20,tg-b,l-end/ambience-of-clinque-264.jpg"
                )
            )
        )


        barAdapter = BarAdapter(dummyBarList)
        binding.recyclerView.layoutManager = LinearLayoutManager(requireActivity())
        binding.recyclerView.adapter = barAdapter


        binding.search.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {

                val searchText = p0.toString().trim()

                val list = if (searchText.isNotEmpty()) {
                    dummyBarList.filter {
                        it.restaurantName.contains(searchText, ignoreCase = true)
                                || it.address.contains(searchText, ignoreCase = true)
                    }
                } else {
                    dummyBarList
                }

                barAdapter.updateList(list)
                barAdapter.notifyDataSetChanged()
            }
        })



        binding.profile.setOnClickListener {
            val intent = Intent(context, ProfileActivity::class.java)
            activity?.startActivity(intent)
        }

        binding.userLocation.setOnClickListener {
            val intent = Intent(context, SelectAddressActivity::class.java)
            activity?.startActivity(intent)
            activity?.overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_top)
        }


        binding.micIcon.setOnClickListener {
            // Create an intent for speech recognition
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to text")
            }

            try {
                startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT)
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(activity, "Speech recognition is not supported on this device.", Toast.LENGTH_SHORT).show()
            }
        }

        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_SPEECH_INPUT) {
            if (resultCode == RESULT_OK && data != null) {
                // Extract the result from the data
                val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                if (!result.isNullOrEmpty()) {
                    // Set the recognized speech text to the search EditText
                    binding.search.setText(result[0])
                } else {
                    Toast.makeText(activity, "No speech input recognized", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    override fun onStart() {
        super.onStart()
        fetchUserData(myHelper.numberIs())
        mainViewModel.getAllAddresses(myHelper.numberIs())
    }


    private fun fetchUserData(userPhoneNumber: String) {
        if (userPhoneNumber.isNotEmpty()) {
            userViewModel.getUserByPhoneNumber(userPhoneNumber)

            activity?.let {
                userViewModel.userLiveData.observe(it) { user ->
                    user?.let {
                        Log.d("userDataProfile", "fetchUserData: $user")

                        if (!user.imageUrl.isNullOrEmpty()) {
                            Glide.with(this).load(user.imageUrl).into(binding.userImage)
                            binding.userFirstCharacter.visibility = View.GONE
                            binding.userImage.visibility = View.VISIBLE
                        } else {
                            binding.userFirstCharacter.visibility = View.VISIBLE
                            binding.userImage.visibility = View.GONE
                        }
                    }
                }
            }
        }
    }

    // set address that has boolean true
    private fun updateToolbarLocation() {
        val selectedAddress = getSelectedAddress()
        if (selectedAddress != null) {
            val address = selectedAddress.selectedLocation.split(",")
            binding.userBlockLocation.text = address[0]
            binding.address.text = "${address[1]}, ${address[2]}"
        } else {
            setLocationOnToolbar()
        }
    }

    private fun getSelectedAddress(): UserSavedAddress? {
        return savedAddresses.find { it.addressSelected }
    }

    // if user not selected the address show current address
    private fun setLocationOnToolbar() {

        if (myHelper.checkLocationPermission() && myHelper.isLocationEnable()) {
            activity?.let {
                fusedLocationClient.lastLocation.addOnCompleteListener(it) { task ->
                    val location: Location? = task.result
                    if (location != null) {
                        val locationData =
                            myHelper.extractAddressDetails(location.latitude, location.longitude)

                        Log.d("userAddress", "${locationData?.fullAddress}")

                        binding.userBlockLocation.text = locationData?.block
                        binding.address.text = "${locationData?.locality},${locationData?.state}"
                    } else {
                        setLocationOnToolbarFromSharedprefrence()
                    }
                }
            }
        } else {
            setLocationOnToolbarFromSharedprefrence()
        }

    }

    // get current address using save coordinates
    private fun setLocationOnToolbarFromSharedprefrence() {
        val locationData = myHelper.extractAddressDetails(myHelper.getLatitude(), myHelper.getLongitude())
        binding.userBlockLocation.text = locationData?.block
        binding.address.text = "${locationData?.locality},${locationData?.state}"
    }

}
