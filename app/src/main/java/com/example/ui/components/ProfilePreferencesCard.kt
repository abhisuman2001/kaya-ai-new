package com.example.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserProfile
import com.example.ui.theme.MetaBlue

@Composable
fun ProfilePreferencesCard(
    profile: UserProfile,
    availableLanguages: List<String>,
    availableThemes: List<String>,
    onLanguageSelected: (String) -> Unit,
    onThemeSelected: (String) -> Unit,
    onBiometricToggled: (Boolean) -> Unit,
    onTelemetryToggled: (Boolean) -> Unit,
    onLocationToggled: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var languageExpanded by remember { mutableStateOf(false) }
    var themeExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(20.dp))
            .testTag("profile_preferences_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = "SYSTEM PREFERENCES & PRIVACY",
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = MetaBlue
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Language Selector Dropdown
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Language, contentDescription = null, tint = MetaBlue, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text("App & HUD Language", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                        Text("Voice synthesis & UI text", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier
                        .clickable { languageExpanded = true }
                        .testTag("language_dropdown_trigger")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(profile.language, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MetaBlue)
                        Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = null, tint = MetaBlue, modifier = Modifier.size(16.dp))
                    }

                    DropdownMenu(
                        expanded = languageExpanded,
                        onDismissRequest = { languageExpanded = false }
                    ) {
                        availableLanguages.forEach { lang ->
                            DropdownMenuItem(
                                text = { Text(lang, fontSize = 12.sp) },
                                onClick = {
                                    onLanguageSelected(lang)
                                    languageExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(0.5f))

            // Theme Selector Dropdown & Switch
            val isDarkMode = profile.theme.contains("Dark", ignoreCase = true)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Palette, contentDescription = null, tint = MetaBlue, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text("Dark Mode Theme", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                        Text(if (isDarkMode) "Dark Slate (Active)" else "Light Mode (Active)", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier
                            .clickable { themeExpanded = true }
                            .testTag("theme_dropdown_trigger")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(if (isDarkMode) "Dark" else "Light", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MetaBlue)
                            Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = null, tint = MetaBlue, modifier = Modifier.size(16.dp))
                        }

                        DropdownMenu(
                            expanded = themeExpanded,
                            onDismissRequest = { themeExpanded = false }
                        ) {
                            availableThemes.forEach { th ->
                                DropdownMenuItem(
                                    text = { Text(th, fontSize = 12.sp) },
                                    onClick = {
                                        onThemeSelected(th)
                                        themeExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Switch(
                        checked = isDarkMode,
                        onCheckedChange = { checked ->
                            onThemeSelected(if (checked) "Dark Mode" else "Light Mode")
                        },
                        colors = SwitchDefaults.colors(checkedThumbColor = MetaBlue, checkedTrackColor = MetaBlue.copy(0.3f)),
                        modifier = Modifier.testTag("toggle_dark_mode")
                    )
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(0.5f))

            // Biometric Auth Switch
            PreferenceToggleRow(
                icon = Icons.Default.Lock,
                title = "Biometric Lock (Face/Fingerprint)",
                subtitle = "Require authentication to launch Kaya HUD",
                isChecked = profile.isBiometricEnabled,
                onCheckedChange = onBiometricToggled,
                testTag = "toggle_biometric"
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(0.5f))

            // AI Data Telemetry Switch
            PreferenceToggleRow(
                icon = Icons.Default.PrivacyTip,
                title = "Anonymized AI Model Telemetry",
                subtitle = "Share spatial point cloud telemetry for fine-tuning",
                isChecked = profile.isTelemetryShared,
                onCheckedChange = onTelemetryToggled,
                testTag = "toggle_telemetry"
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(0.5f))

            // Site GPS Tracking Switch
            PreferenceToggleRow(
                icon = Icons.Default.PrivacyTip,
                title = "High-Precision Site GPS Location",
                subtitle = "Geofenced safety zone warnings on construction site",
                isChecked = profile.isLocationTrackingEnabled,
                onCheckedChange = onLocationToggled,
                testTag = "toggle_location"
            )
        }
    }
}

@Composable
private fun PreferenceToggleRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    testTag: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = null, tint = MetaBlue, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(text = title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                Text(text = subtitle, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        Spacer(modifier = Modifier.width(8.dp))
        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(checkedThumbColor = MetaBlue, checkedTrackColor = MetaBlue.copy(0.3f)),
            modifier = Modifier.testTag(testTag)
        )
    }
}
