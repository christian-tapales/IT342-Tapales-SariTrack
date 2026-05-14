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
        RetrofitClient.getProductService(context).getVendorProducts(vendorId)
            .enqueue(object : Callback<List<Product>> {
                override fun onResponse(call: Call<List<Product>>, response: Response<List<Product>>) {
                    _isLoading.value = false
                    if (response.isSuccessful) {
                        _products.value = response.body() ?: emptyList()
                    } else {
                        _error.value = "Failed to load products"
                    }
                }
                override fun onFailure(call: Call<List<Product>>, t: Throwable) {
                    _isLoading.value = false
                    _error.value = "Network error: ${t.message}"
                }
            })
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
