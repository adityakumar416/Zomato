package com.test.zomato.view.main

import android.location.Location
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.test.zomato.R
import com.test.zomato.databinding.ActivityMainBinding
import com.test.zomato.utils.AppSharedPreferences
import com.test.zomato.utils.MyHelper
import com.test.zomato.utils.PrefKeys
import com.test.zomato.view.main.home.DiningFragment
import com.test.zomato.view.main.home.HomeFragment
import com.test.zomato.view.main.home.LiveFragment
import com.test.zomato.view.orders.bottomSheets.OrderPlacedSuccessfullyDialogFragment
import com.test.zomato.view.orders.interfaces.OrderPlcaeClickListener
import com.test.zomato.viewModels.MainViewModel


class MainActivity : AppCompatActivity(), OrderPlcaeClickListener {

    private lateinit var bottomNav: BottomNavigationView
    private val myHelper by lazy { MyHelper(this) }
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var mainViewModel: MainViewModel
    private lateinit var binding: ActivityMainBinding
    private val appSharedPreferences by lazy { AppSharedPreferences.getInstance(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        appSharedPreferences?.saveBoolean(PrefKeys.VISITED_MAIN_ACTIVITY, true)


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]


        if (myHelper.checkLocationPermission() && myHelper.isLocationEnable()) {
            // Fetch the last known location
            fusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
                val location: Location? = task.result
                if (location != null) {

                    saveLocationToSharedPreferences(location.latitude, location.longitude)

                   // mainViewModel.saveLocation(location.latitude, location.longitude)

                }
            }
        }


        // Initially load the HomeFragment
        loadFragment(HomeFragment())

        bottomNav = findViewById(R.id.bottomNav)

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.delivery -> {
                    bottomNav.menu.findItem(R.id.delivery)
                        .setIcon(R.drawable.delivery_scooter_filled)
                    bottomNav.menu.findItem(R.id.dining).setIcon(R.drawable.dining_bowl)
                    bottomNav.menu.findItem(R.id.live).setIcon(R.drawable.ticket_image)

                    loadFragment(HomeFragment())
                    true
                }

                R.id.dining -> {
                    bottomNav.menu.findItem(R.id.delivery).setIcon(R.drawable.delivery_scooter)
                    bottomNav.menu.findItem(R.id.dining).setIcon(R.drawable.dining_filled)
                    bottomNav.menu.findItem(R.id.live).setIcon(R.drawable.ticket_image)

                    loadFragment(DiningFragment())
                    true
                }

                R.id.live -> {
                    bottomNav.menu.findItem(R.id.delivery).setIcon(R.drawable.delivery_scooter)
                    bottomNav.menu.findItem(R.id.dining).setIcon(R.drawable.dining_bowl)
                    bottomNav.menu.findItem(R.id.live).setIcon(R.drawable.ticket_filled_image)

                    loadFragment(LiveFragment())
                    true
                }

                else -> {
                    false
                }
            }
        }


        /* // Hide the bottom navigation when scrolling down
         binding.containerScrollView.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { _, _, scrollY, _, oldScrollY ->
             if (scrollY > oldScrollY) {
                 bottomNav.visibility = View.GONE
             } else {
                 bottomNav.visibility = View.VISIBLE
             }
         })
 */
        // if user placed the order then show the success dialog
        if (intent.getBooleanExtra("showOrderDialog", false)) {
            showOrderPlacedSuccessfullyDialog()
        }


    }

    private fun showOrderPlacedSuccessfullyDialog() {
        val dialogFragment = OrderPlacedSuccessfullyDialogFragment()
        dialogFragment.show(supportFragmentManager, "OrderPlacedSuccessfullyDialog")
    }

    // Function to save location (latitude and longitude) to SharedPreferences
    private fun saveLocationToSharedPreferences(latitude: Double, longitude: Double) {
        appSharedPreferences?.saveFloat(PrefKeys.LATITUDE, latitude.toFloat())
        appSharedPreferences?.saveFloat(PrefKeys.LONGITUDE, longitude.toFloat())
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container_view, fragment)
            .commit()
    }

    override fun orderPlaceClick() {
    }

    override fun orderPlacedDialogClick() {
        showOrderPlacedSuccessfullyDialog()

    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity();
    }

}
