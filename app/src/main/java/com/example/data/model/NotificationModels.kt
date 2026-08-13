package com.example.data.model

enum class NotificationCategory(val displayName: String) {
    ALL("All Alerts"),
    AI_ALERTS("AI Vision"),
    HAZARDS("Hazards"),
    REPORTS("Reports"),
    FIRMWARE("Firmware"),
    TASKS("Tasks")
}

enum class NotificationPriority {
    CRITICAL, HIGH, MEDIUM, LOW
}

data class SiteNotificationItem(
    val id: String,
    val category: NotificationCategory,
    val title: String,
    val message: String,
    val timestamp: String,
    val priority: NotificationPriority,
    val isRead: Boolean = false,
    val actionText: String? = null,
    val targetRoute: String? = null
)

data class NotificationSettings(
    val isPushNotificationsEnabled: Boolean = true,
    val isAiVisionAlertsEnabled: Boolean = true,
    val isCriticalHazardAlertsEnabled: Boolean = true,
    val isReportDigestEnabled: Boolean = true,
    val isFirmwareUpdateAlertsEnabled: Boolean = true,
    val isTaskAssignmentAlertsEnabled: Boolean = true,
    val soundMode: String = "HUD Earbud Audio + Haptic"
)

data class SiteNotificationState(
    val selectedCategory: NotificationCategory = NotificationCategory.ALL,
    val unreadCount: Int = 3,
    val isTestPushSent: Boolean = false,
    val settings: NotificationSettings = NotificationSettings(),
    val notificationsList: List<SiteNotificationItem> = emptyList()
)
