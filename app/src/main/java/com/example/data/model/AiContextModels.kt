package com.example.data.model

/**
 * Types of structured events collected by the AI Context Engine.
 */
enum class ContextEventType(val displayName: String) {
    VISION_OBSERVATION("Live Vision Observation"),
    HAZARD_DETECTED("Hazard Detected"),
    HAZARD_RESOLVED("Hazard Resolved"),
    VOICE_INTERACTION("Voice AI Interaction"),
    BLUEPRINT_DEVIATION("Blueprint Deviation"),
    REPORT_SUBMITTED("Report Submitted"),
    SESSION_STATE_CHANGED("AI Session Changed"),
    SYSTEM_LOG("System Log")
}

/**
 * Source modules that produce events for the AI Context Engine.
 */
enum class ContextEventSource(val label: String) {
    LIVE_VISION("Live AI Vision"),
    HAZARD_DETECTION("Hazard Engine"),
    VOICE_AI("Voice AI Copilot"),
    BLUEPRINT_COMPARISON("CAD/BIM Blueprint"),
    REPORTS_MODULE("Reports & Incident Log"),
    SMART_GLASSES("Ray-Ban Meta Glasses"),
    SYSTEM("Context Engine System")
}

/**
 * Individual structured event saved in the AI Context Engine's chronological timeline.
 */
data class ContextEvent(
    val id: String,
    val timestampMs: Long = System.currentTimeMillis(),
    val formattedTime: String,
    val type: ContextEventType,
    val source: ContextEventSource,
    val title: String,
    val description: String,
    val severity: String? = null, // e.g. CRITICAL, HIGH, MEDIUM, LOW, INFO
    val location: String? = "Zone B-4 Level 3",
    val metadata: Map<String, String> = emptyMap(),
    val isProcessed: Boolean = true
)

/**
 * Current snapshot of the site state maintained by the AI Context Engine.
 */
data class ActiveSiteContext(
    val projectName: String = "Metro Tower Construction",
    val activeZone: String = "Zone B-4 Level 3",
    val activeWorkerCount: Int = 3,
    val isAiSessionRunning: Boolean = true,
    val currentSceneSummary: String = "Steel framework assembly near scaffold platform B-4 with active tower crane.",
    val lastCameraObservation: String = "3 workers present on steel erection, 1 operating crane overhead.",
    val activeHazards: List<ContextEvent> = emptyList(),
    val resolvedHazardsCount: Int = 1,
    val reportedIssuesCount: Int = 2,
    val blueprintDeviationsCount: Int = 1,
    val lastHazardTimestamp: String? = "14:18 PM",
    val lastVoiceQuery: String? = null,
    val sessionStartTimeMs: Long = System.currentTimeMillis() - (25 * 60 * 1000) // 25 mins ago
)

/**
 * Structured response for context-aware query resolution.
 */
data class ContextAnswer(
    val questionText: String,
    val responseText: String,
    val relevantEvents: List<ContextEvent> = emptyList(),
    val confidenceScore: Float = 0.95f,
    val suggestedFollowUps: List<String> = emptyList()
)

/**
 * Structured Voice-Generated AI Report payload for supervisor portal integration.
 */
data class AiGeneratedReport(
    val reportId: String = "REP-${(1000..9999).random()}",
    val workerId: String = "WRK-8821",
    val workerName: String = "Alex Rivera",
    val projectId: String = "PRJ-METRO-01",
    val projectName: String = "Metro Tower Construction",
    val zone: String = "Level 18 - Zone B-4",
    val timestamp: String,
    val issueType: String = "Safety Hazard", // Safety Hazard, Structural Defect, Equipment Issue, Quality Issue, Material Issue, Environmental Hazard, Other
    val title: String,
    val severity: String = "High", // Low, Medium, High, Critical
    val description: String,
    val detectedObjects: List<String> = listOf("Worker", "Crane", "PPE"),
    val aiObservation: String = "Automated visual evidence captured via Ray-Ban Meta Smart Glasses AI Vision stream.",
    val sceneSummary: String = "Steel framework assembly area with active crane operations.",
    val hasCameraSnapshot: Boolean = true,
    val hazardInfo: String? = null,
    val conversationTranscript: String = "",
    val sessionId: String = "SES-META-2026",
    val status: String = "Open" // Open, Under Review, In Progress, Resolved
)

/**
 * Filter parameters for querying historical timeline events.
 */
data class ContextQueryFilter(
    val source: ContextEventSource? = null,
    val type: ContextEventType? = null,
    val minSeverity: String? = null,
    val keyword: String? = null,
    val limit: Int = 50
)
