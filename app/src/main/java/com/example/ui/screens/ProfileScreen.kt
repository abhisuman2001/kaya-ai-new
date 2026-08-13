package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.ProfileHeaderCard
import com.example.ui.components.ProfileLogoutCard
import com.example.ui.components.ProfilePreferencesCard
import com.example.ui.theme.MetaBlue
import com.example.ui.viewmodel.KayaViewModel

@Composable
fun ProfileScreen(
    viewModel: KayaViewModel,
    modifier: Modifier = Modifier
) {
    val profileState by viewModel.profileState.collectAsStateWithLifecycle()
    val profile = profileState.profile

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp)
            .testTag("profile_screen_list"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(8.dp)) }

        // Header Title
        item {
            Column {
                Text(
                    text = "PROFILE & SETTINGS",
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = MetaBlue,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "User Identity & Account",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Superintendent profile, locale & security controls.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // 1. Profile Header Card
        item {
            ProfileHeaderCard(
                profile = profile,
                onSaveProfile = { updated -> viewModel.saveUserDetailChanges(updated) }
            )
        }

        // 2. System Preferences & Privacy Card
        item {
            ProfilePreferencesCard(
                profile = profile,
                availableLanguages = profileState.availableLanguages,
                availableThemes = profileState.availableThemes,
                onLanguageSelected = { lang -> viewModel.setProfileLanguage(lang) },
                onThemeSelected = { th -> viewModel.setProfileTheme(th) },
                onBiometricToggled = { enabled -> viewModel.toggleBiometricAuth(enabled) },
                onTelemetryToggled = { enabled -> viewModel.toggleDataTelemetry(enabled) },
                onLocationToggled = { enabled -> viewModel.toggleLocationTracking(enabled) }
            )
        }

        // 4. Logout & Account Control Card
        item {
            ProfileLogoutCard(
                profile = profile,
                showLogoutDialog = profileState.showLogoutConfirmationDialog,
                onRequestLogout = { viewModel.setLogoutDialogVisible(true) },
                onCancelLogout = { viewModel.setLogoutDialogVisible(false) },
                onConfirmLogout = { viewModel.logoutUser() },
                onLogin = { viewModel.loginUser() }
            )
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}
