package com.example.ui.components

import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.NotificationSettings
import com.example.ui.theme.MetaBlue

@Composable
fun NotificationSettingsCard(
    settings: NotificationSettings,
    onUpdateSettings: (push: Boolean?, aiVision: Boolean?, hazards: Boolean?, reports: Boolean?, firmware: Boolean?, tasks: Boolean?) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(20.dp))
            .testTag("notification_settings_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.Settings, contentDescription = null, tint = MetaBlue, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "DISPATCH & HUD NOTIFICATION PREFERENCES",
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = MetaBlue
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Master Push Switch
            SettingToggleRow(
                title = "Push Notifications Master",
                subtitle = "Receive HUD earbud alerts & lock screen popups",
                isChecked = settings.isPushNotificationsEnabled,
                onCheckedChange = { onUpdateSettings(it, null, null, null, null, null) },
                testTag = "toggle_master_push"
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(0.5f))

            // AI Vision Alerts
            SettingToggleRow(
                title = "AI Vision Spatial Anomaly Alerts",
                subtitle = "Notify on geometry deviations & CAD clashes",
                isChecked = settings.isAiVisionAlertsEnabled,
                onCheckedChange = { onUpdateSettings(null, it, null, null, null, null) },
                testTag = "toggle_ai_vision"
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(0.5f))

            // Hazards Alerts
            SettingToggleRow(
                title = "Critical Safety & Hazard Popups",
                subtitle = "Immediate audio chime for PPE breaches & edge risk",
                isChecked = settings.isCriticalHazardAlertsEnabled,
                onCheckedChange = { onUpdateSettings(null, null, it, null, null, null) },
                testTag = "toggle_hazards"
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(0.5f))

            // Report Digests
            SettingToggleRow(
                title = "Daily / Weekly Report Digests",
                subtitle = "Automatic DPR summary push when shift ends",
                isChecked = settings.isReportDigestEnabled,
                onCheckedChange = { onUpdateSettings(null, null, null, it, null, null) },
                testTag = "toggle_reports"
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(0.5f))

            // Firmware Updates
            SettingToggleRow(
                title = "Ray-Ban OS Firmware OTA Alerts",
                subtitle = "Prompt when new smart glasses update is available",
                isChecked = settings.isFirmwareUpdateAlertsEnabled,
                onCheckedChange = { onUpdateSettings(null, null, null, null, it, null) },
                testTag = "toggle_firmware"
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(0.5f))

            // Task Assignment
            SettingToggleRow(
                title = "Task Assignment & Punchlist Push",
                subtitle = "Notify when sub-contractor or QA assigns task",
                isChecked = settings.isTaskAssignmentAlertsEnabled,
                onCheckedChange = { onUpdateSettings(null, null, null, null, null, it) },
                testTag = "toggle_tasks"
            )
        }
    }
}

@Composable
private fun SettingToggleRow(
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
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            Text(text = subtitle, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
