package com.test.zomato.view.login.repository

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.test.zomato.utils.RoomDatabaseHelper
import com.test.zomato.view.login.userData.User
import kotlinx.coroutines.launch

class UserViewModel(application: Application) : AndroidViewModel(application) {

    private val userRepository: UserRepository by lazy {
        UserRepository(RoomDatabaseHelper.getInstance(application)!!.userDao())
    }

    private val _userLiveData = MutableLiveData<User?>()
    val userLiveData: LiveData<User?> = _userLiveData


    fun getUserByPhoneNumber(userNumber: String) {
        viewModelScope.launch {
            val user = userRepository.getUserByPhoneNumber(userNumber)
            _userLiveData.postValue(user)
        }
    }


    fun saveUser(user: User){
        viewModelScope.launch {
            userRepository.saveUser(user)
        }
    }

    fun updateUser(user: User){
        viewModelScope.launch {
            userRepository.updateUser(user)
        }
    }


    fun updateUserProfile(imageUrl: String, id:Int){
        viewModelScope.launch {
            userRepository.updateUserProfile(imageUrl,id)
        }
    }

}
