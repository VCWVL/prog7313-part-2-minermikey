package com.example.andriod3c.Databaseimport

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.andriod3c.CatogoryDAO.Category
import com.example.andriod3c.CatogoryDAO.CategoryDao

@Database(entities = [Category::class], version = 4, exportSchema = false) // Increment version to 3
abstract class CategoryDatabase : RoomDatabase() {
    abstract fun category(): CategoryDao

    companion object {
        @Volatile
        private var INSTANCE: CategoryDatabase? = null

        fun getDatabase(context: Context): CategoryDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CategoryDatabase::class.java,
                    "category"
                )
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4) // Add the new migration
                    .build()
                INSTANCE = instance
                instance
            }
        }

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE category ADD COLUMN budget REAL NOT NULL DEFAULT 0.0") // Changed type to REAL
            }
        }
        val MIGRATION_2_3 = object : Migration(2, 3) {  // Define migration from version 2 to 3
            override fun migrate(database: SupportSQLiteDatabase) {
                // 1. Create the new column
                database.execSQL("ALTER TABLE category ADD COLUMN min_budget REAL NOT NULL DEFAULT 0.0")

                // 2. Copy the data from the old column to the new column
                database.execSQL("ALTER TABLE category ADD COLUMN max_budget REAL NOT NULL DEFAULT 0.0")

                //3. Remove the old column
                database.execSQL("ALTER TABLE category DROP COLUMN budget")
            }
        }

        // Migration from version 3 to 4 to add the username column
        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE category ADD COLUMN username TEXT NOT NULL DEFAULT ''") // Add NOT NULL and a default value
            }
        }
    }
}