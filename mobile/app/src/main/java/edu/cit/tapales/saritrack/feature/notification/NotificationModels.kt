package edu.cit.tapales.saritrack.feature.notification

data class NotificationItem(
    val id: Long,
    val vendorId: Long,
    val title: String,
    val message: String,
    val type: String? = "INFO",
    val isRead: Boolean = false,
    val timestamp: String? = null
)
