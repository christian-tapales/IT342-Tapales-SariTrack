package edu.cit.tapales.saritrack

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TransactionAdapter(private var transactions: List<Order>) :
    RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    class TransactionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTitle: TextView = view.findViewById(R.id.tvTransactionTitle)
        val tvDate: TextView = view.findViewById(R.id.tvTransactionDate)
        val tvAmount: TextView = view.findViewById(R.id.tvTransactionAmount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transaction, parent, false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = transactions[position]
        holder.tvTitle.text = "Order #${transaction.id}"
        holder.tvDate.text = transaction.timestamp?.substringBefore("T") ?: "Unknown"
        holder.tvAmount.text = "₱${String.format("%.2f", transaction.totalAmount)}"
        
        // Color based on status if needed
        if (transaction.status == "DEBT") {
            holder.tvAmount.setTextColor(android.graphics.Color.RED)
        } else {
            holder.tvAmount.setTextColor(holder.itemView.context.getColor(R.color.primary_teal))
        }
    }

    override fun getItemCount() = transactions.size

    fun updateData(newTransactions: List<Order>) {
        this.transactions = newTransactions
        notifyDataSetChanged()
    }
}
