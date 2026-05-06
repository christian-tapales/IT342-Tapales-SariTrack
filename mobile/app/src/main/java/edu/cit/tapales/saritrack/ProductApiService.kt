package edu.cit.tapales.saritrack

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ProductApiService {
    @GET("api/products")
    fun getVendorProducts(@retrofit2.http.Query("vendorId") vendorId: Long): Call<List<Product>>

    @retrofit2.http.POST("api/products")
    fun addProduct(@retrofit2.http.Body product: Product): Call<Product>

    @retrofit2.http.PUT("api/products/{id}")
    fun updateProduct(
        @retrofit2.http.Path("id") id: Long,
        @retrofit2.http.Body product: Product,
        @retrofit2.http.Query("vendorId") vendorId: Long
    ): Call<Product>

    @retrofit2.http.DELETE("api/products/{id}")
    fun deleteProduct(
        @retrofit2.http.Path("id") id: Long,
        @retrofit2.http.Query("vendorId") vendorId: Long
    ): Call<okhttp3.ResponseBody>
}
