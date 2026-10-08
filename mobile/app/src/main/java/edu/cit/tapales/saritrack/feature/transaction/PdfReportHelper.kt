package edu.cit.tapales.saritrack.feature.transaction

import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

object PdfReportHelper {

    private val currencyFormat = DecimalFormat("₱#,##0.00")

    fun formatCurrency(amount: Double): String {
        return currencyFormat.format(amount)
    }

    fun formatPaymentStatus(status: String?): String {
        return when (status?.uppercase()?.trim()) {
            "DEBT" -> "UTANG / CREDIT"
            "DIGITAL" -> "DIGITAL PAYMENT"
            "CANCELLED" -> "CANCELLED / VOIDED"
            else -> "CASH PAYMENT"
        }
    }

    fun sanitizeFilename(name: String): String {
        return name.replace(Regex("[^a-zA-Z0-9_-]+"), "_")
            .trim('_')
            .ifEmpty { "document" }
    }

    /**
     * Parses an items summary string which might look like:
     * "2x Coca Cola (₱35.00)\n1x Bread (₱10.00)"
     * or "3x Item Name\n1x Another Item"
     * or plain lines.
     */
    fun parseItemsSummary(summary: String?): List<ReceiptItemLine> {
        if (summary.isNullOrBlank()) return emptyList()

        val lines = summary.lines().map { it.trim() }.filter { it.isNotEmpty() }
        val result = mutableListOf<ReceiptItemLine>()

        val regexWithPrice = Regex("""^(\d+)x\s+(.+?)(?:\s+\(?[₱P]?\s*([0-9,.]+)\)?)?$""")

        for (line in lines) {
            val match = regexWithPrice.find(line)
            if (match != null) {
                val qty = match.groupValues[1].toIntOrNull() ?: 1
                val name = match.groupValues[2].trim()
                val priceStr = match.groupValues.getOrNull(3)?.replace(",", "")
                val price = priceStr?.toDoubleOrNull() ?: 0.0
                val total = if (price > 0.0) price * qty else 0.0
                result.add(ReceiptItemLine(quantity = qty, name = name, unitPrice = price, lineTotal = total))
            } else {
                result.add(ReceiptItemLine(quantity = 1, name = line, unitPrice = 0.0, lineTotal = 0.0))
            }
        }

        return result
    }

    fun buildStatementEntries(
        historyItems: List<HistoryItem>,
        dateFormat: String = "MMM dd, yyyy"
    ): List<StatementEntryLine> {
        val sdf = SimpleDateFormat(dateFormat, Locale.getDefault())
        return historyItems.sortedByDescending { it.date }.map { item ->
            when (item) {
                is HistoryItem.OrderType -> {
                    val order = item.order
                    val itemNames = order.items.joinToString { it.product?.name ?: "Item" }
                    val desc = if (itemNames.isNotBlank()) "Order #${order.id ?: ""}: $itemNames" else "Order #${order.id ?: ""}"
                    StatementEntryLine(
                        dateStr = sdf.format(item.date),
                        type = "DEBT",
                        description = desc,
                        amount = order.totalAmount
                    )
                }
                is HistoryItem.PaymentType -> {
                    StatementEntryLine(
                        dateStr = sdf.format(item.date),
                        type = "PAYMENT",
                        description = "Payment Received (Bayad)",
                        amount = item.payment.amount
                    )
                }
            }
        }
    }

    fun calculateStatementTotals(entries: List<StatementEntryLine>): Triple<Double, Double, Double> {
        var totalDebt = 0.0
        var totalPaid = 0.0
        for (entry in entries) {
            if (entry.type == "DEBT") {
                totalDebt += entry.amount
            } else if (entry.type == "PAYMENT") {
                totalPaid += entry.amount
            }
        }
        val netBalance = totalDebt - totalPaid
        return Triple(totalDebt, totalPaid, netBalance)
    }
}
