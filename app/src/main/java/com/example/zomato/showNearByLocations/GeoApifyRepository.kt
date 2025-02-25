package com.test.zomato.showNearByLocations

import com.test.zomato.showNearByLocations.retrofit.RetrofitInstance
import com.test.zomato.view.location.models.PlaceFeature

class GeoApifyRepository {

    val list = mutableListOf<PlaceFeature>()

    suspend fun fetchNearbyLocation(latitude: Double, longitude: Double): List<PlaceFeature> {

        val call = RetrofitInstance.apiCall.getNearbyPlaces(
            categories = "commercial,education,catering",
            filter = "circle:$longitude,$latitude,5000",
            bias = "proximity:$longitude,$latitude",
            limit = 20,
            apiKey = "8a3768af0f8143a5b82b582da20558c6"
        )

        if (call.isSuccessful) {
            val places = call.body()?.features
            if (places != null) {
                val validPlaces = places.filter {
                    !it.properties.name.isNullOrEmpty() && !it.properties.formatted.isNullOrEmpty()
                }
                list.addAll(validPlaces)
            }

        }

        return list

    }


}