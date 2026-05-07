package edu.cit.tapales.saritrack

import retrofit2.Call
import retrofit2.http.*

interface CustomerApiService {
    @GET("api/customers")
    fun getCustomers(@Query("vendorId") vendorId: Long): Call<List<Customer>>

    @POST("api/customers")
    fun addCustomer(@Body customer: Customer): Call<Customer>

    @POST("api/customers/{id}/pay")
    fun recordPayment(
        @Path("id") id: Long,
        @Body payload: Map<String, Double>
    ): Call<Customer>

    @GET("api/customers/{id}/payments")
    fun getPaymentHistory(@Path("id") id: Long): Call<List<DebtPayment>>

    @DELETE("api/customers/{id}")
    fun deleteCustomer(@Path("id") id: Long): Call<Unit>
}
