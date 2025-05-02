package com.example.andriod3c.UserDOA

import androidx.room.Entity
import androidx.room.PrimaryKey
// this entity will allow for user ifnormation to be stored 
@Entity
data class UserInformation(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val LoginUser: String,
    val LoginPass: String
)
