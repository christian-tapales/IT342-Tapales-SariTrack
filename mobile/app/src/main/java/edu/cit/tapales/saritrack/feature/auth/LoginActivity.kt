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

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import androidx.activity.result.contract.ActivityResultContracts

class LoginActivity : AppCompatActivity() {
    private lateinit var viewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 🌓 Apply Saved Theme
        val sessionManager = SessionManager(this)
        
        // 🚪 Auto-Login Bypass
        if (sessionManager.isLoggedIn()) {
            startActivity(Intent(this, DashboardActivity::class.java))
            finish()
            return
        }

        val targetMode = if (sessionManager.isDarkMode()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

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
        val btnGoogle = findViewById<android.view.View>(R.id.btnGoogle)
        val tvToRegister = findViewById<TextView>(R.id.tvToRegister)

        viewModel = androidx.lifecycle.ViewModelProvider(this)[AuthViewModel::class.java]
        setupObservers()

        // 1. Handle Login Logic
        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                viewModel.login(email, password)
            } else {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
            }
        }

        // 2. Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.google_client_id))
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(this, gso)
        
        // Ensure we start fresh so the next click always shows the account picker
        googleSignInClient.signOut()

        val googleSignInLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            handleGoogleSignInResult(task)
        }

        btnGoogle.setOnClickListener {
            googleSignInLauncher.launch(googleSignInClient.signInIntent)
        }

        // 3. Navigate to Registration Screen
        tvToRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        // 4. Forgot Password Flow
        val tvForgotPassword = findViewById<TextView>(R.id.tvForgotPassword)
        tvForgotPassword?.setOnClickListener {
            showForgotPasswordDialog()
        }
    }

    private fun showForgotPasswordDialog() {
        val input = EditText(this).apply {
            hint = "name@company.com"
            inputType = android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
            setPadding(40, 30, 40, 30)
        }

        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Forgot Password")
            .setMessage("Enter your registered email address to receive a 15-minute reset token.")
            .setView(input)
            .setPositiveButton("Send Token") { _, _ ->
                val email = input.text.toString().trim()
                if (email.isNotEmpty()) {
                    RetrofitClient.authInstance.forgotPassword(ForgotPasswordRequest(email))
                        .enqueue(object : Callback<Map<String, String>> {
                            override fun onResponse(
                                call: Call<Map<String, String>>,
                                response: Response<Map<String, String>>
                            ) {
                                if (response.isSuccessful) {
                                    Toast.makeText(
                                        this@LoginActivity,
                                        response.body()?.get("message") ?: "Reset instructions sent to your email.",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    showResetPasswordDialog(email)
                                } else {
                                    val err = response.errorBody()?.string() ?: "Failed to send reset email."
                                    Toast.makeText(this@LoginActivity, err, Toast.LENGTH_LONG).show()
                                }
                            }

                            override fun onFailure(call: Call<Map<String, String>>, t: Throwable) {
                                Toast.makeText(this@LoginActivity, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
                            }
                        })
                } else {
                    Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showResetPasswordDialog(email: String) {
        val layout = android.widget.LinearLayout(this).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            setPadding(40, 20, 40, 20)
        }
        val etToken = EditText(this).apply {
            hint = "Reset Token"
            setPadding(20, 25, 20, 25)
        }
        val etNewPass = EditText(this).apply {
            hint = "New Password"
            inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
            setPadding(20, 25, 20, 25)
        }
        layout.addView(etToken)
        layout.addView(etNewPass)

        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Reset Password")
            .setMessage("Enter the reset token sent to $email and your new password.")
            .setView(layout)
            .setPositiveButton("Reset Password") { _, _ ->
                val token = etToken.text.toString().trim()
                val newPass = etNewPass.text.toString().trim()
                if (token.isNotEmpty() && newPass.isNotEmpty()) {
                    RetrofitClient.authInstance.resetPassword(ResetPasswordRequest(token, newPass))
                        .enqueue(object : Callback<Map<String, String>> {
                            override fun onResponse(
                                call: Call<Map<String, String>>,
                                response: Response<Map<String, String>>
                            ) {
                                if (response.isSuccessful) {
                                    Toast.makeText(
                                        this@LoginActivity,
                                        response.body()?.get("message") ?: "Password reset successfully!",
                                        Toast.LENGTH_LONG
                                    ).show()
                                } else {
                                    val err = response.errorBody()?.string() ?: "Failed to reset password."
                                    Toast.makeText(this@LoginActivity, err, Toast.LENGTH_LONG).show()
                                }
                            }

                            override fun onFailure(call: Call<Map<String, String>>, t: Throwable) {
                                Toast.makeText(this@LoginActivity, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
                            }
                        })
                } else {
                    Toast.makeText(this, "Please enter both token and new password", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun setupObservers() {
        val sessionManager = SessionManager(this)
        
        viewModel.loginResponse.observe(this) { response ->
            if (response != null) {
                sessionManager.saveAuthToken(response.token)
                sessionManager.saveUserDetail(response.id, response.email, response.role, response.name)
                Toast.makeText(this, "Welcome, ${response.name}!", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, DashboardActivity::class.java))
                finish()
            }
        }

        viewModel.error.observe(this) { msg ->
            if (msg != null) {
                Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
                viewModel.clearError()
            }
        }

        viewModel.isLoading.observe(this) { loading ->
            findViewById<Button>(R.id.btnLogIn).isEnabled = !loading
        }
    }

    private fun handleGoogleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            val idToken = account.idToken
            if (idToken != null) viewModel.loginWithGoogle(idToken)
        } catch (e: ApiException) {
            Toast.makeText(this, "Google error: ${e.statusCode}", Toast.LENGTH_SHORT).show()
        }
    }
}