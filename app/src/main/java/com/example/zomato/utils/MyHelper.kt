package com.test.zomato.utils

import android.Manifest
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.util.Patterns
import android.view.View
import android.view.WindowInsetsController
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity.INPUT_METHOD_SERVICE
import androidx.appcompat.app.AppCompatActivity.LOCATION_SERVICE
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.location.LocationManagerCompat
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationRequest.Builder.IMPLICIT_MIN_UPDATE_INTERVAL
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.OnCompleteListener
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.test.zomato.R
import com.test.zomato.view.location.models.LocationData
import java.io.IOException
import java.util.Locale

class MyHelper(private val context: Context) {

    private lateinit var resultLauncher: ActivityResultLauncher<IntentSenderRequest>

    private val appSharedPreferences by lazy { AppSharedPreferences.getInstance(context) }


    //  val sharedPreferences = context.getSharedPreferences("AppPreferences", MODE_PRIVATE)

    // true → White icons.
    // false → Black icons.
    fun setStatusBarIconColor(activity: Activity, isLightIcons: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            activity.window.insetsController?.setSystemBarsAppearance(
                if (isLightIcons) 0 else WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            )
        } else {
            activity.window.decorView.systemUiVisibility = if (isLightIcons) {
                // For white status bar icons
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            } else {
                // For black status bar icons
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }
    }


     fun getLatitude(): Double {
        val latitude = appSharedPreferences?.getFloat(PrefKeys.LATITUDE)?.toDouble()
        return latitude!!
    }

     fun getLongitude(): Double {
        val longitude = appSharedPreferences?.getFloat(PrefKeys.LONGITUDE)?.toDouble()
        return longitude!!
    }


    fun extractAddressDetails(latitude: Double, longitude: Double): LocationData? {
        try {
            val geocoder = Geocoder(context, Locale.getDefault())
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            if (!addresses.isNullOrEmpty()) {
                val address = addresses[0]
                val fullAddress = address.getAddressLine(0) ?: "Unknown Address"

                // Split the full address into parts
                val addressParts = fullAddress.split(",").map { it.trim() }

                // Extract street and block
                val street = addressParts.getOrNull(0) ?: "Unknown Street"

                val block = if (addressParts.size > 1) {
                    val possibleBlock = addressParts[1]
                    if (possibleBlock.contains("Block", ignoreCase = true)) {
                        possibleBlock
                    } else {
                        addressParts[0]
                    }
                } else {
                    "Unknown Block"
                }

                val locality = address.subLocality ?: addressParts[2]
                val state = address.locality ?: addressParts[3]
                val subState = address.adminArea ?: "Unknown State"
                val postalCode = address.postalCode ?: "Unknown Postal Code"
                val country = address.countryName ?: "Unknown Country"

                Log.d("addressinMyhelper", "${addressParts[2]},${addressParts[1]}")

                Log.d("addressinMyhelper", "${fullAddress}")

                Log.d("addressinMyhelper", "${block},${street},${locality},${state},${subState},${postalCode},${country}")

                // Create a LocationData instance
                return LocationData(
                    fullAddress = fullAddress,
                    street = street,
                    block = block,
                    locality = locality,
                    state = state,
                    subState = subState,
                    postalCode = postalCode,
                    country = country
                )
            } else {
                return null
            }
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }
    }


    fun isOnline(): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
        return networkCapabilities != null && networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }


    fun requestLocationPermission(activity: Activity) {
        ActivityCompat.requestPermissions(
            activity, arrayOf(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ), 10
        )
    }


    fun checkLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun isLocationEnable(): Boolean {
        val location: LocationManager =
            context.getSystemService(LOCATION_SERVICE) as LocationManager
        return LocationManagerCompat.isLocationEnabled(location)
    }

    fun onGPS(resultLauncher: ActivityResultLauncher<IntentSenderRequest>)  {

        // Creates a request for GPS location.
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 3000)
           /* .setWaitForAccurateLocation(false)
           // .setMinUpdateIntervalMillis(IMPLICIT_MIN_UPDATE_INTERVAL)
          //  .setMaxUpdateDelayMillis(100000)
            .setMinUpdateDistanceMeters(10f) // Request updates when location changes by 10 meters
           // .setMaxWaitTime(5000) // Wait for maximum 5 seconds to get location*/
            .build()

        // This builder adds location requests to check settings.
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        // checkLocationSettings method check device gps is on or of
        val task =
            LocationServices.getSettingsClient(context).checkLocationSettings(builder.build())

        // if user gps is off
        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                try {
                   // ek resolution prompt create hota hai
                    val intentSenderRequest = IntentSenderRequest.Builder(exception.resolution).build()
                    // resolution prompt ko launch karne ke liye IntentSenderRequest ko resultLauncher.launch(intentSenderRequest) se isko execute karte hain.
                    resultLauncher.launch(intentSenderRequest)
                    // show a popup
                } catch (e: Exception) {
                    e.printStackTrace()
                 //   Toast.makeText(context, "GPS is already enabled", Toast.LENGTH_SHORT).show()
                }
            }
        }

        task.addOnSuccessListener {
            //   Toast.makeText(context, "GPS is already enabled", Toast.LENGTH_SHORT).show()
        }
    }

    fun hideKeyboard() {
        val imm = context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager?
        imm?.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0)
    }

    fun checkNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13 and above, check for POST_NOTIFICATIONS permission
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            // Below Android 13, permission is not required, assume true
            true
        }
    }

    fun deleteCountry(phone: String): String {
        val phoneInstance = PhoneNumberUtil.getInstance()
        try {
            val phoneNumber = phoneInstance.parse(phone, null)
            return phoneNumber?.nationalNumber?.toString() ?: phone
        } catch (_: Exception) {
        }
        return phone
    }

    fun numberIs():String{
        val userPhoneNumber = appSharedPreferences?.getString(PrefKeys.USER_NUMBER) ?: ""
        return userPhoneNumber
    }




     fun isValidPhoneNumber(phone: String): Boolean {
        return Patterns.PHONE.matcher(phone).matches()
    }


    companion object {
        private const val CHANNEL_ID = "notification_channel"
        private const val CHANNEL_NAME = "Notification Channel"
        private const val NOTIFICATION_ID = 1
    }
}