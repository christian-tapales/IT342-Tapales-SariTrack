package edu.cit.tapales.saritrack

data class Order(
    val id: Long? = null,
    val totalAmount: Double,
    val vendorId: Long,
    val timestamp: String? = null,
    val status: String = "PAID",
    val customerId: Long? = null,
    val items: List<OrderItem>
)

data class OrderItem(
    val id: Long? = null,
    val productId: Long,
    val quantity: Int,
    val priceAtSale: Double,
    val product: Product? = null
)
