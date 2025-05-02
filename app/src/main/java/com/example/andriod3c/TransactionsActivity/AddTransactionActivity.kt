package com.example.andriod3c.TransactionsActivity

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.Gravity
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import android.widget.ToggleButton
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider // Import FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import androidx.room.Room
import com.example.andriod3c.Budget.BudgetPage
import com.example.andriod3c.Database.TransactionsDatabase
import com.example.andriod3c.R
import com.example.andriod3c.TranactionDAO.Transaction
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.util.Locale
import java.io.Serializable //Import Serializable

class AddTransactionActivity : AppCompatActivity(), CategoryDialogFragment.CategorySelectionListener {

    private lateinit var labelInput: TextInputEditText
    private lateinit var amountInput: TextInputEditText
    private lateinit var labelLayout: TextInputLayout
    private lateinit var amountLayout: TextInputLayout
    private lateinit var descriptionInput: TextInputEditText
    private lateinit var descriptionLayout: TextInputLayout
    private lateinit var db: TransactionsDatabase
    private var currentInput = ""
    private var operand1: Double? = null
    private var operator: String? = null
    private lateinit var categoryInput: TextInputEditText
    private lateinit var categoryLayout: TextInputLayout
    private lateinit var type: String
    private lateinit var dateInput: TextInputEditText
    private lateinit var dateLayout: TextInputLayout
    private lateinit var startTimeInput: TextInputEditText
    private lateinit var startTimeLayout: TextInputLayout
    private lateinit var endTimeInput: TextInputEditText
    private lateinit var endTimeLayout: TextInputLayout
    private var selectedDate: Long? = null
    private var selectedStartTime: String? = null
    private var selectedEndTime: String? = null
    private val calendar = Calendar.getInstance()
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    // Image handling
    private lateinit var addImageButton: Button
    private lateinit var imageView: ImageView
    private var imageByteArray: ByteArray? = null
    private lateinit var cameraLauncher: ActivityResultLauncher<Intent>
    private lateinit var galleryLauncher: ActivityResultLauncher<Intent>
    private var currentPhotoPath: String? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_transaction)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val transactionButton = findViewById<Button>(R.id.AddTransactionButton)
        labelInput = findViewById(R.id.labelInput)
        amountInput = findViewById(R.id.amountInput)
        labelLayout = findViewById(R.id.labelLayout)
        amountLayout = findViewById(R.id.amountLayout)
        descriptionInput = findViewById(R.id.descriptionInput)
        descriptionLayout = findViewById(R.id.descriptionLayout)
        val closeButton = findViewById<ImageButton>(R.id.closeBtn)
        categoryInput = findViewById(R.id.categoryInput)
        categoryLayout = findViewById(R.id.categoryLayout)
        val IncomeExpenseToggleButton = findViewById<ToggleButton>(R.id.IncomeExpenseButton)
        dateInput = findViewById(R.id.dateInput)
        dateLayout = findViewById(R.id.dateLayout)
        startTimeInput = findViewById(R.id.startTimeInput)
        startTimeLayout = findViewById(R.id.startTimeLayout)
        endTimeInput = findViewById(R.id.endTimeInput)
        endTimeLayout = findViewById(R.id.endTimeLayout)
        addImageButton = findViewById(R.id.addImageButton)  // Initialize addImageButton
        imageView = findViewById(R.id.imageView) // Initialize imageView

        // Calculator Buttons
        val button0 = findViewById<Button>(R.id.button0)
        val button1 = findViewById<Button>(R.id.button1)
        val button2 = findViewById<Button>(R.id.button2)
        val button3 = findViewById<Button>(R.id.button3)
        val button4 = findViewById<Button>(R.id.button4)
        val button5 = findViewById<Button>(R.id.button5)
        val button6 = findViewById<Button>(R.id.button6)
        val button7 = findViewById<Button>(R.id.button7)
        val button8 = findViewById<Button>(R.id.button8)
        val button9 = findViewById<Button>(R.id.button9)
        val buttonDecimal = findViewById<Button>(R.id.buttonDecimal)
        val buttonClear = findViewById<Button>(R.id.buttonClear)
        val buttonAdd = findViewById<Button>(R.id.buttonAdd)
        val buttonSubtract = findViewById<Button>(R.id.buttonSubtract)
        val buttonMultiply = findViewById<Button>(R.id.buttonMultiply)
        val buttonDivide = findViewById<Button>(R.id.buttonDivide)
        val buttonEquals = findViewById<Button>(R.id.buttonEquls)

        val sharedPreferences = getSharedPreferences("login_prefs", MODE_PRIVATE)
        val loggedInUsername = sharedPreferences.getString("userEmail", "") ?: ""

        // Set up listeners for calculator buttons
        button0.setOnClickListener { appendDigit("0") }
        button1.setOnClickListener { appendDigit("1") }
        button2.setOnClickListener { appendDigit("2") }
        button3.setOnClickListener { appendDigit("3") }
        button4.setOnClickListener { appendDigit("4") }
        button5.setOnClickListener { appendDigit("5") }
        button6.setOnClickListener { appendDigit("6") }
        button7.setOnClickListener { appendDigit("7") }
        button8.setOnClickListener { appendDigit("8") }
        button9.setOnClickListener { appendDigit("9") }
        buttonDecimal.setOnClickListener { appendDecimal() }
        buttonClear.setOnClickListener { clearInput() }
        buttonAdd.setOnClickListener { performOperation("+") }
        buttonSubtract.setOnClickListener { performOperation("-") }
        buttonMultiply.setOnClickListener { performOperation("*") }
        buttonDivide.setOnClickListener { performOperation("/") }
        buttonEquals.setOnClickListener { calculateResult() }

        labelInput.addTextChangedListener {
            it?.let {
                if (it.isNotEmpty()) labelLayout.error = null
            }
        }
        amountInput.addTextChangedListener {
            it?.let {
                if (it.isNotEmpty()) amountLayout.error = null
            }
        }
        descriptionInput.addTextChangedListener {
            it?.let {
                if (it.isNotEmpty()) descriptionLayout.error = null
            }
        }

        IncomeExpenseToggleButton.setOnClickListener {
            if (IncomeExpenseToggleButton.isChecked) {
                IncomeExpenseToggleButton.text = "Expense"
                IncomeExpenseToggleButton.setTextColor(getResources().getColor(R.color.red))
                type = "Expense"
            } else {
                IncomeExpenseToggleButton.text = "Income"
                IncomeExpenseToggleButton.setTextColor(getResources().getColor(R.color.green))
                type = "Income"
            }
        }

        categoryInput.isFocusable = false // Prevent manual typing
        categoryInput.isClickable = true
        categoryInput.setOnClickListener {
            showCategoryDialogFragment()
        }

        dateInput.isFocusable = false
        dateInput.isClickable = true
        dateInput.setOnClickListener {
            showDatePickerDialog()
        }

        startTimeInput.isFocusable = false
        startTimeInput.isClickable = true
        startTimeInput.setOnClickListener {
            showTimePickerDialog(true)
        }

        endTimeInput.isFocusable = false
        endTimeInput.isClickable = true
        endTimeInput.setOnClickListener {
            showTimePickerDialog(false)
        }

        galleryLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    // Handle the image selected from the gallery
                    val selectedImageUri = result.data?.data
                    if (selectedImageUri != null) {
                        try {
                            val imageBitmap =
                                MediaStore.Images.Media.getBitmap(contentResolver, selectedImageUri)
                            processImage(imageBitmap)
                        } catch (e: IOException) {
                            e.printStackTrace()
                            Toast.makeText(
                                this,
                                "Error: Could not retrieve image from gallery.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }

        // Set click listener for the add image button
        addImageButton.setOnClickListener {
            showImagePickerDialog()
        }

        transactionButton.setOnClickListener {
            val labelText = labelInput.text.toString()
            val amountText = amountInput.text.toString()
            val description = descriptionInput.text.toString()
            val categoryText = categoryInput.text.toString()
            val type = type
            val date = selectedDate
            val startTime = selectedStartTime
            val endTime = selectedEndTime

            var hasErrors = false

            if (labelText.isEmpty()) {
                labelLayout.error = "Please enter a valid label"
                hasErrors = true
            } else {
                labelLayout.error = null
            }

            if (categoryText.isEmpty()) {
                categoryLayout.error = "Please select a category"
                hasErrors = true
            } else {
                categoryLayout.error = null
            }

            if (date == null) {
                dateLayout.error = "Please select a date"
                hasErrors = true
            } else {
                dateLayout.error = null
            }

            if (startTime == null) {
                startTimeLayout.error = "Please select start time"
                hasErrors = true
            } else {
                startTimeLayout.error = null
            }

            if (endTime == null) {
                endTimeLayout.error = "Please select end time"
                hasErrors = true
            } else {
                endTimeLayout.error = null
            }

            val amount = try {
                val parsed = amountText.toDouble()
                if (parsed <= 0) {
                    amountLayout.error = "Please enter a valid amount"
                    hasErrors = true
                } else {
                    amountLayout.error = null
                }
                parsed
            } catch (e: NumberFormatException) {
                amountLayout.error = "Amount must be a number"
                hasErrors = true
                0.0
            }

            if (description.isEmpty()) {
                descriptionLayout.error = "Please enter a description"
                hasErrors = true
            } else {
                descriptionLayout.error = null
            }

            if (type != "Income" && type != "Expense") {
                hasErrors = true
                Toast.makeText(this, "Please choose 'Income' or 'Expense'", Toast.LENGTH_SHORT).show()
            }

            if (!hasErrors) {
                val finalAmount = if (type == "Expense") -amount else amount

                val transaction = Transaction(
                    0,
                    labelText,
                    finalAmount,
                    description,
                    categoryText,
                    date!!,
                    startTime,
                    endTime,
                    type,
                    imageByteArray,
                    username = loggedInUsername
                )
                insert(transaction)
            } else {
                val inflater = layoutInflater
                val layout = inflater.inflate(R.layout.custom_toast, null)

                val text: TextView = layout.findViewById(R.id.toast_text)
                text.text = "Please correct the errors above"

                val toast = Toast(applicationContext)
                toast.duration = Toast.LENGTH_LONG
                toast.view = layout
                toast.setGravity(Gravity.BOTTOM, 0, 150)
                toast.show()
            }
        }


        closeButton.setOnClickListener {
            finish()
        }

        // Initially set the amount input to be non-editable to avoid direct typing
        amountInput.isFocusable = false
        amountInput.isClickable = true
        type = if (IncomeExpenseToggleButton.isChecked) "Expense" else "Income"
    }

    private fun showCategoryDialogFragment() {
        val categoryDialogFragment = CategoryDialogFragment(this)
        categoryDialogFragment.show(supportFragmentManager, "CategoryDialogFragment")
    }

    override fun onCategorySelected(categoryName: String) {
        categoryInput.setText(categoryName)
        categoryLayout.error = null // Clear error when a category is selected
    }

    private fun appendDigit(digit: String) {
        currentInput += digit
        amountInput.setText(currentInput)
    }

    private fun appendDecimal() {
        if (!currentInput.contains(".")) {
            currentInput += "."
            amountInput.setText(currentInput)
        }
    }

    private fun performOperation(op: String) {
        if (currentInput.isNotEmpty()) {
            operand1 = currentInput.toDoubleOrNull()
            if (operand1 != null) {
                operator = op
                currentInput = ""
            }
        }
    }

    private fun calculateResult() {
        if (operand1 != null && operator != null && currentInput.isNotEmpty()) {
            val operand2 = currentInput.toDoubleOrNull()
            if (operand2 != null) {
                val result = when (operator) {
                    "+" -> operand1!! + operand2
                    "-" -> operand1!! - operand2
                    "*" -> operand1!! * operand2
                    "/" -> if (operand2 != 0.0) operand1!! / operand2 else "Error"
                    else -> ""
                }
                currentInput =
                    if (result is Double) String.format("%.2f", result) else result.toString()
                amountInput.setText(currentInput)
                operand1 = null
                operator = null
            }
        }
    }

    private fun clearInput() {
        currentInput = ""
        operand1 = null
        operator = null
        amountInput.setText("")
    }

    private fun showDatePickerDialog() {
        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, day ->
            calendar.set(year, month, day)
            selectedDate = calendar.timeInMillis
            dateInput.setText(dateFormat.format(calendar.time))
            dateLayout.error = null
        }

        DatePickerDialog(
            this,
            dateSetListener,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun showTimePickerDialog(isStartTime: Boolean) {
        val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
            calendar.set(Calendar.MINUTE, minute)
            val formattedTime = timeFormat.format(calendar.time)
            if (isStartTime) {
                selectedStartTime = formattedTime
                startTimeInput.setText(formattedTime)
                startTimeLayout.error = null
            } else {
                selectedEndTime = formattedTime
                endTimeInput.setText(formattedTime)
                endTimeLayout.error = null
            }
        }

        TimePickerDialog(
            this,
            timeSetListener,
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true // Use 24-hour format
        ).show()
    }

    private fun insert(transaction: Transaction) {
        db = Room.databaseBuilder(
            applicationContext, // Use applicationContext
            TransactionsDatabase::class.java,
            "transactions"
        )
            .fallbackToDestructiveMigration() // Keep this for development, remove for production
            .addMigrations(
                TransactionsDatabase.MIGRATION_1_2,
                TransactionsDatabase.MIGRATION_2_3,
                TransactionsDatabase.MIGRATION_3_4,
                TransactionsDatabase.MIGRATION_4_5,
                TransactionsDatabase.MIGRATION_5_6 // Add the new migration
            )
            .build()
        CoroutineScope(Dispatchers.IO).launch { // Use CoroutineScope
            db.transactionDao().insertAll(transaction)
            withContext(Dispatchers.Main) {
                finish()
            }
        }
    }

    private fun showImagePickerDialog() {
        val options = arrayOf("Choose from Gallery")
        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("Add Photo")
        builder.setItems(options) { dialog, which ->
            when (which) {
                0 -> openGallery()     // Choose from Gallery
            }
        }
        builder.show()
    }


    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryLauncher.launch(intent)
    }

    private fun processImage(bitmap: Bitmap) {
        imageView.setImageBitmap(bitmap)  // Display the image in the ImageView

        // Convert the Bitmap to a ByteArray
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream) // Adjust quality as needed (80 here)
        imageByteArray = stream.toByteArray()
    }

}


