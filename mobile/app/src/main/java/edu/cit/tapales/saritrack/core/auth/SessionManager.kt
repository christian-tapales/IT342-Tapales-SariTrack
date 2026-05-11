package edu.cit.tapales.saritrack.core.auth

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

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class SessionManager(context: Context) {
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val prefs: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        "saritrack_session",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    companion object {
        private const val KEY_JWT_TOKEN = "jwt_token"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_USER_ROLE = "user_role"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
    }

    fun saveAuthToken(token: String) {
        prefs.edit().putString(KEY_JWT_TOKEN, token).apply()
        prefs.edit().putBoolean(KEY_IS_LOGGED_IN, true).apply()
    }

    fun fetchAuthToken(): String? {
        return prefs.getString(KEY_JWT_TOKEN, null)
    }

    fun saveUserDetail(id: Long, email: String, role: String, name: String? = null) {
        prefs.edit().putLong(KEY_USER_ID, id).apply()
        prefs.edit().putString(KEY_USER_EMAIL, email).apply()
        prefs.edit().putString(KEY_USER_ROLE, role).apply()
        name?.let { prefs.edit().putString(KEY_USER_NAME, it).apply() }
    }

    fun getUserName(): String? {
        return prefs.getString(KEY_USER_NAME, null)
    }

    fun getUserId(): Long {
        return prefs.getLong(KEY_USER_ID, -1L)
    }

    fun getUserEmail(): String? {
        return prefs.getString(KEY_USER_EMAIL, null)
    }

    fun getUserRole(): String? {
        return prefs.getString(KEY_USER_ROLE, null)
    }

    fun isLoggedIn(): Boolean {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    fun logout() {
        prefs.edit().clear().apply()
    }

    // Theme Persistence
    fun saveTheme(isDarkMode: Boolean) {
        prefs.edit().putBoolean("is_dark_mode", isDarkMode).apply()
    }

    fun isDarkMode(): Boolean {
        return prefs.getBoolean("is_dark_mode", false)
    }
}
