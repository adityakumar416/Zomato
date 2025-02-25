package com.test.zomato.view.profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.zomato.view.profile.UserProfileDetailsActivity
import com.test.zomato.R
import com.test.zomato.databinding.ActivityProfileBinding
import com.test.zomato.utils.AppSharedPreferences
import com.test.zomato.utils.MyHelper
import com.test.zomato.utils.PrefKeys
import com.test.zomato.view.location.MyAddressesBookActivity
import com.test.zomato.view.login.UserSignUpActivity
import com.test.zomato.view.login.repository.UserViewModel
import com.test.zomato.view.main.JoinGoldActivity
import com.test.zomato.view.orders.YourOrderActivity

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private lateinit var userViewModel: UserViewModel
    private val myHelper by lazy { MyHelper(this) }
    private val appSharedPreferences by lazy { AppSharedPreferences.getInstance(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)


        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]

        binding.backButton.setOnClickListener {
            finish()
        }

        binding.layout1.setOnClickListener {
            startActivity(Intent(this, UserProfileDetailsActivity::class.java))
        }

        fetchUserData(myHelper.numberIs())

        val isSkipBtnClick = appSharedPreferences?.getBoolean(PrefKeys.SKIP_BTN_CLICK)

        if (isSkipBtnClick == true) {

            binding.profileViews.visibility = View.GONE
            binding.diningAndExperiencesSections.visibility = View.GONE
            binding.feedingIndiaSection.visibility = View.GONE
            binding.logout.visibility = View.GONE
            binding.userDetailsCard.visibility = View.GONE
            binding.loginProfileCard.visibility = View.VISIBLE

            binding.loginProfileCard.setOnClickListener {
                startActivity(Intent(this, UserSignUpActivity::class.java))
            }


        } else {
            binding.userDetailsCard.visibility = View.VISIBLE
            binding.loginProfileCard.visibility = View.GONE
            binding.profileViews.visibility = View.VISIBLE
            binding.diningAndExperiencesSections.visibility = View.VISIBLE
            binding.feedingIndiaSection.visibility = View.VISIBLE
            binding.logout.visibility = View.VISIBLE

        }


        binding.joinGold.setOnClickListener {
            startActivity(Intent(this, JoinGoldActivity::class.java))
        }



        binding.yourOrder.setOnClickListener {
            val intent = Intent(this, YourOrderActivity::class.java)
            startActivity(intent)
        }

        binding.yourProfile.setOnClickListener {
            val intent = Intent(this, UserProfileDetailsActivity::class.java)
            startActivity(intent)
        }

        binding.addressBook.setOnClickListener {
            val intent = Intent(this, MyAddressesBookActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_top)
        }


        binding.logout.setOnClickListener {

            Toast.makeText(this, "Logout clicked", Toast.LENGTH_SHORT).show()
            appSharedPreferences?.clearAllData()
            val intent = Intent(this, UserSignUpActivity::class.java)
            startActivity(intent)
            finish()

        }


        showToastOnViewsClick()

    }

    override fun onRestart() {
        super.onRestart()
        fetchUserData(myHelper.numberIs())
    }

    private fun fetchUserData(userPhoneNumber: String) {
        if (userPhoneNumber.isNotEmpty()) {
            userViewModel.getUserByPhoneNumber(userPhoneNumber)

            userViewModel.userLiveData.observe(this) { user ->

                user?.let {
                    Log.d("userDataProfile", "fetchUserData: $user")

                    if (user.username.isNullOrEmpty()) {
                        binding.userName.text = "Hi, User"
                    } else {
                        binding.userName.text = user.username
                    }

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


    private fun showToastOnViewsClick() {
        binding.foodOrderSection.setOnClickListener {
            Toast.makeText(this, "Food Order Section clicked", Toast.LENGTH_SHORT).show()
        }

        binding.foodOrderLayout.setOnClickListener {
            Toast.makeText(this, "Food Order Layout clicked", Toast.LENGTH_SHORT).show()
        }

        binding.favoriteOrder.setOnClickListener {
            Toast.makeText(this, "Favorite Orders clicked", Toast.LENGTH_SHORT).show()
        }


        binding.hiddenRestaurants.setOnClickListener {
            Toast.makeText(this, "Hidden Restaurants clicked", Toast.LENGTH_SHORT).show()
        }

        binding.onlineOrderingHelp.setOnClickListener {
            Toast.makeText(this, "Online Ordering Help clicked", Toast.LENGTH_SHORT).show()
        }

        binding.yourDiningTransctions.setOnClickListener {
            Toast.makeText(this, "Your Dining Transactions clicked", Toast.LENGTH_SHORT).show()
        }

        binding.yourDiningRewards.setOnClickListener {
            Toast.makeText(this, "Your Dining Rewards clicked", Toast.LENGTH_SHORT).show()
        }

        binding.yourBookings.setOnClickListener {
            Toast.makeText(this, "Your Bookings clicked", Toast.LENGTH_SHORT).show()
        }

        binding.diningHelp.setOnClickListener {
            Toast.makeText(this, "Dining Help clicked", Toast.LENGTH_SHORT).show()
        }

        binding.diningSettings.setOnClickListener {
            Toast.makeText(this, "Dining Settings clicked", Toast.LENGTH_SHORT).show()
        }

        binding.frequentlyAskedQuestions.setOnClickListener {
            Toast.makeText(this, "Frequently Asked Questions clicked", Toast.LENGTH_SHORT).show()
        }

        binding.about.setOnClickListener {
            Toast.makeText(this, "About clicked", Toast.LENGTH_SHORT).show()
        }

        binding.sendFeedback.setOnClickListener {
            Toast.makeText(this, "Send Feedback clicked", Toast.LENGTH_SHORT).show()
        }

        binding.reportSafetyEmergency.setOnClickListener {
            Toast.makeText(this, "Report a Safety Emergency clicked", Toast.LENGTH_SHORT).show()
        }

        binding.settings.setOnClickListener {
            Toast.makeText(this, "Settings clicked", Toast.LENGTH_SHORT).show()
        }
        binding.yourImpact.setOnClickListener {
            Toast.makeText(this, "Your Impact clicked", Toast.LENGTH_SHORT).show()
        }

        binding.getFeedingIndiaReceipt.setOnClickListener {
            Toast.makeText(this, "Get Feeding India Receipt clicked", Toast.LENGTH_SHORT).show()
        }
    }
}
