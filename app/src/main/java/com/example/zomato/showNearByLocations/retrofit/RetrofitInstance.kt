package com.test.zomato.showNearByLocations.retrofit

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    val retrofit:Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.geoapify.com/v2/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val apiCall: GeoapifyApi by lazy {
        retrofit.create(GeoapifyApi::class.java)
    }


}