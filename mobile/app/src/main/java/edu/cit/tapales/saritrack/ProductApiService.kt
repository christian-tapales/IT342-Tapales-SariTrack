package edu.cit.tapales.saritrack

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ProductApiService {
    @GET("api/products")
    fun getVendorProducts(@retrofit2.http.Query("vendorId") vendorId: Long): Call<List<Product>>

    @retrofit2.http.POST("api/products")
    fun addProduct(@retrofit2.http.Body product: Product): Call<Product>
}
