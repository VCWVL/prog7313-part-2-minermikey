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


    /*
        private lateinit var transactionDao: transactionsDAO
    */



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    /*    val recyclerViewss = findViewById<RecyclerView>(R.id.mainrecyclerview)
        recyclerViewss.layoutManager = linearLayoutManager
        recyclerViewss.adapter = transactionAdapter*/


        // Check if the user is logged in
        val sharedPreferences = getSharedPreferences("login_prefs", Context.MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)

        if (!isLoggedIn) {
            // If not logged in, redirect to the login activity
            startActivity(Intent(this, AuthenticationLogin::class.java))
            finish() // Prevent the user from going back to MainActivity without logging in
            return // Important: Exit onCreate() to prevent further initialization
        }

        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        val transactionAdding = findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.fab)
        /*val btnGoToRegister = findViewById<Button>(R.id.btnGoToRegister)
        val btnGoToLogin = findViewById<Button>(R.id.btnGoToLogin)
        val BudgetActivity = findViewById<Button>(R.id.BudgetActivity)

*/
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
            editor.clear() // Removes all stored data (isLoggedIn, userEmail, etc.)
            editor.apply()

            Toast.makeText(this, "Logged out successfully!", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, AuthenticationLogin::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish() // Prevent user from coming back via back button
        }




        transactionAdding.setOnClickListener {
            startActivity(Intent(this, AddTransactionActivity::class.java))
        }

       /* btnGoToRegister.setOnClickListener {
            startActivity(Intent(this, AuthenticationRegister::class.java))
        }

        btnGoToLogin.setOnClickListener {
            startActivity(Intent(this, AuthenticationLogin::class.java)) // You might want to use a login activity here instead
        }

        BudgetActivity.setOnClickListener {
            startActivity(Intent(this, BudgetPage::class.java))  // Start BudgetPage activity
        }*/

        transactions = listOf()
        transactionAdapter = TransactionAdapter(transactions)
        linearLayoutManager = LinearLayoutManager(this)

        val recyclerView = findViewById<RecyclerView>(R.id.mainrecyclerview)

        db = Room.databaseBuilder(
            this,
            TransactionsDatabase::class.java,
            "transactions"
        )   .fallbackToDestructiveMigration() // Keep this for development, remove for production
            .addMigrations(
                TransactionsDatabase.MIGRATION_1_2,
                TransactionsDatabase.MIGRATION_2_3,
                TransactionsDatabase.MIGRATION_3_4,
                TransactionsDatabase.MIGRATION_4_5,
                TransactionsDatabase.MIGRATION_5_6 // Add the new migration
            )
            .build()



        val dateButton: Button = findViewById(R.id.datePickerButton)  // Button for picking the date
        dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())



        // Set up RecyclerView and Adapter
        val recyclerViews: RecyclerView = findViewById(R.id.mainrecyclerview)
        transactionAdapter = TransactionAdapter(transactions)
        recyclerViews.adapter = transactionAdapter

        dateButton.setOnClickListener {
            showDatePickerDialog()
        }

       /* // teseting to see if the code picks up
        val recyclerViewsd = findViewById<RecyclerView>(R.id.mainrecyclerview) // assuming this is the ID of your RecyclerView
        recyclerViewsd.layoutManager = LinearLayoutManager(this)
        transactionAdapter = TransactionAdapter(emptyList())
        recyclerView.adapter = transactionAdapter

*/

        recyclerView.apply {
            adapter = transactionAdapter
            layoutManager = linearLayoutManager
        }

    }

    private fun fetchAll() {
        GlobalScope.launch {
            transactions = db.transactionDao().getAll() // ✅ NEW
            runOnUiThread {
                transactionAdapter.setData(transactions)
                updateDashboard()
               // loadChartData() // <-- move it here!
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

   /* private fun loadChartData() {
        CoroutineScope(Dispatchers.IO).launch {
            val lineChart = findViewById<LineChart>(R.id.lineChart)

            val incomeEntries = mutableListOf<Entry>()
            val expenseEntries = mutableListOf<Entry>()

            transactions.forEachIndexed { index, transaction ->
                val amount = transaction.amount.toFloat()

                if (transaction.type == "Income") {
                    incomeEntries.add(Entry(index.toFloat(), amount))
                } else if (transaction.type.equals("Expense")) {
                    expenseEntries.add(Entry(index.toFloat(), amount))
                }
            }
            withContext(Dispatchers.Main) {
                val incomeDataSet = LineDataSet(incomeEntries, "Income").apply {
                    color = Color.GREEN
                    valueTextColor = Color.BLACK
                    lineWidth = 2f
                    circleRadius = 4f
                }

                val expenseDataSet = LineDataSet(expenseEntries, "Expenses").apply {
                    color = Color.RED
                    valueTextColor = Color.BLACK
                    lineWidth = 2f
                    circleRadius = 4f
                }

                val lineData = LineData(incomeDataSet, expenseDataSet)
                lineChart.data = lineData
                lineChart.description.text = "Income vs Expenses"
                lineChart.animateX(1500)
                lineChart.invalidate()
            }
        }

    }*/
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
               PieEntry(totalIncome, "Income"),    // Green
               PieEntry(totalExpenses, "Expenses") // Red
           )

           withContext(Dispatchers.Main) {
               val dataSet = PieDataSet(entries, "Income").apply {
                   valueTextColor = Color.BLACK
                   valueTextSize = 16f
                   setColors(
                       Color.rgb(76, 175, 80), // Green for Income
                       Color.rgb(244, 67, 54)  // Red for Expenses

                   )
               }

               val data = PieData(dataSet)

               pieChart.data = data

               /*Color.rgb(76, 175, 80), // Green for Income
               Color.rgb(244, 67, 54)  // Red for Expenses*/

               pieChart.setUsePercentValues(true)
               pieChart.description.isEnabled = false
               pieChart.setEntryLabelColor(Color.BLACK)
               pieChart.animateY(1400, Easing.EaseInOutQuad)
               pieChart.invalidate()
           }
       }
   }

/*    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { view, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(selectedYear, selectedMonth, selectedDay)
                val selectedTimestamp = selectedDate.timeInMillis

                // Filter the transactions by the selected date
                val filteredTransactions = filterTransactionsByDate(selectedTimestamp)
                transactionAdapter.setData(filteredTransactions)  // Update the adapter with filtered data
            },
            year, month, day
        )
        datePickerDialog.show()
    }*/


   /* private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val dateRangePicker =
            MaterialDatePicker.Builder.dateRangePicker()
                .setTitleText("Select Date Range")
                .build()

        dateRangePicker.show(supportFragmentManager, "DATE_RANGE_PICKER")

        dateRangePicker.addOnPositiveButtonClickListener { selection ->
            val startDate = selection.first
            val endDate = selection.second
            // Do something with startDate and endDate
            Toast.makeText(this, "From: ${Date(startDate)}\nTo: ${Date(endDate)}", Toast.LENGTH_LONG).show()
        }

    }*/
   /*private fun showDatePickerDialog() {
       val calendar = Calendar.getInstance()
       val year = calendar.get(Calendar.YEAR)
       val month = calendar.get(Calendar.MONTH)
       val day = calendar.get(Calendar.DAY_OF_MONTH)

       // Initialize the Date Range Picker
       val dateRangePicker = MaterialDatePicker.Builder.dateRangePicker()
           .setTitleText("Select Date Range")
           .build()

       dateRangePicker.show(supportFragmentManager, "DATE_RANGE_PICKER")

       // Handling the selection of dates from the picker
       dateRangePicker.addOnPositiveButtonClickListener { selection ->
           val startDate = selection.first
           val endDate = selection.second
           // Handle the start and end dates
           Toast.makeText(this, "From: ${Date(startDate)}\nTo: ${Date(endDate)}", Toast.LENGTH_LONG).show()
       }
   }
*/





    /*private fun filterTransactionsByDate(selectedTimestamp: Long): List<Transaction> {
        return transactions.filter {
            val transactionDate = Date(it.date)  // Assuming date is stored as a timestamp
            val selectedDate = Date(selectedTimestamp)

            // Compare dates (ignoring time)
            val sdf = SimpleDateFormat("yyyy-MM-dd")
            sdf.format(transactionDate) == sdf.format(selectedDate)
        }
    }*/
   /* private fun filterTransactionsByDate(selectedTimestamp: Long): List<Transaction> {
        // Get the current date
        val currentTimestamp = System.currentTimeMillis()

        // Filter transactions between the selected date and the current date
        val filtered = transactions.filter {
            val transactionDate = Date(it.date)  // Assuming date is stored as a timestamp

            // Check if the transaction date is between selected date and current date
            transactionDate.time in selectedTimestamp..currentTimestamp
        }

        Log.d("FilteredTransactions", filtered.toString())  // Check the filtered list
        return filtered
    }*/

    // Start Date Picker
//    val startDateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, day ->
//        val calendar = Calendar.getInstance()
//        calendar.set(year, month, day)
//        startDateTimestamp = calendar.timeInMillis
//
//        // Show End Date Picker after selecting Start Date
//        showEndDatePicker()
//    }

 /*   fun showStartDatePicker() {
        val calendar = Calendar.getInstance()
        DatePickerDialog(this, startDateSetListener,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)).show()
    }
*/
    // End Date Picker
    /*val endDateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, day ->
        val calendar = Calendar.getInstance()
        calendar.set(year, month, day)
        endDateTimestamp = calendar.timeInMillis

        // Now filter the transactions
        val filtered = filterTransactionsBetweenDates(startDateTimestamp, endDateTimestamp)
        updateRecyclerView(filtered)
    }

    fun showEndDatePicker() {
        val calendar = Calendar.getInstance()
        DatePickerDialog(this, endDateSetListener,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)).show()
    }*/
    /*private fun filterTransactionsBetweenDates(start: Long, end: Long): List<Transaction> {
        return transactions.filter {
            val transactionTime = it.date  // assuming this is a Long (timestamp)
            transactionTime in start..end
        }
    }

    private fun updateRecyclerView(transactions: List<Transaction>) {
        transactionAdapter.setData(transactions)
    }*/
// Assuming you have a filter function already defined
 private fun filterTransactionsByDateRange(startDateTimestamp: Long, endDateTimestamp: Long): List<Transaction> {
     return transactions.filter {
         val transactionDate = Date(it.date) // Assuming transaction date is stored as a timestamp
         transactionDate.time in startDateTimestamp..endDateTimestamp
     }
 }

    // In your date range picker dialog:
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

            // Convert to milliseconds for easier comparison
            val startDateTimestamp = startDate
            val endDateTimestamp = endDate

            // Filter the transactions by the selected date range
            val filteredTransactions = filterTransactionsByDateRange(startDateTimestamp, endDateTimestamp)

            // Update the adapter with the filtered data
            transactionAdapter.setData(filteredTransactions)

            // Notify the adapter that the data has changed
            transactionAdapter.notifyDataSetChanged()

            // Show Toast with the selected dates
            Toast.makeText(this, "From: ${Date(startDateTimestamp)}\nTo: ${Date(endDateTimestamp)}", Toast.LENGTH_LONG).show()
        }
    }









}

