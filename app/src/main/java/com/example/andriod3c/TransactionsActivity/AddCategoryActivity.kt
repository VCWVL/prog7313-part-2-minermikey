package com.example.andriod3c.TransactionsActivity

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings // Import the Settings class
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.room.Room
import com.example.andriod3c.CatogoryDAO.Category
import com.example.andriod3c.Databaseimport.CategoryDatabase
import com.example.andriod3c.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddCategoryActivity : AppCompatActivity() {

    private lateinit var categoryNameInput: TextInputEditText
    private lateinit var categoryNameLayout: TextInputLayout
    private lateinit var categoryImageView: ImageView
    private lateinit var selectFromGalleryButton: Button
    private lateinit var saveCategoryButton: Button
    private lateinit var minBudgetAmountInput: EditText
    private lateinit var maxBudgetAmountInput: EditText
    private var imageBytes: ByteArray? = null
    private lateinit var galleryLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_category)

        categoryNameInput = findViewById(R.id.categoryNameInput)
        categoryNameLayout = findViewById(R.id.categoryNameLayout)
        categoryImageView = findViewById(R.id.categoryImageView)
        selectFromGalleryButton = findViewById(R.id.selectFromGalleryButton)
        saveCategoryButton = findViewById(R.id.saveCategoryButton)
        minBudgetAmountInput = findViewById(R.id.minBudgetAmountInput)
        maxBudgetAmountInput = findViewById(R.id.maxBudgetAmountInput)

        val sharedPreferences = getSharedPreferences("login_prefs", MODE_PRIVATE)
        val loggedInUsername = sharedPreferences.getString("userEmail", "") ?: ""


        galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                result.data?.data?.let { uri ->
                    val imageBitmap = uriToBitmap(uri)
                    categoryImageView.setImageBitmap(imageBitmap)
                    imageBytes = bitmapToByteArray(imageBitmap)
                }
            }
        }

        selectFromGalleryButton.setOnClickListener {
            dispatchGalleryIntent()
        }

        saveCategoryButton.setOnClickListener {
            val categoryName = categoryNameInput.text.toString().trim()
            val minBudgetText = minBudgetAmountInput.text.toString().trim()
            val maxBudgetText = maxBudgetAmountInput.text.toString().trim()

            if (categoryName.isEmpty()) {
                categoryNameLayout.error = "Category name cannot be empty"
                return@setOnClickListener
            }
            categoryNameLayout.error = null

            val minBudget = try {
                if (minBudgetText.isNotEmpty()) {
                    minBudgetText.toDouble()
                } else {
                    0.0
                }
            } catch (e: NumberFormatException) {
                Toast.makeText(this, "Invalid minimum budget amount", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val maxBudget = try {
                if (maxBudgetText.isNotEmpty()) {
                    maxBudgetText.toDouble()
                } else {
                    0.0
                }
            } catch (e: NumberFormatException) {
                Toast.makeText(this, "Invalid maximum budget amount", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (maxBudget < minBudget) {
                Toast.makeText(this, "Maximum budget cannot be less than minimum budget", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val category = Category(
                categoryName = categoryName,
                imageBytes = imageBytes,
                minBudget = minBudget,
                maxBudget = maxBudget,
                username =  loggedInUsername
            )
            insertCategory(category)
        }
    }

    private fun dispatchGalleryIntent() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryLauncher.launch(galleryIntent)
    }

    private fun uriToBitmap(uri: Uri): Bitmap? {
        return try {
            val inputStream = contentResolver.openInputStream(uri)
            BitmapFactory.decodeStream(inputStream)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    private fun bitmapToByteArray(bitmap: Bitmap?): ByteArray? {
        return bitmap?.let {
            val outputStream = ByteArrayOutputStream()
            it.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            outputStream.toByteArray()
        }
    }

    private fun insertCategory(category: Category) {
        val db = Room.databaseBuilder(
            applicationContext,
            CategoryDatabase::class.java,
            "category"
        ).build()

        CoroutineScope(Dispatchers.IO).launch {
            db.category().insert(category)
            db.close() // Close the database after operation
            withContext(Dispatchers.Main) {
                finish() // Return to the suspended activity (AddTransactionActivity)
            }
        }
    }
}