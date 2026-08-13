package com.example.data.model

enum class ReportFilterCategory(val displayName: String) {
    ALL("All Reports"),
    DAILY("Daily (DPR)"),
    WEEKLY("Weekly (WPR)"),
    INCIDENT("Incident (IR)"),
    SAFETY("Safety (SVR)"),
    QUALITY("Quality (QAR)")
}

data class ComprehensiveReportItem(
    val id: String,
    val typeCode: String, // "DAILY", "WEEKLY", "INCIDENT", "SAFETY", "QUALITY"
    val title: String,
    val date: String,
    val author: String,
    val executiveSummary: String,
    val keyMetrics: Map<String, String>,
    val aiGeneratedInsights: List<String>,
    val hazardsReported: Int,
    val qualityDefectsFound: Int,
    val crewSize: Int,
    val status: String = "APPROVED",
    val pdfUrl: String = "file:///site_reports/pdf_export.pdf"
)

data class ComprehensiveReportState(
    val selectedFilter: ReportFilterCategory = ReportFilterCategory.ALL,
    val isGeneratingAiReport: Boolean = false,
    val activeReportDetail: ComprehensiveReportItem? = null,
    val shareDialogMessage: String? = null,
    val pdfExportToast: String? = null,
    val reportsList: List<ComprehensiveReportItem> = emptyList()
)
