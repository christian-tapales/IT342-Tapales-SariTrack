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

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment() {

    private var tvGreeting: TextView? = null
    private var tvTodaySales: TextView? = null
    private var tvLowStockCount: TextView? = null
    private var tvInventoryValue: TextView? = null
    private var tvTotalOutstanding: TextView? = null
    private var rvRecentTransactions: RecyclerView? = null
    private var chartContainer: FrameLayout? = null
    private var tvEmptyRecentSales: TextView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        
        tvGreeting = view.findViewById(R.id.tvGreeting)
        tvTodaySales = view.findViewById(R.id.tvTodaySales)
        tvLowStockCount = view.findViewById(R.id.tvLowStockCount)
        tvInventoryValue = view.findViewById(R.id.tvInventoryValue)
        tvTotalOutstanding = view.findViewById(R.id.tvTotalOutstanding)
        rvRecentTransactions = view.findViewById(R.id.rvRecentTransactions)
        chartContainer = view.findViewById(R.id.chartContainer)
        tvEmptyRecentSales = view.findViewById(R.id.tvEmptyRecentSales)
        
        val btnLogout: View? = view.findViewById(R.id.btnLogout)
        val btnThemeToggle: View? = view.findViewById(R.id.btnThemeToggle)
        val tvRefresh: View? = view.findViewById(R.id.tvViewAllSales)

        // KPI Cards for navigation
        val cardSales: View? = view.findViewById(R.id.cardTodaySales)
        val cardLowStock: View? = view.findViewById(R.id.cardLowStock)
        val cardDebt: View? = view.findViewById(R.id.cardTotalOutstanding)
        val cardInventoryValue: View? = view.findViewById(R.id.cardInventoryValue)
        
        val context = context ?: return view
        val sessionManager = SessionManager(context)
        val name = sessionManager.getUserName() ?: "Vendor"
        
        tvGreeting?.text = name

        rvRecentTransactions?.layoutManager = LinearLayoutManager(context)
        
        fetchDashboardData()
        
        tvRefresh?.setOnClickListener { fetchDashboardData() }

        // Navigation Shortcuts
        val mainActivity = activity as? DashboardActivity
        cardSales?.setOnClickListener { mainActivity?.navigateToFragment(R.id.nav_sales) }
        cardLowStock?.setOnClickListener { mainActivity?.navigateToFragment(R.id.nav_products) }
        cardDebt?.setOnClickListener { mainActivity?.navigateToFragment(R.id.nav_credits) }
        cardInventoryValue?.setOnClickListener { mainActivity?.navigateToFragment(R.id.nav_products) }

        btnLogout?.setOnClickListener {
            sessionManager.logout()
            val intent = Intent(context, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        btnThemeToggle?.setOnClickListener {
            val isCurrentlyDark = sessionManager.isDarkMode()
            val newMode = !isCurrentlyDark
            sessionManager.saveTheme(newMode)
            AppCompatDelegate.setDefaultNightMode(if (newMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO)
        }
        
        return view
    }

    private fun fetchDashboardData() {
        val context = context ?: return
        val vendorId = SessionManager(context).getUserId()

        RetrofitClient.getDashboardService(context).getVendorStats(vendorId)
            .enqueue(object : Callback<DashboardStats> {
                override fun onResponse(call: Call<DashboardStats>, response: Response<DashboardStats>) {
                    if (response.isSuccessful) {
                        val stats = response.body() ?: return
                        updateUI(stats)
                    } else {
                        Toast.makeText(context, "Failed to load dashboard data", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<DashboardStats>, t: Throwable) {
                    Toast.makeText(context, "Dashboard Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun updateUI(stats: DashboardStats) {
        tvTodaySales?.text = "₱${String.format("%.0f", stats.todaySales)}"
        tvLowStockCount?.text = stats.lowStockCount.toString()
        tvInventoryValue?.text = "₱${String.format("%.0f", stats.inventoryValue)}"
        tvTotalOutstanding?.text = "₱${String.format("%.0f", stats.totalDebt)}"

        // Update Recent Transactions
        if (stats.recentTransactions.isEmpty()) {
            tvEmptyRecentSales?.visibility = View.VISIBLE
            rvRecentTransactions?.visibility = View.GONE
        } else {
            tvEmptyRecentSales?.visibility = View.GONE
            rvRecentTransactions?.visibility = View.VISIBLE
            
            // Reusing SaleSuccessActivity to show details
            rvRecentTransactions?.adapter = TransactionAdapter(stats.recentTransactions) { order ->
                val intent = Intent(context, SaleSuccessActivity::class.java)
                intent.putExtra("ORDER_ID", order.id)
                intent.putExtra("TOTAL", order.totalAmount)
                intent.putExtra("STATUS", order.status)
                
                val itemsSummary = order.items.joinToString("\n") { item ->
                    "${item.quantity}x ${item.product?.name ?: "Product #${item.productId}"} (₱${item.priceAtSale})"
                }
                intent.putExtra("ITEMS", itemsSummary)
                startActivity(intent)
            }
        }

        // Draw line chart with Y-axis
        drawWeeklyChart(stats.weeklySales)
    }

    private fun drawWeeklyChart(data: List<WeeklySalesData>) {
        val container = chartContainer ?: return
        container.removeAllViews()

        // Create a custom view for the line chart
        val lineChartView = object : View(context) {
            private val paint = android.graphics.Paint().apply {
                color = ContextCompat.getColor(context, R.color.primary_teal)
                strokeWidth = 8f
                style = android.graphics.Paint.Style.STROKE
                strokeCap = android.graphics.Paint.Cap.ROUND
                isAntiAlias = true
            }
            
            private val fillPaint = android.graphics.Paint().apply {
                color = (0x3314B8A6).toInt() // Transparent teal
                style = android.graphics.Paint.Style.FILL
                isAntiAlias = true
            }

            override fun onDraw(canvas: android.graphics.Canvas) {
                super.onDraw(canvas)
                if (data.isEmpty()) return

                val width = canvas.width.toFloat()
                val height = canvas.height.toFloat()
                val padding = 40f
                val chartWidth = width - (padding * 2)
                val chartHeight = height - (padding * 2)
                
                val maxSales = data.maxOfOrNull { it.sales } ?: 1.0
                val finalMax = if (maxSales == 0.0) 1.0 else maxSales
                
                val stepX = chartWidth / (data.size - 1)
                val path = android.graphics.Path()
                val fillPath = android.graphics.Path()

                // Draw Y-axis labels
                val textPaint = android.graphics.Paint().apply {
                    color = ContextCompat.getColor(context, R.color.text_body)
                    textSize = 24f
                    isAntiAlias = true
                }
                
                // Draw grid lines and labels
                val gridPaint = android.graphics.Paint().apply {
                    color = (0x1A000000).toInt()
                    strokeWidth = 2f
                    isAntiAlias = true
                }

                val labelCount = 3
                for (i in 0 until labelCount) {
                    val labelY = height - padding - (i.toFloat() / (labelCount - 1) * chartHeight)
                    val labelValue = (i.toFloat() / (labelCount - 1) * finalMax)
                    canvas.drawText("₱${String.format("%.0f", labelValue)}", 5f, labelY, textPaint)
                    canvas.drawLine(padding, labelY, width - padding, labelY, gridPaint)
                }

                data.forEachIndexed { i, item ->
                    val x = padding + (i * stepX)
                    val y = height - padding - (item.sales.toFloat() / finalMax.toFloat() * chartHeight)
                    
                    if (i == 0) {
                        path.moveTo(x, y)
                        fillPath.moveTo(x, height - padding)
                        fillPath.lineTo(x, y)
                    } else {
                        path.lineTo(x, y)
                        fillPath.lineTo(x, y)
                    }
                    
                    if (i == data.size - 1) {
                        fillPath.lineTo(x, height - padding)
                        fillPath.close()
                    }

                    // Draw dots
                    val dotPaint = android.graphics.Paint().apply {
                        color = ContextCompat.getColor(context, R.color.primary_teal)
                        style = android.graphics.Paint.Style.FILL
                        isAntiAlias = true
                    }
                    canvas.drawCircle(x, y, 10f, dotPaint)
                }
                
                canvas.drawPath(fillPath, fillPaint)
                canvas.drawPath(path, paint)
            }
        }

        lineChartView.layoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT
        )
        container.addView(lineChartView)

        // Add Day Labels below
        val labelLayout = LinearLayout(context)
        labelLayout.orientation = LinearLayout.HORIZONTAL
        labelLayout.layoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        ).apply { 
            gravity = android.view.Gravity.BOTTOM
            leftMargin = 40
            rightMargin = 40
        }

        for (item in data) {
            val label = TextView(context)
            label.text = item.day
            label.textSize = 10f
            label.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_body))
            label.gravity = android.view.Gravity.CENTER
            label.layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            labelLayout.addView(label)
        }
        container.addView(labelLayout)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        tvGreeting = null
        tvTodaySales = null
        tvLowStockCount = null
        tvInventoryValue = null
        tvTotalOutstanding = null
        rvRecentTransactions = null
        chartContainer = null
        tvEmptyRecentSales = null
    }
}
