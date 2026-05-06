package edu.cit.tapales.saritrack

data class Product(
    val id: Long,
    val vendorId: Long,
    val name: String,
    val barcode: String,
    val price: Double,
    val stockQuantity: Int,
    val category: String,
    val imageUrl: String?
)
