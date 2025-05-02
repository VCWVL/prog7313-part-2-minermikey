package com.example.andriod3c.UserDOA

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface DOAUserInformaiton {
    @Insert
    fun insertAll(vararg users: UserInformation)

    @Query("SELECT * FROM UserInformation WHERE LoginUser = :username")
    fun findByName(username: String): UserInformation?
}
