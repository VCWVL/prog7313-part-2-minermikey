package com.example.andriod3c.CatogoryDAO

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "category")
data class Category(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    val imageBytes: ByteArray? = null,
    val categoryName: String,
    @ColumnInfo(name = "min_budget")
    val minBudget: Double = 0.0,
    @ColumnInfo(name = "max_budget")
    val maxBudget: Double = 0.0,
    val username: String
)