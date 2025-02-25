package com.test.zomato.view.orders.orderModels

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "order_details")
@Parcelize
data class OrderDetails(
    @PrimaryKey(autoGenerate = true)
    val orderId: Long = 0,
    val restaurantId: Int,
    val restaurantName: String,
    val totalPrice: Double,
    val totalSaved: Double,
    val deliveryAddress: String,
    val orderStatus: String,
    val timestamp: Long,
    val currentUserNumber:String,
    val receiverNumber:String,
    val receiverName:String
) : Parcelable

