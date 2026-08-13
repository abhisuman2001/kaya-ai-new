package com.example.data.model

enum class AnalyticsTimeframe(val displayName: String) {
    WEEK("7 Days"),
    MONTH("30 Days"),
    QUARTER("90 Days"),
    YEAR("1 Year"),
    ALL_TIME("All Time")
}

enum class TradeCategory(val displayName: String) {
    ALL("All Trades"),
    CONCRETE("Concrete & Core"),
    MEP("MEP & Electrical"),
    STEEL("Rebar & Steel"),
    FORMWORK("Formwork & Scaffolding")
}

data class AnalyticsMetric(
    val title: String,
    val value: String,
    val unit: String,
    val changePercent: String,
    val isPositive: Boolean,
    val description: String
)

data class ChartDataPoint(
    val label: String,
    val productivityValue: Float, // 0.0 to 100.0
    val hazardsCount: Int,
    val qualityScore: Float // 0.0 to 100.0
)

data class HazardCategoryBreakdown(
    val categoryName: String,
    val count: Int,
    val percentage: Float,
    val colorHex: Long
)

data class AnalyticsState(
    val timeframe: AnalyticsTimeframe = AnalyticsTimeframe.MONTH,
    val selectedTrade: TradeCategory = TradeCategory.ALL,
    val productivityMetric: AnalyticsMetric = AnalyticsMetric(
        title = "Productivity Rate",
        value = "94.2",
        unit = "%",
        changePercent = "+8.4%",
        isPositive = true,
        description = "320 Man-Hours saved via AI hands-free glasses logging"
    ),
    val hazardsPreventedMetric: AnalyticsMetric = AnalyticsMetric(
        title = "Hazards Prevented",
        value = "48",
        unit = "Alerts",
        changePercent = "+18%",
        isPositive = true,
        description = "0 Lost Time Injuries (LTI) across 12,400 site work hours"
    ),
    val projectProgressMetric: AnalyticsMetric = AnalyticsMetric(
        title = "Overall Project Progress",
        value = "78.5",
        unit = "%",
        changePercent = "+2.5 Days",
        isPositive = true,
        description = "Level 18 slab +2 days ahead of BIM Master Schedule"
    ),
    val qualityScoreMetric: AnalyticsMetric = AnalyticsMetric(
        title = "Site Quality Index (SQI)",
        value = "95.8",
        unit = "/ 100",
        changePercent = "+3.2%",
        isPositive = true,
        description = "ASTM & Eurocode audit pass rate across all trades"
    ),
    val weeklyTrend: List<ChartDataPoint> = listOf(
        ChartDataPoint("Mon", 88f, 6, 92f),
        ChartDataPoint("Tue", 91f, 8, 94f),
        ChartDataPoint("Wed", 95f, 12, 96f),
        ChartDataPoint("Thu", 92f, 9, 95f),
        ChartDataPoint("Fri", 97f, 7, 98f),
        ChartDataPoint("Sat", 94f, 4, 96f),
        ChartDataPoint("Sun", 96f, 2, 97f)
    ),
    val hazardBreakdown: List<HazardCategoryBreakdown> = listOf(
        HazardCategoryBreakdown("PPE Violations", 22, 45.8f, 0xFF3B82F6),
        HazardCategoryBreakdown("Edge & Slab Risk", 12, 25.0f, 0xFFEF4444),
        HazardCategoryBreakdown("Electrical / Cables", 9, 18.8f, 0xFFF59E0B),
        HazardCategoryBreakdown("Trip & Material Spills", 5, 10.4f, 0xFF10B981)
    ),
    val isRunningPredictiveSim: Boolean = false,
    val simulationResult: String? = null
)
