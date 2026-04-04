package edu.cit.tapales.saritrack

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
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
        val etName = findViewById<EditText>(R.id.etName)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val etConfirmPassword = findViewById<EditText>(R.id.etConfirmPassword) // Added this
        val tvToLogin = findViewById<TextView>(R.id.tvToLogin)

        tvToLogin.setOnClickListener {
            finish()
        }

        btnRegister.setOnClickListener {
            val name = etName.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val confirmPassword = etConfirmPassword.text.toString().trim() // Get confirm password

            // 1. Check if any fields are empty
            if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 2. Check if passwords match
            if (password != confirmPassword) {
                // Option A: Set an error directly on the text box (Cleaner UI)
                etConfirmPassword.error = "Passwords do not match!"
                etConfirmPassword.requestFocus()

                // Option B: Show a Toast
                Toast.makeText(this, "Passwords do not match. Please try again.", Toast.LENGTH_SHORT).show()

                return@setOnClickListener // Stops the code from proceeding to the API call
            }

            // 3. Proceed with Registration if validation passes
            val request = RegisterRequest(name, email, password)

            RetrofitClient.instance.registerUser(request).enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        val message = response.body()?.string() ?: "Account Created!"
                        Toast.makeText(this@RegisterActivity, message, Toast.LENGTH_LONG).show()
                        finish() // Return to login after successful registration
                    } else {
                        Toast.makeText(this@RegisterActivity, "Registration Failed", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toast.makeText(this@RegisterActivity, "Network Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}