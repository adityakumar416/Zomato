package com.test.zomato.view.notification

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.test.zomato.databinding.ActivityEnableNotificationPermissionBinding
import com.test.zomato.view.location.SetLocationPermissionActivity
import com.test.zomato.view.main.MainActivity
import com.test.zomato.utils.MyHelper

class EnableNotificationPermissionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEnableNotificationPermissionBinding
    private val myHelper: MyHelper by lazy { MyHelper(this) }

    // Declare the permission launcher at the class level
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        // Navigate to SetLocationActivity regardless of the permission result
        navigateToMainActivity()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEnableNotificationPermissionBinding.inflate(layoutInflater)
        setContentView(binding.root)


        if (checkNotificationPermission()) {
            navigateToMainActivity()
            return
        }

        binding.notNow.setOnClickListener {
            navigateToMainActivity()
        }


        binding.enableAppNotification.setOnClickListener {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                // Check and request permission only for Android 13 and above
                if (ContextCompat.checkSelfPermission(
                        this, Manifest.permission.POST_NOTIFICATIONS
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // Request the permission if not granted
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                } else {
                    // Permission already granted, navigate
                    navigateToMainActivity()
                }
            } else {
                // For devices below Android 13, navigate directly
                navigateToMainActivity()
            }

        }
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }



    private fun checkNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13 and above, check for POST_NOTIFICATIONS permission
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            // Below Android 13, permission is not required, assume true
            true
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity();
        finish()
    }

}
