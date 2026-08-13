package com.example.data.model

enum class GlassAiState(val label: String, val description: String) {
    IDLE("Idle", "Ray-Ban Meta standby"),
    CONNECTED("Connected", "Paired over BLE & Wi-Fi Direct"),
    LISTENING("Listening...", "Microphone array capturing voice input"),
    THINKING("Thinking...", "Multi-Agent AI reasoning"),
    ANALYZING("Analyzing Vision", "Camera frame processing via Vision Agent"),
    SPEAKING("Speaking", "Audio feedback streaming to glasses speaker"),
    CHARGING("Charging", "Docked in Meta charging case (92%)"),
    OFFLINE("Offline", "Glasses disconnected")
}

data class GlassDeviceState(
    val connectionState: GlassAiState = GlassAiState.CONNECTED,
    val deviceName: String = "Ray-Ban Meta Wayfarer (Matte Black)",
    val batteryPercent: Int = 92,
    val caseBatteryPercent: Int = 88,
    val isCharging: Boolean = false,
    val chargingStatusText: String = "Discharging",
    val batteryHealth: String = "Good",
    val tempCelsius: Int = 31,
    val storageFreeGb: Float = 28.5f,
    val storageTotalGb: Float = 32.0f,
    val isMicActive: Boolean = true,
    val isCameraActive: Boolean = true,
    val isLiveStreaming: Boolean = true,
    val voiceTriggerEnabled: Boolean = true,
    val volumePercent: Int = 80,
    val brightnessPercent: Int = 85,
    val firmwareVersion: String = "v3.1.4-build92",
    val serialNumber: String = "RBM-88942-CON",
    val isPhoneBridgeMode: Boolean = false,
    val connectedPhoneName: String = "",
    val cameraFacing: String = "REAR",
    val isTorchActive: Boolean = false,
    val bluetoothMacAddress: String = "94:11:AB:22:90:FF",
    val isDemoSimulatedMode: Boolean = false
)

data class DiscoveredGlassDevice(
    val id: String,
    val name: String,
    val macAddress: String,
    val rssiDbm: Int,
    val model: String,
    val color: String,
    val batteryPercent: Int = 92,
    val isPaired: Boolean = false,
    val isSmartphoneBridge: Boolean = false
)

enum class PairingStep {
    DISCOVERY,
    PERMISSIONS,
    HANDSHAKE,
    FIRMWARE_CHECK,
    SUCCESS,
    ERROR
}

data class ConnectionQualityInfo(
    val rssiDbm: Int = -58,
    val bandwidthMbps: Int = 54,
    val latencyMs: Int = 18,
    val signalRating: String = "Excellent"
)

data class GlassPermissionsState(
    val cameraGranted: Boolean = true,
    val microphoneGranted: Boolean = true,
    val notificationGranted: Boolean = true,
    val bluetoothGranted: Boolean = true
) {
    val allGranted: Boolean
        get() = cameraGranted && microphoneGranted && notificationGranted && bluetoothGranted
}

