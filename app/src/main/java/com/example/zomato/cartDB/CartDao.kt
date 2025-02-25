package com.test.zomato.cartDB

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.test.zomato.view.main.home.models.FoodItem
import com.test.zomato.view.orders.orderModels.FoodItemInOrder
import com.test.zomato.view.orders.orderModels.OrderDetails

@Dao
interface CartDao {

    // this only use in AddFoodInCartBottomSheet for fetch current foodQuantity
    @Query("SELECT * FROM foodItem WHERE id = :id")
    suspend fun getFoodItemById(id: Int): FoodItem?

    // this only use in AddFoodInCartBottomSheet for add food in cart
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addFoodInCart(foodItem: FoodItem)

    // this function use to change the food quantity
    @Query("UPDATE foodItem SET foodQuantity = :foodQuantity WHERE id = :id")
    suspend fun changeQuantity(foodQuantity: Int, id: Int)

    // get all food
    @Query("SELECT * FROM foodItem")
    suspend fun getAllFood(): List<FoodItem>


    // this function add the order details and return the order id in long
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertOrderDetails(orderDetails: OrderDetails): Long

    // this function is use to add the all cart food in RoomDb
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertFoodItemInOrder(foodItemInOrder: FoodItemInOrder)


    // this function is use to delete the food using food id
    @Query("DELETE FROM foodItem WHERE id = :id")
    suspend fun deleteFoodById(id: Int)

    // this function is use to delete all food after the order place
    @Query("DELETE FROM foodItem WHERE restaurantId = :restaurantId")
    suspend fun deleteFoodItemsByRestaurantId(restaurantId: Int)

    // get all order
    @Query("SELECT * FROM order_details where currentUserNumber = :numberIs")
    suspend fun getAllOrders(numberIs: String): List<OrderDetails>

    // get all food that match orderId to show in your order
    @Query("SELECT * FROM food_items_in_cart WHERE orderId = :orderId")
    suspend fun getFoodItemsForOrder(orderId: Long): List<FoodItemInOrder>
}


