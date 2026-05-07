package edu.cit.tapales.saritrack

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface TransactionApiService {
    @GET("api/orders/history")
    fun getOrderHistory(@Query("vendorId") vendorId: Long): Call<List<Order>>

    @retrofit2.http.POST("api/orders")
    fun placeOrder(@retrofit2.http.Body order: Order): Call<Order>

    @GET("api/orders/customer/{customerId}")
    fun getCustomerOrderHistory(@retrofit2.http.Path("customerId") customerId: Long): Call<List<Order>>
}
