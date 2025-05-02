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
import com.example.andriod3c.Database.TransactionsDatabase // Unused import, consider removing
import com.example.andriod3c.Database.UserDatabase
import com.example.andriod3c.R
import com.example.andriod3c.UserDOA.DOAUserInformaiton
import com.example.andriod3c.UserDOA.UserInformation

/**
 * Activity for user registration. Allows users to create a new account by entering an email
 * and confirming their password.
 */
class AuthenticationRegister : AppCompatActivity() {

    private lateinit var db: UserDatabase
    private lateinit var userDao: DOAUserInformaiton

    /**
     * Called when the activity is first created. Initializes UI components, database connection,
     * and sets up listeners for button clicks to handle user registration and navigation to the login screen.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     * previously being shut down then this Bundle contains the data it most
     * recently supplied in {@link #onSaveInstanceState}. Otherwise it is null.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authentication_register)

        // Initialize the Room database for user information
        db = Room.databaseBuilder(
            applicationContext,
            UserDatabase::class.java,
            "UserInformation"
        )
            .build()
        // Get the Data Access Object for UserInformation
        userDao = db.userDao()

        // Initialize UI elements
        val regiserUsername = findViewById<EditText>(R.id.SignUpPageUserNameInputField)
        val firstpassword = findViewById<EditText>(R.id.SignUpPagePasswordInputField)
        val secondpassword = findViewById<EditText>(R.id.SignUpPagePasswordConfirmationInputField)
        val signUpButton = findViewById<Button>(R.id.SignUpPageSignUpButton)
        val loginText = findViewById<TextView>(R.id.SignUpPageLoginInnText)

        // Set OnClickListener for the login text to navigate to the login activity
        loginText.setOnClickListener {
            startActivity(Intent(this, AuthenticationLogin::class.java))
        }

        // Set OnClickListener for the sign-up button to handle user registration
        signUpButton.setOnClickListener {
            val email = regiserUsername.text.toString().trim()
            val password = firstpassword.text.toString().trim()
            val confirmPassword = secondpassword.text.toString().trim()

            // Check if any of the input fields are empty
            if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                // Inflate a custom toast layout
                val inflater = layoutInflater
                val layout = inflater.inflate(R.layout.custom_toast, null)

                // Set the text for the custom toast
                val text: TextView = layout.findViewById(R.id.toast_text)
                text.text = "Please fill in all fields"

                // Create and show the custom toast
                val toast = Toast(applicationContext)
                toast.duration = Toast.LENGTH_LONG
                toast.view = layout
                toast.setGravity(Gravity.BOTTOM, 0, 150) // Display at the bottom with an offset
                toast.show()
            }
            // Check if the entered passwords match
            if (password != confirmPassword) {
                // Inflate a custom toast layout
                val inflater = layoutInflater
                val layout = inflater.inflate(R.layout.custom_toast, null)

                // Set the text for the custom toast
                val text: TextView = layout.findViewById(R.id.toast_text)
                text.text = "Passwords do not match"

                // Create and show the custom toast
                val toast = Toast(applicationContext)
                toast.duration = Toast.LENGTH_LONG
                toast.view = layout
                toast.setGravity(Gravity.BOTTOM, 0, 150) // Display at the bottom with an offset
                toast.show()
            }

            // If all fields are filled and passwords match, create a new UserInformation object
            val newUser = UserInformation(0, email, password)
            // Insert the new user into the database in a background thread
            Thread {
                userDao.insertAll(newUser)
                // Switch back to the main thread to update UI after successful registration
                runOnUiThread {
                    // Display a success message
                    Toast.makeText(this, "User saved locally!", Toast.LENGTH_SHORT).show()
                    // Navigate to the login activity
                    startActivity(Intent(this, AuthenticationLogin::class.java))
                    finish() // Finish the registration activity so the user can't go back without logging in
                }
            }.start()
        }
    }
}
