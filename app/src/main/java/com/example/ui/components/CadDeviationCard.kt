package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Grid4x4
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CadDeviationItem
import com.example.ui.theme.MetaBlue
import com.example.ui.theme.StatusError
import com.example.ui.theme.StatusSuccess
import com.example.ui.theme.StatusWarning

@Composable
fun CadDeviationCard(
    deviation: CadDeviationItem,
    isVoicePlaying: Boolean,
    onVoiceFeedbackClick: () -> Unit,
    onResolveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val severityColor = when (deviation.severity) {
        "HIGH" -> StatusError
        "MEDIUM" -> StatusWarning
        else -> MetaBlue
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = if (deviation.isResolved) StatusSuccess.copy(0.4f) else severityColor.copy(0.5f),
                shape = RoundedCornerShape(20.dp)
            )
            .testTag("cad_deviation_card_${deviation.id}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (deviation.isResolved) MaterialTheme.colorScheme.surfaceVariant.copy(0.4f) else MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row
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
                            .background(if (deviation.isResolved) StatusSuccess.copy(0.15f) else severityColor.copy(0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (deviation.isResolved) Icons.Default.CheckCircle else Icons.Default.Warning,
                            contentDescription = null,
                            tint = if (deviation.isResolved) StatusSuccess else severityColor,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = deviation.elementName,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Grid4x4, contentDescription = null, tint = MetaBlue, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = deviation.gridLocation, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (deviation.isResolved) StatusSuccess.copy(0.15f) else severityColor.copy(0.15f)
                ) {
                    Text(
                        text = if (deviation.isResolved) "RESOLVED" else "${deviation.severity} VARIANCE",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (deviation.isResolved) StatusSuccess else severityColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Spec Details & Measurement Comparison
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "CAD BLUEPRINT SPEC", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(text = deviation.cadSpec, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = MetaBlue)
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "AS-BUILT MEASURED", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(text = deviation.asBuiltMeasured, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = severityColor)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Standard Code Reference
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MetaBlue.copy(0.12f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.Shield, contentDescription = null, tint = MetaBlue, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = deviation.oshaBimCode, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MetaBlue)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Row: Voice Feedback to Glasses & Mark Resolved
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Ray-Ban Meta Glasses Voice Feedback Button
                Button(
                    onClick = onVoiceFeedbackClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isVoicePlaying) StatusSuccess else MetaBlue
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1.3f)
                        .height(38.dp)
                        .testTag("voice_feedback_btn_${deviation.id}")
                ) {
                    Icon(imageVector = Icons.Default.VolumeUp, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isVoicePlaying) "SPEAKING TO GLASSES..." else "PLAY GLASSES VOICE HUD",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                // Resolve / Acknowledge Toggle
                OutlinedButton(
                    onClick = onResolveClick,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp)
                        .testTag("resolve_dev_btn_${deviation.id}")
                ) {
                    Text(
                        text = if (deviation.isResolved) "REOPEN" else "MARK RESOLVED",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (deviation.isResolved) StatusSuccess else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}
