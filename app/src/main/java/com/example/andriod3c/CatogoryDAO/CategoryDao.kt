package com.example.andriod3c.CatogoryDAO

import androidx.room.*

@Dao
interface CategoryDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(category: Category): Long
    @Update
    suspend fun update(category: Category)
    @Query("SELECT * FROM category WHERE id = :categoryId")
    suspend fun getCategoryById(categoryId: Int): Category?
    @Query("SELECT * FROM category ORDER BY categoryName ASC")
    suspend fun getAllCategories(): List<Category>
    @Delete
    suspend fun delete(category: Category)
    @Query("SELECT COUNT(*) FROM category")
    suspend fun getCategoryCount(): Int
    @Query("SELECT * FROM category WHERE username = :username ORDER BY categoryName ASC")
    suspend fun getCategoriesByUsername(username: String): List<Category>
}