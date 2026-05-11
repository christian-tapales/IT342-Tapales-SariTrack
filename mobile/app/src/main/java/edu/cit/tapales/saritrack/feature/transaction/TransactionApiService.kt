package edu.cit.tapales.saritrack.feature.transaction

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
