package edu.cit.tapales.saritrack.core.ui

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.*

class DateTimeUtilsExpandedTest {

    @Test
    fun testFormatTimestampFallbackOnFailure() {
        val invalidIso = "invalid-date-format"
        val result = DateTimeUtils.formatTimestamp(invalidIso)
        assertEquals(invalidIso, result)
    }

    @Test
    fun testFormatTimestampFallbackOnMillis() {
        val isoWithMillis = "2024-05-14T14:30:00.123"
        val result = DateTimeUtils.formatTimestamp(isoWithMillis)
        assertEquals("May 14, 2024 02:30 PM", result)
    }

    @Test
    fun testFormatTimestampHandlesSpaces() {
        val spaces = "   "
        assertEquals("N/A", DateTimeUtils.formatTimestamp(spaces))
    }

    @Test
    fun testIsTodayFalseForPast() {
        assertFalse(DateTimeUtils.isToday("2000-01-01T12:00:00"))
    }

    @Test
    fun testIsTodayFalseForFuture() {
        assertFalse(DateTimeUtils.isToday("3000-12-31T12:00:00"))
    }

    @Test
    fun testIsTodayFalseForInvalid() {
        assertFalse(DateTimeUtils.isToday("not-a-date"))
    }

    @Test
    fun testIsTodayFalseForEmpty() {
        assertFalse(DateTimeUtils.isToday(""))
    }

    @Test
    fun testIsTodayFalseForNull() {
        assertFalse(DateTimeUtils.isToday(null))
    }

    @Test
    fun testIsTodayFalseForWrongFormat() {
        assertFalse(DateTimeUtils.isToday("12-31-2024T12:00:00"))
    }

    @Test
    fun testFormatTimestampParsesMorning() {
        val input = "2024-05-14T08:15:00"
        val result = DateTimeUtils.formatTimestamp(input)
        assertEquals("May 14, 2024 08:15 AM", result)
    }

    @Test
    fun testFormatTimestampParsesMidnight() {
        val input = "2024-05-14T00:05:00"
        val result = DateTimeUtils.formatTimestamp(input)
        assertEquals("May 14, 2024 12:05 AM", result)
    }

    @Test
    fun testFormatTimestampParsesNoon() {
        val input = "2024-05-14T12:00:00"
        val result = DateTimeUtils.formatTimestamp(input)
        assertEquals("May 14, 2024 12:00 PM", result)
    }
}
