package edu.cit.tapales.saritrack

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


    // Use 10.0.2.2 for Emulator, and your Laptop IP for Physical Phone
    private const val LAPTOP_IP = "10.173.184.119" // Put your IP here
    private val BASE_URL = if (isEmulator) "http://10.0.2.2:8080" else "http://$LAPTOP_IP:8080"

    val instance: AuthApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(ScalarsConverterFactory.create()) // 2. ADD THIS FIRST (for Plain Text)
            .addConverterFactory(GsonConverterFactory.create())    // 3. KEEP THIS SECOND (for JSON)
            .build()

        retrofit.create(AuthApiService::class.java)
    }
}