package edu.cit.tapales.saritrack.feature.pos

import edu.cit.tapales.saritrack.feature.inventory.Product

class CartManager {
    private val cart = mutableMapOf<Product, Int>()

    fun addItem(product: Product): Boolean {
        if (product.stockQuantity <= 0) return false
        
        val currentQty = cart[product] ?: 0
        if (currentQty >= product.stockQuantity) return false // Cannot exceed stock
        
        cart[product] = currentQty + 1
        return true
    }

    fun removeItem(product: Product) {
        val currentQty = cart[product] ?: 0
        if (currentQty > 1) {
            cart[product] = currentQty - 1
        } else {
            cart.remove(product)
        }
    }

    fun getItems(): Map<Product, Int> = cart.toMap()

    fun getTotalPrice(): Double {
        return cart.entries.sumOf { (product, quantity) ->
            (product.price ?: 0.0) * quantity
        }
    }

    fun getTotalItems(): Int {
        return cart.values.sum()
    }

    fun clear() {
        cart.clear()
    }

    fun isEmpty(): Boolean = cart.isEmpty()
}
