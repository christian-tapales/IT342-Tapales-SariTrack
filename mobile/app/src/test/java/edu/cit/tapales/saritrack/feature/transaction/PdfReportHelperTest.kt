package edu.cit.tapales.saritrack.feature.transaction

import edu.cit.tapales.saritrack.feature.customer.DebtPayment
import edu.cit.tapales.saritrack.feature.inventory.Product
import org.junit.Assert.*
import org.junit.Test
import java.util.*

class PdfReportHelperTest {

    @Test
    fun `parseItemsSummary correctly parses items with quantities and prices`() {
        val summary = """
            2x San Miguel Beer (₱45.00)
            1x Lucky Me Pancit Canton (₱15.00)
        """.trimIndent()

        val parsed = PdfReportHelper.parseItemsSummary(summary)

        assertEquals(2, parsed.size)
        assertEquals(2, parsed[0].quantity)
        assertEquals("San Miguel Beer", parsed[0].name)
        assertEquals(45.0, parsed[0].unitPrice, 0.001)
        assertEquals(90.0, parsed[0].lineTotal, 0.001)

        assertEquals(1, parsed[1].quantity)
        assertEquals("Lucky Me Pancit Canton", parsed[1].name)
        assertEquals(15.0, parsed[1].unitPrice, 0.001)
        assertEquals(15.0, parsed[1].lineTotal, 0.001)
    }

    @Test
    fun `parseItemsSummary handles items without prices gracefully`() {
        val summary = "3x Coca Cola 1.5L\n1x Gardenia Bread"
        val parsed = PdfReportHelper.parseItemsSummary(summary)

        assertEquals(2, parsed.size)
        assertEquals(3, parsed[0].quantity)
        assertEquals("Coca Cola 1.5L", parsed[0].name)
        assertEquals(0.0, parsed[0].unitPrice, 0.0)
        assertEquals(0.0, parsed[0].lineTotal, 0.0)

        assertEquals(1, parsed[1].quantity)
        assertEquals("Gardenia Bread", parsed[1].name)
    }

    @Test
    fun `parseItemsSummary handles empty or blank input`() {
        assertTrue(PdfReportHelper.parseItemsSummary(null).isEmpty())
        assertTrue(PdfReportHelper.parseItemsSummary("").isEmpty())
        assertTrue(PdfReportHelper.parseItemsSummary("   \n  ").isEmpty())
    }

    @Test
    fun `formatPaymentStatus maps all supported statuses correctly`() {
        assertEquals("UTANG / CREDIT", PdfReportHelper.formatPaymentStatus("DEBT"))
        assertEquals("UTANG / CREDIT", PdfReportHelper.formatPaymentStatus("debt"))
        assertEquals("DIGITAL PAYMENT", PdfReportHelper.formatPaymentStatus("DIGITAL"))
        assertEquals("CANCELLED / VOIDED", PdfReportHelper.formatPaymentStatus("CANCELLED"))
        assertEquals("CASH PAYMENT", PdfReportHelper.formatPaymentStatus("PAID"))
        assertEquals("CASH PAYMENT", PdfReportHelper.formatPaymentStatus(null))
        assertEquals("CASH PAYMENT", PdfReportHelper.formatPaymentStatus("UNKNOWN"))
    }

    @Test
    fun `formatCurrency formats Philippine peso currency correctly`() {
        val formattedZero = PdfReportHelper.formatCurrency(0.0)
        assertTrue(formattedZero.contains("0.00"))

        val formattedAmount = PdfReportHelper.formatCurrency(1250.50)
        assertTrue(formattedAmount.contains("1,250.50"))

        val formattedLarge = PdfReportHelper.formatCurrency(1000000.0)
        assertTrue(formattedLarge.contains("1,000,000.00"))
    }

    @Test
    fun `sanitizeFilename removes invalid characters for filesystem safety`() {
        assertEquals("Juan_Dela_Cruz", PdfReportHelper.sanitizeFilename("Juan Dela Cruz"))
        assertEquals("Store_Co_Receipt", PdfReportHelper.sanitizeFilename("Store & Co. #Receipt!"))
        assertEquals("order-101", PdfReportHelper.sanitizeFilename("order-101"))
        assertEquals("order_101", PdfReportHelper.sanitizeFilename("order 101"))
    }

    @Test
    fun `buildStatementEntries converts order and payment history items correctly`() {
        val date = Date()
        val product = Product(
            id = 1L,
            vendorId = 1L,
            name = "Canned Sardines",
            barcode = "12345678",
            price = 25.0,
            stockQuantity = 10,
            category = "Canned Goods",
            imageUrl = null
        )
        val order = Order(
            id = 55L,
            totalAmount = 50.0,
            vendorId = 1L,
            items = listOf(OrderItem(productId = 1L, quantity = 2, priceAtSale = 25.0, product = product))
        )
        val payment = DebtPayment(
            id = 12L,
            customerId = 7L,
            amount = 30.0,
            timestamp = "2026-10-08T10:00:00"
        )

        val history = listOf(
            HistoryItem.OrderType(order, date),
            HistoryItem.PaymentType(payment, date)
        )

        val entries = PdfReportHelper.buildStatementEntries(history)

        assertEquals(2, entries.size)

        val debtEntry = entries.first { it.type == "DEBT" }
        assertEquals(50.0, debtEntry.amount, 0.001)
        assertTrue(debtEntry.description.contains("Order #55"))
        assertTrue(debtEntry.description.contains("Canned Sardines"))

        val paymentEntry = entries.first { it.type == "PAYMENT" }
        assertEquals(30.0, paymentEntry.amount, 0.001)
        assertTrue(paymentEntry.description.contains("Payment Received"))
    }

    @Test
    fun `calculateStatementTotals computes debt paid and net balance correctly`() {
        val entries = listOf(
            StatementEntryLine(dateStr = "Oct 01", type = "DEBT", description = "Order 1", amount = 100.0),
            StatementEntryLine(dateStr = "Oct 02", type = "DEBT", description = "Order 2", amount = 150.0),
            StatementEntryLine(dateStr = "Oct 03", type = "PAYMENT", description = "Bayad 1", amount = 70.0),
            StatementEntryLine(dateStr = "Oct 04", type = "PAYMENT", description = "Bayad 2", amount = 80.0)
        )

        val (totalDebt, totalPaid, netBalance) = PdfReportHelper.calculateStatementTotals(entries)

        assertEquals(250.0, totalDebt, 0.001)
        assertEquals(150.0, totalPaid, 0.001)
        assertEquals(100.0, netBalance, 0.001)
    }

    @Test
    fun `receipt and statement report data models retain expected properties`() {
        val receiptData = ReceiptReportData(
            orderId = 999L,
            storeName = "Aling Nena Store",
            totalAmount = 250.0,
            paymentStatus = "CASH PAYMENT",
            dateString = "Oct 08, 2026",
            items = listOf(ReceiptItemLine(quantity = 2, name = "Soap", unitPrice = 25.0, lineTotal = 50.0))
        )

        assertEquals(999L, receiptData.orderId)
        assertEquals("Aling Nena Store", receiptData.storeName)
        assertEquals(250.0, receiptData.totalAmount, 0.0)
        assertEquals(1, receiptData.items.size)

        val statementData = StatementReportData(
            customerName = "Maria Clara",
            storeName = "Aling Nena Store",
            dateString = "Oct 08, 2026",
            currentDebt = 350.0,
            entries = emptyList()
        )

        assertEquals("Maria Clara", statementData.customerName)
        assertEquals(350.0, statementData.currentDebt, 0.0)
        assertTrue(statementData.entries.isEmpty())
    }
}
