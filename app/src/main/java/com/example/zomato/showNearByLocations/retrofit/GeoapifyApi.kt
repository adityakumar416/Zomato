package com.test.zomato.showNearByLocations.retrofit

import com.test.zomato.view.location.models.PlacesResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface GeoapifyApi {
    @GET("places")
    suspend fun getNearbyPlaces(
        @Query("categories") categories: String,
        @Query("filter") filter: String,
        @Query("bias") bias: String,
        @Query("limit") limit: Int,
        @Query("apiKey") apiKey: String
    ): Response<PlacesResponse>

}