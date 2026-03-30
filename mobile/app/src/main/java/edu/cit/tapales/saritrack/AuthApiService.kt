package edu.cit.tapales.saritrack

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {
    @POST("/api/auth/register")
    fun registerUser(@Body request: RegisterRequest): Call<ResponseBody>

    @POST("/api/auth/login")
    fun loginUser(@Body request: LoginRequest): Call<ResponseBody>
}