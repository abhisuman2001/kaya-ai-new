package com.example.data.model

data class DetectedMaterialItem(
    val name: String,
    val quantityEst: String,
    val condition: String,
    val specCompliance: String,
    val confidencePercent: Int
)

data class DetectedWorkerItem(
    val workerTag: String,
    val location: String,
    val ppeStatus: String, // "FULL PPE", "MISSING VEST", "MISSING HARNESS"
    val isCompliant: Boolean,
    val stanceSafety: String
)

data class DetectedEquipmentItem(
    val equipmentName: String,
    val status: String, // "OPERATIONAL", "IDLE", "MAINTENANCE REQ"
    val zone: String,
    val safetyClearanceOk: Boolean
)

data class SceneHazardItem(
    val title: String,
    val severity: String, // "CRITICAL", "HIGH", "MEDIUM", "LOW"
    val codeReference: String,
    val description: String
)

data class SceneAnalysisData(
    val snapshotId: String = "snap_8842",
    val timestamp: String = "2026-07-25 14:22:10",
    val locationTag: String = "Level 18 Deck • Grid B-4",
    val riskScore: Int = 34, // 0 to 100
    val riskRating: String = "MODERATE RISK", // CRITICAL, HIGH, MODERATE, LOW
    val imageUrl: String = "https://images.unsplash.com/photo-1541888946425-d0fbb186a5b3",
    val boundingBoxes: List<VisionBoundingBox> = listOf(
        VisionBoundingBox("Worker #1 (PPE OK)", 0.98f, false, 0.12f, 0.20f, 0.26f, 0.52f, "LOW", "Worker"),
        VisionBoundingBox("Missing Glasses & Harness ⚠️", 0.94f, true, 0.52f, 0.28f, 0.28f, 0.55f, "HIGH", "Worker"),
        VisionBoundingBox("Structural Rebar Grid C35", 0.99f, false, 0.15f, 0.65f, 0.70f, 0.28f, "LOW", "Material"),
        VisionBoundingBox("Tower Crane Zone Overhead", 0.92f, false, 0.68f, 0.08f, 0.28f, 0.25f, "MEDIUM", "Equipment"),
        VisionBoundingBox("Unprotected Deck Edge Fall Risk 🚨", 0.96f, true, 0.02f, 0.78f, 0.94f, 0.18f, "CRITICAL", "Hazard")
    ),
    val materials: List<DetectedMaterialItem> = listOf(
        DetectedMaterialItem("Grade 60 Steel Rebar Grid", "12.5 Tons", "Good Condition", "ASTM A615 Compliant", 99),
        DetectedMaterialItem("C35/40 High-Strength Concrete", "35 m³ Poured", "Curing Stage 2", "Slump Test Verified", 96),
        DetectedMaterialItem("Glazed Curtain Wall Glass Units", "8 Panels", "Uninstalled Frame", "Double Insulated Spec", 92)
    ),
    val workers: List<DetectedWorkerItem> = listOf(
        DetectedWorkerItem("Worker #1 (Ironworker)", "Grid B-4 Deck", "FULL PPE (Hardhat, Vest, Boots)", true, "Proper Stance"),
        DetectedWorkerItem("Worker #2 (Subcontractor)", "Perimeter Shaft", "MISSING HARNESS & EYEWEAR", false, "High Fall Exposure"),
        DetectedWorkerItem("Worker #3 (Crane Spotter)", "Zone 2 High Deck", "FULL PPE", true, "Radio In Hand")
    ),
    val equipment: List<DetectedEquipmentItem> = listOf(
        DetectedEquipmentItem("Tower Crane #2 (Potain MD)", "OPERATIONAL (Lifting Rebar)", "Sector North", true),
        DetectedEquipmentItem("Ultrasonic Concrete Strength Scanner", "ACTIVE RECORDING", "Grid C-2 Core", true),
        DetectedEquipmentItem("Mobile Scaffolding Tower L18", "INSPECTION DUE", "East Deck Edge", false)
    ),
    val hazards: List<SceneHazardItem> = listOf(
        SceneHazardItem("Perimeter Edge Fall Exposure", "CRITICAL", "OSHA 1926.501(b)(1)", "Unprotected deck perimeter on Level 18 without temporary wire rope guardrail."),
        SceneHazardItem("Worker Missing High-Vis & Lanyard", "HIGH", "OSHA 1926.95", "Worker #2 operating within 2 meters of edge without tie-off harness."),
        SceneHazardItem("Scaffold Inspection Tag Expired", "MEDIUM", "ANSI A10.8", "East scaffolding platform missing current green inspection tag for today's shift.")
    ),
    val aiSuggestions: List<String> = listOf(
        "Halts work near perimeter until 1/2 inch steel cable perimeter safety fence is installed.",
        "Issue mandatory tie-off directive to Subcontractor Team #3 before proceeding with rebar tie.",
        "Request immediate re-certification of East Scaffolding platform from Safety Inspector.",
        "Log automated safety infraction report to Site Supervisor."
    )
)
