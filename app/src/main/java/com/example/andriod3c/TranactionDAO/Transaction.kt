package com.example.andriod3c.TranactionDAO

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo

@Entity(tableName = "transactions")
data class Transaction (
    @PrimaryKey(autoGenerate = true) val id: Int,
    val label: String,
    val amount: Double,
    val description: String,
    val category: String,
    val date: Long,
    val startTime: String?,
    val endTime: String?,
    val type: String,
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    val image: ByteArray? ,
    val username: String

)
