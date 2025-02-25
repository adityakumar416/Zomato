package com.test.zomato.view.orders.orderModels

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "food_items_in_cart")
@Parcelize
data class FoodItemInOrder(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val orderId: Long,
    val foodId: Int,
    val foodName: String,
    val foodPrice: String,
    val foodQuantity: Int,
    val foodOffer: String,
    val foodType: String,  //  item is "Veg" or "Non-Veg"
    val foodSize: String,
    val foodImage: String,
    val restaurantId: Int
) : Parcelable
