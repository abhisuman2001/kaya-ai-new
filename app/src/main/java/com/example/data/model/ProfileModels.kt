package com.example.data.model

data class ProfileState(
    val profile: UserProfile = UserProfile(),
    val availableLanguages: List<String> = listOf("English (US)", "Spanish (ES)", "German (DE)", "French (FR)", "Japanese (JP)"),
    val availableThemes: List<String> = listOf("Dark Mode", "Light Mode"),
    val showLogoutConfirmationDialog: Boolean = false
)
