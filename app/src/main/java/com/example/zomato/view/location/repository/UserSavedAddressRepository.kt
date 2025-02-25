package com.test.zomato.view.location.repository

import com.test.zomato.view.location.models.UserSavedAddress

class UserSavedAddressRepository(private val userSavedAddressDao: UserSavedAddressDao) {

    suspend fun saveAddress(userSavedAddress: UserSavedAddress) {
        userSavedAddressDao.insertAddress(userSavedAddress)
    }

    suspend fun getAllAddresses(numberIs: String): List<UserSavedAddress> {
        return userSavedAddressDao.getAllAddresses(numberIs)
    }

    suspend fun deleteAddress(addressId: Int) {
        return userSavedAddressDao.deleteAddress(addressId)
    }

    suspend fun updateAddress(userSavedAddress: UserSavedAddress){
        return userSavedAddressDao.updateAddress(userSavedAddress)
    }

    suspend fun deleteAddressesWithUserNumber(currentUserNumber: String) {
        userSavedAddressDao.deleteAddressesWithUserNumber(currentUserNumber)
    }

    suspend fun saveMultipleAddresses(addresses: List<UserSavedAddress>) {
        userSavedAddressDao.insertMultipleAddresses(addresses)
    }

    suspend fun updateAllAddressesToNotSelected(userPhoneNumber: String) {
        // Update all addresses to set addressSelected = false for the given user
        userSavedAddressDao.updateAddressSelectedStatus(userPhoneNumber, false)
    }


}
