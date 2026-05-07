package edu.cit.tapales.saritrack

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class CartBottomSheet(
    private val cart: MutableMap<Product, Int>,
    private val onCartUpdated: () -> Unit
) : BottomSheetDialogFragment() {

    private lateinit var rvCart: RecyclerView
    private lateinit var adapter: CartItemAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.layout_customer_picker, container, false)
        view.findViewById<TextView>(R.id.tvSheetCustomerName)?.let {
            it.visibility = View.VISIBLE
            it.text = "Review Cart"
        }
        // I'll use layout_customer_picker as base since it has a list area.
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        rvCart = view.findViewById(R.id.rvCustomerPicker) // reusing the ID for now
        rvCart.layoutManager = LinearLayoutManager(context)
        
        setupAdapter()
    }

    private fun setupAdapter() {
        val items = cart.entries.map { it.key to it.value }.toMutableList()
        adapter = CartItemAdapter(items) { product, newQty ->
            if (newQty <= 0) {
                cart.remove(product)
            } else {
                cart[product] = newQty
            }
            onCartUpdated()
            setupAdapter() // refresh entire list structure
        }
        rvCart.adapter = adapter
    }

    override fun getTheme(): Int = R.style.CustomBottomSheetDialog
}
