package com.example.andriod3c.TranactionDAO

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface transactionsDAO {

    @Query("SELECT * FROM `transactions` ORDER BY date ASC ")
    fun getAll(): List<Transaction>

    @Insert
    fun insertAll(vararg transaction: Transaction)

    @Delete
    fun delete (transaction: Transaction)

    @Update
    fun update(vararg transaction: Transaction )

    @Query("SELECT * FROM transactions ORDER BY date ASC")
    fun getAllTransactions(): List<Transaction>

    @Query("SELECT * FROM transactions WHERE date BETWEEN :startOfDay AND :endOfDay")
    fun getDataByDate(startOfDay: Long, endOfDay: Long): Flow<List<Transaction>>



}