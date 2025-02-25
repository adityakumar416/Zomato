package com.test.zomato.view.location.interfaces

import com.test.zomato.view.location.models.UserSavedAddress

interface AddressMenuClickListener {
    fun menuClick(address: UserSavedAddress, action: String)
    fun addressClick(userSavedAddress: UserSavedAddress)
}
