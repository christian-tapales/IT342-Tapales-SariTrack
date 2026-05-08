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

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {
    @POST("/api/auth/register")
    fun registerUser(@Body request: RegisterRequest): Call<String>

    @POST("/api/auth/login")
    fun loginUser(@Body request: LoginRequest): Call<LoginResponse>

    @POST("/api/auth/google-mobile")
    fun googleMobileLogin(@Body request: Map<String, String>): Call<LoginResponse>
}
