package com.test.zomato.view.cart.interfaces

import com.test.zomato.view.main.home.models.RestaurantDetails

interface RestaurantsClickListener {
    fun onRestaurantsClick(restaurantDetails: RestaurantDetails)
}