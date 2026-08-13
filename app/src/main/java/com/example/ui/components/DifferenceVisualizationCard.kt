package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Architecture
import androidx.compose.material.icons.filled.Compare
import androidx.compose.material.icons.filled.Grid4x4
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.MetaBlue
import com.example.ui.theme.StatusError
import com.example.ui.theme.StatusSuccess
import com.example.ui.theme.StatusWarning

@Composable
fun DifferenceVisualizationCard(
    selectedViewMode: String,
    xOffsetMm: Int,
    yOffsetMm: Int,
    rotationDeg: Double,
    isComparing: Boolean,
    onViewModeChange: (String) -> Unit,
    onRunCompare: () -> Unit,
    modifier: Modifier = Modifier
) {
    val modes = listOf(
        Pair("OVERLAY", "AR Overlay"),
        Pair("HEATMAP", "Deviation Heatmap"),
        Pair("SPLIT_VIEW", "Split View"),
        Pair("REDLINE_WIREFRAME", "Redline Wireframe")
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, MetaBlue.copy(0.4f), RoundedCornerShape(24.dp))
            .testTag("difference_visualization_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
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
                            .background(MetaBlue.copy(0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.Compare, contentDescription = null, tint = MetaBlue, modifier = Modifier.size(18.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "CAD / BIM vs AS-BUILT COMPARISON",
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = MetaBlue
                        )
                        Text(
                            text = "Difference Visualization Engine",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = StatusWarning.copy(0.15f)
                ) {
                    Text(
                        text = "2 VARIANCES",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = StatusWarning,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // View Mode Toggle Bar (Scrollable for small screens)
            androidx.compose.foundation.lazy.LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(modes) { (modeKey, modeLabel) ->
                    val isSelected = selectedViewMode == modeKey
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) MetaBlue else MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier
                            .clickable { onViewModeChange(modeKey) }
                            .testTag("cad_mode_$modeKey")
                    ) {
                        Text(
                            text = modeLabel,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Main Visual Canvas Display (CAD Wireframe + As-Built Camera Grid)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(190.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF0F172A))
                    .border(1.dp, MetaBlue.copy(0.5f), RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                val gridColor = Color(0xFF1E293B)
                val cadColor = MetaBlue
                val asBuiltColor = StatusSuccess
                val devColor = StatusError
                val dashedEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)

                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height

                    // Background Grid Lines
                    for (i in 1..8) {
                        drawLine(gridColor, Offset(w * (i / 9f), 0f), Offset(w * (i / 9f), h), strokeWidth = 1f)
                        drawLine(gridColor, Offset(0f, h * (i / 9f)), Offset(w, h * (i / 9f)), strokeWidth = 1f)
                    }

                    when (selectedViewMode) {
                        "HEATMAP" -> {
                            // Heatmap gradient overlay blocks
                            drawRect(
                                color = StatusError.copy(0.35f),
                                topLeft = Offset(w * 0.25f, h * 0.3f),
                                size = Size(w * 0.2f, h * 0.4f)
                            )
                            drawRect(
                                color = StatusWarning.copy(0.25f),
                                topLeft = Offset(w * 0.55f, h * 0.25f),
                                size = Size(w * 0.25f, h * 0.35f)
                            )
                            drawRect(
                                color = StatusSuccess.copy(0.15f),
                                topLeft = Offset(w * 0.1f, h * 0.1f),
                                size = Size(w * 0.8f, h * 0.8f)
                            )
                        }
                        "SPLIT_VIEW" -> {
                            // Left: CAD Model wireframe, Right: Vision camera frame
                            drawLine(Color.White.copy(0.5f), Offset(w * 0.5f, 0f), Offset(w * 0.5f, h), strokeWidth = 2f, pathEffect = dashedEffect)

                            // Left CAD Wireframe
                            drawRect(cadColor, Offset(w * 0.1f, h * 0.25f), Size(w * 0.35f, h * 0.5f), style = Stroke(2f))
                            // Right As-Built
                            drawRect(asBuiltColor, Offset(w * 0.55f, h * 0.25f), Size(w * 0.35f, h * 0.5f), style = Stroke(2f))
                        }
                        "REDLINE_WIREFRAME" -> {
                            // Pure Redline Wireframe overlay
                            drawRect(devColor, Offset(w * 0.2f, h * 0.2f), Size(w * 0.6f, h * 0.6f), style = Stroke(3f, pathEffect = dashedEffect))
                            drawRect(asBuiltColor, Offset(w * 0.22f, h * 0.2f), Size(w * 0.58f, h * 0.6f), style = Stroke(2f))
                        }
                        else -> { // "OVERLAY"
                            // CAD Spec (Blue Wireframe)
                            drawRect(cadColor, Offset(w * 0.2f, h * 0.25f), Size(w * 0.6f, h * 0.5f), style = Stroke(2f, pathEffect = dashedEffect))

                            // As-Built Physical Position (Green Solid)
                            drawRect(asBuiltColor, Offset(w * 0.23f, h * 0.25f), Size(w * 0.59f, h * 0.5f), style = Stroke(2f))

                            // Deviation Highlight Box (Red Offset)
                            drawRect(devColor, Offset(w * 0.2f, h * 0.25f), Size(w * 0.03f, h * 0.5f))
                        }
                    }
                }

                // Top Floating HUD Labels
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Surface(shape = RoundedCornerShape(6.dp), color = Color.Black.copy(0.7f)) {
                            Text(
                                text = "MODE: $selectedViewMode",
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = MetaBlue,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }

                        Surface(shape = RoundedCornerShape(6.dp), color = Color.Black.copy(0.7f)) {
                            Text(
                                text = "GRID B-4 • L18 DECK",
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                    }

                    // Bottom Legend Overlay
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(8.dp).background(MetaBlue, CircleShape))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("CAD SPEC", fontSize = 8.sp, fontFamily = FontFamily.Monospace, color = Color.White)
                            Spacer(modifier = Modifier.width(10.dp))
                            Box(modifier = Modifier.size(8.dp).background(StatusSuccess, CircleShape))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("AS-BUILT", fontSize = 8.sp, fontFamily = FontFamily.Monospace, color = Color.White)
                            Spacer(modifier = Modifier.width(10.dp))
                            Box(modifier = Modifier.size(8.dp).background(StatusError, CircleShape))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("DEVIATION (+14mm)", fontSize = 8.sp, fontFamily = FontFamily.Monospace, color = StatusError)
                        }

                        Text(
                            text = "OFF: X=${xOffsetMm}mm Y=${yOffsetMm}mm R=${rotationDeg}°",
                            fontSize = 8.sp,
                            fontFamily = FontFamily.Monospace,
                            color = MetaBlue
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action Button: Re-run Compare Scan
            Button(
                onClick = onRunCompare,
                colors = ButtonDefaults.buttonColors(containerColor = MetaBlue),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .testTag("run_cad_compare_button")
            ) {
                if (isComparing) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White, strokeWidth = 2.dp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("ALIGNING CAD MODEL TO VISION FEED...", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                } else {
                    Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("RE-COMPARE CAMERA FEED VS CAD/IFC MODEL", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
