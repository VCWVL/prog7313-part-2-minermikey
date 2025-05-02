package com.example.andriod3c.UserDOA

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class UserInformation(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val LoginUser: String,
    val LoginPass: String
)