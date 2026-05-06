package edu.cit.tapales.saritrack

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    val id: Long,
    val name: String,
    val email: String,
    val role: String,
    val token: String
)