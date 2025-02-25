package com.test.zomato.view.main.home.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "Orders")
@Parcelize
data class RestaurantDetails(
    @PrimaryKey
    val id: Int,
    val imageSliderImages: List<String>,
    val bookmarkIcon: Int,
    val closeEyeIcon: Int,
    val time: String,
    val distance: String,
    val restaurantName: String,
    val rating: Float,
    val foodType1: String?,
    val foodType2: String?,
    val costForOne: String,
    val discountText: String,
    val timeToReach: String,
    val restaurantLocation: String,
    val offerUpToText: String,
    val totalOffers: Int,
    val recommendedFoodList: List<FoodItem>
) : Parcelable

@Entity(tableName = "foodItem")
@Parcelize
data class FoodItem(
    @PrimaryKey
    val id: Int,  // Added the ID for food item
    val foodType: String,
    val foodName: String,
    val foodRating: Double,
    val totalRatingCount: Int,
    val foodPrice: String,
    val foodDescription: String,
    val foodImage: String,
    var foodQuantity: Int = 0,

    val restaurantId: Int,
    val restaurantName: String,

    val foodOffer: String,
    val foodSize: String,

    val eggFood: Boolean,
    val sweetFood: Boolean

) : Parcelable


/*package com.test.zomato.view.main.home.models

data class RestaurantCardData(
    val imageSliderImages: List<String>,
    val bookmarkIcon: Int,
    val closeEyeIcon: Int,
    val time: String,
    val distance: String,
    val restaurantName: String,
    val rating: Float,
    val foodType1: String?,
    val foodType2: String?,
    val costForOne: String,
    val discountText: String
)*/





