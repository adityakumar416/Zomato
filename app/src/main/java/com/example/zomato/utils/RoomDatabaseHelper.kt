package com.test.zomato.utils

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.test.zomato.cartDB.CartDao
import com.test.zomato.view.location.models.UserSavedAddress
import com.test.zomato.view.location.repository.UserSavedAddressDao
import com.test.zomato.view.login.repository.UserDao
import com.test.zomato.view.login.userData.User
import com.test.zomato.view.main.home.models.FoodItem
import com.test.zomato.view.orders.orderModels.FoodItemInOrder
import com.test.zomato.view.orders.orderModels.OrderDetails

@Database(entities =[User::class,FoodItem::class, OrderDetails::class, FoodItemInOrder::class,UserSavedAddress::class], version = 1, exportSchema = false)
abstract class RoomDatabaseHelper:RoomDatabase() {

    abstract fun cartDao(): CartDao

    abstract fun userDao(): UserDao

    abstract fun userSavedAddressDao(): UserSavedAddressDao

    companion object {
        private var instance: RoomDatabaseHelper? = null
        fun getInstance( context: Context): RoomDatabaseHelper? {
            if (instance == null) {
                synchronized(RoomDatabaseHelper::class.java) {
                    instance = Room.databaseBuilder(context, RoomDatabaseHelper::class.java, "Zomatppao")
                        .build()
                }
            }
            return instance
        }
    }


}