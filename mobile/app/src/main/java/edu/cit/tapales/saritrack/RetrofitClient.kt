package edu.cit.tapales.saritrack

import android.content.Context
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://snippet-sheath-cloak.ngrok-free.dev/"

    private fun getOkHttpClient(context: Context?): OkHttpClient {
        val builder = OkHttpClient.Builder()
            .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .writeTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
        
        if (context != null) {
            builder.addInterceptor(AuthInterceptor(SessionManager(context)))
        }
        return builder.build()
    }

    val authInstance: AuthApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(getOkHttpClient(null)) // Login doesn't need auth interceptor but needs timeout
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofit.create(AuthApiService::class.java)
    }

    private var instance: Retrofit? = null

    fun getInstance(context: Context): Retrofit {
        if (instance == null) {
            instance = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(getOkHttpClient(context))
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return instance!!
    }

    fun getDashboardService(context: Context): DashboardApiService {
        return getInstance(context).create(DashboardApiService::class.java)
    }

    fun getProductService(context: Context): ProductApiService {
        return getInstance(context).create(ProductApiService::class.java)
    }

    fun getTransactionService(context: Context): TransactionApiService {
        return getInstance(context).create(TransactionApiService::class.java)
    }

    fun getCustomerService(context: Context): CustomerApiService {
        return getInstance(context).create(CustomerApiService::class.java)
    }
}
