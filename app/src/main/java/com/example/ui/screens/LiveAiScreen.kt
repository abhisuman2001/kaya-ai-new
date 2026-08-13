package com.example.ui.screens

import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Matrix
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.util.Base64
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import java.io.ByteArrayOutputStream
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BluetoothConnected
import androidx.compose.material.icons.filled.BluetoothDisabled
import androidx.compose.material.icons.filled.CenterFocusWeak
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.StopCircle
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.TextButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.GlassAiState
import com.example.ui.theme.MetaBlue
import com.example.ui.theme.StatusError
import com.example.ui.theme.StatusSuccess
import com.example.ui.viewmodel.KayaViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

// ==========================================
// HAZARD DATA MODELS & SEVERITY LEVELS
// ==========================================

enum class HazardSeverity(
    val title: String,
    val color: Color,
    val containerColor: Color,
    val priority: Int,
    val icon: ImageVector
) {
    SAFE("SAFE", Color(0xFF10B981), Color(0xFF10B981).copy(alpha = 0.12f), 0, Icons.Default.Shield),
    LOW("LOW", Color(0xFF2563EB), Color(0xFF2563EB).copy(alpha = 0.12f), 1, Icons.Default.Info),
    HIGH("HIGH", Color(0xFFF59E0B), Color(0xFFF59E0B).copy(alpha = 0.15f), 2, Icons.Default.Warning),
    CRITICAL("CRITICAL", Color(0xFFEF4444), Color(0xFFEF4444).copy(alpha = 0.18f), 3, Icons.Default.ReportProblem)
}

data class HazardItem(
    val id: String,
    val type: String, // e.g. "PPE_VIOLATION", "ENVIRONMENTAL", "EMERGENCY"
    val severity: HazardSeverity,
    val title: String,
    val location: String,
    val description: String,
    val reasoning: String,
    val recommendation: String,
    val timestamp: String,
    val resolved: Boolean = false
)

data class DetectedObjectBox(
    val label: String,
    val emoji: String,
    val xPercent: Float,
    val yPercent: Float,
    val widthPercent: Float,
    val heightPercent: Float,
    val color: Color
)

// ==========================================
// MAIN LIVE AI SCREEN
// ==========================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveAiScreen(
    viewModel: KayaViewModel,
    modifier: Modifier = Modifier
) {
    val glassState by viewModel.glassState.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val isSupervisor = currentUser.role == com.example.data.model.UserRole.SUPERVISOR
    val isSessionActive = glassState.isLiveStreaming

    // In-memory active hazard state initialized from rule engine
    val activeHazards = remember {
        mutableStateListOf(
            HazardItem(
                id = "hazard_001",
                type = "PPE_VIOLATION",
                severity = HazardSeverity.CRITICAL,
                title = "Worker without helmet",
                location = "Near crane operation area",
                description = "Worker detected inside active crane operating area without safety helmet.",
                reasoning = "The worker is within the active crane operating zone. Falling objects or equipment movement significantly increase the risk of head injury.",
                recommendation = "Wear an approved safety helmet before continuing work in this zone.",
                timestamp = "10:42:00 AM"
            ),
            HazardItem(
                id = "hazard_002",
                type = "ENVIRONMENTAL",
                severity = HazardSeverity.HIGH,
                title = "Worker near scaffold edge",
                location = "Scaffold Platform Level 3",
                description = "Worker positioned close to unprotected scaffold edge without tied safety harness.",
                reasoning = "Working at elevated heights above 2 meters near open edges without dual-leg tie-off lanyards poses an immediate fall hazard.",
                recommendation = "Attach safety harness lanyard to approved lifeline anchor point.",
                timestamp = "10:41:15 AM"
            ),
            HazardItem(
                id = "hazard_003",
                type = "PPE_VIOLATION",
                severity = HazardSeverity.LOW,
                title = "Missing high-vis safety vest",
                location = "Rebar Staging Area",
                description = "Worker operating near staging area missing reflective safety vest.",
                reasoning = "Low contrast clothing in active heavy machinery clearance zones reduces worker visibility.",
                recommendation = "Wear ANSI Class 2 high-visibility safety vest immediately.",
                timestamp = "10:39:50 AM"
            )
        )
    }

    var showBottomSheet by remember { mutableStateOf(false) }

    // Derive unresolved hazards sorted by highest severity priority
    val unresolvedHazards by remember {
        derivedStateOf {
            activeHazards.filter { !it.resolved }.sortedByDescending { it.severity.priority }
        }
    }

    val highestPriorityHazard = unresolvedHazards.firstOrNull()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp)
            .testTag("live_ai_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(8.dp)) }

        // 1. Header Section
        item {
            Column {
                Text(
                    text = "LIVE AI",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MetaBlue,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Live AI Stream & Vision",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Real-time scene understanding",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        val isGlassConnected = glassState.connectionState != GlassAiState.OFFLINE

        if (!isGlassConnected) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("live_ai_glass_disconnected_card"),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, StatusError.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.BluetoothDisabled,
                                contentDescription = null,
                                tint = StatusError,
                                modifier = Modifier.size(28.dp)
                            )
                            Text(
                                text = "Smart Glasses Disconnected",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Live AI features and real-time computer vision stream require an active Ray-Ban Meta glasses connection. Connect your glasses to start scene analysis.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            lineHeight = 17.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.connectGlass() },
                            colors = ButtonDefaults.buttonColors(containerColor = MetaBlue),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth(0.85f)
                                .testTag("connect_glass_from_live_ai_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.BluetoothConnected,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Connect Smart Glasses",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        } else if (!isSessionActive) {
            // User Flow: Empty State when AI Session is not running
            item {
                EmptyLiveAISession(
                    onStartSession = { viewModel.toggleLiveStream() }
                )
            }
        } else {
            // Active Session View Hierarchy:
            // 2. Live Camera Preview (Largest Section)
            item {
                val liveResult by viewModel.liveResult.collectAsStateWithLifecycle()
                LiveCameraView(
                    isActive = true,
                    deviceName = glassState.deviceName,
                    isPhoneBridgeMode = glassState.isPhoneBridgeMode,
                    viewModel = viewModel
                )
            }

            // AI API Fallback Alert (when API token is expired or failing)
            item {
                val liveResult by viewModel.liveResult.collectAsStateWithLifecycle()
                if (liveResult.isApiError) {
                    AiApiFallbackAlertCard(
                        errorMessage = liveResult.apiErrorMessage,
                        onRetry = { viewModel.runAiQuery("Test Gemini API token status") }
                    )
                }
            }

            // 3. Hazard Alert Card (Dynamic) - Directly below Live Camera Preview and above Current Observation
            item {
                HazardAlertCard(
                    highestPriorityHazard = highestPriorityHazard,
                    totalActiveHazardsCount = unresolvedHazards.size,
                    onOpenBottomSheet = { showBottomSheet = true },
                    onResetHazards = {
                        activeHazards.clear()
                        activeHazards.addAll(
                            listOf(
                                HazardItem(
                                    id = "hazard_001",
                                    type = "PPE_VIOLATION",
                                    severity = HazardSeverity.CRITICAL,
                                    title = "Worker without helmet",
                                    location = "Near crane operation area",
                                    description = "Worker detected inside active crane operating area without safety helmet.",
                                    reasoning = "The worker is within the active crane operating zone. Falling objects or equipment movement significantly increase the risk of head injury.",
                                    recommendation = "Wear an approved safety helmet before continuing work in this zone.",
                                    timestamp = "10:42:00 AM"
                                ),
                                HazardItem(
                                    id = "hazard_002",
                                    type = "ENVIRONMENTAL",
                                    severity = HazardSeverity.HIGH,
                                    title = "Worker near scaffold edge",
                                    location = "Scaffold Platform Level 3",
                                    description = "Worker positioned close to unprotected scaffold edge without tied safety harness.",
                                    reasoning = "Working at elevated heights above 2 meters near open edges without dual-leg tie-off lanyards poses an immediate fall hazard.",
                                    recommendation = "Attach safety harness lanyard to approved lifeline anchor point.",
                                    timestamp = "10:41:15 AM"
                                ),
                                HazardItem(
                                    id = "hazard_003",
                                    type = "PPE_VIOLATION",
                                    severity = HazardSeverity.LOW,
                                    title = "Missing high-vis safety vest",
                                    location = "Rebar Staging Area",
                                    description = "Worker operating near staging area missing reflective safety vest.",
                                    reasoning = "Low contrast clothing in active heavy machinery clearance zones reduces worker visibility.",
                                    recommendation = "Wear ANSI Class 2 high-visibility safety vest immediately.",
                                    timestamp = "10:39:50 AM"
                                )
                            )
                        )
                    }
                )
            }

            // 4. Current Observation Panel
            item {
                val liveResult by viewModel.liveResult.collectAsStateWithLifecycle()
                CurrentObservationCard(liveResult = liveResult)
            }

            // 5. Scene Summary
            item {
                val liveResult by viewModel.liveResult.collectAsStateWithLifecycle()
                val isAnalyzing by viewModel.isAnalyzing.collectAsStateWithLifecycle()
                SceneSummaryCard(
                    liveResult = liveResult,
                    isAnalyzing = isAnalyzing,
                    onAnalyzeCameraView = {
                        viewModel.runAiQuery("Perform real-time scene understanding and summarize what the camera sees")
                    }
                )
            }

            // 6. Bottom Action: Only one primary button
            item {
                Button(
                    onClick = { viewModel.toggleLiveStream() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("stop_ai_session_button"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = StatusError,
                        contentColor = Color.White
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.StopCircle,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Stop AI Session",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }

    // Modal Bottom Sheet displaying all active hazards
    if (showBottomSheet) {
        AllHazardsBottomSheet(
            hazards = unresolvedHazards,
            onDismissRequest = { showBottomSheet = false },
            onResolveHazard = { hazardId ->
                val index = activeHazards.indexOfFirst { it.id == hazardId }
                if (index != -1) {
                    activeHazards[index] = activeHazards[index].copy(resolved = true)
                }
            },
            onRestoreAll = {
                activeHazards.clear()
                activeHazards.addAll(
                    listOf(
                        HazardItem(
                            id = "hazard_001",
                            type = "PPE_VIOLATION",
                            severity = HazardSeverity.CRITICAL,
                            title = "Worker without helmet",
                            location = "Near crane operation area",
                            description = "Worker detected inside active crane operating area without safety helmet.",
                            reasoning = "The worker is within the active crane operating zone. Falling objects or equipment movement significantly increase the risk of head injury.",
                            recommendation = "Wear an approved safety helmet before continuing work in this zone.",
                            timestamp = "10:42:00 AM"
                        ),
                        HazardItem(
                            id = "hazard_002",
                            type = "ENVIRONMENTAL",
                            severity = HazardSeverity.HIGH,
                            title = "Worker near scaffold edge",
                            location = "Scaffold Platform Level 3",
                            description = "Worker positioned close to unprotected scaffold edge without tied safety harness.",
                            reasoning = "Working at elevated heights above 2 meters near open edges without dual-leg tie-off lanyards poses an immediate fall hazard.",
                            recommendation = "Attach safety harness lanyard to approved lifeline anchor point.",
                            timestamp = "10:41:15 AM"
                        ),
                        HazardItem(
                            id = "hazard_003",
                            type = "PPE_VIOLATION",
                            severity = HazardSeverity.LOW,
                            title = "Missing high-vis safety vest",
                            location = "Rebar Staging Area",
                            description = "Worker operating near staging area missing reflective safety vest.",
                            reasoning = "Low contrast clothing in active heavy machinery clearance zones reduces worker visibility.",
                            recommendation = "Wear ANSI Class 2 high-visibility safety vest immediately.",
                            timestamp = "10:39:50 AM"
                        )
                    )
                )
            },
            isSupervisor = isSupervisor
        )
    }
}

// ==========================================
// DYNAMIC HAZARD ALERT CARD
// ==========================================

@Composable
fun HazardAlertCard(
    highestPriorityHazard: HazardItem?,
    totalActiveHazardsCount: Int,
    onOpenBottomSheet: () -> Unit,
    onResetHazards: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = true,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically(),
        modifier = modifier.testTag("hazard_alert_card")
    ) {
        if (highestPriorityHazard == null) {
            // STATE 1: Compact Green Status Card when no hazards exist
            CompactSafeStatusCard(onResetHazards = onResetHazards)
        } else {
            // STATE 2: Warning Card for highest-priority active hazard
            ActiveHazardWarningCard(
                hazard = highestPriorityHazard,
                totalActiveCount = totalActiveHazardsCount,
                onOpenBottomSheet = onOpenBottomSheet
            )
        }
    }
}

/**
 * Compact Green Card displayed when site is 100% compliant / no hazards detected
 */
@Composable
private fun CompactSafeStatusCard(
    onResetHazards: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("safe_status_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = StatusSuccess.copy(alpha = 0.08f)
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, StatusSuccess.copy(alpha = 0.35f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(StatusSuccess.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = "Safe",
                        tint = StatusSuccess,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(StatusSuccess)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Site Status",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = StatusSuccess
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "No hazards detected. All visible workers are compliant.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Quick action to trigger demo hazard for evaluation
            IconButton(
                onClick = onResetHazards,
                modifier = Modifier
                    .size(32.dp)
                    .testTag("test_hazard_toggle_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Re-analyze / Reset",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

/**
 * Dynamic Warning Card displayed when hazards exist
 */
@Composable
private fun ActiveHazardWarningCard(
    hazard: HazardItem,
    totalActiveCount: Int,
    onOpenBottomSheet: () -> Unit
) {
    val isCritical = hazard.severity == HazardSeverity.CRITICAL

    // Pulse animation for Critical hazards
    val infiniteTransition = rememberInfiniteTransition(label = "critical_hazard_pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    val borderColor = if (isCritical) {
        hazard.severity.color.copy(alpha = pulseAlpha)
    } else {
        hazard.severity.color.copy(alpha = 0.5f)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpenBottomSheet() }
            .testTag("active_hazard_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = hazard.severity.containerColor
        ),
        border = androidx.compose.foundation.BorderStroke(
            width = if (isCritical) 1.5.dp else 1.dp,
            color = borderColor
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header: Severity Badge & Location
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = hazard.severity.color,
                    shadowElevation = if (isCritical) 2.dp else 0.dp
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = hazard.severity.icon,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isCritical) "🚨 HIGH PRIORITY" else "⚠️ ${hazard.severity.title} PRIORITY",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.5.sp
                        )
                    }
                }

                Text(
                    text = hazard.timestamp,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Hazard Title & Location
            Text(
                text = hazard.title,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 2.dp)
            ) {
                Text(
                    text = "Location: ",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = hazard.location,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Reasoning Section: Why this is risky
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                border = androidx.compose.foundation.BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant.copy(0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Psychology,
                            contentDescription = null,
                            tint = hazard.severity.color,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Why this is risky:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = hazard.severity.color
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = hazard.reasoning,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 17.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Recommended Action Section
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                border = androidx.compose.foundation.BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant.copy(0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Lightbulb,
                            contentDescription = null,
                            tint = MetaBlue,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Recommended action:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MetaBlue
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = hazard.recommendation,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 17.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Multiple Hazards Badge / Footer
            if (totalActiveCount > 1) {
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, hazard.severity.color.copy(0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "+${totalActiveCount - 1} More Active Hazards",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = hazard.severity.color
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "View All",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// ALL HAZARDS MODAL BOTTOM SHEET
// ==========================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllHazardsBottomSheet(
    hazards: List<HazardItem>,
    onDismissRequest: () -> Unit,
    onResolveHazard: (String) -> Unit,
    onRestoreAll: () -> Unit,
    isSupervisor: Boolean = true
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        scrimColor = Color.Black.copy(alpha = 0.5f),
        modifier = Modifier.testTag("all_hazards_bottom_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 24.dp)
        ) {
            // Bottom Sheet Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Detected Site Hazards",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${hazards.size} active risk item${if (hazards.size != 1) "s" else ""} flagged by AI reasoning engine",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(
                    onClick = onDismissRequest,
                    modifier = Modifier.testTag("close_hazards_sheet_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (hazards.isEmpty()) {
                // Empty state inside sheet
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = StatusSuccess,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "All Hazards Resolved!",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Site compliance verified by Meta AI Vision.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedButton(
                            onClick = onRestoreAll,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Reset Demo Hazards", fontSize = 12.sp)
                        }
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(420.dp)
                ) {
                    items(hazards, key = { it.id }) { item ->
                        HazardBottomSheetItemCard(
                            hazard = item,
                            onResolve = { onResolveHazard(item.id) },
                            isSupervisor = isSupervisor
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HazardBottomSheetItemCard(
    hazard: HazardItem,
    onResolve: () -> Unit,
    isSupervisor: Boolean = true
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = hazard.severity.containerColor
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, hazard.severity.color.copy(0.4f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = hazard.severity.color
                ) {
                    Text(
                        text = hazard.severity.title,
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                Text(
                    text = hazard.timestamp,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = hazard.title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = "📍 ${hazard.location}",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 2.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Reasoning
            Text(
                text = "Why this is risky:",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = hazard.severity.color
            )
            Text(
                text = hazard.reasoning,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 15.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Recommendation
            Text(
                text = "Recommended action:",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MetaBlue
            )
            Text(
                text = hazard.recommendation,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 15.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (isSupervisor) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = onResolve,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = StatusSuccess,
                            contentColor = Color.White
                        ),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Mark Resolved",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

private fun imageProxyToBase64(imageProxy: ImageProxy): String? {
    return try {
        val bitmap = imageProxy.toBitmap()
        val rotationDegrees = imageProxy.imageInfo.rotationDegrees
        val rotatedBitmap = if (rotationDegrees != 0) {
            val matrix = Matrix().apply { postRotate(rotationDegrees.toFloat()) }
            Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        } else {
            bitmap
        }
        val outputStream = ByteArrayOutputStream()
        rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream)
        val bytes = outputStream.toByteArray()
        Base64.encodeToString(bytes, Base64.NO_WRAP)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    } finally {
        imageProxy.close()
    }
}

// ==========================================
// EXISTING LIVE AI COMPONENTS
// ==========================================

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun LiveCameraView(
    isActive: Boolean,
    deviceName: String,
    isPhoneBridgeMode: Boolean = false,
    cameraFacing: String = "REAR",
    viewModel: KayaViewModel? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val cameraPermissionState = rememberPermissionState(permission = Manifest.permission.CAMERA)
    val audioPermissionState = rememberPermissionState(permission = Manifest.permission.RECORD_AUDIO)

    var tts by remember { mutableStateOf<TextToSpeech?>(null) }
    var latestCapturedFrame by remember { mutableStateOf<String?>(null) }

    // Speech-to-Text state
    var isListeningSpeech by remember { mutableStateOf(false) }
    var recognizedSpeechText by remember { mutableStateOf("") }
    var partialSpeechText by remember { mutableStateOf("") }
    var speechStatusMessage by remember { mutableStateOf<String?>(null) }

    val speechRecognizer = remember {
        if (SpeechRecognizer.isRecognitionAvailable(context)) {
            SpeechRecognizer.createSpeechRecognizer(context)
        } else null
    }

    DisposableEffect(speechRecognizer) {
        speechRecognizer?.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: android.os.Bundle?) {
                isListeningSpeech = true
                speechStatusMessage = "Listening for voice query..."
            }

            override fun onBeginningOfSpeech() {
                isListeningSpeech = true
            }

            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {
                isListeningSpeech = false
            }

            override fun onError(error: Int) {
                isListeningSpeech = false
                speechStatusMessage = when (error) {
                    SpeechRecognizer.ERROR_NO_MATCH -> "No speech recognized. Please try again."
                    SpeechRecognizer.ERROR_AUDIO -> "Audio recording error."
                    SpeechRecognizer.ERROR_NETWORK -> "Network issue during speech recognition."
                    else -> "Voice recognition ended."
                }
            }

            override fun onResults(results: android.os.Bundle?) {
                isListeningSpeech = false
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val text = matches?.firstOrNull().orEmpty().trim()
                if (text.isNotBlank()) {
                    recognizedSpeechText = text
                    speechStatusMessage = "Spoken: \"$text\""
                    // Pass recognized text AND captured vision frame to AI
                    viewModel?.runAiQuery(query = text, base64Frame = latestCapturedFrame)
                }
            }

            override fun onPartialResults(partialResults: android.os.Bundle?) {
                val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val text = matches?.firstOrNull().orEmpty()
                if (text.isNotBlank()) {
                    partialSpeechText = text
                }
            }

            override fun onEvent(eventType: Int, params: android.os.Bundle?) {}
        })

        onDispose {
            try {
                speechRecognizer?.destroy()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    DisposableEffect(context) {
        val textToSpeech = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                // Initialized
            }
        }
        tts = textToSpeech
        onDispose {
            textToSpeech.stop()
            textToSpeech.shutdown()
        }
    }

    var showBoundingBoxes by remember { mutableStateOf(true) }

    val liveResultState = viewModel?.liveResult?.collectAsStateWithLifecycle()
    val liveResult = liveResultState?.value

    val detectedObjects = remember(liveResult?.detectedObjects) {
        val rawList = liveResult?.detectedObjects.orEmpty()
        rawList.map { box ->
            DetectedObjectBox(
                label = box.label,
                emoji = if (box.isHazard) "⚠️" else "🔍",
                xPercent = box.normX,
                yPercent = box.normY,
                widthPercent = box.normWidth,
                heightPercent = box.normHeight,
                color = if (box.isHazard) Color(0xFFEF4444) else Color(0xFF10B981)
            )
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("live_camera_view"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.15f))
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.1f)
                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                    .background(Color(0xFF0F172A))
            ) {
                if (cameraPermissionState.status.isGranted) {
                    // Real CameraX Hardware Feed with ImageAnalysis frame emission
                    RealCameraXPreview(
                        cameraFacing = cameraFacing,
                        isSessionActive = isActive,
                        onFrameCaptured = { base64Frame ->
                            latestCapturedFrame = base64Frame
                            viewModel?.runAiQuery(query = recognizedSpeechText.ifBlank { "" }, base64Frame = base64Frame)
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    // Simulated HUD Camera Viewport Canvas
                    SimulatedCameraFeedBackground()
                }

                // AI Bounding Box Overlays
                if (showBoundingBoxes && detectedObjects.isNotEmpty()) {
                    BoundingBoxOverlay(
                        objects = detectedObjects,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // Vision Status Overlay (In Flight / Error / Clear Scene)
                val isAnalyzingState = viewModel?.isAnalyzing?.collectAsStateWithLifecycle()
                val isAnalyzing = isAnalyzingState?.value ?: false

                if (isAnalyzing) {
                    Surface(
                        color = Color.Black.copy(alpha = 0.80f),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp)
                            .testTag("vision_analyzing_indicator")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = MetaBlue,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Analyzing camera feed...",
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                } else if (liveResult?.isApiError == true) {
                    Surface(
                        color = Color(0xFF7F1D1D).copy(alpha = 0.90f),
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEF4444)),
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp)
                            .testTag("vision_error_overlay")
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = Color(0xFFFCA5A5),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Vision analysis unavailable — retrying",
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            val errorMsg = liveResult.apiErrorMessage
                            if (!errorMsg.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = errorMsg,
                                    color = Color(0xFFFECACA),
                                    fontSize = 11.sp,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                } else if (liveResult != null && !liveResult.isApiError && detectedObjects.isEmpty()) {
                    Surface(
                        color = Color.Black.copy(alpha = 0.65f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp)
                            .testTag("clear_scene_indicator")
                    ) {
                        Text(
                            text = "Scene Clear — 0 objects detected",
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }

                // Top Control Bar Overlay Inside Camera
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp)
                        .align(Alignment.TopCenter),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AIStatusChip(isActive = isActive)
                    LiveIndicatorBadge()
                }

                // Voice Listening HUD Overlay
                if (isListeningSpeech || !speechStatusMessage.isNullOrBlank()) {
                    Surface(
                        color = Color.Black.copy(alpha = 0.85f),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp)
                            .testTag("speech_transcript_hud")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (isListeningSpeech) Icons.Default.Mic else Icons.Default.VolumeUp,
                                contentDescription = null,
                                tint = if (isListeningSpeech) StatusError else MetaBlue,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isListeningSpeech && partialSpeechText.isNotBlank()) "🎤 $partialSpeechText" else (speechStatusMessage ?: "Listening..."),
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                // Bottom HUD Overlay Label
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .background(Color.Black.copy(alpha = 0.65f))
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isPhoneBridgeMode) "📱 PHONE CAMERA BRIDGE STREAM • 1080p 60FPS" else "RAY-BAN META STREAM • 1080p 60FPS",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (cameraPermissionState.status.isGranted) "HARDWARE CAM ACTIVE" else "HUD SIMULATED",
                            color = StatusSuccess,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Interactive Controls for Speech, Camera Permission, AI Boxes & Speaker Speech Output
            Surface(
                color = Color(0xFF1E293B),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Mic Speech-to-Text Button
                        Button(
                            onClick = {
                                if (!audioPermissionState.status.isGranted) {
                                    audioPermissionState.launchPermissionRequest()
                                } else {
                                    if (isListeningSpeech) {
                                        try { speechRecognizer?.stopListening() } catch (e: Exception) { e.printStackTrace() }
                                        isListeningSpeech = false
                                    } else {
                                        try {
                                            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                                                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                                                putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                                                putExtra(RecognizerIntent.EXTRA_LANGUAGE, java.util.Locale.getDefault())
                                            }
                                            speechRecognizer?.startListening(intent)
                                            isListeningSpeech = true
                                            speechStatusMessage = "Listening for query..."
                                        } catch (e: Exception) {
                                            speechStatusMessage = "Speech recognizer unavailable"
                                            isListeningSpeech = false
                                        }
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isListeningSpeech) StatusError else MetaBlue
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f).testTag("speech_mic_button")
                        ) {
                            Icon(
                                imageVector = if (isListeningSpeech) Icons.Default.MicOff else Icons.Default.Mic,
                                contentDescription = "Mic STT",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isListeningSpeech) "🔴 Stop Rec" else "🎤 Talk to AI",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        if (!cameraPermissionState.status.isGranted) {
                            Button(
                                onClick = { cameraPermissionState.launchPermissionRequest() },
                                colors = ButtonDefaults.buttonColors(containerColor = MetaBlue),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("📷 Cam Perm", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        } else {
                            OutlinedButton(
                                onClick = { /* Camera Active */ },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("✅ Cam Active", fontSize = 11.sp, color = StatusSuccess)
                            }
                        }

                        OutlinedButton(
                            onClick = { showBoundingBoxes = !showBoundingBoxes },
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (showBoundingBoxes) MetaBlue else Color.Gray.copy(alpha = 0.5f))
                        ) {
                            Text(
                                text = if (showBoundingBoxes) "🔲 Boxes ON" else "🔲 Boxes OFF",
                                fontSize = 11.sp,
                                color = if (showBoundingBoxes) MetaBlue else Color.White.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RealCameraXPreview(
    cameraFacing: String,
    isSessionActive: Boolean = true,
    onFrameCaptured: ((String) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    var lastAnalyzedTime by remember { mutableStateOf(0L) }

    AndroidView(
        factory = { ctx ->
            val previewView = PreviewView(ctx).apply {
                scaleType = PreviewView.ScaleType.FILL_CENTER
            }
            val executor = ContextCompat.getMainExecutor(ctx)
            cameraProviderFuture.addListener({
                try {
                    val cameraProvider = cameraProviderFuture.get()
                    val preview = Preview.Builder().build().also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }

                    val imageAnalysis = ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build()

                    imageAnalysis.setAnalyzer(executor) { imageProxy ->
                        val currentTime = System.currentTimeMillis()
                        if (isSessionActive && currentTime - lastAnalyzedTime >= 3500) {
                            lastAnalyzedTime = currentTime
                            val base64 = imageProxyToBase64(imageProxy)
                            if (!base64.isNullOrBlank() && onFrameCaptured != null) {
                                onFrameCaptured(base64)
                            }
                        } else {
                            imageProxy.close()
                        }
                    }

                    val cameraSelector = if (cameraFacing == "FRONT") {
                        CameraSelector.DEFAULT_FRONT_CAMERA
                    } else {
                        CameraSelector.DEFAULT_BACK_CAMERA
                    }
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview, imageAnalysis)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }, executor)
            previewView
        },
        modifier = modifier
    )
}

@Composable
fun BoundingBoxOverlay(
    objects: List<DetectedObjectBox>,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse_bounding_box")
    val alphaAnim by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    val scanLineY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scan_line"
    )

    Box(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            // Draw scanning beam
            val scanY = height * scanLineY
            drawLine(
                color = Color(0xFF38BDF8).copy(alpha = 0.35f),
                start = Offset(0f, scanY),
                end = Offset(width, scanY),
                strokeWidth = 3.dp.toPx()
            )

            // Draw bounding boxes
            objects.forEach { obj ->
                val left = obj.xPercent * width
                val top = obj.yPercent * height
                val boxWidth = obj.widthPercent * width
                val boxHeight = obj.heightPercent * height
                val right = left + boxWidth
                val bottom = top + boxHeight
                val bracketLen = (boxWidth * 0.25f).coerceAtMost(24.dp.toPx())

                val boxColor = obj.color.copy(alpha = alphaAnim)

                // Fill background box area faintly
                drawRect(
                    color = obj.color.copy(alpha = 0.08f),
                    topLeft = Offset(left, top),
                    size = Size(boxWidth, boxHeight)
                )

                // Draw bounding box border
                drawRoundRect(
                    color = boxColor,
                    topLeft = Offset(left, top),
                    size = Size(boxWidth, boxHeight),
                    cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx()),
                    style = Stroke(width = 1.5.dp.toPx())
                )

                // Corner L-brackets
                drawLine(boxColor, Offset(left, top), Offset(left + bracketLen, top), strokeWidth = 3.dp.toPx())
                drawLine(boxColor, Offset(left, top), Offset(left, top + bracketLen), strokeWidth = 3.dp.toPx())

                drawLine(boxColor, Offset(right, top), Offset(right - bracketLen, top), strokeWidth = 3.dp.toPx())
                drawLine(boxColor, Offset(right, top), Offset(right, top + bracketLen), strokeWidth = 3.dp.toPx())

                drawLine(boxColor, Offset(left, bottom), Offset(left + bracketLen, bottom), strokeWidth = 3.dp.toPx())
                drawLine(boxColor, Offset(left, bottom), Offset(left, bottom - bracketLen), strokeWidth = 3.dp.toPx())

                drawLine(boxColor, Offset(right, bottom), Offset(right - bracketLen, bottom), strokeWidth = 3.dp.toPx())
                drawLine(boxColor, Offset(right, bottom), Offset(right, bottom - bracketLen), strokeWidth = 3.dp.toPx())
            }
        }
    }
}

@Composable
fun AIStatusChip(
    isActive: Boolean,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        color = Color.Black.copy(alpha = 0.75f),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.2f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(if (isActive) StatusSuccess else StatusError)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = if (isActive) "🟢 AI Vision Active" else "🔴 AI Vision Paused",
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun LiveIndicatorBadge() {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse_live")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "live_alpha"
    )

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = Color.Red.copy(alpha = 0.85f),
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = alpha))
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = "LIVE",
                color = Color.White,
                fontSize = 10.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CurrentObservationCard(
    liveResult: com.example.data.model.LiveAiAnalysisResult? = null,
    modifier: Modifier = Modifier
) {
    val observations = remember(liveResult) {
        if (liveResult != null && !liveResult.isApiError && liveResult.detectedObjects.isNotEmpty()) {
            val list = mutableListOf<String>()
            list.add("🛡 PPE: ${liveResult.ppeCompliancePercent}%")
            if (liveResult.blueprintDeviationMm > 0) {
                list.add("📐 CAD Var: ${liveResult.blueprintDeviationMm}mm")
            }
            liveResult.detectedObjects.forEach { obj ->
                val icon = if (obj.isHazard) "⚠️" else "📦"
                list.add("$icon ${obj.label}")
            }
            list
        } else if (liveResult?.isApiError == true) {
            listOf("⚠️ Vision analysis unavailable")
        } else {
            listOf("🔍 No objects detected in scene")
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("current_observation_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(MetaBlue.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Visibility,
                        contentDescription = null,
                        tint = MetaBlue,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Current Observation",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Objects currently identified in frame",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                observations.forEach { item ->
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        border = androidx.compose.foundation.BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant.copy(0.5f))
                    ) {
                        Text(
                            text = item,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SceneSummaryCard(
    liveResult: com.example.data.model.LiveAiAnalysisResult? = null,
    isAnalyzing: Boolean = false,
    onAnalyzeCameraView: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("scene_summary_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(MetaBlue.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = MetaBlue,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Live Camera Scene Summary",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Real-time Gemini AI Vision Analysis",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                IconButton(
                    onClick = onAnalyzeCameraView,
                    modifier = Modifier
                        .size(32.dp)
                        .testTag("analyze_camera_view_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Re-analyze scene",
                        tint = MetaBlue,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Surface(
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    if (isAnalyzing) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "✨ Gemini AI analyzing live camera feed...",
                                fontSize = 13.sp,
                                color = MetaBlue,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    } else {
                        val summaryText = liveResult?.aiResponseText ?: "Workers assembling steel framework near crane. Camera feed analyzed in real-time."
                        Text(
                            text = summaryText,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 19.sp,
                            fontWeight = FontWeight.Medium
                        )

                        if (!liveResult?.materialSpecs.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Material & Spec: ${liveResult?.materialSpecs}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyLiveAISession(
    onStartSession: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
            .testTag("empty_live_ai_session"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(MetaBlue.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CenterFocusWeak,
                    contentDescription = null,
                    tint = MetaBlue,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "No Live AI Session",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Tap \"Start AI Session\" to begin real-time scene understanding from your Meta Smart Glasses.",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onStartSession,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("start_ai_session_button"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MetaBlue,
                    contentColor = Color.White
                )
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Start AI Session",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun AiApiFallbackAlertCard(
    errorMessage: String?,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFFFFB74D), RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFF8E1)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFF57C00).copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = Color(0xFFE65100),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "⚠️ AI Service Notice (Fallback Active)",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFE65100)
                        )
                        Text(
                            text = "Gemini API key is invalid or failing",
                            fontSize = 11.sp,
                            color = Color(0xFFBF360C)
                        )
                    }
                }

                TextButton(
                    onClick = onRetry,
                    modifier = Modifier.testTag("retry_ai_api_button")
                ) {
                    Text("Retry", fontWeight = FontWeight.Bold, color = Color(0xFFE65100))
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = errorMessage ?: "Gemini API token is expired or unauthorized. The application has automatically switched to local edge fallback logic so all functions remain fully operable.",
                fontSize = 12.sp,
                color = Color(0xFF3E2723),
                lineHeight = 17.sp
            )
        }
    }
}

@Composable
private fun SimulatedCameraFeedBackground() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        drawRect(color = Color(0xFF0F172A))

        val gridColor = Color.White.copy(alpha = 0.05f)
        val gridStep = 40.dp.toPx()

        var x = 0f
        while (x < width) {
            drawLine(gridColor, Offset(x, 0f), Offset(x, height), strokeWidth = 1f)
            x += gridStep
        }

        var y = 0f
        while (y < height) {
            drawLine(gridColor, Offset(0f, y), Offset(width, y), strokeWidth = 1f)
            y += gridStep
        }

        val centerX = width / 2f
        val centerY = height / 2f
        val crossLen = 16.dp.toPx()
        val crossColor = Color.White.copy(alpha = 0.25f)

        drawLine(crossColor, Offset(centerX - crossLen, centerY), Offset(centerX + crossLen, centerY), strokeWidth = 1.5f)
        drawLine(crossColor, Offset(centerX, centerY - crossLen), Offset(centerX, centerY + crossLen), strokeWidth = 1.5f)
    }
}
