package edu.cit.tapales.saritrack.feature.pos

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import edu.cit.tapales.saritrack.core.api.RetrofitClient
import edu.cit.tapales.saritrack.feature.inventory.Product
import edu.cit.tapales.saritrack.feature.transaction.Order
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SalesViewModel : ViewModel() {

    private val _products = MutableLiveData<List<Product>>()
    val products: LiveData<List<Product>> = _products

    private val _cartManager = MutableLiveData<CartManager>(CartManager())
    val cartManager: LiveData<CartManager> = _cartManager

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun fetchProducts(vendorId: Long, context: android.content.Context) {
        _isLoading.value = true
        val sharedPrefs = context.getSharedPreferences("saritrack_product_cache", android.content.Context.MODE_PRIVATE)

        RetrofitClient.getProductService(context).getVendorProducts(vendorId)
            .enqueue(object : Callback<List<Product>> {
                override fun onResponse(call: Call<List<Product>>, response: Response<List<Product>>) {
                    _isLoading.value = false
                    if (response.isSuccessful) {
                        val productsList = response.body() ?: emptyList()
                        _products.value = productsList
                        
                        // 💾 Cache products list locally for offline use
                        try {
                            val jsonProducts = com.google.gson.Gson().toJson(productsList)
                            sharedPrefs.edit().putString("products_$vendorId", jsonProducts).apply()
                        } catch (e: Exception) {
                            android.util.Log.e("SalesViewModel", "Caching products failed", e)
                        }
                    } else {
                        loadCachedProducts(vendorId, sharedPrefs)
                    }
                }
                override fun onFailure(call: Call<List<Product>>, t: Throwable) {
                    _isLoading.value = false
                    loadCachedProducts(vendorId, sharedPrefs)
                }
            })
    }

    private fun loadCachedProducts(vendorId: Long, sharedPrefs: android.content.SharedPreferences) {
        try {
            val cachedJson = sharedPrefs.getString("products_$vendorId", null)
            if (cachedJson != null) {
                val type = object : com.google.gson.reflect.TypeToken<List<Product>>() {}.type
                val productsList: List<Product> = com.google.gson.Gson().fromJson(cachedJson, type)
                _products.value = productsList
                _error.value = "Viewing offline cached POS catalog"
            } else {
                _error.value = "POS Offline: No cached catalog available"
            }
        } catch (e: Exception) {
            _error.value = "Failed to load cached products catalog"
        }
    }

    fun addToCart(product: Product) {
        val currentManager = _cartManager.value ?: CartManager()
        if (currentManager.addItem(product)) {
            _cartManager.value = currentManager // Trigger UI update
        } else {
            _error.value = "Stock limit reached!"
        }
    }

    fun removeFromCart(product: Product) {
        val currentManager = _cartManager.value ?: CartManager()
        currentManager.removeItem(product)
        _cartManager.value = currentManager
    }

    fun clearCart() {
        val currentManager = _cartManager.value ?: CartManager()
        currentManager.clear()
        _cartManager.value = currentManager
    }

    fun clearError() {
        _error.value = null
    }
}
