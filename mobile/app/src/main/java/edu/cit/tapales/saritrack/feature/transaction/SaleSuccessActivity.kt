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

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*

class SaleSuccessActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sale_success)

        val totalAmount = intent.getDoubleExtra("TOTAL", 0.0)
        val status = intent.getStringExtra("STATUS") ?: "PAID"
        val itemsSummary = intent.getStringExtra("ITEMS") ?: ""
        val orderId = intent.getLongExtra("ORDER_ID", -1)

        val tvTotal = findViewById<TextView>(R.id.tvReceiptTotal)
        val tvMethod = findViewById<TextView>(R.id.tvPaymentMethod)
        val tvItems = findViewById<TextView>(R.id.tvReceiptItems)
        val tvDate = findViewById<TextView>(R.id.tvOrderDate)

        tvTotal.text = "₱${String.format("%.2f", totalAmount)}"
        tvMethod.text = when(status) {
            "DEBT" -> "UTANG / CREDIT"
            "DIGITAL" -> "DIGITAL PAYMENT"
            else -> "CASH PAYMENT"
        }
        tvItems.text = itemsSummary
        
        val sdf = SimpleDateFormat("MMM dd, yyyy - hh:mm a", Locale.getDefault())
        tvDate.text = "Order #$orderId\n${sdf.format(Date())}"

        findViewById<View>(R.id.btnDone).setOnClickListener {
            finish()
        }

        findViewById<View>(R.id.btnShareReceipt).setOnClickListener {
            shareReceipt(orderId, totalAmount, status, itemsSummary)
        }
    }

    private fun shareReceipt(orderId: Long, total: Double, status: String, items: String) {
        val shareText = """
            📦 SariTrack Receipt
            Order #$orderId
            Status: ${status.uppercase()}
            ------------------------
            $items
            ------------------------
            TOTAL: ₱${String.format("%.2f", total)}
            
            Thank you for shopping! 🏪
        """.trimIndent()

        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, shareText)
        startActivity(Intent.createChooser(intent, "Share Receipt via"))
    }
}
