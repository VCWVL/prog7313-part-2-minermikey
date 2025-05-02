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

class AuthenticationLogin : AppCompatActivity() {

    private lateinit var loginEmailInput: EditText
    private lateinit var loginPasswordInput: EditText
    private lateinit var loginButton: Button
    private lateinit var loginSignUpText: TextView
    private lateinit var loginProgressBar: ProgressBar

    private lateinit var db: UserDatabase
    private lateinit var userDao: DOAUserInformaiton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authentication_login)

        // Initialize Room DB with correct name
        db = Room.databaseBuilder(
            applicationContext,
            UserDatabase::class.java, "UserInformation" // ✅ match this name!
        ).build()

        userDao = db.userDao()

        // View binding
        loginEmailInput = findViewById(R.id.loginEmailInput)
        loginPasswordInput = findViewById(R.id.loginPasswordInput)
        loginButton = findViewById(R.id.loginButton)
        loginSignUpText = findViewById(R.id.loginSignUpText)
        loginProgressBar = findViewById(R.id.loginProgressBar)

        // Underline sign-up text
        val signUp = "Sign Up"
        val mSpannableString = SpannableString(signUp)
        mSpannableString.setSpan(UnderlineSpan(), 0, signUp.length, 0)
        loginSignUpText.text = mSpannableString

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 🔒 LOGIN BUTTON LOGIC
        loginButton.setOnClickListener {
            val username = loginEmailInput.text.toString().trim()
            val password = loginPasswordInput.text.toString().trim()

            // Check if either the username or password is empty
            if (username.isEmpty() || password.isEmpty()) {
                // Show a custom toast to inform the user
                val inflater = layoutInflater
                val layout = inflater.inflate(R.layout.custom_toast, null)

                val text: TextView = layout.findViewById(R.id.toast_text)
                text.text = "Please enter both username and password"

                val toast = Toast(applicationContext)
                toast.duration = Toast.LENGTH_LONG
                toast.view = layout
                toast.setGravity(Gravity.BOTTOM, 0, 150)
                toast.show()

            } else {
                // Proceed with login logic
                Thread {
                    val user = userDao.findByName(username)
                    runOnUiThread {
                        if (user != null && user.LoginPass == password) {
                            // Save login status and user email
                            val sharedPreferences = getSharedPreferences("login_prefs", Context.MODE_PRIVATE)
                            val editor = sharedPreferences.edit()
                            editor.putBoolean("isLoggedIn", true)
                            editor.putString("userEmail", username) // Save the email
                            editor.apply()

                            Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show()
                        }
                    }
                }.start()
            }
        }

        loginSignUpText.setOnClickListener {
            startActivity(Intent(this, AuthenticationRegister::class.java))
        }
    }
}
