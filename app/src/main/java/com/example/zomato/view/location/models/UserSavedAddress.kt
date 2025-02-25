package com.test.zomato.view.location.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_saved_address")
data class UserSavedAddress(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val receiverName: String,
    val receiverNumber: String,
    val currentUserNumber: String,
    val saveAddressAs: String, // Home, Work, etc.
    val selectedLocation: String,
    val houseAddress: String,
    val nearbyLandmark: String,
    val addressSelected: Boolean = false
)


