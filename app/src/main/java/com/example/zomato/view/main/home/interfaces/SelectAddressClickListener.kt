package com.test.zomato.view.main.home.interfaces

import com.test.zomato.view.location.models.UserSavedAddress

interface SelectAddressClickListener {
    fun addressSelectedNowPlaceTheOrder(userSavedAddress: UserSavedAddress)
}