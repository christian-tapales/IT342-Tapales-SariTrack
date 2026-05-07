package edu.cit.tapales.saritrack

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

data class DashboardStats(
    val todaySales: Double,
    val lowStockCount: Int,
    val totalDebt: Double,
    val recentTransactions: List<Order>,
    val weeklySales: List<WeeklySalesData>
)

data class WeeklySalesData(
    val day: String,
    val sales: Double
)

interface DashboardApiService {
    @GET("api/vendor/dashboard/stats")
    fun getVendorStats(@Query("vendorId") vendorId: Long): Call<DashboardStats>
}
