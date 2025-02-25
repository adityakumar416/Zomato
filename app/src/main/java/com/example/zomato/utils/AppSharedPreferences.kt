package com.test.zomato.utils

import android.content.Context

class AppSharedPreferences private constructor(context: Context){


    private var sharedPreferences = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)


    companion object{

        private var INSTANCE: AppSharedPreferences? = null

        fun getInstance(context: Context): AppSharedPreferences? {
            if (INSTANCE == null) {
                synchronized(this) {
                    if (INSTANCE == null)
                        INSTANCE = AppSharedPreferences(context)
                }
            }
            return INSTANCE
        }

    }


    fun saveString(key: String, value: String) {
        sharedPreferences.edit().putString(key, value).apply()
    }

    fun saveBoolean(key: String, value: Boolean) {
        sharedPreferences.edit().putBoolean(key, value).apply()
    }

    fun saveFloat(key: String, value: Float) {
        sharedPreferences.edit().putFloat(key, value).apply()
    }

    fun getString(key: String): String? {
        return sharedPreferences.getString(key, null)
    }

    fun getBoolean(key: String): Boolean {
        return sharedPreferences.getBoolean(key, false)
    }

    fun getFloat(key: String): Float {
        return sharedPreferences.getFloat(key, 0f)
    }

    fun clearAllData() {
        sharedPreferences.edit().clear().apply()
    }

}
