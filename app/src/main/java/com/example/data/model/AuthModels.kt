package com.example.data.model

enum class UserRole(
    val title: String,
    val description: String,
    val iconName: String
) {
    WORKER("Site Worker", "Execute site tasks & view assigned hazard warnings", "Psychology"),
    SUPERVISOR("Site Supervisor", "File hazard observations & assign to site workers", "Shield")
}

data class WorkerItem(
    val id: String,
    val name: String,
    val jobTitle: String,
    val currentZone: String
)

val sampleWorkerRoster = emptyList<WorkerItem>()

enum class AuthScreenState {
    SPLASH,
    ONBOARDING,
    LOGIN,
    REGISTER,
    FORGOT_PASSWORD,
    OTP_VERIFICATION,
    ROLE_SELECTION,
    AUTHENTICATED
}

data class UserProfile(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val role: UserRole = UserRole.WORKER,
    val jobTitle: String = "Site Engineer & Inspector",
    val company: String = "BuildTech Construction",
    val jwtToken: String = "",
    val isGoogleAuth: Boolean = false,
    val avatarUrl: String = "",
    val siteLocation: String = "Metro Tower Construction — Active Site",
    val connectedGlassesModel: String = "Ray-Ban Meta Smart Glasses (Gen 2)",
    val glassesBattery: Int = 100,
    val glassesStatus: String = "Connected & Active",
    val language: String = "English (US)",
    val theme: String = "Dark Mode",
    val isBiometricEnabled: Boolean = true,
    val isTelemetryShared: Boolean = true,
    val isLocationTrackingEnabled: Boolean = true,
    val isLoggedOut: Boolean = false
)

