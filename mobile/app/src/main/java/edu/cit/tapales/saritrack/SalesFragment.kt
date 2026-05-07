package edu.cit.tapales.saritrack

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SalesFragment : Fragment() {

    private lateinit var rvProducts: RecyclerView
    private lateinit var adapter: ProductAdapter
    private lateinit var etSearch: EditText
    private lateinit var tvCartItemsCount: TextView
    private lateinit var tvCartTotal: TextView
    private lateinit var btnCheckout: MaterialButton
    private lateinit var btnChargeDebt: MaterialButton

    private var allProducts = listOf<Product>()
    private val cart = mutableMapOf<Product, Int>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_sales, container, false)

        rvProducts = view.findViewById(R.id.rvSalesProducts)
        etSearch = view.findViewById(R.id.etSearchProduct)
        tvCartItemsCount = view.findViewById(R.id.tvCartItemsCount)
        tvCartTotal = view.findViewById(R.id.tvCartTotal)
        btnCheckout = view.findViewById(R.id.btnCheckout)
        btnChargeDebt = view.findViewById(R.id.btnChargeDebt)
        val btnOpenScanner = view.findViewById<MaterialButton>(R.id.btnOpenScanner)

        rvProducts.layoutManager = androidx.recyclerview.widget.GridLayoutManager(context, 2)
        adapter = ProductAdapter(emptyList()) { product ->
            addToCart(product)
        }
        rvProducts.adapter = adapter

        fetchProducts()

        etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) { filterProducts(s.toString()) }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        val btnFilter = view.findViewById<MaterialButton>(R.id.btnFilter)

        btnOpenScanner.setOnClickListener { openScanner() }
        btnCheckout.setOnClickListener { performCheckout("PAID") }
        btnChargeDebt.setOnClickListener { openCustomerPicker() }
        btnFilter.setOnClickListener { showFilterMenu(it) }

        view.findViewById<View>(R.id.cartSummaryCard).setOnClickListener {
            if (cart.isNotEmpty()) {
                val cartSheet = CartBottomSheet(cart) {
                    updateCartUI()
                }
                cartSheet.show(parentFragmentManager, "CartSheet")
            }
        }

        return view
    }

    private fun openCustomerPicker() {
        val picker = CustomerPickerBottomSheet { customer ->
            performCheckout("DEBT", customer.id)
        }
        picker.show(parentFragmentManager, "CustomerPicker")
    }

    private fun showFilterMenu(view: View) {
        val popup = androidx.appcompat.widget.PopupMenu(requireContext(), view)
        popup.menu.add("Sort: A to Z")
        popup.menu.add("Price: Low to High")
        popup.menu.add("Price: High to Low")
        
        popup.setOnMenuItemClickListener { item ->
            when (item.title) {
                "Sort: A to Z" -> {
                    allProducts = allProducts.sortedBy { it.name }
                }
                "Price: Low to High" -> {
                    allProducts = allProducts.sortedBy { it.price }
                }
                "Price: High to Low" -> {
                    allProducts = allProducts.sortedByDescending { it.price }
                }
            }
            filterProducts(etSearch.text.toString()) // Apply filter after sorting
            true
        }
        popup.show()
    }

    private fun fetchProducts() {
        val context = context ?: return
        val vendorId = SessionManager(context).getUserId()
        RetrofitClient.getProductService(context).getVendorProducts(vendorId)
            .enqueue(object : Callback<List<Product>> {
                override fun onResponse(call: Call<List<Product>>, response: Response<List<Product>>) {
                    if (response.isSuccessful) {
                        allProducts = response.body() ?: emptyList()
                        adapter.updateProducts(allProducts)
                    }
                }
                override fun onFailure(call: Call<List<Product>>, t: Throwable) {
                    Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun filterProducts(query: String) {
        val filtered = allProducts.filter { 
            (it.name?.contains(query, ignoreCase = true) == true) || 
            (it.barcode?.contains(query) == true) 
        }
        adapter.updateProducts(filtered)
    }

    private fun openScanner() {
        if (androidx.core.content.ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.CAMERA) 
            == android.content.pm.PackageManager.PERMISSION_GRANTED) {
            
            val scanner = ScannerBottomSheet { code ->
                val cleanScannedCode = code.replace("\\s".toRegex(), "").trim()
                val product = allProducts.find { 
                    (it.barcode?.replace("\\s".toRegex(), "")?.trim() ?: "") == cleanScannedCode 
                }
                
                if (product != null) {
                    addToCart(product)
                    Toast.makeText(context, "Added: ${product.name ?: "Product"}", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Product not in inventory: $cleanScannedCode", Toast.LENGTH_LONG).show()
                }
            }
            scanner.show(parentFragmentManager, "SalesScanner")
        } else {
            requestPermissions(arrayOf(android.Manifest.permission.CAMERA), 1001)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == 1001 && grantResults.isNotEmpty() && grantResults[0] == android.content.pm.PackageManager.PERMISSION_GRANTED) {
            openScanner()
        } else {
            Toast.makeText(context, "Camera permission is required to scan barcodes", Toast.LENGTH_SHORT).show()
        }
    }

    private fun addToCart(product: Product) {
        try {
            if (product.stockQuantity <= 0) {
                Toast.makeText(context, "Out of stock!", Toast.LENGTH_SHORT).show()
                return
            }
            val currentQty = cart[product] ?: 0
            cart[product] = currentQty + 1
            updateCartUI()
        } catch (e: Exception) {
            Toast.makeText(context, "Error: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
        }
    }

    private fun updateCartUI() {
        try {
            val totalItems = cart.values.sum()
            val totalPrice = cart.entries.sumOf { (it.key.price ?: 0.0) * it.value }

            tvCartItemsCount.text = "$totalItems Items"
            tvCartTotal.text = "₱${String.format("%.2f", totalPrice)}"
            btnCheckout.isEnabled = totalItems > 0
            btnChargeDebt.isEnabled = totalItems > 0
        } catch (e: Exception) {
            Log.e("SalesFragment", "UI Update failed", e)
        }
    }

    private fun performCheckout(status: String, customerId: Long? = null) {
        val context = context ?: return
        val vendorId = SessionManager(context).getUserId()
        
        try {
            val orderItems = cart.map { (product, qty) ->
                OrderItem(
                    productId = product.id ?: 0L,
                    quantity = qty,
                    priceAtSale = product.price ?: 0.0
                )
            }

            val totalPrice = cart.entries.sumOf { (it.key.price ?: 0.0) * it.value }
            val order = Order(
                totalAmount = totalPrice,
                vendorId = vendorId,
                status = status,
                customerId = customerId,
                items = orderItems
            )

            btnCheckout.isEnabled = false
            btnChargeDebt.isEnabled = false

            RetrofitClient.getTransactionService(context).placeOrder(order)
                .enqueue(object : Callback<Order> {
                    override fun onResponse(call: Call<Order>, response: Response<Order>) {
                        if (response.isSuccessful) {
                            Toast.makeText(context, "Sale Recorded: $status", Toast.LENGTH_LONG).show()
                            cart.clear()
                            updateCartUI()
                            fetchProducts()
                        } else {
                            Toast.makeText(context, "Error: ${response.errorBody()?.string()}", Toast.LENGTH_LONG).show()
                        }
                        btnCheckout.isEnabled = cart.isNotEmpty()
                        btnChargeDebt.isEnabled = cart.isNotEmpty()
                    }

                    override fun onFailure(call: Call<Order>, t: Throwable) {
                        Toast.makeText(context, "Network Error", Toast.LENGTH_SHORT).show()
                        btnCheckout.isEnabled = true
                        btnChargeDebt.isEnabled = true
                    }
                })
        } catch (e: Exception) {
            Log.e("SalesFragment", "Checkout failed", e)
            btnCheckout.isEnabled = true
            btnChargeDebt.isEnabled = true
        }
    }
}
