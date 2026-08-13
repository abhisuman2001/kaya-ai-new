package com.example.data.model

enum class HazardCategory(val displayName: String, val iconName: String) {
    HELMET("Helmet Detection", "Hardhat"),
    VEST("Vest Detection", "Vest"),
    GLOVE("Glove Detection", "Hand"),
    FALL("Fall Protection", "Fall"),
    CRANE("Crane Safety Zone", "Crane"),
    ELECTRICAL("Electrical Panel", "Zap"),
    SCAFFOLD("Scaffolding Rig", "Construction"),
    FIRE("Fire & Thermal", "Flame")
}

data class HazardDetectionItem(
    val id: String,
    val category: HazardCategory,
    val title: String,
    val location: String,
    val severity: String, // "CRITICAL", "HIGH", "MEDIUM", "LOW"
    val timestamp: String,
    val isAcknowledged: Boolean = false,
    val audioAlertText: String,
    val oshaStandard: String,
    val description: String,
    val detectionConfidence: Int,
    val imageUrl: String = "https://images.unsplash.com/photo-1541888946425-d0fbb186a5b3",
    val assignedWorkerId: String? = null,
    val assignedWorkerName: String? = null
)

data class HazardDetectionState(
    val hazards: List<HazardDetectionItem> = emptyList(),
    val activeVoicePlayingId: String? = null,
    val selectedCategoryFilter: HazardCategory? = null,
    val selectedSeverityFilter: String? = null, // "CRITICAL", "HIGH", "MEDIUM", "LOW"
    val lastVoiceFiledHazard: HazardDetectionItem? = null,
    val voiceCommandFeedbackMessage: String? = null
)

