package com.test.zomato.view.login.cartSkipUserLogin

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.test.zomato.R
import com.test.zomato.databinding.ActivityMobileNumberLoginWithSkipBinding
import com.test.zomato.utils.PrefKeys

class MobileNumberLoginWithSkipActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMobileNumberLoginWithSkipBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       // enableEdgeToEdge()
        binding = ActivityMobileNumberLoginWithSkipBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.statusBarColor = Color.parseColor("#F3F4FA")

        binding.continueButton.setOnClickListener {

            val mobileNumber = binding.mobileNumberEditText.text.toString()
            val name = binding.nameEditText.text.toString()

            // Validate name and mobile number
            if (name.isNullOrEmpty()) {
                binding.nameEditText.requestFocus()
                Toast.makeText(this, "Enter Name", Toast.LENGTH_SHORT).show()
            } else if (mobileNumber.length != 10) {
                binding.mobileNumberEditText.requestFocus()
                Toast.makeText(this, "Enter Correct Number.", Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(this, VerifyCodeWithSkipUserActivity::class.java)
                intent.putExtra(PrefKeys.SKIP_USER_NUMBER, mobileNumber)
                intent.putExtra(PrefKeys.SKIP_USER_NAME, name)
                startActivity(intent)
                finish()
            }
        }
    }
}
