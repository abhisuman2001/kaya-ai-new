package com.example.data.model

enum class CadFileType(val displayName: String, val extension: String) {
    DWG("Autodesk CAD", ".dwg"),
    DXF("Drawing Exchange", ".dxf"),
    IFC("BIM Building Model", ".ifc"),
    BIM_RVT("Revit Model", ".rvt")
}

data class CadBimFileItem(
    val id: String,
    val fileName: String,
    val fileType: CadFileType,
    val fileSize: String,
    val uploadDate: String,
    val projectGridMapping: String,
    val elementCount: Int,
    val revision: String,
    val isMapped: Boolean = true
)

data class BimMeasurement(
    val id: String,
    val label: String,
    val cadValue: String,
    val asBuiltValue: String,
    val delta: String,
    val tolerance: String,
    val isWithinTolerance: Boolean
)

data class CadDeviationItem(
    val id: String,
    val elementName: String,
    val gridLocation: String,
    val cadSpec: String,
    val asBuiltMeasured: String,
    val deviationMm: Double,
    val severity: String,
    val oshaBimCode: String,
    val voiceFeedbackText: String,
    val isResolved: Boolean = false
)

data class CadBimState(
    val files: List<CadBimFileItem> = emptyList(),
    val activeFileId: String? = null,
    val selectedViewMode: String = "OVERLAY", // "OVERLAY", "HEATMAP", "SPLIT_VIEW", "REDLINE_WIREFRAME"
    val alignmentXOffsetMm: Int = 2,
    val alignmentYOffsetMm: Int = -1,
    val alignmentRotationDeg: Double = 0.4,
    val isAligning: Boolean = false,
    val measurements: List<BimMeasurement> = emptyList(),
    val deviations: List<CadDeviationItem> = emptyList(),
    val activeVoicePlayingId: String? = null,
    val isComparing: Boolean = false
)
