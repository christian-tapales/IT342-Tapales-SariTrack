package edu.cit.tapales.saritrack.feature.inventory

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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProductsFragment : Fragment() {

    private var adapter: ProductAdapter? = null
    private var rvProducts: RecyclerView? = null
    private var tvProductCount: TextView? = null

    private val addProductLauncher = registerForActivityResult(androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            fetchProducts() // Refresh the list
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_products, container, false)
        
        rvProducts = view.findViewById(R.id.rvProducts)
        tvProductCount = view.findViewById(R.id.tvProductCount)
        
        rvProducts?.layoutManager = androidx.recyclerview.widget.GridLayoutManager(context, 2)
        adapter = ProductAdapter(emptyList()) { product ->
            val intent = android.content.Intent(context, AddProductActivity::class.java).apply {
                putExtra("PRODUCT_ID", product.id)
                putExtra("PRODUCT_NAME", product.name)
                putExtra("PRODUCT_PRICE", product.price)
                putExtra("PRODUCT_STOCK", product.stockQuantity)
                putExtra("PRODUCT_BARCODE", product.barcode)
                putExtra("PRODUCT_CATEGORY", product.category)
                putExtra("PRODUCT_IMAGE_URL", product.imageUrl)
            }
            addProductLauncher.launch(intent)
        }
        rvProducts?.adapter = adapter

        val fabAddProduct = view.findViewById<com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton>(R.id.fabAddProduct)
        fabAddProduct.setOnClickListener {
            val intent = android.content.Intent(context, AddProductActivity::class.java)
            addProductLauncher.launch(intent)
        }
        
        fetchProducts()
        
        return view
    }

    private fun fetchProducts() {
        val context = context ?: return
        val sessionManager = SessionManager(context)
        val vendorId = sessionManager.getUserId()
        
        if (vendorId == -1L) return

        val sharedPrefs = context.getSharedPreferences("saritrack_product_cache", android.content.Context.MODE_PRIVATE)

        RetrofitClient.getProductService(context).getVendorProducts(vendorId)
            .enqueue(object : Callback<List<Product>> {
                override fun onResponse(call: Call<List<Product>>, response: Response<List<Product>>) {
                    if (!isAdded || activity == null) return

                    if (response.isSuccessful) {
                        val products = response.body() ?: emptyList()
                        adapter?.updateProducts(products)
                        tvProductCount?.text = "${products.size} Products"

                        // 💾 Cache products list locally for offline use
                        try {
                            val jsonProducts = com.google.gson.Gson().toJson(products)
                            sharedPrefs.edit().putString("products_$vendorId", jsonProducts).apply()
                        } catch (e: Exception) {
                            android.util.Log.e("ProductsFragment", "Caching products failed", e)
                        }
                    } else {
                        loadCachedProducts(vendorId, sharedPrefs)
                    }
                }

                override fun onFailure(call: Call<List<Product>>, t: Throwable) {
                    if (!isAdded || activity == null) return
                    loadCachedProducts(vendorId, sharedPrefs)
                }
            })
    }

    private fun loadCachedProducts(vendorId: Long, sharedPrefs: android.content.SharedPreferences) {
        val context = context ?: return
        try {
            val cachedJson = sharedPrefs.getString("products_$vendorId", null)
            if (cachedJson != null) {
                val type = object : com.google.gson.reflect.TypeToken<List<Product>>() {}.type
                val products: List<Product> = com.google.gson.Gson().fromJson(cachedJson, type)
                adapter?.updateProducts(products)
                tvProductCount?.text = "${products.size} Products"
                Toast.makeText(context, "Viewing offline cached products", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(context, "Products offline: No cached data available", Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Failed to load cached products", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        rvProducts = null
        tvProductCount = null
        adapter = null
    }
}
