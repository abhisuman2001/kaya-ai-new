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
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Hearing
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.VolumeUp
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.MetaBlue
import com.example.ui.theme.StatusError
import com.example.ui.theme.StatusSuccess

@Composable
fun VoiceFirstHudCard(
    isListening: Boolean,
    isThinking: Boolean,
    isSpeaking: Boolean,
    isHandsFreeEnabled: Boolean,
    onToggleHandsFree: () -> Unit,
    onMicTap: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "voice_wave_anim")
    val waveScale1 by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "wave_scale_1"
    )

    val waveScale2 by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 0.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "wave_scale_2"
    )

    val waveScale3 by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "wave_scale_3"
    )

    val stateColor = when {
        isListening -> StatusError
        isThinking -> MetaBlue
        isSpeaking -> StatusSuccess
        else -> MetaBlue
    }

    val stateText = when {
        isListening -> "LISTENING TO RAY-BAN GLASSES MIC..."
        isThinking -> "SYNTHESIZING SITE SAFETY CONTEXT..."
        isSpeaking -> "BROADCASTING AUDIO TO GLASSES SPEAKER..."
        else -> "RAY-BAN META VOICE HUB ACTIVE (\"HEY META\")"
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.5.dp, stateColor.copy(0.6f), RoundedCornerShape(24.dp))
            .testTag("voice_first_hud_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Status Bar: Mode Toggle & Wake Word Indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (isHandsFreeEnabled) MetaBlue else MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier
                        .clickable { onToggleHandsFree() }
                        .testTag("toggle_hands_free_button")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Hearing,
                            contentDescription = null,
                            tint = if (isHandsFreeEnabled) Color.White else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isHandsFreeEnabled) "HANDS-FREE ACTIVE" else "PUSH-TO-TALK MODE",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isHandsFreeEnabled) Color.White else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = stateColor.copy(0.15f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = when {
                                isListening -> Icons.Default.Mic
                                isThinking -> Icons.Default.Psychology
                                isSpeaking -> Icons.Default.VolumeUp
                                else -> Icons.Default.GraphicEq
                            },
                            contentDescription = null,
                            tint = stateColor,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isListening) "REC 16kHz" else if (isSpeaking) "TTS 24kHz" else "HUD LINK",
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = stateColor
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Waveform Graphic Visualizer
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val heights = listOf(
                    waveScale1, waveScale2, waveScale3, waveScale1 * 0.7f,
                    waveScale2 * 1.1f, waveScale3 * 0.8f, waveScale1,
                    waveScale2, waveScale3, waveScale1 * 0.9f
                )

                heights.forEachIndexed { idx, factor ->
                    val barHeight = (8 + (factor * 38)).dp
                    Box(
                        modifier = Modifier
                            .width(6.dp)
                            .height(if (isListening || isThinking || isSpeaking) barHeight else 12.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(stateColor, stateColor.copy(alpha = 0.4f))
                                )
                            )
                    )
                    if (idx < heights.size - 1) {
                        Spacer(modifier = Modifier.width(6.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = stateText,
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = stateColor
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Center PTT / Voice Action Button
            Button(
                onClick = onMicTap,
                colors = ButtonDefaults.buttonColors(
                    containerColor = stateColor
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("voice_hud_mic_button")
            ) {
                Icon(
                    imageVector = if (isListening) Icons.Default.Mic else Icons.Default.RecordVoiceOver,
                    contentDescription = "Voice Input",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isListening) "TAP TO FINISH SPEAKING" else "HOLD OR TAP TO TALK TO AI",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
