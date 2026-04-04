package edu.cit.tapales.saritrack

import android.content.Intent
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

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Ensure this matches your XML filename (e.g., activity_login or activity_main)
        setContentView(R.layout.activity_login)

        // Apply edge-to-edge padding (Assumes your XML root ID is "main")
        val rootView = findViewById<android.view.View>(R.id.main)
        if (rootView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(rootView) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }
        }

        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogIn)
        val tvToRegister = findViewById<TextView>(R.id.tvToRegister)

        // 1. Handle Login Logic
        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                val loginRequest = LoginRequest(email, password)

                // Use okhttp3.ResponseBody here
                RetrofitClient.instance.loginUser(loginRequest).enqueue(object : retrofit2.Callback<okhttp3.ResponseBody> {
                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                        android.util.Log.d("LOGIN_DEBUG", "Status Code: ${response.code()}")
                        if (response.isSuccessful) {
                            val message = response.body()?.string() ?: ""

                            // Check if the body actually contains a success message
                            if (message.contains("Successful", ignoreCase = true)) {
                                Toast.makeText(this@LoginActivity, "Welcome!", Toast.LENGTH_SHORT).show()
                                val intent = Intent(this@LoginActivity, DashboardActivity::class.java)
                                startActivity(intent)
                                finish()
                            } else {
                                // Treat 200 OK with "Invalid" text as a failure
                                Toast.makeText(this@LoginActivity, message, Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(this@LoginActivity, "Invalid Credentials (401)", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: retrofit2.Call<okhttp3.ResponseBody>, t: Throwable) {
                        android.widget.Toast.makeText(this@LoginActivity, "Error: ${t.message}", android.widget.Toast.LENGTH_SHORT).show()
                    }
                })
            }
        }

        // 2. Navigate to Registration Screen
        tvToRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}