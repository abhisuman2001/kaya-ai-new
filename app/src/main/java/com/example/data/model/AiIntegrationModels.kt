package com.example.data.model

enum class AiModuleType(val displayName: String) {
    ALL("All AI Engines"),
    LANG_GRAPH("LangGraph Agents"),
    YOLO("YOLO Spatial Vision"),
    OCR("OCR Blueprint Reader"),
    QDRANT_RAG("Qdrant Vector RAG"),
    MEMORY_STREAM("Memory & Streaming")
}

data class LangGraphAgentNode(
    val id: String,
    val name: String,
    val role: String,
    val model: String = "Gemini 1.5 Pro / GPT-4o",
    val status: String = "ACTIVE_LISTENING",
    val lastAction: String,
    val confidenceScore: Float = 0.98f
)

data class YoloSpatialBoundingBox(
    val id: String,
    val label: String,
    val confidence: Float,
    val coordinates: String, // e.g. "[x: 120, y: 340, w: 200, h: 180]"
    val isHazard: Boolean = false,
    val oshaRule: String? = null
)

data class OcrExtractedSpec(
    val documentTitle: String,
    val codeSnippet: String,
    val extractedSpec: String,
    val complianceStatus: String = "VERIFIED_COMPLIANT",
    val confidencePct: Float = 99.2f
)

data class QdrantVectorRecord(
    val vectorId: String,
    val collectionName: String = "kaya_blueprints_v2",
    val vectorDimensions: Int = 1536,
    val cosineSimilarity: Float,
    val payloadText: String
)

data class MemorySessionItem(
    val memoryId: String,
    val timestamp: String,
    val contextTopic: String,
    val rememberedFact: String
)

data class AiIntegrationState(
    val selectedModule: AiModuleType = AiModuleType.ALL,
    val isStreamingActive: Boolean = true,
    val streamingTextBuffer: String = "Analyzing real-time video frame from Ray-Ban Meta Smart Glasses... [LangGraph Orchestrator dispatching to Safety & Rebar agents]... verified 100% tie-off compliance at Level 18 slab edge.",
    val agents: List<LangGraphAgentNode> = listOf(
        LangGraphAgentNode("agent_01", "Safety Guard Agent", "PPE & Fall Protection Monitor", "GPT-4o Vision", "ACTIVE_MONITORING", "Scanned slab perimeter. Zero harness violations."),
        LangGraphAgentNode("agent_02", "Structural Rebar Agent", "CAD vs Physical Placement Auditor", "Gemini 1.5 Pro", "COMPUTING_GEOMETRY", "Comparing rebar grid with IFC structural drawing."),
        LangGraphAgentNode("agent_03", "OCR Spec Verifier", "Drawing & ASTM Standards Parser", "GPT-4o OCR Engine", "IDLE_STANDBY", "Extracted ASTM A615 Grade 60 tensile specification."),
        LangGraphAgentNode("agent_04", "Qdrant Vector Retriever", "RAG Knowledge Base Search", "Qdrant Hybrid Engine", "QUERY_INDEXED", "Indexed 2,400 drawing blueprint vector chunks.")
    ),
    val yoloDetections: List<YoloSpatialBoundingBox> = listOf(
        YoloSpatialBoundingBox("yolo_01", "Hard Hat (PPE)", 0.99f, "[x:140, y:80, w:90, h:90]", false, "OSHA 1926.100(a) Compliant"),
        YoloSpatialBoundingBox("yolo_02", "Safety Harness Tie-Off", 0.97f, "[x:160, y:210, w:120, h:180]", false, "OSHA 1926.502 Anchor Point OK"),
        YoloSpatialBoundingBox("yolo_03", "Unguarded Deck Gap", 0.92f, "[x:420, y:310, w:210, h:140]", true, "CRITICAL: Guardrail Missing within 6ft Edge")
    ),
    val ocrSpec: OcrExtractedSpec = OcrExtractedSpec(
        documentTitle = "S-108 Structural Concrete & Deck Specifications",
        codeSnippet = "SECTION 03300 — CAST-IN-PLACE CONCRETE",
        extractedSpec = "Minimum compressive strength f'c = 5,000 PSI at 28 days. Maximum water-cement ratio = 0.40. Epoxy coated grade 60 rebar required.",
        complianceStatus = "SPECIFICATION_MATCHED"
    ),
    val qdrantVectors: List<QdrantVectorRecord> = listOf(
        QdrantVectorRecord("vec_8820", "kaya_blueprints_v2", 1536, 0.964f, "Level 18 Pour Schedule: Concrete placement restricted when ambient temp exceeds 95°F."),
        QdrantVectorRecord("vec_8821", "kaya_osha_regulations", 1536, 0.921f, "100% tie-off mandatory for elevated work surfaces without perimeter netting.")
    ),
    val memoryLogs: List<MemorySessionItem> = listOf(
        MemorySessionItem("mem_01", "08:15 AM", "Site Location", "Site Supervisor checked in at Level 18 West Deck."),
        MemorySessionItem("mem_02", "08:42 AM", "Hardware Connection", "Ray-Ban Meta Smart Glasses paired with 88% battery & low-latency HUD stream.")
    )
)
