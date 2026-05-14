package edu.cit.tapales.saritrack.core.ui

import java.text.SimpleDateFormat
import java.util.*

object DateTimeUtils {
    fun formatTimestamp(isoString: String?): String {
        if (isoString.isNullOrBlank()) return "N/A"
        return try {
            // Assume format from backend is "yyyy-MM-dd'T'HH:mm:ss"
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)
            val outputFormat = SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.US)
            val date = inputFormat.parse(isoString)
            outputFormat.format(date ?: Date())
        } catch (e: Exception) {
            isoString // Return original if parsing fails
        }
    }

    fun isToday(isoString: String?): Boolean {
        if (isoString.isNullOrBlank()) return false
        val now = Calendar.getInstance()
        val date = try {
            SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(isoString.substring(0, 10))
        } catch (e: Exception) {
            return false
        }
        val target = Calendar.getInstance().apply { time = date ?: return false }
        return now.get(Calendar.YEAR) == target.get(Calendar.YEAR) &&
               now.get(Calendar.DAY_OF_YEAR) == target.get(Calendar.DAY_OF_YEAR)
    }
}
