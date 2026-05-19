package edu.cit.tapales.saritrack.feature.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import edu.cit.tapales.saritrack.core.api.RetrofitClient
import edu.cit.tapales.saritrack.core.auth.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthViewModel : ViewModel() {

    private val _loginResponse = MutableLiveData<LoginResponse?>()
    val loginResponse: LiveData<LoginResponse?> = _loginResponse

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun login(email: String, password: String) {
        _isLoading.value = true
        val request = LoginRequest(email, password)
        
        RetrofitClient.authInstance.loginUser(request).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _loginResponse.value = response.body()
                } else {
                    val errorMsg = try {
                        val errorBody = response.errorBody()?.string()
                        val json = com.google.gson.JsonParser.parseString(errorBody).asJsonObject
                        json.get("error").asString
                    } catch (e: Exception) {
                        "Invalid email or password"
                    }
                    _error.value = errorMsg
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                _isLoading.value = false
                _error.value = "Connection Error: ${t.message}"
            }
        })
    }

    fun loginWithGoogle(idToken: String) {
        _isLoading.value = true
        val request = mapOf("idToken" to idToken)

        RetrofitClient.authInstance.googleMobileLogin(request).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _loginResponse.value = response.body()
                } else {
                    _error.value = "Google Authentication Failed"
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                _isLoading.value = false
                _error.value = "Network Error: ${t.message}"
            }
        })
    }

    fun clearError() { _error.value = null }
}
