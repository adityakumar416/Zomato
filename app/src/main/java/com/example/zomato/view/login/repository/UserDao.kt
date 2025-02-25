package com.test.zomato.view.login.repository

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.test.zomato.view.login.userData.User

@Dao
interface UserDao {

    @Insert
    suspend fun insert(user: User)

    @Update
    suspend fun update(user: User)

    @Query("SELECT * FROM user_data WHERE userNumber = :userNumber")
    suspend fun getUserByPhoneNumber(userNumber: String): User?

    @Query("UPDATE user_data SET imageUrl = :imageUrl WHERE id = :id")
    suspend fun changeProfilePhoto(imageUrl: String, id: Int)

}
