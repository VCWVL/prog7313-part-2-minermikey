package com.example.andriod3c.Authentication

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.Gravity
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.room.Room
import com.example.andriod3c.R
import com.example.andriod3c.UserDOA.DOAUserInformaiton
import com.example.andriod3c.Database.UserDatabase
import com.example.andriod3c.MainActivity

/**
 * Activity for user login authentication.
 */
class AuthenticationLogin : AppCompatActivity() {

    private lateinit var loginEmailInput: EditText
    private lateinit var loginPasswordInput: EditText
    private lateinit var loginButton: Button
    private lateinit var loginSignUpText: TextView
    private lateinit var loginProgressBar: ProgressBar

    private lateinit var db: UserDatabase
    private lateinit var userDao: DOAUserInformaiton

    /**
     * Called when the activity is first created. Initializes UI components, database connection,
     * sets up listeners for button clicks, and handles login logic.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     * previously being shut down then this Bundle contains the data it most
     * recently supplied in {@link #onSaveInstanceState}. Otherwise it is null.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authentication_login)

        // Initialize the Room database for user information
        db = Room.databaseBuilder(
            applicationContext,
            UserDatabase::class.java, "UserInformation"
        ).build()

        // Get the Data Access Object for UserInformation
        userDao = db.userDao()


        // Initialize UI elements
        loginEmailInput = findViewById(R.id.loginEmailInput)
        loginPasswordInput = findViewById(R.id.loginPasswordInput)
        loginButton = findViewById(R.id.loginButton)
        loginSignUpText = findViewById(R.id.loginSignUpText)
        loginProgressBar = findViewById(R.id.loginProgressBar)

        // Underline the "Sign Up" text to indicate it's clickable
        val signUp = "Sign Up"
        val mSpannableString = SpannableString(signUp)
        mSpannableString.setSpan(UnderlineSpan(), 0, signUp.length, 0)
        loginSignUpText.text = mSpannableString

        // Handle window insets to avoid overlapping with system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // SetOnClickListener for the login button
        loginButton.setOnClickListener {
            val username = loginEmailInput.text.toString().trim()
            val password = loginPasswordInput.text.toString().trim()

            // Check if username or password fields are empty
            if (username.isEmpty() || password.isEmpty()) {
                // Inflate a custom toast layout
                val inflater = layoutInflater
                val layout = inflater.inflate(R.layout.custom_toast, null)

                // Set the text for the custom toast
                val text: TextView = layout.findViewById(R.id.toast_text)
                text.text = "Please enter both username and password"

                // Create and show the custom toast
                val toast = Toast(applicationContext)
                toast.duration = Toast.LENGTH_LONG
                toast.view = layout
                toast.setGravity(Gravity.BOTTOM, 0, 150) // Display at the bottom with an offset
                toast.show()

            } else {
                // Perform login authentication in a background thread
                Thread {
                    // Find the user in the database by username
                    val user = userDao.findByName(username)
                    // Switch back to the main thread to update UI based on the authentication result
                    runOnUiThread {
                        // Check if the user exists and the entered password matches the stored password
                        if (user != null && user.LoginPass == password) {
                            // Store login status and user email in SharedPreferences
                            val sharedPreferences = getSharedPreferences("login_prefs", Context.MODE_PRIVATE)
                            val editor = sharedPreferences.edit()
                            editor.putBoolean("isLoggedIn", true)
                            editor.putString("userEmail", username)
                            editor.apply()

                            // Display a success message
                            Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show()
                            // Navigate to the main activity
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                            finish() // Finish the login activity so the user can't go back without logging out
                        } else {
                            // Display an error message for invalid credentials
                            Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show()
                        }
                    }
                }.start()
            }
        }

        // SetOnClickListener for the "Sign Up" text to navigate to the registration activity
        loginSignUpText.setOnClickListener {
            startActivity(Intent(this, AuthenticationRegister::class.java))
        }
    }
}