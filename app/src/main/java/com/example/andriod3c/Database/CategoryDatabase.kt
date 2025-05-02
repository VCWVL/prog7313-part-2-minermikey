package com.example.andriod3c.Databaseimport

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.andriod3c.CatogoryDAO.Category
import com.example.andriod3c.CatogoryDAO.CategoryDao

@Database(entities = [Category::class], version = 4, exportSchema = false)
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
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
                    .build()
                INSTANCE = instance
                instance
            }
        }

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE category ADD COLUMN budget REAL NOT NULL DEFAULT 0.0")
            }
        }
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE category ADD COLUMN min_budget REAL NOT NULL DEFAULT 0.0")
                database.execSQL("ALTER TABLE category ADD COLUMN max_budget REAL NOT NULL DEFAULT 0.0")
                database.execSQL("ALTER TABLE category DROP COLUMN budget")
            }
        }

        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE category ADD COLUMN username TEXT NOT NULL DEFAULT ''")
            }
        }
    }
}