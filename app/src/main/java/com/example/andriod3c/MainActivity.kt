package com.example.andriod3c

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.red
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.andriod3c.Adapters.TransactionAdapter
import com.example.andriod3c.Authentication.AuthenticationLogin
import com.example.andriod3c.Authentication.AuthenticationRegister
import com.example.andriod3c.Budget.BudgetPage
import com.example.andriod3c.Database.TransactionsDatabase
import com.example.andriod3c.TranactionDAO.Transaction
import com.example.andriod3c.TranactionDAO.transactionsDAO
import com.example.andriod3c.TransactionsActivity.AddTransactionActivity
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var transactions: List<Transaction>
    private lateinit var transactionAdapter: TransactionAdapter
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var db: TransactionsDatabase
    private lateinit var dateFormat: SimpleDateFormat
    private var startDateTimestamp: Long = 0L
    private var endDateTimestamp: Long = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPreferences = getSharedPreferences("login_prefs", Context.MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)

        if (!isLoggedIn) {
            startActivity(Intent(this, AuthenticationLogin::class.java))
            finish()
            return
        }

        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        val transactionAdding = findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.fab)

        val logoutButton: Button = findViewById(R.id.logoutButton)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    true
                }

                R.id.nav_profile -> {
                    val intent = Intent(this, BudgetPage::class.java)
                    startActivity(intent)
                    true
                }

                R.id.nav_settings -> {
                    val intent = Intent(this, AuthenticationLogin::class.java)
                    startActivity(intent)
                    true
                }

                else -> false
            }
        }


        logoutButton.setOnClickListener {
            val sharedPreferences = getSharedPreferences("login_prefs", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.clear()
            editor.apply()

            Toast.makeText(this, "Logged out successfully!", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, AuthenticationLogin::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }




        transactionAdding.setOnClickListener {
            startActivity(Intent(this, AddTransactionActivity::class.java))
        }

        transactions = listOf()
        transactionAdapter = TransactionAdapter(transactions)
        linearLayoutManager = LinearLayoutManager(this)

        val recyclerView = findViewById<RecyclerView>(R.id.mainrecyclerview)

        db = Room.databaseBuilder(
            this,
            TransactionsDatabase::class.java,
            "transactions"
        )   .fallbackToDestructiveMigration()
            .addMigrations(
                TransactionsDatabase.MIGRATION_1_2,
                TransactionsDatabase.MIGRATION_2_3,
                TransactionsDatabase.MIGRATION_3_4,
                TransactionsDatabase.MIGRATION_4_5,
                TransactionsDatabase.MIGRATION_5_6
            )
            .build()



        val dateButton: Button = findViewById(R.id.datePickerButton)
        dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())



        val recyclerViews: RecyclerView = findViewById(R.id.mainrecyclerview)
        transactionAdapter = TransactionAdapter(transactions)
        recyclerViews.adapter = transactionAdapter

        dateButton.setOnClickListener {
            showDatePickerDialog()
        }

        recyclerView.apply {
            adapter = transactionAdapter
            layoutManager = linearLayoutManager
        }

    }

    private fun fetchAll() {
        GlobalScope.launch {
            transactions = db.transactionDao().getAll()
            runOnUiThread {
                transactionAdapter.setData(transactions)
                updateDashboard()
                loadPieChartData()
                Log.d("MainActivity", "Transactions fetched: $transactions")
            }
        }
    }



    private fun updateDashboard() {
        val budgetTextView = findViewById<TextView>(R.id.Budget)
        val expenseTextView = findViewById<TextView>(R.id.Expense)
        val balanceTextView = findViewById<TextView>(R.id.TotalBalanceTextView)

        val totalAmount = transactions.sumOf { it.amount }
        val budgetAmount = transactions.filter { it.amount > 0 }.sumOf { it.amount }
        val expenseAmount = totalAmount - budgetAmount

        balanceTextView.text = "$%.2f".format(totalAmount)
        budgetTextView.text = "$%.2f".format(budgetAmount)
        expenseTextView.text = "$%.2f".format(expenseAmount)
    }

    override fun onResume() {
        super.onResume()
        fetchAll()
    }


   private fun loadPieChartData() {
       CoroutineScope(Dispatchers.IO).launch {
           val pieChart = findViewById<PieChart>(R.id.pieChart)

           var totalIncome = 0f
           var totalExpenses = 0f

           transactions.forEach { transaction ->
               val amount = transaction.amount.toFloat()

               if (transaction.type.equals("Income", ignoreCase = true)) {
                   totalIncome += amount
               } else if (transaction.type.equals("Expense", ignoreCase = true)) {
                   totalExpenses -= amount
               }
           }

           val entries = listOf(
               PieEntry(totalIncome, "Income"),
               PieEntry(totalExpenses, "Expenses")
           )

           withContext(Dispatchers.Main) {
               val dataSet = PieDataSet(entries, "Income").apply {
                   valueTextColor = Color.BLACK
                   valueTextSize = 16f
                   setColors(
                       Color.rgb(76, 175, 80),
                       Color.rgb(244, 67, 54)

                   )
               }

               val data = PieData(dataSet)

               pieChart.data = data

               pieChart.setUsePercentValues(true)
               pieChart.description.isEnabled = false
               pieChart.setEntryLabelColor(Color.BLACK)
               pieChart.animateY(1400, Easing.EaseInOutQuad)
               pieChart.invalidate()
           }
       }
   }

 private fun filterTransactionsByDateRange(startDateTimestamp: Long, endDateTimestamp: Long): List<Transaction> {
     return transactions.filter {
         val transactionDate = Date(it.date)
         transactionDate.time in startDateTimestamp..endDateTimestamp
     }
 }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val dateRangePicker = MaterialDatePicker.Builder.dateRangePicker()
            .setTitleText("Select Date Range")
            .build()

        dateRangePicker.show(supportFragmentManager, "DATE_RANGE_PICKER")

        dateRangePicker.addOnPositiveButtonClickListener { selection ->
            val startDate = selection.first
            val endDate = selection.second

            val startDateTimestamp = startDate
            val endDateTimestamp = endDate

            val filteredTransactions = filterTransactionsByDateRange(startDateTimestamp, endDateTimestamp)

            transactionAdapter.setData(filteredTransactions)

            transactionAdapter.notifyDataSetChanged()

            Toast.makeText(this, "From: ${Date(startDateTimestamp)}\nTo: ${Date(endDateTimestamp)}", Toast.LENGTH_LONG).show()
        }
    }
}

