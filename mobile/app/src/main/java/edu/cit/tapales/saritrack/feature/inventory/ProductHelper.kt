package edu.cit.tapales.saritrack.feature.inventory

class ProductHelper {
    fun filter(products: List<Product>, query: String): List<Product> {
        if (query.isEmpty()) return products
        return products.filter { 
            (it.name?.contains(query, ignoreCase = true) == true) || 
            (it.barcode?.contains(query) == true) 
        }
    }

    fun sortByName(products: List<Product>): List<Product> {
        return products.sortedBy { it.name }
    }

    fun sortByPriceLowToHigh(products: List<Product>): List<Product> {
        return products.sortedBy { it.price }
    }

    fun sortByPriceHighToLow(products: List<Product>): List<Product> {
        return products.sortedByDescending { it.price }
    }
}
