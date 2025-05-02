package com.example.andriod3c.Budget

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.andriod3c.Adapters.CategoryTransactionAdapter
import com.example.andriod3c.Adapters.summarizeTransactionsByCategory
import com.example.andriod3c.Authentication.AuthenticationLogin
import com.example.andriod3c.Database.TransactionsDatabase
import com.example.andriod3c.MainActivity
import com.example.andriod3c.R
import com.example.andriod3c.TranactionDAO.Transaction
import com.example.andriod3c.TranactionDAO.transactionsDAO
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BudgetPage : AppCompatActivity() {

    private lateinit var transactions: List<Transaction>
    private lateinit var categoryTransactionAdapter: CategoryTransactionAdapter
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var db: TransactionsDatabase
    private lateinit var dao: transactionsDAO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_budget_page)

        db = Room.databaseBuilder(
            applicationContext,
            TransactionsDatabase::class.java,
            "transactions"
        )
            .fallbackToDestructiveMigration()
            .addMigrations(TransactionsDatabase.MIGRATION_1_2)
            .build()

        dao = db.transactionDao()

        transactions = listOf()
        val categorySummaries = summarizeTransactionsByCategory(transactions)
        categoryTransactionAdapter = CategoryTransactionAdapter(categorySummaries)
        linearLayoutManager = LinearLayoutManager(this)

        val recyclerView = findViewById<RecyclerView>(R.id.BudgetRecyclerView)
        recyclerView.adapter = categoryTransactionAdapter
        recyclerView.layoutManager = linearLayoutManager

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val categorySpinner: Spinner = findViewById(R.id.categorySpinner)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    true
                }
                R.id.nav_profile -> {
                    startActivity(Intent(this, BudgetPage::class.java))
                    true
                }
                R.id.nav_settings -> {
                    startActivity(Intent(this, AuthenticationLogin::class.java))
                    true
                }
                else -> false
            }
        }

        // Fetch data and update UI safely
        lifecycleScope.launch {
            val data = withContext(Dispatchers.IO) {
                dao.getAllTransactions()
            }

            transactions = data

            // Update RecyclerView
            val updatedSummaries = summarizeTransactionsByCategory(transactions)
            categoryTransactionAdapter.updateData(updatedSummaries)

            // Populate Spinner with "All Categories" option
            val categoryList = mutableListOf("All Categories")
            categoryList.addAll(transactions.map { it.category }.distinct())

            val adapter = ArrayAdapter(this@BudgetPage, android.R.layout.simple_spinner_item, categoryList)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            categorySpinner.adapter = adapter


            // Spinner selection listener
            categorySpinner.setOnItemSelectedListener(object : android.widget.AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: android.widget.AdapterView<*>, view: android.view.View, position: Int, id: Long) {
                    val selectedCategory = parent.getItemAtPosition(position).toString()

                    if (selectedCategory == "All Categories") {
                        // Show all transactions
                        updateRecyclerView(transactions)
                    } else {
                        // Show filtered
                        val filteredTransactions = filterTransactionsByCategory(transactions, selectedCategory)
                        updateRecyclerView(filteredTransactions)
                    }
                }

                override fun onNothingSelected(parent: android.widget.AdapterView<*>) {
                    // No action needed
                }
            })

        }
    }

    private fun filterTransactionsByCategory(transactions: List<Transaction>, category: String): List<Transaction> {
        return transactions.filter { it.category == category }
    }

    private fun updateRecyclerView(filteredTransactions: List<Transaction>) {
        val updatedSummaries = summarizeTransactionsByCategory(filteredTransactions)
        categoryTransactionAdapter.updateData(updatedSummaries)
    }

}
