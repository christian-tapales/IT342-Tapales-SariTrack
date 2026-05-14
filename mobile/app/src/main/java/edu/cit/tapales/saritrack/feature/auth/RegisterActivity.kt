package edu.cit.tapales.saritrack.feature.auth

import edu.cit.tapales.saritrack.feature.auth.*
import edu.cit.tapales.saritrack.feature.transaction.*
import edu.cit.tapales.saritrack.feature.customer.*
import edu.cit.tapales.saritrack.core.auth.*
import edu.cit.tapales.saritrack.R
import edu.cit.tapales.saritrack.feature.pos.*
import edu.cit.tapales.saritrack.feature.dashboard.*
import edu.cit.tapales.saritrack.feature.payment.*
import edu.cit.tapales.saritrack.core.ui.*
import edu.cit.tapales.saritrack.feature.inventory.*
import edu.cit.tapales.saritrack.core.api.*

import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import android.content.Intent
import android.net.Uri
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 🌓 Apply Saved Theme
        val sessionManager = SessionManager(this)
        val targetMode = if (sessionManager.isDarkMode()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        setContentView(R.layout.activity_register)

        val rootView = findViewById<android.view.View>(R.id.main)
        if (rootView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(rootView) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }
        }


        // --- VIEWS ---
        val btnRegister = findViewById<Button>(R.id.btnRegister)
        btnRegister.isEnabled = false
        val etName = findViewById<EditText>(R.id.etName)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val etConfirmPassword = findViewById<EditText>(R.id.etConfirmPassword) // Added this
        val tvToLogin = findViewById<TextView>(R.id.tvToLogin)
        val cbTerms = findViewById<CheckBox>(R.id.cbTerms)

        val tvTermsLink = findViewById<TextView>(R.id.tvTermsLink)

        tvTermsLink.setOnClickListener {
            showTermsBottomSheet()
        }
        // The Checkbox Listener: This is where you put the cbTerms logic
        cbTerms.setOnCheckedChangeListener { _, isChecked ->
            // Enable the button only if the checkbox is ticked
            btnRegister.isEnabled = isChecked
        }

        tvToLogin.setOnClickListener {
            finish()
        }

        btnRegister.setOnClickListener {

            val name = etName.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val confirmPassword = etConfirmPassword.text.toString().trim() // Get confirm password

            // 1. Validate using Utils
            if (!ValidationUtils.isValidName(name)) {
                etName.error = "Name must be at least 2 characters"
                etName.requestFocus()
                return@setOnClickListener
            }

            if (!ValidationUtils.isValidEmail(email)) {
                etEmail.error = "Invalid email format"
                etEmail.requestFocus()
                return@setOnClickListener
            }

            if (!ValidationUtils.isStrongPassword(password)) {
                etPassword.error = "Password must be at least 6 characters"
                etPassword.requestFocus()
                return@setOnClickListener
            }

            // 2. Check if passwords match
            if (password != confirmPassword) {
                etConfirmPassword.error = "Passwords do not match!"
                etConfirmPassword.requestFocus()
                return@setOnClickListener
            }

            // 3. Check Terms
            if (!cbTerms.isChecked) {
                Toast.makeText(this, "Please agree to the Terms & Conditions", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 4. Proceed with Registration if validation passes
            val request = RegisterRequest(name, email, password)

            RetrofitClient.authInstance.registerUser(request).enqueue(object : Callback<String> {
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    android.util.Log.d("REG_DEBUG", "Status Code: ${response.code()}")

                    if (response.isSuccessful) {
                        // response.body() is now the actual String: "User registered successfully!"
                        val message = response.body() ?: "Account Created!"
                        Toast.makeText(this@RegisterActivity, message, Toast.LENGTH_LONG).show()

                        // Success! Go back to Login
                        finish()
                    } else {
                        // This handles the "Error: Email already exists!" string from backend
                        val errorMsg = response.errorBody()?.string() ?: "Registration Failed"
                        android.util.Log.e("REG_DEBUG", "Error Body: $errorMsg")
                        Toast.makeText(this@RegisterActivity, errorMsg, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    // This is for actual network crashes (e.g. Laptop is off)
                    Toast.makeText(this@RegisterActivity, "Network Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun showTermsBottomSheet() {
        val bottomSheetDialog = com.google.android.material.bottomsheet.BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.layout_terms_sheet, null)
        bottomSheetDialog.setContentView(view)
        
        view.findViewById<android.view.View>(R.id.btnCloseTerms).setOnClickListener {
            bottomSheetDialog.dismiss()
        }
        
        bottomSheetDialog.show()
    }
}