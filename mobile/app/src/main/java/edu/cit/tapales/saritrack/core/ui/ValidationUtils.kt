package edu.cit.tapales.saritrack.core.ui

object ValidationUtils {
    fun isValidEmail(email: String?): Boolean {
        if (email.isNullOrBlank()) return false
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
        return emailRegex.matches(email)
    }

    fun isStrongPassword(password: String?): Boolean {
        if (password.isNullOrBlank()) return false
        return password.length >= 6 // Minimum 6 characters for SariTrack
    }

    fun isValidName(name: String?): Boolean {
        return !name.isNullOrBlank() && name.length >= 2
    }
}
