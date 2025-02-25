package com.test.zomato.view.login

import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.credentials.Credential
import com.google.android.gms.auth.api.credentials.Credentials
import com.google.android.gms.auth.api.credentials.CredentialsApi
import com.google.android.gms.auth.api.credentials.CredentialsOptions
import com.google.android.gms.auth.api.credentials.HintRequest
import com.test.zomato.databinding.ActivityUserSignUpBinding
import com.test.zomato.utils.AppSharedPreferences
import com.test.zomato.utils.CustomProgressDialog
import com.test.zomato.utils.MyHelper
import com.test.zomato.utils.PrefKeys
import com.test.zomato.utils.RequestPermissionDialog
import com.test.zomato.view.location.SetLocationPermissionActivity

class UserSignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserSignUpBinding
    private val myHelper by lazy { MyHelper(this) }

    // A launcher object that accepts IntentSenderRequests. It is use to trigger external intents.
    private lateinit var resultLauncher: ActivityResultLauncher<IntentSenderRequest>

    private val appSharedPreferences by lazy { AppSharedPreferences.getInstance(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityUserSignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        myHelper.setStatusBarIconColor(this,true)

        if (myHelper.checkLocationPermission()) {
            Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show()
        } else {
            myHelper.requestLocationPermission(this)
        }

        getGpsResult()


        binding.userNumber.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                // Show phone number hint picker dialog when the user clicks on the EditText
                phoneSelection()
            }
        }


        binding.moreLoginOption.setOnClickListener {
            val bottomSheetFragment = LoginBottomSheetFragment()
            bottomSheetFragment.show(supportFragmentManager, "loginBottomSheetFragment")
        }

        binding.changeLanguage.setOnClickListener {
            Toast.makeText(this, "Change Language Clicked", Toast.LENGTH_SHORT).show()
        }

        binding.skip.setOnClickListener {
           // val appPreferences = AppSharedPreferences
            appSharedPreferences?.saveBoolean(PrefKeys.SKIP_BTN_CLICK,true)

            val intent = Intent(this, SetLocationPermissionActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.continueButton.setOnClickListener {
            val number = binding.userNumber.text.toString().trim()

            if (number.isEmpty()) {
                binding.userNumber.requestFocus()
            } else if (number.length != 10 && myHelper.isValidPhoneNumber(number)) {
                Toast.makeText(
                    this,
                    "Please enter a valid 10-digit phone number.",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                // save skip status
                appSharedPreferences?.saveBoolean(PrefKeys.SKIP_BTN_CLICK,false)

                showProgressBar()
                Handler(Looper.getMainLooper()).postDelayed({
                    val countryCodeWithPlus = binding.countryPicker.selectedCountryCodeWithPlus
                    val intent = Intent(this, OtpVerifyActivity::class.java)
                    intent.putExtra("countryCodeWithPlus", countryCodeWithPlus)
                    intent.putExtra("mobileNumber", number)
                    startActivity(intent)
                   // myHelper.showNotification("000000 is your login OTP for Zomato App. Do not share it with anyone. AdITyak416x - ZOMATO")
                    dismissProgressBar()
                }, 1000)
            }
        }


        binding.countryPicker.setOnCountryChangeListener {
            binding.countryCode.text = binding.countryPicker.selectedCountryCodeWithPlus
        }
    }

    private fun getGpsResult() {
        resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    // GPS Enabled
                } else {
                    // GPS Not Enabled
                }
            }
    }

    companion object {
        const val CREDENTIAL_PICKER_REQUEST = 10
    }

    private fun phoneSelection() {
        // To retrieve the Phone Number hints, first, configure
        // the hint selector dialog by creating a HintRequest object.
        val hintRequest = HintRequest.Builder()
            .setPhoneNumberIdentifierSupported(true)
            .build()

        val options = CredentialsOptions.Builder()
            .forceEnableSaveDialog()
            .build()

        // Then, pass the HintRequest object to
        // credentialsClient.getHintPickerIntent()
        // to get an intent to prompt the user to
        // choose a phone number.
        val credentialsClient = Credentials.getClient(applicationContext, options)
        val intent = credentialsClient.getHintPickerIntent(hintRequest)
        try {
            startIntentSenderForResult(
                intent.intentSender,
                CREDENTIAL_PICKER_REQUEST, null, 0, 0, 0, Bundle()
            )
        } catch (e: IntentSender.SendIntentException) {
            e.printStackTrace()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CREDENTIAL_PICKER_REQUEST && resultCode == RESULT_OK) {

            // get data from the dialog which is of type Credential
            val credential: Credential? = data?.getParcelableExtra(Credential.EXTRA_KEY)

            // set the received data t the text view
            credential?.let {
                binding.userNumber.setText(myHelper.deleteCountry(it.id))

            }
        } else if (requestCode == CREDENTIAL_PICKER_REQUEST && resultCode == CredentialsApi.ACTIVITY_RESULT_NO_HINTS_AVAILABLE) {
            Toast.makeText(this, "No phone numbers found", Toast.LENGTH_LONG).show();
        }
    }



    private fun showProgressBar() {
        val progressDialog = CustomProgressDialog()
        progressDialog.show(supportFragmentManager, "customProgressDialog")
    }

    private fun dismissProgressBar() {
        val progressDialog =
            supportFragmentManager.findFragmentByTag("customProgressDialog") as? CustomProgressDialog
        progressDialog?.dismiss()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 10 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            myHelper.onGPS(resultLauncher)
        } else {
            if (permissions.isNotEmpty() && shouldShowRequestPermissionRationale(permissions[0])) {
                val progressDialog = RequestPermissionDialog()
                progressDialog.show(supportFragmentManager, "requestPermissionDialog")
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}
