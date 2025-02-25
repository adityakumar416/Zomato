package com.test.zomato.view.location

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.test.zomato.databinding.ActivitySetLocationBinding
import com.test.zomato.utils.AppSharedPreferences
import com.test.zomato.utils.CustomProgressDialog
import com.test.zomato.utils.EnableAppLocationPermissionDialogFragment
import com.test.zomato.utils.MyHelper
import com.test.zomato.utils.RequestPermissionDialog
import com.test.zomato.view.main.MainActivity

class SetLocationPermissionActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySetLocationBinding
    private val myHelper: MyHelper by lazy { MyHelper(this) }
    private lateinit var resultLauncher: ActivityResultLauncher<IntentSenderRequest>

    //  var skip :Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // Set up result launcher for GPS settings
        resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
                if (result.resultCode == RESULT_OK) {

                    navigateToMainActivity()
                } else {
                    Toast.makeText(this, "Turn On the GPS for get your current location.", Toast.LENGTH_SHORT).show()
                   // navigateToMainActivity()
                }
            }

        if (myHelper.checkLocationPermission()) {
            if (myHelper.isLocationEnable()) {
                navigateToMainActivity()
            }
        }

        binding.enableDeviceLocation.setOnClickListener {
            if (myHelper.checkLocationPermission()) {
                if (!myHelper.isLocationEnable()) {
                    myHelper.onGPS(resultLauncher)
                } else {
                    navigateToMainActivity()
                }
            } else {
                myHelper.requestLocationPermission(this)
            }
        }
    }


    override fun onResume() {
        super.onResume()
        if (myHelper.checkLocationPermission()) {
            if (myHelper.isLocationEnable()) {
                navigateToMainActivity()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 10) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (myHelper.isLocationEnable()) {
                    navigateToMainActivity()
                } else {
                    myHelper.onGPS(resultLauncher)
                }
            } else {
                //  true: The user denied the permission without selecting "Don't ask again."
                //  false: The user denied the permission and selected "Don't ask again."
                if (permissions.isNotEmpty()) {
                    if (!shouldShowRequestPermissionRationale(permissions[0])) {
                        // User selected "Don't Ask Again"
                        val progressDialog = EnableAppLocationPermissionDialogFragment()
                        progressDialog.show(supportFragmentManager, "requestLocationPermission")
                    } else {
                        // Permission denied without "Don't Ask Again"
                        val progressDialog = RequestPermissionDialog()
                        progressDialog.show(supportFragmentManager, "requestPermissionDialog")

                    }
                }
            }
        }
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity();
       // finish()
    }
}
