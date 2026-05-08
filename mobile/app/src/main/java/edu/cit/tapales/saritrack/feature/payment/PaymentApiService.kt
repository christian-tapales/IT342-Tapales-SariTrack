package edu.cit.tapales.saritrack.feature.payment

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
import retrofit2.http.Body
import retrofit2.http.POST

interface PaymentApiService {
    @POST("api/orders")
    fun createPendingOrder(@Body order: Order): Call<Order>

    @POST("api/payments/create-session")
    fun createCheckoutSession(@Body payload: PaymentRequest): Call<PaymentResponse>
}
