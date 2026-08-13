package com.example.data.model

data class AgentExecutionStep(
    val agentName: String, // Vision Agent, Safety Agent, Quality Agent, Knowledge Agent, Reporting Agent, Decision Engine
    val status: AgentStatus,
    val output: String,
    val latencyMs: Long
)

enum class AgentStatus {
    IDLE, RUNNING, SUCCESS, WARNING, ALERT
}

data class VisionBoundingBox(
    val label: String,
    val confidence: Float,
    val isHazard: Boolean,
    val normX: Float, // 0.0 to 1.0
    val normY: Float,
    val normWidth: Float,
    val normHeight: Float,
    val riskLevel: String = "LOW", // CRITICAL, HIGH, MEDIUM, LOW
    val category: String = "General"
)

data class LiveTranscriptEntry(
    val id: String,
    val speaker: String, // "User (Ray-Ban Mic)" or "Kaya AI (Glasses Audio)"
    val text: String,
    val timestamp: String,
    val isAi: Boolean
)

data class WebSocketTelemetryState(
    val isConnected: Boolean = true,
    val socketUrl: String = "wss://stream.kaya.ai/v1/hud",
    val latencyMs: Int = 14,
    val fps: Int = 60,
    val bitrateMbps: Float = 2.4f,
    val activeStreamers: Int = 3
)

data class LiveAiAnalysisResult(
    val timestamp: Long = System.currentTimeMillis(),
    val queryText: String = "",
    val aiResponseText: String = "",
    val detectedObjects: List<VisionBoundingBox> = emptyList(),
    val ppeCompliancePercent: Int = 94,
    val blueprintDeviationMm: Float = 0f,
    val materialSpecs: String = "C35 Concrete with Grade 500 Rebar",
    val agentSteps: List<AgentExecutionStep> = emptyList(),
    val isApiError: Boolean = false,
    val apiErrorMessage: String? = null
)
