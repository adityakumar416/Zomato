package com.test.zomato.view.orders.orderModels

data class OrderWithFoodItems(
    val order: OrderDetails,
    val foodItems: List<FoodItemInOrder>
)
