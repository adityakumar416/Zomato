package com.test.zomato.view.main.home.models

data class BarData(
    val restaurantName: String,
    val rating: Float,
    val address: String,
    val distance: String,
    val cuisines1: String,
    val cuisines2: String,
    val costForTwo: String,
    val bankBenefits: String,
    val sliderImages: List<String>
)
