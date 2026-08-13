package com.example.data.model

enum class MaterialCategory(val displayName: String) {
    ALL("All Materials"),
    CONCRETE("Concrete & Cement"),
    REBAR_STEEL("Rebar & Structural Steel"),
    PIPING_MEP("Piping & Valves"),
    CHEMICALS("Chemicals & Admixtures"),
    FASTENERS("Bolts & Anchor Fasteners"),
    ELECTRICAL("Cabling & Electrical")
}

data class MaterialItem(
    val id: String,
    val materialName: String,
    val category: MaterialCategory,
    val brand: String,
    val specification: String,
    val expiryDate: String,
    val batchNumber: String,
    val isCompliant: Boolean,
    val complianceCode: String,
    val currentStockQuantity: String,
    val unit: String,
    val deliveryDate: String,
    val locationGrid: String,
    val detectedByVision: Boolean = true,
    val aiConfidence: Float = 0.96f,
    val notes: String,
    val isInventoryUpdated: Boolean = true
)

data class MaterialVerificationState(
    val selectedCategory: MaterialCategory = MaterialCategory.ALL,
    val totalMaterialsInspected: Int = 48,
    val compliantMaterialsCount: Int = 46,
    val complianceRatePercent: Int = 95,
    val expiredOrNonCompliantCount: Int = 2,
    val materials: List<MaterialItem> = emptyList(),
    val isScanningMaterial: Boolean = false,
    val scanSuccessMessage: String? = null
)
