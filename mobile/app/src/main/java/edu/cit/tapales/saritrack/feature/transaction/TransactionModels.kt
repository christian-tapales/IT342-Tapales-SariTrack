package edu.cit.tapales.saritrack.feature.transaction

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
