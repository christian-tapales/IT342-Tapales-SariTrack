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

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

sealed class HistoryItem(val date: Date) {
    class OrderType(val order: Order, date: Date) : HistoryItem(date)
    class PaymentType(val payment: DebtPayment, date: Date) : HistoryItem(date)
}

class HistoryAdapter(private var items: List<HistoryItem>) :
    RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val typeIndicator: View = view.findViewById(R.id.typeIndicator)
        val tvType: TextView = view.findViewById(R.id.tvHistoryType)
        val tvDate: TextView = view.findViewById(R.id.tvHistoryDate)
        val tvAmount: TextView = view.findViewById(R.id.tvHistoryAmount)
        val tvItems: TextView = view.findViewById(R.id.tvHistoryItems)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_history, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        val sdf = SimpleDateFormat("MMM dd, yyyy - hh:mm a", Locale.getDefault())

        when (item) {
            is HistoryItem.OrderType -> {
                val order = item.order
                holder.tvType.text = "Order #${order.id}"
                holder.tvDate.text = sdf.format(item.date)
                holder.tvAmount.text = "₱${String.format("%.2f", order.totalAmount)}"
                holder.tvAmount.setTextColor(android.graphics.Color.parseColor("#EF4444")) // Red for debt
                holder.typeIndicator.setBackgroundColor(android.graphics.Color.parseColor("#EF4444"))
                
                val itemText = order.items.joinToString { it.product?.name ?: "Item" }
                holder.tvItems.visibility = View.VISIBLE
                holder.tvItems.text = "${order.items.size} items: $itemText"
            }
            is HistoryItem.PaymentType -> {
                val payment = item.payment
                holder.tvType.text = "Payment Received"
                holder.tvDate.text = sdf.format(item.date)
                holder.tvAmount.text = "₱${String.format("%.2f", payment.amount)}"
                holder.tvAmount.setTextColor(android.graphics.Color.parseColor("#10B981")) // Green for credit
                holder.typeIndicator.setBackgroundColor(android.graphics.Color.parseColor("#10B981"))
                holder.tvItems.visibility = View.GONE
            }
        }
    }

    override fun getItemCount() = items.size

    fun updateItems(newItems: List<HistoryItem>) {
        items = newItems.sortedByDescending { it.date }
        notifyDataSetChanged()
    }
}
