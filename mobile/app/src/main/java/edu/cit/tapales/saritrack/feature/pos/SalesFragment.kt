package edu.cit.tapales.saritrack.feature.pos

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
    private lateinit var btnDigitalPay: MaterialButton

    private var allProducts = listOf<Product>()
    private val cartManager = CartManager()
    private val productHelper = ProductHelper()

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
        btnDigitalPay = view.findViewById(R.id.btnDigitalPay)
        val btnOpenScanner = view.findViewById<MaterialButton>(R.id.btnOpenScanner)
        val btnFilter = view.findViewById<MaterialButton>(R.id.btnFilter)

        rvProducts.layoutManager = androidx.recyclerview.widget.GridLayoutManager(context, 2)
        adapter = ProductAdapter(emptyList()) { product ->
            addToCart(product)
        }
        rvProducts.adapter = adapter

        fetchProducts()

        etSearch.addTextChangedListener(object : android.text.TextWatcher {
            override fun afterTextChanged(s: android.text.Editable?) { filterProducts(s.toString()) }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        btnOpenScanner.setOnClickListener { openScanner() }
        btnCheckout.setOnClickListener { performCheckout("PAID") }
        btnChargeDebt.setOnClickListener { openCustomerPicker() }
        btnFilter.setOnClickListener { showFilterMenu(it) }
        
        btnDigitalPay.setOnClickListener {
            try {
                startDigitalPayment()
            } catch (e: Exception) {
                handlePaymentError("Crash caught: ${e.message}")
            }
        }

        view.findViewById<View>(R.id.cartSummaryCard).setOnClickListener {
            try {
                if (!cartManager.isEmpty()) {
                    val cartSheet = CartBottomSheet(cartManager.getItems() as MutableMap<Product, Int>) {
                        updateCartUI()
                    }
                    cartSheet.show(parentFragmentManager, "CartSheet")
                }
            } catch (e: Exception) {
                Log.e("SalesFragment", "Cart click error", e)
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
                    allProducts = productHelper.sortByName(allProducts)
                }
                "Price: Low to High" -> {
                    allProducts = productHelper.sortByPriceLowToHigh(allProducts)
                }
                "Price: High to Low" -> {
                    allProducts = productHelper.sortByPriceHighToLow(allProducts)
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
        val filtered = productHelper.filter(allProducts, query)
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
            if (cartManager.addItem(product)) {
                updateCartUI()
            } else {
                Toast.makeText(context, "Stock Limit Reached!", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Error: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
        }
    }

    private fun updateCartUI() {
        try {
            val totalItems = cartManager.getTotalItems()
            val totalPrice = cartManager.getTotalPrice()

            tvCartItemsCount.text = "$totalItems Items"
            tvCartTotal.text = "₱${String.format("%.2f", totalPrice)}"
            btnCheckout.isEnabled = totalItems > 0
            btnChargeDebt.isEnabled = totalItems > 0
            btnDigitalPay.isEnabled = totalItems > 0
        } catch (e: Exception) {
            Log.e("SalesFragment", "UI Update failed", e)
        }
    }

    private fun startDigitalPayment() {
        val context = context ?: return
        val vendorId = SessionManager(context).getUserId()
        val totalAmount = cartManager.getTotalPrice()

        if (cartManager.isEmpty()) {
            Toast.makeText(context, "Cart is empty", Toast.LENGTH_SHORT).show()
            return
        }

        // 1. Create Pending Order
        val orderItems = cartManager.getItems().map { (product, qty) ->
            OrderItem(productId = product.id ?: 0L, quantity = qty, priceAtSale = product.price ?: 0.0)
        }
        val order = Order(totalAmount = totalAmount, vendorId = vendorId, status = "PENDING", items = orderItems)

        btnDigitalPay.isEnabled = false
        btnDigitalPay.text = "Wait..."

        RetrofitClient.getTransactionService(context).placeOrder(order)
            .enqueue(object : Callback<Order> {
                override fun onResponse(call: Call<Order>, response: Response<Order>) {
                    try {
                        if (response.isSuccessful && response.body() != null) {
                            val savedOrder = response.body()!!
                            val orderId = savedOrder.id ?: throw Exception("Missing Order ID")

                            // 2. Create PayMongo Session
                            val payload = PaymentRequest(
                                amount = totalAmount,
                                orderId = orderId,
                                description = "SariTrack Purchase - Order #$orderId"
                            )
                            
                            RetrofitClient.getPaymentService(context).createCheckoutSession(payload)
                                .enqueue(object : Callback<PaymentResponse> {
                                    override fun onResponse(call: Call<PaymentResponse>, res: Response<PaymentResponse>) {
                                        btnDigitalPay.text = "Digital"
                                        btnDigitalPay.isEnabled = true
                                        
                                        if (res.isSuccessful && res.body() != null) {
                                            val checkoutUrl = res.body()?.checkout_url
                                            if (checkoutUrl != null) {
                                                val intent = android.content.Intent(context, PaymentActivity::class.java)
                                                intent.putExtra("CHECKOUT_URL", checkoutUrl)
                                                startActivityForResult(intent, 2002)
                                            } else {
                                                handlePaymentError("No URL received")
                                            }
                                        } else {
                                            handlePaymentError("Payment session failed")
                                        }
                                    }
                                    override fun onFailure(call: Call<PaymentResponse>, t: Throwable) {
                                        handlePaymentError("Network Error (Payment)")
                                    }
                                })
                        } else {
                            handlePaymentError("Failed to create order")
                        }
                    } catch (e: Exception) {
                        handlePaymentError("Inner Error: ${e.message}")
                    }
                }
                override fun onFailure(call: Call<Order>, t: Throwable) {
                    handlePaymentError("Network Error (Order)")
                }
            })
    }

    private fun handlePaymentError(message: String) {
        btnDigitalPay.text = "Digital"
        btnDigitalPay.isEnabled = true
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        Log.e("SalesFragment", message)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: android.content.Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 2002) {
            if (resultCode == android.app.Activity.RESULT_OK) {
                // Success screen for Digital is slightly different as cart is already cleared in some logic
                // For now, let's just clear and show success toast or simple screen
                Toast.makeText(context, "Digital Payment Success!", Toast.LENGTH_LONG).show()
                cartManager.clear()
                updateCartUI()
                fetchProducts()
            } else {
                Toast.makeText(context, "Payment was not completed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun performCheckout(status: String, customerId: Long? = null) {
        val context = context ?: return
        val vendorId = SessionManager(context).getUserId()
        
        try {
            val orderItems = cartManager.getItems().map { (product, qty) ->
                OrderItem(
                    productId = product.id ?: 0L,
                    quantity = qty,
                    priceAtSale = product.price ?: 0.0
                )
            }

            val totalPrice = cartManager.getTotalPrice()
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
                        if (response.isSuccessful && response.body() != null) {
                            showSuccessScreen(response.body()!!, status)
                            cartManager.clear()
                            updateCartUI()
                            fetchProducts()
                        } else {
                            Toast.makeText(context, "Error: ${response.errorBody()?.string()}", Toast.LENGTH_LONG).show()
                        }
                        btnCheckout.isEnabled = !cartManager.isEmpty()
                        btnChargeDebt.isEnabled = !cartManager.isEmpty()
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

    private fun showSuccessScreen(order: Order, status: String) {
        val context = context ?: return
        val intent = android.content.Intent(context, SaleSuccessActivity::class.java)
        intent.putExtra("ORDER_ID", order.id)
        intent.putExtra("TOTAL", order.totalAmount)
        intent.putExtra("STATUS", status)
        
        val itemsSummary = cartManager.getItems().entries.joinToString("\n") { (p, qty) ->
            "$qty x ${p.name} (₱${p.price})"
        }
        intent.putExtra("ITEMS", itemsSummary)
        startActivity(intent)
    }
}
