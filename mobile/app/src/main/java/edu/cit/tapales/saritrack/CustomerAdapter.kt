package edu.cit.tapales.saritrack

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CustomerAdapter(
    private var customers: List<Customer>,
    private val onClick: (Customer) -> Unit
) : RecyclerView.Adapter<CustomerAdapter.CustomerViewHolder>() {

    class CustomerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvInitials: TextView = view.findViewById(R.id.tvInitials)
        val tvFullName: TextView = view.findViewById(R.id.tvFullName)
        val tvLastUpdate: TextView = view.findViewById(R.id.tvLastUpdate)
        val tvDebt: TextView = view.findViewById(R.id.tvDebt)
        val tvStatus: TextView = view.findViewById(R.id.tvStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_customer, parent, false)
        return CustomerViewHolder(view)
    }

    override fun onBindViewHolder(holder: CustomerViewHolder, position: Int) {
        val customer = customers[position]
        holder.tvFullName.text = customer.fullName ?: "Unknown Customer"
        holder.tvDebt.text = "₱${String.format("%.2f", customer.currentDebt)}"
        holder.tvStatus.text = customer.status.uppercase()
        
        // Handle initials
        val name = customer.fullName ?: ""
        val initials = name.split(" ").filter { it.isNotEmpty() }
            .take(2)
            .map { it[0].uppercase() }
            .joinToString("")
        holder.tvInitials.text = initials.ifEmpty { "?" }

        // Date placeholder for now
        holder.tvLastUpdate.text = "Last active: ${customer.lastUpdate?.substringBefore("T") ?: "N/A"}"

        holder.itemView.setOnClickListener { onClick(customer) }
    }

    override fun getItemCount() = customers.size

    fun updateCustomers(newCustomers: List<Customer>) {
        customers = newCustomers
        notifyDataSetChanged()
    }
}
