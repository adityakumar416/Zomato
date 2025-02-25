package com.test.zomato.cartDB

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.test.zomato.utils.RoomDatabaseHelper
import com.test.zomato.view.main.home.models.FoodItem
import com.test.zomato.view.orders.orderModels.FoodItemInOrder
import com.test.zomato.view.orders.orderModels.OrderDetails
import com.test.zomato.view.orders.orderModels.OrderWithFoodItems
import kotlinx.coroutines.launch

class CartAndOrderViewModel(application: Application) : AndroidViewModel(application) {

    private val roomDbRepository: CartAndOrderRepository by lazy {
        CartAndOrderRepository(RoomDatabaseHelper.getInstance(application)!!.cartDao())
    }

    private val setFoodItemsInList = MutableLiveData<List<FoodItem>>()
    val fetchFoodItemsInCart: LiveData<List<FoodItem>> = setFoodItemsInList

    private val setOrdersInList = MutableLiveData<List<OrderWithFoodItems>>()
    val fetchOrdersInDb: LiveData<List<OrderWithFoodItems>> = setOrdersInList

    // Fetch all orders
    fun fetchOrdersFromDb(numberIs: String) {
        viewModelScope.launch {
            val orders = roomDbRepository.getAllOrders(numberIs) // Fetch all orders
            val ordersWithFoodItems = mutableListOf<OrderWithFoodItems>()

            // Fetch food items for each order and create a list of OrderWithFoodItems
            orders.forEach { order ->
                val foodItems = roomDbRepository.getFoodItemsForOrder(order.orderId)
                ordersWithFoodItems.add(OrderWithFoodItems(order, foodItems))
            }
            setOrdersInList.postValue(ordersWithFoodItems)
        }
    }

    // Update the quantity of a specific FoodItem
    fun changeFoodQuantity(foodQuantity: Int, foodItemId: Int) {
        viewModelScope.launch {
            roomDbRepository.changeFoodQuantity(foodQuantity, foodItemId)
            fetchAndUpdateCartItems()
        }
    }

    // Get a FoodItem by its ID
    fun getFoodItemById(id: Int): LiveData<FoodItem?> {
        val foodItemLiveData = MutableLiveData<FoodItem?>()
        viewModelScope.launch {
            val foodItem = roomDbRepository.getFoodItemById(id)
            foodItemLiveData.postValue(foodItem)
        }
        return foodItemLiveData
    }

    // Add a new FoodItem to the cart
    fun addFoodItemToCart(foodItem: FoodItem) {
        viewModelScope.launch {
            roomDbRepository.addFoodItemToCart(foodItem)
        }
    }

    // Delete a FoodItem from the cart by its ID
    fun deleteFoodItemById(foodItemId: Int) {
        viewModelScope.launch {
            roomDbRepository.deleteFoodItemById(foodItemId)
            fetchAndUpdateCartItems()
        }
    }

    // Fetch and update all food items in the cart
    fun fetchAndUpdateCartItems() {
        viewModelScope.launch {
            val foodItems = roomDbRepository.getAllFoodList()
            setFoodItemsInList.postValue(foodItems)
        }
    }

    // Insert order details and food items with the order id
    fun insertOrderDetails(orderDetails: OrderDetails, foodItemsInOrder: List<FoodItemInOrder>) {
        viewModelScope.launch {
            // Insert the order details and get the generated orderId
            val orderId = roomDbRepository.insertOrderDetails(orderDetails)
            if (orderId > 0) {
                // Now insert the food items with the correct orderId
                foodItemsInOrder.forEach {
                    val foodItemWithOrderId = it.copy(orderId = orderId)  // Set the generated orderId
                    roomDbRepository.insertFoodItemInOrder(foodItemWithOrderId)  // Insert food item into the order
                }
            }
        }
    }

    // Delete all food items related to a specific restaurant by restaurant ID
    fun deleteFoodItemsByRestaurantId(restaurantId: Int) {
        viewModelScope.launch {
            roomDbRepository.deleteFoodItemsByRestaurantId(restaurantId)
            fetchAndUpdateCartItems()
        }
    }
}
