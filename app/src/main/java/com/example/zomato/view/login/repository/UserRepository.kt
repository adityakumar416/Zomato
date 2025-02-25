package com.test.zomato.view.login.repository

import com.test.zomato.view.login.userData.User

class UserRepository(private val userDao: UserDao) {

    suspend fun saveUser(user: User) {
        // Insert the user only if the user does not already exist.
        if (user.userNumber?.let { userDao.getUserByPhoneNumber(it) } == null) {
            userDao.insert(user)
        }
    }

    suspend fun getUserByPhoneNumber(userNumber: String): User? {
        return userDao.getUserByPhoneNumber(userNumber)
    }

    suspend fun updateUserProfile(imageUrl: String,id:Int){
        return userDao.changeProfilePhoto(imageUrl,id)
    }

    suspend fun updateUser(user: User) {
        userDao.update(user)
    }
}

