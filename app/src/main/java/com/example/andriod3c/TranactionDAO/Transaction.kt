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
    val date: Long, // Store timestamp (should be non-null)
    val startTime: String?,  // Make these nullable
    val endTime: String?,    // Make these nullable
    val type: String, // "income" or "expense"
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)  // Add this annotation for the image
    val image: ByteArray? , // Use ByteArray? to store the image data
    val username: String // Add the username field

)
