package edu.cit.tapales.saritrack

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CartItemAdapter(
    private val cartItems: MutableList<Pair<Product, Int>>,
    private val onQtyChanged: (Product, Int) -> Unit
) : RecyclerView.Adapter<CartItemAdapter.CartViewHolder>() {

    class CartViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvCartItemName)
        val tvPrice: TextView = view.findViewById(R.id.tvCartItemPrice)
        val tvQty: TextView = view.findViewById(R.id.tvCartItemQty)
        val btnPlus: ImageButton = view.findViewById(R.id.btnPlus)
        val btnMinus: ImageButton = view.findViewById(R.id.btnMinus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_cart_product, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val (product, qty) = cartItems[position]
        holder.tvName.text = product.name ?: "Unknown"
        holder.tvPrice.text = "₱${String.format("%.2f", product.price ?: 0.0)}"
        holder.tvQty.text = qty.toString()

        holder.btnPlus.setOnClickListener { onQtyChanged(product, qty + 1) }
        holder.btnMinus.setOnClickListener { onQtyChanged(product, qty - 1) }
    }

    override fun getItemCount() = cartItems.size
}
