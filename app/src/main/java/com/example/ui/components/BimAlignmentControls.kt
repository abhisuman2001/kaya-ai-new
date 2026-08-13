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
import androidx.compose.material.icons.filled.CenterFocusStrong
import androidx.compose.material.icons.filled.RestartAlt
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.MetaBlue

@Composable
fun BimAlignmentControls(
    xOffsetMm: Int,
    yOffsetMm: Int,
    rotationDeg: Double,
    onAdjustAlignment: (dx: Int, dy: Int, dRot: Double) -> Unit,
    onResetAlignment: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(20.dp))
            .testTag("bim_alignment_controls_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.CenterFocusStrong, contentDescription = null, tint = MetaBlue, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "AR MATRIX ALIGNMENT & CALIBRATION",
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = MetaBlue
                    )
                }

                OutlinedButton(
                    onClick = onResetAlignment,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("reset_alignment_button")
                ) {
                    Icon(imageVector = Icons.Default.RestartAlt, contentDescription = null, tint = MetaBlue, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Zero Reset", fontSize = 10.sp, color = MetaBlue, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Adjustment Row: X-Offset (mm)
            AlignmentRow(
                label = "X-AXIS (EAST/WEST)",
                value = "${if (xOffsetMm >= 0) "+$xOffsetMm" else "$xOffsetMm"} mm",
                onDecrement = { onAdjustAlignment(-1, 0, 0.0) },
                onIncrement = { onAdjustAlignment(1, 0, 0.0) },
                tagPrefix = "align_x"
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Adjustment Row: Y-Offset (mm)
            AlignmentRow(
                label = "Y-AXIS (NORTH/SOUTH)",
                value = "${if (yOffsetMm >= 0) "+$yOffsetMm" else "$yOffsetMm"} mm",
                onDecrement = { onAdjustAlignment(0, -1, 0.0) },
                onIncrement = { onAdjustAlignment(0, 1, 0.0) },
                tagPrefix = "align_y"
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Adjustment Row: Rotation (deg)
            AlignmentRow(
                label = "CAMERA ROTATION",
                value = "${if (rotationDeg >= 0) "+$rotationDeg" else "$rotationDeg"}°",
                onDecrement = { onAdjustAlignment(0, 0, -0.1) },
                onIncrement = { onAdjustAlignment(0, 0, 0.1) },
                tagPrefix = "align_rot"
            )
        }
    }
}

@Composable
private fun AlignmentRow(
    label: String,
    value: String,
    onDecrement: () -> Unit,
    onIncrement: () -> Unit,
    tagPrefix: String
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = label, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(text = value, fontSize = 12.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, color = MetaBlue)
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.testTag("${tagPrefix}_dec")
                ) {
                    IconButton(onClick = onDecrement, modifier = Modifier.size(32.dp)) {
                        Text("-", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.testTag("${tagPrefix}_inc")
                ) {
                    IconButton(onClick = onIncrement, modifier = Modifier.size(32.dp)) {
                        Text("+", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    }
                }
            }
        }
    }
}
