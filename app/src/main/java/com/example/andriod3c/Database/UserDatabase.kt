package com.example.andriod3c.Database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.andriod3c.UserDOA.DOAUserInformaiton
import com.example.andriod3c.UserDOA.UserInformation

@Database(entities = [UserInformation::class], version = 1)
abstract class UserDatabase : RoomDatabase() {
    abstract fun userDao(): DOAUserInformaiton
}