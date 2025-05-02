package com.example.andriod3c.Database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.andriod3c.TranactionDAO.Transaction
import com.example.andriod3c.TranactionDAO.transactionsDAO

@Database(entities = [Transaction::class], version = 6) // Increment the version number!
abstract class TransactionsDatabase : RoomDatabase() {

        // Change this name to match your call in BudgetPage
        abstract fun transactionDao(): transactionsDAO

        companion object {
                val MIGRATION_1_2 = object : Migration(1, 2) {
                        override fun migrate(database: SupportSQLiteDatabase) {
                                database.execSQL("ALTER TABLE `transactions` ADD COLUMN type TEXT NOT NULL DEFAULT 'Income'")
                        }
                }
                val MIGRATION_2_3 = object : Migration(2, 3) {
                        override fun migrate(database: SupportSQLiteDatabase) {
                                database.execSQL("ALTER TABLE `transactions` ADD COLUMN startTime TEXT")
                                database.execSQL("ALTER TABLE `transactions` ADD COLUMN endTime TEXT")
                        }
                }
                val MIGRATION_3_4 = object : Migration(3, 4) {
                        override fun migrate(database: SupportSQLiteDatabase) {
                                database.execSQL("ALTER TABLE `transactions` ADD COLUMN image BLOB")
                        }
                }
                val MIGRATION_4_5 = object : Migration(4, 5) {
                        override fun migrate(database: SupportSQLiteDatabase) {
                                database.execSQL("ALTER TABLE `transactions` ADD COLUMN image BLOB")
                        }
                }

                // Add the new migration for version 5 to 6
                val MIGRATION_5_6 = object : Migration(5, 6) {
                        override fun migrate(database: SupportSQLiteDatabase) {
                                database.execSQL("ALTER TABLE `transactions` ADD COLUMN username TEXT NOT NULL DEFAULT ''")
                        }
                }
        }
}