package com.example.andriod3c.Authentication

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.example.andriod3c.Database.TransactionsDatabase
import com.example.andriod3c.Database.UserDatabase
import com.example.andriod3c.R
import com.example.andriod3c.UserDOA.DOAUserInformaiton
import com.example.andriod3c.UserDOA.UserInformation

class AuthenticationRegister : AppCompatActivity() {

    private lateinit var db: UserDatabase
    private lateinit var userDao: DOAUserInformaiton
// This entire class is for registering a user
        // its takes information and saves it to Room DB
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authentication_register) // ✅ This matches your provided layout

        // Initialize Room database
        db = Room.databaseBuilder(
            applicationContext,
            UserDatabase::class.java,
            "UserInformation"
        )
            .build()
        userDao = db.userDao()

        // Match IDs with XML
        val regiserUsername = findViewById<EditText>(R.id.SignUpPageUserNameInputField)
        val firstpassword = findViewById<EditText>(R.id.SignUpPagePasswordInputField)
        val secondpassword = findViewById<EditText>(R.id.SignUpPagePasswordConfirmationInputField)
        val signUpButton = findViewById<Button>(R.id.SignUpPageSignUpButton)
        val loginText = findViewById<TextView>(R.id.SignUpPageLoginInnText)

        // Go to login page
        loginText.setOnClickListener {
            startActivity(Intent(this, AuthenticationLogin::class.java))
        }

        // Sign up click logic
        signUpButton.setOnClickListener {
            val email = regiserUsername.text.toString().trim()
            val password = firstpassword.text.toString().trim()
            val confirmPassword = secondpassword.text.toString().trim()
// checks for null fields
            if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {

                val inflater = layoutInflater
                val layout = inflater.inflate(R.layout.custom_toast, null)

                val text: TextView = layout.findViewById(R.id.toast_text)
                text.text = "Please fill in all fields"

                val toast = Toast(applicationContext)
                toast.duration = Toast.LENGTH_LONG
                toast.view = layout
                toast.setGravity(Gravity.BOTTOM, 0, 150)
                toast.show()
            }
// checks to ensure all fields are the same
            if (password != confirmPassword) {

                val inflater = layoutInflater
                val layout = inflater.inflate(R.layout.custom_toast, null)

                val text: TextView = layout.findViewById(R.id.toast_text)
                text.text = "Passwords do not match"

                val toast = Toast(applicationContext)
                toast.duration = Toast.LENGTH_LONG
                toast.view = layout
                toast.setGravity(Gravity.BOTTOM, 0, 150)
                toast.show()
            }

            val newUser = UserInformation(0, email, password)
            Thread {
                userDao.insertAll(newUser)
                runOnUiThread {
                    Toast.makeText(this, "User saved locally!", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, AuthenticationLogin::class.java))
                    finish()
                }
            }.start()
        }
    }
}
