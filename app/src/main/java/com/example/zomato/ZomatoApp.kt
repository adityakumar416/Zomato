package com.test.zomato

import android.app.Application
import com.test.zomato.utils.AppSharedPreferences

class ZomatoApp: Application() {

    override fun onCreate() {
        super.onCreate()
     //   AppSharedPreferences.initilize(applicationContext)
    }

}