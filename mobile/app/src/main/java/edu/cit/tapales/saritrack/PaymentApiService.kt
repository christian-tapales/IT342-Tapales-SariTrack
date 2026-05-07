package edu.cit.tapales.saritrack

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface PaymentApiService {
    @POST("api/orders")
    fun createPendingOrder(@Body order: Order): Call<Order>

    @POST("api/payments/create-session")
    fun createCheckoutSession(@Body payload: PaymentRequest): Call<PaymentResponse>
}
