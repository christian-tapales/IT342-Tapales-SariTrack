package edu.cit.tapales.saritrack

import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val sessionManager: SessionManager) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()

        // 🛡️ CRITICAL: This stops the "Too many follow-up requests" error with ngrok
        requestBuilder.addHeader("ngrok-skip-browser-warning", "true")

        // If token exists, add it to the header
        sessionManager.fetchAuthToken()?.let {
            requestBuilder.addHeader("Authorization", "Bearer $it")
        }

        return chain.proceed(requestBuilder.build())
    }
}
