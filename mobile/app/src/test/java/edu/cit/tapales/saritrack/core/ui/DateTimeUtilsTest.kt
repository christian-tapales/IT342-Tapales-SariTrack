package edu.cit.tapales.saritrack.core.ui

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.*

class DateTimeUtilsTest {

    @Test
    fun `formatTimestamp should convert ISO string to human readable format`() {
        val input = "2024-05-14T14:30:00"
        val result = DateTimeUtils.formatTimestamp(input)
        // Format: MMM dd, yyyy hh:mm a
        assertEquals("May 14, 2024 02:30 PM", result)
    }

    @Test
    fun `formatTimestamp should return NA for null or empty`() {
        assertEquals("N/A", DateTimeUtils.formatTimestamp(null))
        assertEquals("N/A", DateTimeUtils.formatTimestamp(""))
    }

    @Test
    fun `isToday should return true for current date`() {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
        assertTrue(DateTimeUtils.isToday("${today}T10:00:00"))
    }
}
