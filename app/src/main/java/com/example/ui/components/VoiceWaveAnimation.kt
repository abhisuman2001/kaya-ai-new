package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.VolumeUp
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.GlassAiState
import com.example.ui.theme.MetaBlue
import com.example.ui.theme.StatusError
import com.example.ui.theme.StatusSuccess
import com.example.ui.theme.StatusWarning

@Composable
fun VoiceWaveAnimationCard(
    aiState: GlassAiState,
    onSimulateListening: () -> Unit,
    onSimulateThinking: () -> Unit,
    onSimulateSpeaking: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(
                1.dp,
                when (aiState) {
                    GlassAiState.LISTENING -> MetaBlue
                    GlassAiState.THINKING -> StatusWarning
                    GlassAiState.SPEAKING -> StatusSuccess
                    else -> MaterialTheme.colorScheme.outlineVariant
                },
                RoundedCornerShape(20.dp)
            )
            .testTag("voice_wave_animation_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row with State Indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = when (aiState) {
                            GlassAiState.LISTENING -> Icons.Default.Mic
                            GlassAiState.THINKING -> Icons.Default.Psychology
                            GlassAiState.SPEAKING -> Icons.Default.VolumeUp
                            else -> Icons.Default.Mic
                        },
                        contentDescription = null,
                        tint = when (aiState) {
                            GlassAiState.LISTENING -> MetaBlue
                            GlassAiState.THINKING -> StatusWarning
                            GlassAiState.SPEAKING -> StatusSuccess
                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        modifier = Modifier.size(20.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "RAY-BAN VOICE MATRIX • ${aiState.label.uppercase()}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        letterSpacing = 1.sp
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = when (aiState) {
                        GlassAiState.LISTENING -> MetaBlue.copy(0.15f)
                        GlassAiState.THINKING -> StatusWarning.copy(0.15f)
                        GlassAiState.SPEAKING -> StatusSuccess.copy(0.15f)
                        else -> MaterialTheme.colorScheme.surfaceVariant
                    }
                ) {
                    Text(
                        text = aiState.description,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = when (aiState) {
                            GlassAiState.LISTENING -> MetaBlue
                            GlassAiState.THINKING -> StatusWarning
                            GlassAiState.SPEAKING -> StatusSuccess
                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Animated Waveform Bars
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF0F172A)),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val barCount = 18
                    val activeColor = when (aiState) {
                        GlassAiState.LISTENING -> MetaBlue
                        GlassAiState.THINKING -> StatusWarning
                        GlassAiState.SPEAKING -> StatusSuccess
                        else -> Color.Gray.copy(0.4f)
                    }

                    val infiniteTransition = rememberInfiniteTransition(label = "waveform")

                    for (i in 0 until barCount) {
                        val durationMs = 400 + (i % 5) * 120
                        val targetHeight = if (aiState != GlassAiState.IDLE && aiState != GlassAiState.OFFLINE) {
                            0.2f + ((i * 7) % 10) * 0.08f
                        } else 0.15f

                        val animatedHeightScale by infiniteTransition.animateFloat(
                            initialValue = 0.15f,
                            targetValue = if (aiState != GlassAiState.IDLE && aiState != GlassAiState.OFFLINE) targetHeight else 0.15f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(durationMs, easing = FastOutSlowInEasing),
                                repeatMode = RepeatMode.Reverse
                            ),
                            label = "bar_$i"
                        )

                        Box(
                            modifier = Modifier
                                .width(6.dp)
                                .height((48 * animatedHeightScale).dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(activeColor, activeColor.copy(0.5f))
                                    )
                                )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Interactive Mode Selector Row (Voice Listening / Thinking / Speaking Toggles)
            Text(
                text = "SIMULATE GLASSES VOICE STATE:",
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                VoiceStateChip(
                    label = "🎤 Listening",
                    isSelected = aiState == GlassAiState.LISTENING,
                    activeColor = MetaBlue,
                    onClick = onSimulateListening,
                    modifier = Modifier.weight(1f)
                )

                VoiceStateChip(
                    label = "🧠 Thinking",
                    isSelected = aiState == GlassAiState.THINKING,
                    activeColor = StatusWarning,
                    onClick = onSimulateThinking,
                    modifier = Modifier.weight(1f)
                )

                VoiceStateChip(
                    label = "🔊 Speaking",
                    isSelected = aiState == GlassAiState.SPEAKING,
                    activeColor = StatusSuccess,
                    onClick = onSimulateSpeaking,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun VoiceStateChip(
    label: String,
    isSelected: Boolean,
    activeColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = if (isSelected) activeColor.copy(0.2f) else MaterialTheme.colorScheme.surfaceVariant,
        modifier = modifier
            .border(
                1.dp,
                if (isSelected) activeColor else Color.Transparent,
                RoundedCornerShape(10.dp)
            )
            .clickable { onClick() }
            .testTag("voice_state_chip_${label.take(8).lowercase().replace(" ", "_")}")
    ) {
        Box(
            modifier = Modifier.padding(vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) activeColor else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
