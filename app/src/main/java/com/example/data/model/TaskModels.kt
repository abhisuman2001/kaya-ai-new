package com.example.data.model

/**
 * Task priority classification.
 */
enum class TaskPriority(val label: String) {
    HIGH("High"),
    MEDIUM("Medium"),
    LOW("Low")
}

/**
 * Task completion status states.
 */
enum class TaskStatus(val label: String) {
    PENDING("Pending"),
    IN_PROGRESS("In Progress"),
    COMPLETED("Completed"),
    BLOCKED("Blocked")
}

/**
 * Enterprise Construction Task Model for Meta Smart Glasses Worker App.
 */
data class ConstructionTask(
    val taskId: String,
    val projectId: String = "PRJ-METRO-01",
    val projectName: String = "Metro Tower Construction",
    val workerId: String = "WRK-8821",
    val zone: String,
    val title: String,
    val description: String,
    val priority: TaskPriority,
    val status: TaskStatus = TaskStatus.PENDING,
    val estimatedDuration: String,
    val dueTime: String,
    val assignedSupervisor: String = "Site Supervisor",
    val requiredPpe: List<String> = listOf("Safety Helmet", "High-Vis Vest", "Steel-toe Boots"),
    val safetyRequirements: List<String> = listOf("Ensure work area is perimeter barricaded.", "Check 100% tie-off fall protection harness."),
    val aiRecommendations: List<String> = listOf("Inspect beam alignment before installation.", "Use crane communication protocol."),
    val isExpanded: Boolean = false
)

/**
 * Shift progress summary for today's tasks.
 */
data class ShiftTaskSummary(
    val completedCount: Int,
    val totalCount: Int,
    val remainingTimeFormatted: String
) {
    val progressFraction: Float
        get() = if (totalCount > 0) completedCount.toFloat() / totalCount.toFloat() else 0f
}
