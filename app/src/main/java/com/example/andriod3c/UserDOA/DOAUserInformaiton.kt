package com.example.andriod3c.UserDOA

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
// this si the data access object for the transactiosn and will allow for the informaitno to be sotred
@Dao
interface DOAUserInformaiton {
    @Insert
    fun insertAll(vararg users: UserInformation)

    @Query("SELECT * FROM UserInformation WHERE LoginUser = :username")
    fun findByName(username: String): UserInformation?
}
