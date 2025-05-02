package com.example.andriod3c.TranactionDAO

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface transactionsDAO {

    // will select alll the infomriaont from the table
    @Query("SELECT * FROM `transactions` ORDER BY date ASC ")
    fun getAll(): List<Transaction>

    // insert informaiton into the table
    @Insert
    fun insertAll(vararg transaction: Transaction)

    // delete ifnormaiton from the table
    @Delete
    fun delete (transaction: Transaction)

    // update the table
    @Update
    fun update(vararg transaction: Transaction )

    // gets informaiton for the graph
    @Query("SELECT * FROM transactions ORDER BY date ASC")
    fun getAllTransactions(): List<Transaction>

    @Query("SELECT * FROM transactions WHERE date BETWEEN :startOfDay AND :endOfDay")
    fun getDataByDate(startOfDay: Long, endOfDay: Long): Flow<List<Transaction>>



}