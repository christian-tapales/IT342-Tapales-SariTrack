package edu.cit.tapales.saritrack

import android.content.Context
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

object RetrofitClient {
    // Check if the device is an emulator
    private val isEmulator: Boolean = (android.os.Build.BRAND.startsWith("generic")
            || android.os.Build.DEVICE.startsWith("generic")
            || android.os.Build.FINGERPRINT.contains("generic")
            || android.os.Build.MODEL.contains("google_sdk")
            || android.os.Build.MODEL.contains("Emulator")
            || android.os.Build.MODEL.contains("Android SDK built for x86"))

    // 🚀 DEMO MODE: Replace this with your ngrok URL (e.g., "https://abcdef.ngrok-free.app")
    private const val GLOBAL_URL = "https://snippet-sheath-cloak.ngrok-free.dev/"

    // Use ngrok URL for everything to ensure cross-network stability
    private val BASE_URL = GLOBAL_URL

    private fun getOkHttpClient(context: Context): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(SessionManager(context)))
            .build()
    }

    fun <T> getService(serviceClass: Class<T>, context: Context): T {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(getOkHttpClient(context))
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create(serviceClass)
    }

    // For Auth (no interceptor needed or handled gracefully if token is null)
    val authInstance: AuthApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofit.create(AuthApiService::class.java)
    }

    fun getProductService(context: Context): ProductApiService {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(getOkHttpClient(context))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create(ProductApiService::class.java)
    }
}
