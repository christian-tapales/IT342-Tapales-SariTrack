package edu.cit.tapales.saritrack

data class Customer(
    val id: Long? = null,
    val vendorId: Long,
    val fullName: String,
    val email: String?,
    val currentDebt: Double = 0.0,
    val status: String = "Unpaid",
    val lastUpdate: String? = null
)

data class DebtPayment(
    val id: Long? = null,
    val customerId: Long,
    val amount: Double,
    val timestamp: String? = null
)
