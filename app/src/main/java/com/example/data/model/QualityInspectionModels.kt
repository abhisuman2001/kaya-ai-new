package com.example.data.model

enum class QualityCategory(val displayName: String) {
    ALL("All Checks"),
    CRACKS("Cracks & Voids"),
    SURFACE("Surface Finish"),
    ALIGNMENT("Plumb & Alignment"),
    CONCRETE("Concrete & Curing"),
    PIPE("Pipe & MEP Seal"),
    BOLT("Bolt & Steel Torque")
}

data class QualityInspectionItem(
    val id: String,
    val title: String,
    val category: QualityCategory,
    val locationGrid: String,
    val measuredValue: String,
    val specificationThreshold: String,
    val isPassed: Boolean,
    val scoreImpact: Int, // e.g. -5 pts
    val inspectorName: String,
    val timestamp: String,
    val aiConfidence: Float,
    val detailNotes: String,
    val recommendation: String,
    val isRemediated: Boolean = false
)

data class QualityRecommendationItem(
    val id: String,
    val category: QualityCategory,
    val priority: String, // "CRITICAL", "HIGH", "MEDIUM", "LOW"
    val title: String,
    val location: String,
    val actionPlan: String,
    val estimatedFixTime: String,
    val assignedTrade: String,
    val isCompleted: Boolean = false
)

data class QualityHistoryRecord(
    val id: String,
    val inspectionDate: String,
    val inspector: String,
    val zone: String,
    val itemsInspected: Int,
    val passRatePercent: Int,
    val overallScore: Int,
    val status: String
)

data class QualityInspectionState(
    val selectedCategory: QualityCategory = QualityCategory.ALL,
    val overallQualityScore: Int = 92, // Score 0..100
    val qualityGrade: String = "A- GRADE",
    val crackScore: Int = 90,
    val surfaceScore: Int = 89,
    val alignmentScore: Int = 94,
    val concreteScore: Int = 95,
    val pipeScore: Int = 96,
    val boltScore: Int = 88,
    val inspectionItems: List<QualityInspectionItem> = emptyList(),
    val recommendations: List<QualityRecommendationItem> = emptyList(),
    val historyLogs: List<QualityHistoryRecord> = emptyList(),
    val isScanning: Boolean = false
)
