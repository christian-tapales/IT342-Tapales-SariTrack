package edu.cit.tapales.saritrack.feature.customer

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

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class CustomerHistoryActivity : AppCompatActivity() {

    private lateinit var rvHistory: RecyclerView
    private lateinit var adapter: HistoryAdapter
    private lateinit var tvEmptyState: TextView
    private var customerId: Long = -1

    private val historyItems = mutableListOf<HistoryItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer_history)

        customerId = intent.getLongExtra("CUSTOMER_ID", -1)
        val customerName = intent.getStringExtra("CUSTOMER_NAME") ?: "History"
        val currentDebt = intent.getDoubleExtra("CURRENT_DEBT", 0.0)

        findViewById<TextView>(R.id.tvCustomerName).text = customerName
        findViewById<TextView>(R.id.tvTotalDebt).text = "Total Debt: ₱${String.format("%.2f", currentDebt)}"
        findViewById<View>(R.id.btnBack).setOnClickListener { finish() }

        rvHistory = findViewById(R.id.rvHistory)
        tvEmptyState = findViewById(R.id.tvEmptyState)

        rvHistory.layoutManager = LinearLayoutManager(this)
        adapter = HistoryAdapter(emptyList())
        rvHistory.adapter = adapter

        fetchHistory()
    }

    private fun fetchHistory() {
        if (customerId == -1L) return

        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())

        // Fetch Orders
        RetrofitClient.getTransactionService(this).getCustomerOrderHistory(customerId)
            .enqueue(object : Callback<List<Order>> {
                override fun onResponse(call: Call<List<Order>>, response: Response<List<Order>>) {
                    if (response.isSuccessful) {
                        response.body()?.forEach { order ->
                            val date = order.timestamp?.let { parseDate(it) } ?: Date()
                            historyItems.add(HistoryItem.OrderType(order, date))
                        }
                        updateUI()
                    }
                }
                override fun onFailure(call: Call<List<Order>>, t: Throwable) {}
            })

        // Fetch Payments
        RetrofitClient.getCustomerService(this).getPaymentHistory(customerId)
            .enqueue(object : Callback<List<DebtPayment>> {
                override fun onResponse(call: Call<List<DebtPayment>>, response: Response<List<DebtPayment>>) {
                    if (response.isSuccessful) {
                        response.body()?.forEach { payment ->
                            val date = payment.timestamp?.let { parseDate(it) } ?: Date()
                            historyItems.add(HistoryItem.PaymentType(payment, date))
                        }
                        updateUI()
                    }
                }
                override fun onFailure(call: Call<List<DebtPayment>>, t: Throwable) {}
            })
    }

    private fun parseDate(dateStr: String): Date? {
        val formats = listOf(
            "yyyy-MM-dd'T'HH:mm:ss.SSS",
            "yyyy-MM-dd'T'HH:mm:ss",
            "yyyy-MM-dd HH:mm:ss"
        )
        for (format in formats) {
            try {
                return SimpleDateFormat(format, Locale.getDefault()).parse(dateStr)
            } catch (e: Exception) {}
        }
        return null
    }

    private fun updateUI() {
        if (historyItems.isEmpty()) {
            tvEmptyState.visibility = View.VISIBLE
            rvHistory.visibility = View.GONE
        } else {
            tvEmptyState.visibility = View.GONE
            rvHistory.visibility = View.VISIBLE
            adapter.updateItems(historyItems)
        }
    }
}
