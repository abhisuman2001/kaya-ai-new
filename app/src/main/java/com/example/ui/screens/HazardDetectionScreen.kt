package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.UserRole
import com.example.ui.components.CreateHazardDialog
import com.example.ui.components.HazardCard
import com.example.ui.components.HazardCategoryFilterChips
import com.example.ui.components.VoiceHazardCommandCard
import com.example.ui.theme.MetaBlue
import com.example.ui.theme.StatusError
import com.example.ui.theme.StatusSuccess
import com.example.ui.viewmodel.KayaViewModel

@Composable
fun HazardDetectionScreen(
    viewModel: KayaViewModel,
    modifier: Modifier = Modifier
) {
    val hazardState by viewModel.hazardDetectionState.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    var showCreateDialog by remember { mutableStateOf(false) }

    val isSupervisor = currentUser.role == UserRole.SUPERVISOR

    val filteredHazards = hazardState.hazards.filter { hazard ->
        val catMatch = hazardState.selectedCategoryFilter == null || hazard.category == hazardState.selectedCategoryFilter
        val sevMatch = hazardState.selectedSeverityFilter == null || hazard.severity.equals(hazardState.selectedSeverityFilter, ignoreCase = true)
        catMatch && sevMatch
    }

    val activeCount = hazardState.hazards.count { !it.isAcknowledged }
    val criticalCount = hazardState.hazards.count { it.severity == "CRITICAL" && !it.isAcknowledged }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp)
            .testTag("hazard_detection_screen_list"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(8.dp)) }

        // Top Header Title & Create Button
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "OPTICAL AI HAZARD ENGINE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MetaBlue,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Hazard Detection & Safety Matrix",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                if (isSupervisor) {
                    Button(
                        onClick = { showCreateDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = StatusError),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("create_hazard_button")
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Add Hazard", tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("FILE HAZARD", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Summary Statistics Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(20.dp))
                    .testTag("hazard_summary_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(if (criticalCount > 0) StatusError.copy(0.15f) else StatusSuccess.copy(0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (criticalCount > 0) Icons.Default.Warning else Icons.Default.Shield,
                                contentDescription = null,
                                tint = if (criticalCount > 0) StatusError else StatusSuccess,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = "$activeCount Active Hazard Observations",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "$criticalCount Critical • Helmet, Vest, Fall, Crane, Fire",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MetaBlue.copy(0.15f)
                    ) {
                        Text(
                            text = "100% OSHA 1926",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = MetaBlue,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        // Voice Command Feature to File Hazard
        item {
            VoiceHazardCommandCard(
                onProcessVoiceCommand = { voiceCmd ->
                    viewModel.processVoiceHazardCommand(voiceCmd)
                },
                lastVoiceFiledHazard = hazardState.lastVoiceFiledHazard,
                voiceFeedbackMessage = hazardState.voiceCommandFeedbackMessage,
                onDismissFeedback = { viewModel.clearVoiceCommandFeedback() }
            )
        }

        // Voice Alert Active Playing Banner
        item {
            AnimatedVisibility(visible = hazardState.activeVoicePlayingId != null) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = StatusError,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("voice_alert_playing_banner")
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.VolumeUp, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "RAY-BAN GLASSES AUDIO ACTIVE",
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = Color.White.copy(0.85f)
                            )
                            Text(
                                text = "Broadcasting voice alert to subcontractor headset...",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                        Button(
                            onClick = { viewModel.stopVoiceAlert() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("STOP", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = StatusError)
                        }
                    }
                }
            }
        }

        // Category & Severity Filter Chips
        item {
            HazardCategoryFilterChips(
                selectedCategory = hazardState.selectedCategoryFilter,
                selectedSeverity = hazardState.selectedSeverityFilter,
                onCategorySelect = { cat -> viewModel.setHazardCategoryFilter(cat) },
                onSeveritySelect = { sev -> viewModel.setHazardSeverityFilter(sev) }
            )
        }

        // Hazard List Items
        if (filteredHazards.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 20.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(0.4f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = StatusSuccess, modifier = Modifier.size(36.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "No Hazards Found for Selected Filters", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                        Text(text = "All site workers and equipment conform to OSHA safety guidelines.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        } else {
            items(
                items = filteredHazards,
                key = { it.id }
            ) { hazard ->
                HazardCard(
                    hazard = hazard,
                    isVoicePlaying = hazardState.activeVoicePlayingId == hazard.id,
                    onVoiceAlertClick = { viewModel.playVoiceAlert(hazard.id) },
                    onDismissClick = { viewModel.dismissHazardItem(hazard.id) },
                    onReportClick = { viewModel.reportHazardToDb(hazard) },
                    isSupervisor = isSupervisor
                )
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }

    // Modal Create Hazard Observation Dialog - Strictly restricted to Supervisor
    if (showCreateDialog && isSupervisor) {
        CreateHazardDialog(
            onDismiss = { showCreateDialog = false },
            isSupervisor = true,
            onCreate = { title, cat, sev, loc, osha, desc, workerId, workerName ->
                viewModel.createHazardItem(title, cat, sev, loc, osha, desc, workerId, workerName)
            }
        )
    }
}
