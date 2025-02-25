package com.test.zomato.view.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.test.zomato.R
import com.test.zomato.utils.AppSharedPreferences
import com.test.zomato.view.location.SetLocationPermissionActivity
import com.test.zomato.view.login.UserSignUpActivity
import com.test.zomato.view.main.MainActivity
import com.test.zomato.utils.MyHelper
import com.test.zomato.utils.PrefKeys

class SplashScreen : AppCompatActivity() {

    private lateinit var myHelper: MyHelper
    private val appSharedPreferences by lazy { AppSharedPreferences.getInstance(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.splash_screen)

        myHelper = MyHelper(this)
        myHelper.setStatusBarIconColor(this,true)


        val isSkipBtnClick = appSharedPreferences?.getBoolean(PrefKeys.SKIP_BTN_CLICK)
        val hasVisitedMainActivity = appSharedPreferences?.getBoolean(PrefKeys.VISITED_MAIN_ACTIVITY)
        val hasVisitedSetLocation = appSharedPreferences?.getBoolean(PrefKeys.VISITED_SET_LOCATION)

        if (isSkipBtnClick == true) {
            Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, UserSignUpActivity::class.java))
            }, 1000)
        }else{
            Handler(Looper.getMainLooper()).postDelayed({
                when {
                    hasVisitedMainActivity == true -> {
                        // Navigate directly to MainActivity
                        startActivity(Intent(this, MainActivity::class.java))
                    }
                    hasVisitedSetLocation == true -> {
                        // Navigate directly to SetLocationActivity
                        startActivity(Intent(this, SetLocationPermissionActivity::class.java))
                    }
                    else -> {
                        // Navigate to UserSignUpActivity
                        startActivity(Intent(this, UserSignUpActivity::class.java))
                    }
                }
                finish()
            }, 1000)
        }


    }
}
