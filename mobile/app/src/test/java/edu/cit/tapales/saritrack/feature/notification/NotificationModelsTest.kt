package edu.cit.tapales.saritrack.feature.notification

import org.junit.Assert.*
import org.junit.Test

class NotificationModelsTest {

    @Test
    fun testNotificationItemProperties() {
        val item = NotificationItem(
            id = 1L,
            vendorId = 100L,
            title = "Low Stock Alert",
            message = "Safeguard soap is below threshold (2 left).",
            type = "WARNING",
            isRead = false,
            timestamp = "2026-10-08T10:00:00"
        )

        assertEquals(1L, item.id)
        assertEquals(100L, item.vendorId)
        assertEquals("Low Stock Alert", item.title)
        assertEquals("Safeguard soap is below threshold (2 left).", item.message)
        assertEquals("WARNING", item.type)
        assertFalse(item.isRead)
        assertEquals("2026-10-08T10:00:00", item.timestamp)
    }

    @Test
    fun testNotificationItemDefaultValues() {
        val item = NotificationItem(
            id = 2L,
            vendorId = 100L,
            title = "System Update",
            message = "New features available."
        )

        assertEquals("INFO", item.type)
        assertFalse(item.isRead)
        assertNull(item.timestamp)
    }

    @Test
    fun testNotificationItemCopy() {
        val original = NotificationItem(
            id = 3L,
            vendorId = 100L,
            title = "Debt Payment Received",
            message = "Juan paid ₱500.",
            type = "SUCCESS",
            isRead = false
        )

        val updated = original.copy(isRead = true)
        assertEquals(3L, updated.id)
        assertTrue(updated.isRead)
        assertFalse(original.isRead)
    }

    @Test
    fun testNotificationItemEquality() {
        val item1 = NotificationItem(4L, 100L, "Title", "Message", "INFO", false, "2026-10-08")
        val item2 = NotificationItem(4L, 100L, "Title", "Message", "INFO", false, "2026-10-08")

        assertEquals(item1, item2)
        assertEquals(item1.hashCode(), item2.hashCode())
    }

    @Test
    fun testNotificationItemInequality() {
        val item1 = NotificationItem(5L, 100L, "Title", "Message", "INFO", false)
        val item2 = NotificationItem(6L, 100L, "Title", "Message", "INFO", false)

        assertNotEquals(item1, item2)
    }

    @Test
    fun testNotificationItemToString() {
        val item = NotificationItem(7L, 100L, "Stock Restocked", "Milk added", "SUCCESS", true)
        val str = item.toString()
        assertTrue(str.contains("Stock Restocked"))
        assertTrue(str.contains("Milk added"))
        assertTrue(str.contains("SUCCESS"))
    }
}
