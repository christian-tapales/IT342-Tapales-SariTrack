package edu.cit.tapales.saritrack.feature.dashboard

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

data class DashboardStats(
    val todaySales: Double,
    val lowStockCount: Int,
    val inventoryValue: Double,
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
