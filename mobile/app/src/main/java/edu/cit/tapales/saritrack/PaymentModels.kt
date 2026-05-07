package edu.cit.tapales.saritrack

data class PaymentRequest(
    val amount: Double,
    val orderId: Long,
    val description: String
)

data class PaymentResponse(
    val checkout_url: String? = null
)
