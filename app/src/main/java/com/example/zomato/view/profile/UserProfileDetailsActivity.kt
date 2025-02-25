package com.example.zomato.view.profile

import android.app.DatePickerDialog
import android.graphics.Color
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.DatePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.material.textfield.TextInputEditText
import com.test.zomato.R
import com.test.zomato.databinding.ActivityUserProfileDetailsBinding
import com.test.zomato.utils.MyHelper
import com.test.zomato.view.login.repository.UserViewModel
import com.test.zomato.view.login.userData.User
import com.test.zomato.view.profile.bottomSheets.AddAndUpdateProfilePictureBottomSheetFragment
import com.test.zomato.view.profile.interfaces.OnProfileClickListener
import java.util.Calendar
import java.util.Locale

class UserProfileDetailsActivity : AppCompatActivity(), OnProfileClickListener {

    private lateinit var binding: ActivityUserProfileDetailsBinding
    private lateinit var userViewModel: UserViewModel
    private val myHelper by lazy { MyHelper(this) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserProfileDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.statusBarColor = Color.parseColor("#F3F4FA")

        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]

        val items = listOf("Male", "Female", "Other", "Prefer not to disclose")
        val adapter = ArrayAdapter(this, R.layout.list_item, items)

        binding.textInputLayout.editText?.let {
            if (it is AutoCompleteTextView) {
                it.setAdapter(adapter)
                it.setDropDownBackgroundResource(android.R.color.white)

            }
        }

        (binding.autoCompleteTextView as AutoCompleteTextView).setAdapter(adapter)

        fetchUserData(myHelper.numberIs())

        binding.backButton.setOnClickListener {
            finish()
        }

        binding.dobInput.setOnClickListener {
            showDatePickerDialog(binding.dobInput)
        }
        binding.dobInput.isFocusable = false
        binding.dobInput.isClickable = true

        binding.anniversaryInput.setOnClickListener {
            showDatePickerDialog(binding.anniversaryInput)
        }
        binding.anniversaryInput.isFocusable = false
        binding.anniversaryInput.isClickable = true



        binding.updateProfile.setOnClickListener {
            val name = binding.nameInput.text.toString().trim()
            val mobile = binding.mobileInput.text.toString().trim()
            val email = binding.emailInput.text.toString().trim()
            val dob = binding.dobInput.text.toString().trim()
            val anniversary = binding.anniversaryInput.text.toString().trim()
            val gender = binding.autoCompleteTextView.text.toString()

            if (name.isNotEmpty() && mobile.isNotEmpty()) {


                if (!email.isNullOrEmpty() && !isValidEmail(email)) {
                    Toast.makeText(this, "Please enter a valid email", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                if (!dob.isNullOrEmpty() && !isValidDate(dob)) {
                    Toast.makeText(this, "Please enter a valid date of birth", Toast.LENGTH_SHORT)
                        .show()
                    return@setOnClickListener
                }

                if (!anniversary.isNullOrEmpty() && !isValidDate(anniversary)) {
                    Toast.makeText(
                        this,
                        "Please enter a valid anniversary date",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }

                val user = User(
                    userNumber = mobile,
                    username = name,
                    userEmail = email,
                    userDOB = dob,
                    anniversaryDate = anniversary,
                    gender = gender,
                    imageUrl = ""
                )

                userViewModel.userLiveData.observe(this) { existingUser ->
                    if (existingUser != null) {
                        user.id = existingUser.id
                        user.imageUrl = existingUser.imageUrl
                        userViewModel.updateUser(user)
                        Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            } else {
                Toast.makeText(this, "Name and Mobile are required", Toast.LENGTH_SHORT).show()
            }
        }

        binding.profileImageLayout.setOnClickListener {
            val addAndUpdateProfilePictureBottomSheetFragment =
                AddAndUpdateProfilePictureBottomSheetFragment()
            val bundle = Bundle()
            bundle.putString("number", myHelper.numberIs())
            addAndUpdateProfilePictureBottomSheetFragment.arguments = bundle
            addAndUpdateProfilePictureBottomSheetFragment.show(
                supportFragmentManager,
                "addAndUpdateProfilePictureBottomSheetFragment"
            )
        }
    }

    private fun fetchUserData(userPhoneNumber: String) {
        if (userPhoneNumber.isNotEmpty()) {
            userViewModel.getUserByPhoneNumber(userPhoneNumber)

            userViewModel.userLiveData.observe(this) { user ->
                user?.let {
                    Log.d("userDataProfile", "fetchUserData: $user")

                    binding.nameInput.setText(user.username)
                    binding.mobileInput.setText(user.userNumber)
                    binding.emailInput.setText(user.userEmail)
                    binding.dobInput.setText(user.userDOB)
                    binding.anniversaryInput.setText(user.anniversaryDate)
                    binding.autoCompleteTextView.setText(user.gender, false)

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

    private fun showDatePickerDialog(id: TextInputEditText) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            R.style.DialogTheme,
            { _: DatePicker, selectedYear: Int, selectedMonth: Int, selectedDay: Int ->
                val formattedDate = "${selectedDay}-${selectedMonth + 1}-${selectedYear}"
                id.setText(formattedDate)
            },
            year,
            month,
            day
        )

        datePickerDialog.show()
        datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE)
            .setTextColor(resources.getColor(R.color.colorPrimary))
        datePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE)
            .setTextColor(resources.getColor(R.color.colorPrimary))
    }

    private fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun isValidDate(date: String): Boolean {
        return try {
            val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
            val selectedDate = dateFormat.parse(date)
            val currentDate = Calendar.getInstance().time
            selectedDate.before(currentDate)
        } catch (e: Exception) {
            false
        }
    }


    override fun uploadProfilePhoto() {
        fetchUserData(myHelper.numberIs())
    }

    override fun deleteProfilePhoto() {
        myHelper.numberIs().let { userViewModel.getUserByPhoneNumber(it) }

        userViewModel.userLiveData.observe(this, Observer { currentUser ->
            currentUser?.let {
                // Update the imageUrl field to null
                userViewModel.updateUserProfile("", it.id)
                Toast.makeText(this, "Profile photo deleted", Toast.LENGTH_SHORT).show()

            }
        })
        fetchUserData(myHelper.numberIs())
    }

}
