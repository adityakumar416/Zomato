package com.test.zomato.view.login

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.test.zomato.databinding.ActivityOtpVerifyBinding
import com.test.zomato.utils.AppSharedPreferences
import com.test.zomato.utils.MyHelper
import com.test.zomato.utils.PrefKeys
import com.test.zomato.view.location.SetLocationPermissionActivity
import com.test.zomato.view.login.repository.UserViewModel
import com.test.zomato.view.login.userData.User
import com.test.zomato.view.notification.EnableNotificationPermissionActivity

class OtpVerifyActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOtpVerifyBinding
    private val myHelper: MyHelper by lazy { MyHelper(this) }
    private lateinit var userViewModel: UserViewModel
    private val appSharedPreferences by lazy { AppSharedPreferences.getInstance(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityOtpVerifyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]

        val countryCodeWithPlus = intent.getStringExtra("countryCodeWithPlus")
        val number = intent.getStringExtra("mobileNumber")
        var numberIs = "$countryCodeWithPlus-$number"
        binding.mobileNumber.text = numberIs

        startResendOtpTimer()

        binding.resendOtp.setOnClickListener {
            startResendOtpTimer()
        }

        binding.backButton.setOnClickListener {
            finish()
        }

        binding.backToLoginMethod.setOnClickListener {
            finish()
        }

        binding.skip.setOnClickListener {
            appSharedPreferences?.saveBoolean(PrefKeys.SKIP_BTN_CLICK,true)

            val intent = Intent(this, SetLocationPermissionActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Bottom sheet for more login options
        binding.tryMoreOption.setOnClickListener {
            val bottomSheetFragment = LoginBottomSheetFragment()
            bottomSheetFragment.show(supportFragmentManager, "loginBottomSheetFragment")
        }

        binding.otpIs.setOnCompleteOtpListener { otp ->
            binding.progressBar.visibility = View.VISIBLE

            if (otp == "000000") {
                myHelper.hideKeyboard()

                if (number != null) {
                    appSharedPreferences?.saveString(PrefKeys.USER_NUMBER, number)
                }

                // Create a User object for the new user
                val user = User(
                    imageUrl = null,
                    username = null,
                    userNumber = number,
                    userEmail = null,
                    userDOB = null,
                    anniversaryDate = null,
                    gender = null
                )

                // Check if the user already exists in the database
                if (number != null) {
                    userViewModel.getUserByPhoneNumber(number)
                }

                // check if the user exists
                userViewModel.userLiveData.observe(this) { existingUser ->
                    binding.progressBar.visibility = View.GONE

                    if (existingUser != null) {
                        // User already exists, proceed with existing data
                        navigateToNextActivity()
                    } else {
                        // User does not exist, save the new user data
                        userViewModel.saveUser(user)
                        navigateToNextActivity()
                    }
                }
            } else {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this, "Enter Correct OTP", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun navigateToNextActivity() {
        if (!myHelper.isLocationEnable() || !myHelper.checkLocationPermission()) {
            Handler(Looper.getMainLooper()).postDelayed({
                val intent = Intent(this, SetLocationPermissionActivity::class.java)
                startActivity(intent)
                finish()
                binding.progressBar.visibility = View.GONE
            }, 1000)
        } else {
            Handler(Looper.getMainLooper()).postDelayed({
                val intent = Intent(this, EnableNotificationPermissionActivity::class.java)
                startActivity(intent)
                finish()
                binding.progressBar.visibility = View.GONE
            }, 1000)
        }
    }

    private fun startResendOtpTimer() {
        val countdownTimer = object : CountDownTimer(10000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsRemaining = millisUntilFinished / 1000
                binding.resendOtpTime.text = "Resend SMS in ${secondsRemaining}s"
                binding.resendOtpTime.visibility = View.VISIBLE
                binding.resendOtp.visibility = View.GONE
                binding.openMessageApp.visibility = View.VISIBLE

                if (secondsRemaining <= 5) {
                    binding.openMessageApp.visibility = View.GONE
                }
            }

            override fun onFinish() {
                binding.resendOtpTime.visibility = View.GONE
                binding.resendOtp.visibility = View.VISIBLE
                binding.tryMoreOption.visibility = View.VISIBLE
                binding.skip.visibility = View.VISIBLE
            }
        }

        countdownTimer.start()
    }
}
