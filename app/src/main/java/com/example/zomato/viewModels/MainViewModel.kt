package com.test.zomato.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.test.zomato.showNearByLocations.GeoApifyRepository
import com.test.zomato.utils.RoomDatabaseHelper
import com.test.zomato.view.location.models.PlaceFeature
import com.test.zomato.view.location.models.UserSavedAddress
import com.test.zomato.view.location.repository.UserSavedAddressRepository
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val userSavedAddressRepository by lazy {
        UserSavedAddressRepository(RoomDatabaseHelper.getInstance(application)!!.userSavedAddressDao())
    }

    private val setCount = MutableLiveData<Int>(1)
    val count: LiveData<Int> = setCount

    fun plus() {
        setCount.value = (setCount.value ?: 0) + 1
    }

    fun minus() {
        val currentValue = setCount.value ?: 0
        if (currentValue >= 1) {
            setCount.value = currentValue - 1
        }
    }



    private val setLocationList = MutableLiveData<List<PlaceFeature>>()
    val nearLocationList: LiveData<List<PlaceFeature>> = setLocationList

    private val geoApifyRepository by lazy { GeoApifyRepository() }

    fun getNearByLocation(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            val list = geoApifyRepository.fetchNearbyLocation(latitude, longitude)
            setLocationList.postValue(list)
        }
    }

    private val _addresses = MutableLiveData<List<UserSavedAddress>>()
    val addresses: LiveData<List<UserSavedAddress>> = _addresses

    // Function to fetch all saved addresses
    fun getAllAddresses(numberIs: String) {
        viewModelScope.launch {
            val addressesList = userSavedAddressRepository.getAllAddresses(numberIs)
            _addresses.postValue(addressesList)
        }
    }

    // Function to save user address
    fun saveAddress(
        receiverName: String,
        receiverNumber: String,
        currentUserNumber: String,
        saveAddressAs: String,
        selectedLocation: String,
        houseAddress: String,
        nearbyLandmark: String
    ) {
        viewModelScope.launch {

            // First, set all other addresses to addressSelected = false
            userSavedAddressRepository.updateAllAddressesToNotSelected(currentUserNumber)

            val address = UserSavedAddress(
                receiverName = receiverName,
                receiverNumber = receiverNumber,
                currentUserNumber = currentUserNumber,
                saveAddressAs = saveAddressAs,
                selectedLocation = selectedLocation,
                houseAddress = houseAddress,
                nearbyLandmark = nearbyLandmark,
                addressSelected = true
            )
            userSavedAddressRepository.saveAddress(address)
        }
    }


    fun updateAddress(
        addressId: Int,
        receiverName: String,
        receiverNumber: String,
        currentUserNumber: String,
        saveAddressAs: String,
        selectedLocation: String,
        houseAddress: String,
        nearbyLandmark: String
    ) {
        viewModelScope.launch {
            val address = UserSavedAddress(
                id = addressId,
                receiverName = receiverName,
                receiverNumber = receiverNumber,
                currentUserNumber = currentUserNumber,
                saveAddressAs = saveAddressAs,
                selectedLocation = selectedLocation,
                houseAddress = houseAddress,
                nearbyLandmark = nearbyLandmark,
                addressSelected = false
            )
            userSavedAddressRepository.updateAddress(address)
        }
    }



    fun deleteAddress(addressId: Int) {
        viewModelScope.launch {
            userSavedAddressRepository.deleteAddress(addressId)
        }
    }


    fun saveAddressesForUser(addresses: List<UserSavedAddress>) {
        viewModelScope.launch {
            userSavedAddressRepository.deleteAddressesWithUserNumber("")
            userSavedAddressRepository.saveMultipleAddresses(addresses)
        }
    }




}
