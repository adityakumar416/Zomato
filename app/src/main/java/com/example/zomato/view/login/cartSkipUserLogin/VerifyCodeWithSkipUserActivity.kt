package com.test.zomato.view.login.cartSkipUserLogin

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import com.test.zomato.databinding.ActivityVerifyCodeWithSkipUserBinding
import com.test.zomato.utils.AppSharedPreferences
import com.test.zomato.utils.PrefKeys

class VerifyCodeWithSkipUserActivity : AppCompatActivity() {
    private lateinit var binding: ActivityVerifyCodeWithSkipUserBinding
    private val appSharedPreferences by lazy { AppSharedPreferences.getInstance(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // enableEdgeToEdge()
        binding = ActivityVerifyCodeWithSkipUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.statusBarColor = Color.parseColor("#F3F4FA")

        val mobileNumber = intent?.getStringExtra(PrefKeys.SKIP_USER_NUMBER) ?: ""
        val skipUserName = intent?.getStringExtra(PrefKeys.SKIP_USER_NAME) ?: ""


        binding.backButton.setOnClickListener {
            finish()
        }

        binding.editPhoneNumber.setOnClickListener {
            val intent = Intent(this, MobileNumberLoginWithSkipActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.enterOTP.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {
                val otpText = p0.toString().trim()
                if (otpText.length == 4 && otpText == "0000") {
                    appSharedPreferences?.saveString(PrefKeys.SKIP_USER_NUMBER, mobileNumber)
                    appSharedPreferences?.saveString(PrefKeys.SKIP_USER_NAME, skipUserName)
                    finish()
                }
            }
        })



        binding.resendOtpTime.setOnClickListener {
            startResendTimer()
        }

        startResendTimer()

    }

    private fun startResendTimer() {
        // 30 seconds timer
        object : CountDownTimer(30000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                binding.resendOtpTime.text = "Resend in ${millisUntilFinished / 1000} sec"
            }

            override fun onFinish() {
                binding.resendOtpTime.text = "Resend OTP"
                binding.resendOtpTime.isEnabled = true
                // binding.resendOtpTime.setBackgroundColor(Color.parseColor("#FF0000"))
            }
        }.start()

        binding.resendOtpTime.isEnabled = false
        //  binding.resendOtpTime.setBackgroundColor(Color.parseColor("#BDBDBD"))
    }
}
