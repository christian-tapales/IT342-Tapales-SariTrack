package edu.cit.tapales.saritrack.feature.transaction

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.pdf.PdfDocument
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

object PdfDocumentGenerator {

    /**
     * Generates a sleek, receipt-sized PDF document for an order.
     */
    fun generateReceiptPdf(context: Context, data: ReceiptReportData): File {
        val pageHeight = maxOf(600, 380 + (data.items.size * 28))
        val pageWidth = 380

        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas: Canvas = page.canvas

        // Background
        canvas.drawColor(Color.WHITE)

        val paint = Paint().apply { isAntiAlias = true }

        // Top Accent Bar (Teal #16A394)
        paint.color = Color.parseColor("#16A394")
        paint.style = Paint.Style.FILL
        canvas.drawRect(0f, 0f, pageWidth.toFloat(), 12f, paint)

        var y = 45f

        // Brand Title
        paint.color = Color.parseColor("#0F172A") // Slate 900
        paint.textSize = 20f
        paint.isFakeBoldText = true
        paint.textAlign = Paint.Align.CENTER
        canvas.drawText("SARITRACK", (pageWidth / 2).toFloat(), y, paint)

        // Store Name
        y += 20f
        paint.textSize = 12f
        paint.color = Color.parseColor("#475569") // Slate 600
        paint.isFakeBoldText = false
        canvas.drawText(data.storeName.ifBlank { "Sari-Sari Retail Store" }, (pageWidth / 2).toFloat(), y, paint)

        // Official Receipt Subtitle
        y += 16f
        paint.textSize = 10f
        paint.color = Color.parseColor("#64748B") // Slate 500
        canvas.drawText("OFFICIAL SALES RECEIPT", (pageWidth / 2).toFloat(), y, paint)

        // Divider
        y += 18f
        drawDivider(canvas, 20f, y, (pageWidth - 20).toFloat(), Color.parseColor("#CBD5E1"))

        // Order Meta
        y += 24f
        paint.textAlign = Paint.Align.LEFT
        paint.textSize = 11f
        paint.color = Color.parseColor("#334155")
        canvas.drawText("Order ID: #${data.orderId}", 24f, y, paint)

        paint.textAlign = Paint.Align.RIGHT
        paint.textSize = 10f
        paint.color = Color.parseColor("#64748B")
        canvas.drawText(data.dateString, (pageWidth - 24).toFloat(), y, paint)

        y += 18f
        paint.textAlign = Paint.Align.LEFT
        paint.textSize = 11f
        paint.color = Color.parseColor("#334155")
        canvas.drawText("Payment Method:", 24f, y, paint)

        paint.textAlign = Paint.Align.RIGHT
        paint.isFakeBoldText = true
        val statusColor = when (data.paymentStatus) {
            "UTANG / CREDIT" -> Color.parseColor("#EF4444")
            "DIGITAL PAYMENT" -> Color.parseColor("#0284C7")
            else -> Color.parseColor("#10B981")
        }
        paint.color = statusColor
        canvas.drawText(data.paymentStatus, (pageWidth - 24).toFloat(), y, paint)
        paint.isFakeBoldText = false

        // Divider
        y += 16f
        drawDivider(canvas, 20f, y, (pageWidth - 20).toFloat(), Color.parseColor("#CBD5E1"))

        // Table Header
        y += 20f
        paint.textSize = 10f
        paint.isFakeBoldText = true
        paint.color = Color.parseColor("#475569")

        paint.textAlign = Paint.Align.LEFT
        canvas.drawText("ITEM", 24f, y, paint)

        paint.textAlign = Paint.Align.CENTER
        canvas.drawText("QTY", 230f, y, paint)

        paint.textAlign = Paint.Align.RIGHT
        canvas.drawText("TOTAL", (pageWidth - 24).toFloat(), y, paint)

        y += 10f
        drawDivider(canvas, 20f, y, (pageWidth - 20).toFloat(), Color.parseColor("#E2E8F0"))

        // Table Rows
        paint.isFakeBoldText = false
        paint.textSize = 10f

        for (item in data.items) {
            y += 22f
            paint.textAlign = Paint.Align.LEFT
            paint.color = Color.parseColor("#1E293B")
            // Truncate long item names to fit receipt width
            val displayName = if (item.name.length > 24) item.name.substring(0, 22) + ".." else item.name
            canvas.drawText(displayName, 24f, y, paint)

            paint.textAlign = Paint.Align.CENTER
            paint.color = Color.parseColor("#475569")
            canvas.drawText("${item.quantity}x", 230f, y, paint)

            paint.textAlign = Paint.Align.RIGHT
            paint.color = Color.parseColor("#1E293B")
            val amountStr = if (item.lineTotal > 0.0) {
                PdfReportHelper.formatCurrency(item.lineTotal)
            } else if (item.unitPrice > 0.0) {
                PdfReportHelper.formatCurrency(item.unitPrice * item.quantity)
            } else {
                "-"
            }
            canvas.drawText(amountStr, (pageWidth - 24).toFloat(), y, paint)
        }

        // Summary Divider
        y += 18f
        drawDivider(canvas, 20f, y, (pageWidth - 20).toFloat(), Color.parseColor("#94A3B8"), strokeWidth = 1.5f)

        // Total Row
        y += 28f
        paint.textAlign = Paint.Align.LEFT
        paint.textSize = 13f
        paint.isFakeBoldText = true
        paint.color = Color.parseColor("#0F172A")
        canvas.drawText("TOTAL AMOUNT", 24f, y, paint)

        paint.textAlign = Paint.Align.RIGHT
        paint.textSize = 17f
        paint.color = Color.parseColor("#10B981") // Green
        canvas.drawText(PdfReportHelper.formatCurrency(data.totalAmount), (pageWidth - 24).toFloat(), y, paint)

        // Bottom Divider
        y += 20f
        drawDivider(canvas, 20f, y, (pageWidth - 20).toFloat(), Color.parseColor("#E2E8F0"))

        // Footer Thank You & SaaS Watermark
        y += 24f
        paint.textAlign = Paint.Align.CENTER
        paint.textSize = 10f
        paint.isFakeBoldText = true
        paint.color = Color.parseColor("#334155")
        canvas.drawText("Thank you for your purchase!", (pageWidth / 2).toFloat(), y, paint)

        y += 16f
        paint.textSize = 8f
        paint.isFakeBoldText = false
        paint.color = Color.parseColor("#94A3B8")
        canvas.drawText("Powered by SariTrack Retail Cloud POS", (pageWidth / 2).toFloat(), y, paint)

        pdfDocument.finishPage(page)

        val outputFile = File(context.cacheDir, "receipt_order_${data.orderId}.pdf")
        FileOutputStream(outputFile).use { out ->
            pdfDocument.writeTo(out)
        }
        pdfDocument.close()

        return outputFile
    }

    /**
     * Generates an official A4 Statement of Account / Utang Ledger PDF for a customer.
     */
    fun generateStatementPdf(context: Context, data: StatementReportData): File {
        val pageWidth = 595 // Standard A4 points
        val pageHeight = maxOf(842, 450 + (data.entries.size * 28))

        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas: Canvas = page.canvas

        canvas.drawColor(Color.WHITE)

        val paint = Paint().apply { isAntiAlias = true }

        // Top Accent Band
        paint.color = Color.parseColor("#16A394")
        paint.style = Paint.Style.FILL
        canvas.drawRect(0f, 0f, pageWidth.toFloat(), 18f, paint)

        var y = 55f

        // Document Title & Brand Header
        paint.color = Color.parseColor("#0F172A")
        paint.textSize = 22f
        paint.isFakeBoldText = true
        paint.textAlign = Paint.Align.LEFT
        canvas.drawText("SARITRACK", 40f, y, paint)

        paint.textAlign = Paint.Align.RIGHT
        paint.textSize = 12f
        paint.color = Color.parseColor("#16A394")
        canvas.drawText("STATEMENT OF ACCOUNT", (pageWidth - 40).toFloat(), y, paint)

        y += 20f
        paint.textAlign = Paint.Align.LEFT
        paint.textSize = 12f
        paint.color = Color.parseColor("#475569")
        paint.isFakeBoldText = false
        canvas.drawText("Store: ${data.storeName.ifBlank { "SariTrack Retail Store" }}", 40f, y, paint)

        paint.textAlign = Paint.Align.RIGHT
        paint.textSize = 10f
        paint.color = Color.parseColor("#64748B")
        canvas.drawText("Date: ${data.dateString}", (pageWidth - 40).toFloat(), y, paint)

        y += 18f
        drawDivider(canvas, 40f, y, (pageWidth - 40).toFloat(), Color.parseColor("#CBD5E1"), strokeWidth = 1.5f)

        // Customer Summary Box
        y += 25f
        val boxTop = y
        val boxHeight = 70f
        val cardRect = RectF(40f, boxTop, (pageWidth - 40).toFloat(), boxTop + boxHeight)
        paint.color = Color.parseColor("#F8FAFC")
        paint.style = Paint.Style.FILL
        canvas.drawRoundRect(cardRect, 12f, 12f, paint)

        paint.color = Color.parseColor("#E2E8F0")
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 1f
        canvas.drawRoundRect(cardRect, 12f, 12f, paint)

        // Customer Details Inside Box
        paint.style = Paint.Style.FILL
        paint.textAlign = Paint.Align.LEFT
        paint.textSize = 11f
        paint.color = Color.parseColor("#64748B")
        canvas.drawText("CUSTOMER NAME", 58f, boxTop + 26f, paint)

        paint.textSize = 16f
        paint.isFakeBoldText = true
        paint.color = Color.parseColor("#0F172A")
        canvas.drawText(data.customerName, 58f, boxTop + 50f, paint)

        paint.textAlign = Paint.Align.RIGHT
        paint.textSize = 11f
        paint.isFakeBoldText = false
        paint.color = Color.parseColor("#64748B")
        canvas.drawText("TOTAL OUTSTANDING DEBT", (pageWidth - 58).toFloat(), boxTop + 26f, paint)

        paint.textSize = 18f
        paint.isFakeBoldText = true
        paint.color = Color.parseColor("#EF4444") // Red
        canvas.drawText(PdfReportHelper.formatCurrency(data.currentDebt), (pageWidth - 58).toFloat(), boxTop + 50f, paint)

        y = boxTop + boxHeight + 35f

        // Ledger History Table Header
        paint.textAlign = Paint.Align.LEFT
        paint.textSize = 12f
        paint.isFakeBoldText = true
        paint.color = Color.parseColor("#1E293B")
        canvas.drawText("TRANSACTION & PAYMENT HISTORY", 40f, y, paint)

        y += 18f
        // Header background strip
        val headerStripRect = RectF(40f, y - 14f, (pageWidth - 40).toFloat(), y + 12f)
        paint.color = Color.parseColor("#F1F5F9")
        paint.style = Paint.Style.FILL
        canvas.drawRect(headerStripRect, paint)

        paint.textSize = 10f
        paint.isFakeBoldText = true
        paint.color = Color.parseColor("#475569")

        paint.textAlign = Paint.Align.LEFT
        canvas.drawText("DATE", 50f, y, paint)
        canvas.drawText("TYPE", 140f, y, paint)
        canvas.drawText("DESCRIPTION", 220f, y, paint)

        paint.textAlign = Paint.Align.RIGHT
        canvas.drawText("AMOUNT", (pageWidth - 50).toFloat(), y, paint)

        y += 16f
        drawDivider(canvas, 40f, y, (pageWidth - 40).toFloat(), Color.parseColor("#CBD5E1"))

        // Ledger Rows
        paint.isFakeBoldText = false
        paint.textSize = 10f

        val (totalDebt, totalPaid, _) = PdfReportHelper.calculateStatementTotals(data.entries)

        for (entry in data.entries) {
            y += 24f
            paint.textAlign = Paint.Align.LEFT
            paint.color = Color.parseColor("#334155")
            canvas.drawText(entry.dateStr, 50f, y, paint)

            val isDebt = entry.type == "DEBT"
            paint.isFakeBoldText = true
            paint.color = if (isDebt) Color.parseColor("#DC2626") else Color.parseColor("#059669")
            canvas.drawText(if (isDebt) "UTANG" else "BAYAD", 140f, y, paint)

            paint.isFakeBoldText = false
            paint.color = Color.parseColor("#1E293B")
            val desc = if (entry.description.length > 35) entry.description.substring(0, 32) + "..." else entry.description
            canvas.drawText(desc, 220f, y, paint)

            paint.textAlign = Paint.Align.RIGHT
            paint.isFakeBoldText = true
            paint.color = if (isDebt) Color.parseColor("#DC2626") else Color.parseColor("#059669")
            val prefix = if (isDebt) "+ " else "- "
            canvas.drawText(prefix + PdfReportHelper.formatCurrency(entry.amount), (pageWidth - 50).toFloat(), y, paint)
            paint.isFakeBoldText = false
        }

        // Table Bottom Divider
        y += 18f
        drawDivider(canvas, 40f, y, (pageWidth - 40).toFloat(), Color.parseColor("#94A3B8"), strokeWidth = 1.5f)

        // Summary Calculations Section
        y += 24f
        paint.textAlign = Paint.Align.RIGHT
        paint.textSize = 11f
        paint.color = Color.parseColor("#475569")
        canvas.drawText("Total Credit Purchases: ${PdfReportHelper.formatCurrency(totalDebt)}", (pageWidth - 50).toFloat(), y, paint)

        y += 18f
        canvas.drawText("Total Payments Received: ${PdfReportHelper.formatCurrency(totalPaid)}", (pageWidth - 50).toFloat(), y, paint)

        y += 22f
        paint.textSize = 13f
        paint.isFakeBoldText = true
        paint.color = Color.parseColor("#0F172A")
        canvas.drawText("Net Remaining Balance: ${PdfReportHelper.formatCurrency(data.currentDebt)}", (pageWidth - 50).toFloat(), y, paint)

        // Signature & Notice Line
        y += 50f
        drawDivider(canvas, 40f, y, 220f, Color.parseColor("#94A3B8"))
        drawDivider(canvas, (pageWidth - 220).toFloat(), y, (pageWidth - 40).toFloat(), Color.parseColor("#94A3B8"))

        y += 16f
        paint.textAlign = Paint.Align.LEFT
        paint.textSize = 9f
        paint.isFakeBoldText = false
        paint.color = Color.parseColor("#64748B")
        canvas.drawText("Store Owner / Cashier Signature", 40f, y, paint)

        paint.textAlign = Paint.Align.RIGHT
        canvas.drawText("Customer Acknowledgment", (pageWidth - 40).toFloat(), y, paint)

        // Footer
        y += 40f
        paint.textAlign = Paint.Align.CENTER
        paint.textSize = 8f
        paint.color = Color.parseColor("#94A3B8")
        canvas.drawText("This digital ledger is generated by SariTrack Cloud POS • Multi-Tenant Retail Management System", (pageWidth / 2).toFloat(), y, paint)

        pdfDocument.finishPage(page)

        val sanitizedName = PdfReportHelper.sanitizeFilename(data.customerName)
        val outputFile = File(context.cacheDir, "statement_${sanitizedName}_${System.currentTimeMillis()}.pdf")
        FileOutputStream(outputFile).use { out ->
            pdfDocument.writeTo(out)
        }
        pdfDocument.close()

        return outputFile
    }

    /**
     * Dispatches the generated PDF through Android's system share sheet.
     */
    fun sharePdfFile(context: Context, pdfFile: File, chooserTitle: String) {
        val authority = "${context.packageName}.fileprovider"
        val fileUri = FileProvider.getUriForFile(context, authority, pdfFile)

        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, fileUri)
            putExtra(Intent.EXTRA_SUBJECT, pdfFile.nameWithoutExtension)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        val chooser = Intent.createChooser(sendIntent, chooserTitle).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(chooser)
    }

    private fun drawDivider(
        canvas: Canvas,
        startX: Float,
        y: Float,
        endX: Float,
        color: Int,
        strokeWidth: Float = 1.0f
    ) {
        val paint = Paint().apply {
            this.color = color
            this.strokeWidth = strokeWidth
            this.style = Paint.Style.STROKE
        }
        canvas.drawLine(startX, y, endX, y, paint)
    }
}
