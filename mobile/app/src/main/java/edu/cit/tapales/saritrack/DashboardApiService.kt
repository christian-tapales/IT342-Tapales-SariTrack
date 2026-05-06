package edu.cit.tapales.saritrack

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

data class DashboardStats(
    val todaySales: Double,
    val orderCount: Int,
    val totalDebt: Double,
    val collectionRate: Double
)

interface DashboardApiService {
    @GET("api/vendor/{vendorId}/stats")
    fun getVendorStats(@Path("vendorId") vendorId: Long): Call<DashboardStats>
}
