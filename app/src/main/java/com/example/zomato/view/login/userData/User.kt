package com.test.zomato.view.login.userData

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_data")
data class User(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    var imageUrl: String?,
    val username: String?,
    val userNumber: String?,
    val userEmail: String?,
    val userDOB: String?,
    val anniversaryDate: String?,
    val gender: String?
)
