package com.example.data.model

enum class ProductionPillar(val displayName: String) {
    ALL("All Readiness Pillars"),
    PERFORMANCE("Performance & Caching"),
    OFFLINE("Offline Mode & Sync"),
    SECURITY("Security & Encryption"),
    ACCESSIBILITY("Accessibility & UI"),
    CICD("CI/CD & Testing"),
    STORE_READINESS("Play & App Store")
}

data class ReadinessAuditCheck(
    val id: String,
    val pillar: ProductionPillar,
    val title: String,
    val description: String,
    val isPassed: Boolean = true,
    val metricValue: String,
    val priority: String = "CRITICAL"
)

data class OfflineCacheStatus(
    val isOfflineModeActive: Boolean = false,
    val cachedSyncItemsCount: Int = 142,
    val lastSyncTimestamp: String = "2026-07-26 01:05:12",
    val encryptedDbSizeMb: Float = 14.8f,
    val pendingUploadsCount: Int = 0
)

data class StoreReleaseChecklist(
    val taskName: String,
    val category: String,
    val isCompleted: Boolean = true,
    val notes: String
)

data class ProductionState(
    val selectedPillar: ProductionPillar = ProductionPillar.ALL,
    val overallReadinessScorePct: Float = 98.5f,
    val cacheStatus: OfflineCacheStatus = OfflineCacheStatus(),
    val isRunningAudit: Boolean = false,
    val auditLogOutput: String? = null,
    val auditChecks: List<ReadinessAuditCheck> = listOf(
        ReadinessAuditCheck("chk_perf_01", ProductionPillar.PERFORMANCE, "Compose Recomposition & R8 ProGuard", "Full R8 code shrinking, obfuscation & baseline profile optimizations active.", true, "60 FPS Verified", "CRITICAL"),
        ReadinessAuditCheck("chk_perf_02", ProductionPillar.PERFORMANCE, "Coil Image Disk Caching & Memory Pools", "LRU image cache with 250MB disk buffer & compressed WebP telemetry.", true, "12ms Cache Latency", "HIGH"),
        ReadinessAuditCheck("chk_off_01", ProductionPillar.OFFLINE, "Room Encrypted Offline SQLite Queue", "SQLCipher 256-bit AES database encryption for offline field telemetry storage.", true, "100% Offline Functional", "CRITICAL"),
        ReadinessAuditCheck("chk_off_02", ProductionPillar.OFFLINE, "Background Sync WorkManager Engine", "Exponential backoff auto-retry queue for Ray-Ban HUD field snapshots.", true, "Zero Data Loss", "HIGH"),
        ReadinessAuditCheck("chk_sec_01", ProductionPillar.SECURITY, "TLS 1.3 Pinning & Encrypted SharedPreferences", "Biometric authentication & AES-256 Android Keystore credential storage.", true, "AES-256 Hardware Key", "CRITICAL"),
        ReadinessAuditCheck("chk_sec_02", ProductionPillar.SECURITY, "OSHA Compliance Telemetry Anonymization", "PII sanitization pipeline before cloud vector embedding.", true, "100% PII Clean", "HIGH"),
        ReadinessAuditCheck("chk_acc_01", ProductionPillar.ACCESSIBILITY, "48dp Minimum Touch Targets & Contrast Ratios", "Material 3 WCAG AA compliance with dynamic screen reader content descriptions.", true, "4.5:1 Contrast AA", "HIGH"),
        ReadinessAuditCheck("chk_cicd_01", ProductionPillar.CICD, "GitHub Actions & Fastlane Automated Pipeline", "Robolectric JVM unit testing & Roborazzi screenshot verification CI workflow.", true, "Build Green (100% Pass)", "CRITICAL"),
        ReadinessAuditCheck("chk_store_01", ProductionPillar.STORE_READINESS, "Google Play & Apple App Store Privacy Manifest", "Target SDK 34, Android 15 edge-to-edge layout & Data Safety declaration.", true, "Play Store Approved", "CRITICAL")
    ),
    val storeChecklist: List<StoreReleaseChecklist> = listOf(
        StoreReleaseChecklist("Signed App Bundle (AAB)", "Google Play Console", true, "Signed with production keystore and v2/v3 signature scheme."),
        StoreReleaseChecklist("App Privacy & Data Safety Declarations", "Google Play Console", true, "Declared hardware camera/microphone usage for Ray-Ban HUD stream."),
        StoreReleaseChecklist("Target SDK 34 (Android 14/15)", "Build Configuration", true, "Fully compliant with Google Play API level requirements."),
        StoreReleaseChecklist("Adaptive Icons & Feature Graphic Assets", "Store Assets", true, "High-resolution vector adaptive launcher icons and promo graphics."),
        StoreReleaseChecklist("Automated Roborazzi UI Screenshot Suite", "Quality Assurance", true, "Generated localized screenshots for phone, tablet and foldable form factors.")
    )
)
