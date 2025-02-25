package com.test.zomato.view.cart.interfaces

import com.test.zomato.view.main.home.models.FoodItem

interface AddFoodClickListener {
    fun onFoodClick(foodItem: FoodItem)
    fun onFoodQuantityChange(foodItem: FoodItem)
}
