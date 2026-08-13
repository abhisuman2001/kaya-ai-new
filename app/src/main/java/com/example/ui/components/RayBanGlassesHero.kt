package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.BluetoothConnected
import androidx.compose.material.icons.filled.BluetoothDisabled
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Videocam
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.R
import com.example.data.model.GlassAiState
import com.example.data.model.GlassDeviceState
import com.example.ui.theme.BorderDark
import com.example.ui.theme.GlassAnalyzing
import com.example.ui.theme.GlassCharging
import com.example.ui.theme.GlassConnected
import com.example.ui.theme.GlassIdle
import com.example.ui.theme.GlassListening
import com.example.ui.theme.GlassOffline
import com.example.ui.theme.GlassSpeaking
import com.example.ui.theme.GlassThinking
import com.example.ui.theme.MetaBlue
import com.example.ui.theme.SiteSurfaceDark
import com.example.ui.theme.StatusError
import com.example.ui.theme.StatusSuccess

@Composable
fun RayBanGlassesHero(
    deviceState: GlassDeviceState,
    onStateSelect: (GlassAiState) -> Unit,
    onToggleConnection: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val stateColor by animateColorAsState(
        targetValue = when (deviceState.connectionState) {
            GlassAiState.IDLE -> GlassIdle
            GlassAiState.CONNECTED -> GlassConnected
            GlassAiState.LISTENING -> GlassListening
            GlassAiState.THINKING -> GlassThinking
            GlassAiState.ANALYZING -> GlassAnalyzing
            GlassAiState.SPEAKING -> GlassSpeaking
            GlassAiState.CHARGING -> GlassCharging
            GlassAiState.OFFLINE -> GlassOffline
        },
        label = "glass_state_color"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "pulse_transition")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(24.dp))
            .testTag("rayban_glasses_hero_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Top Bar: Ray-Ban Meta Branding + Status Pill
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f, fill = false),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(stateColor)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "RAY-BAN META SMART GLASSES",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val batteryColor = when {
                            deviceState.batteryPercent > 50 -> Color(0xFF22C55E)
                            deviceState.batteryPercent > 20 -> Color(0xFFEAB308)
                            else -> Color(0xFFEF4444)
                        }

                        if (deviceState.isCharging) {
                            Text(
                                text = "⚡",
                                fontSize = 10.sp,
                                modifier = Modifier.padding(end = 4.dp)
                            )
                        }

                        // Battery visual bar representation
                        Box(
                            modifier = Modifier
                                .width(20.dp)
                                .height(10.dp)
                                .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f), RoundedCornerShape(2.5.dp))
                                .padding(1.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .fillMaxWidth((deviceState.batteryPercent.coerceIn(0, 100)) / 100f)
                                    .clip(RoundedCornerShape(1.dp))
                                    .background(batteryColor)
                            )
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        Text(
                            text = "${deviceState.batteryPercent}% (${deviceState.batteryHealth})",
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Ray-Ban Meta Smart Glasses Video Display
            KayaVideoPlayer(
                badgeText = "GLASSES AI • ${deviceState.connectionState.label}",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Quick Info Chips (Mobile Battery, Camera/HUD, Battery Health)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                InfoChip(
                    icon = Icons.Default.BatteryChargingFull,
                    label = "Mobile Battery",
                    value = "${deviceState.batteryPercent}% • ${if (deviceState.isCharging) "Charging" else "On Battery"}",
                    modifier = Modifier.weight(1.2f)
                )
                InfoChip(
                    icon = Icons.Default.Videocam,
                    label = "12MP Camera",
                    value = if (deviceState.isLiveStreaming) "1080p HUD" else "Standby",
                    modifier = Modifier.weight(1f)
                )
                InfoChip(
                    icon = Icons.Default.Mic,
                    label = "Battery Health",
                    value = "${deviceState.batteryHealth} (${deviceState.tempCelsius}°C)",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Interactive Connect / Disconnect Action Control
            val isConnected = deviceState.connectionState != GlassAiState.OFFLINE

            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (isConnected) "GLASSES LINK ACTIVE" else "GLASSES DISCONNECTED",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isConnected) StatusSuccess else StatusError
                        )
                        Text(
                            text = if (isConnected) "Paired to Meta Wayfarer" else "Tap button to pair glasses",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Button(
                        onClick = { onToggleConnection() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isConnected) StatusError.copy(alpha = 0.2f) else MetaBlue,
                            contentColor = if (isConnected) StatusError else Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        border = if (isConnected) androidx.compose.foundation.BorderStroke(1.dp, StatusError) else null,
                        modifier = Modifier.testTag("toggle_glass_connection_button")
                    ) {
                        Icon(
                            imageVector = if (isConnected) Icons.Default.BluetoothDisabled else Icons.Default.BluetoothConnected,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isConnected) "Disconnect" else "Connect Glass",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = MetaBlue,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = label,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 9.sp,
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
                Text(
                    text = value,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun StatePill(
    state: GlassAiState,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(10.dp),
        color = if (isSelected) MetaBlue else MaterialTheme.colorScheme.surfaceVariant,
        border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Box(
            modifier = Modifier
                .padding(vertical = 6.dp)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = state.label,
                fontSize = 10.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
