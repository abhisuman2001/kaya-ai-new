package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.LiveAiAnalysisResult
import com.example.data.model.VisionBoundingBox
import com.example.ui.theme.MetaBlue
import com.example.ui.theme.StatusError
import com.example.ui.theme.StatusSuccess

@Composable
fun LiveCameraHUDOverlay(
    analysisResult: LiveAiAnalysisResult,
    isAnalyzing: Boolean,
    onCaptureSnapshot: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "hud_scan")
    val scanLineY by infiniteTransition.animateFloat(
        initialValue = 0.1f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scanLine"
    )

    androidx.compose.foundation.layout.BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .height(260.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFF0F172A))
            .testTag("live_camera_hud_overlay")
    ) {
        val overlayWidthDp = maxWidth
        val overlayHeightDp = maxHeight
        // Construction Site Camera Simulation Background Canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Simulated dark construction floor grid lines
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF1E293B), Color(0xFF0F172A))
                )
            )

            // Draw architectural structural grid overlay
            val gridSpacing = 40.dp.toPx()
            var x = 0f
            while (x < w) {
                drawLine(
                    color = Color.White.copy(alpha = 0.06f),
                    start = Offset(x, 0f),
                    end = Offset(x, h),
                    strokeWidth = 1f
                )
                x += gridSpacing
            }

            var y = 0f
            while (y < h) {
                drawLine(
                    color = Color.White.copy(alpha = 0.06f),
                    start = Offset(0f, y),
                    end = Offset(w, y),
                    strokeWidth = 1f
                )
                y += gridSpacing
            }

            // Draw Bounding Boxes from AI Analysis
            analysisResult.detectedObjects.forEach { box ->
                val boxX = box.normX * w
                val boxY = box.normY * h
                val boxW = box.normWidth * w
                val boxH = box.normHeight * h

                val boxColor = if (box.isHazard) StatusError else StatusSuccess

                drawRect(
                    color = boxColor.copy(alpha = 0.15f),
                    topLeft = Offset(boxX, boxY),
                    size = Size(boxW, boxH)
                )

                drawRect(
                    color = boxColor,
                    topLeft = Offset(boxX, boxY),
                    size = Size(boxW, boxH),
                    style = Stroke(width = 2.dp.toPx())
                )
            }

            // Draw Scanning HUD Laser
            if (isAnalyzing) {
                drawLine(
                    color = Color(0xFF00D2FF),
                    start = Offset(0f, scanLineY * h),
                    end = Offset(w, scanLineY * h),
                    strokeWidth = 3.dp.toPx()
                )
            }

            // Draw HUD Crosshair Center
            val cx = w / 2f
            val cy = h / 2f
            drawLine(
                color = Color.Cyan.copy(alpha = 0.5f),
                start = Offset(cx - 16, cy),
                end = Offset(cx + 16, cy),
                strokeWidth = 1.5f
            )
            drawLine(
                color = Color.Cyan.copy(alpha = 0.5f),
                start = Offset(cx, cy - 16),
                end = Offset(cx, cy + 16),
                strokeWidth = 1.5f
            )
        }

        // Overlay Bounding Box Labels (Rendered cleanly as Jetpack Compose Surfaces)
        analysisResult.detectedObjects.forEach { box ->
            Box(
                modifier = Modifier
                    .padding(
                        start = (box.normX * overlayWidthDp.value).dp,
                        top = (box.normY * overlayHeightDp.value).dp
                    )
            ) {
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = if (box.isHazard) StatusError else StatusSuccess
                ) {
                    Text(
                        text = "${box.label} (${(box.confidence * 100).toInt()}%)",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }

        // Top HUD Header Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color.Black.copy(alpha = 0.6f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(StatusError)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "RAY-BAN META LIVE FEED • 1080P 60FPS",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MetaBlue.copy(alpha = 0.8f)
            ) {
                Text(
                    text = "ZONE B-4 • LEVEL 3",
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }

        // Bottom HUD Bar with AI metrics & Snapshot Trigger
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f))
                    )
                )
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "PPE COMPLIANCE: ${analysisResult.ppeCompliancePercent}%",
                    color = if (analysisResult.ppeCompliancePercent > 90) StatusSuccess else StatusError,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "CAD DEVIATION: ${analysisResult.blueprintDeviationMm} mm",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 10.sp
                )
            }

            Button(
                onClick = onCaptureSnapshot,
                colors = ButtonDefaults.buttonColors(containerColor = MetaBlue),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("capture_frame_button")
            ) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = "Capture",
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("ANALYZE FRAME", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
