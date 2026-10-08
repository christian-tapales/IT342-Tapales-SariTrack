package edu.cit.tapales.saritrack.feature.transaction

data class ReceiptReportData(
    val orderId: Long,
    val storeName: String,
    val totalAmount: Double,
    val paymentStatus: String,
    val dateString: String,
    val items: List<ReceiptItemLine>
)

data class ReceiptItemLine(
    val quantity: Int,
    val name: String,
    val unitPrice: Double,
    val lineTotal: Double
)

data class StatementReportData(
    val customerName: String,
    val storeName: String,
    val dateString: String,
    val currentDebt: Double,
    val entries: List<StatementEntryLine>
)

data class StatementEntryLine(
    val dateStr: String,
    val type: String, // "DEBT" or "PAYMENT"
    val description: String,
    val amount: Double
)
