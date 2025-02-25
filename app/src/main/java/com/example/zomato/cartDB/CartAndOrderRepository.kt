package com.test.zomato.cartDB

import com.test.zomato.view.main.home.models.FoodItem
import com.test.zomato.view.orders.orderModels.FoodItemInOrder
import com.test.zomato.view.orders.orderModels.OrderDetails

class CartAndOrderRepository(private val cartDao: CartDao) {

    // Function to get FoodItem by ID
    suspend fun getFoodItemById(id: Int): FoodItem? {
        return cartDao.getFoodItemById(id)
    }

    // Function to update the quantity of the FoodItem
    suspend fun changeFoodQuantity(foodQuantity: Int, id: Int) {
        cartDao.changeQuantity(foodQuantity, id)
    }

    // Fetch all FoodItems in the cart
    suspend fun getAllFoodList(): List<FoodItem>{
        return cartDao.getAllFood()
    }

    // Add a new FoodItem to the cart
    suspend fun addFoodItemToCart(foodItem: FoodItem) {
        cartDao.addFoodInCart(foodItem)
    }

    // Delete a FoodItem from the cart by its ID
    suspend fun deleteFoodItemById(id: Int) {
        cartDao.deleteFoodById(id)
    }

    // Insert the order details into the database
    suspend fun insertOrderDetails(orderDetails: OrderDetails): Long {
        return cartDao.insertOrderDetails(orderDetails)
    }

    // Insert a FoodItem in an order
    suspend fun insertFoodItemInOrder(foodItemInOrder: FoodItemInOrder) {
        cartDao.insertFoodItemInOrder(foodItemInOrder)
    }

    // Delete all FoodItems by restaurant ID
    suspend fun deleteFoodItemsByRestaurantId(restaurantId: Int) {
        cartDao.deleteFoodItemsByRestaurantId(restaurantId)
    }

    // Fetch all orders in the system
    suspend fun getAllOrders(numberIs: String): List<OrderDetails> {
        return cartDao.getAllOrders(numberIs)
    }

    // Fetch food items for a specific order by orderId
    suspend fun getFoodItemsForOrder(orderId: Long): List<FoodItemInOrder> {
        return cartDao.getFoodItemsForOrder(orderId)
    }

}
