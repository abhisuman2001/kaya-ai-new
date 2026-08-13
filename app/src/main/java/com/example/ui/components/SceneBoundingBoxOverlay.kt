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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CenterFocusWeak
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.VisionBoundingBox
import com.example.ui.theme.MetaBlue
import com.example.ui.theme.StatusError
import com.example.ui.theme.StatusSuccess
import com.example.ui.theme.StatusWarning

@Composable
fun SceneBoundingBoxOverlay(
    imageUrl: String,
    boundingBoxes: List<VisionBoundingBox>,
    selectedBox: VisionBoundingBox?,
    isCapturing: Boolean,
    onBoxSelect: (VisionBoundingBox) -> Unit,
    onCaptureClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(20.dp))
            .testTag("scene_image_preview_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CenterFocusWeak,
                        contentDescription = null,
                        tint = MetaBlue,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "RAY-BAN CAMERA FRAME PREVIEW",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        letterSpacing = 1.sp
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MetaBlue.copy(0.15f)
                ) {
                    Text(
                        text = "1080p HD Frame • 60 FPS",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = MetaBlue,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Camera Canvas with Bounding Boxes & Grid Overlay
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF0F172A))
                    .testTag("scene_bounding_box_canvas")
            ) {
                // Background Simulated Construction Image
                AsyncImage(
                    model = imageUrl,
                    contentDescription = "Construction Site Scene Frame",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Dark vignette overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Black.copy(0.3f), Color.Transparent, Color.Black.copy(0.6f))
                            )
                        )
                )

                // HUD Grid & Crosshair Canvas
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height

                    // Gridlines
                    val strokeDash = Stroke(width = 1f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f))
                    drawLine(Color.White.copy(0.2f), Offset(w / 3, 0f), Offset(w / 3, h), strokeWidth = 1f, pathEffect = strokeDash.pathEffect)
                    drawLine(Color.White.copy(0.2f), Offset(2 * w / 3, 0f), Offset(2 * w / 3, h), strokeWidth = 1f, pathEffect = strokeDash.pathEffect)
                    drawLine(Color.White.copy(0.2f), Offset(0f, h / 2), Offset(w, h / 2), strokeWidth = 1f, pathEffect = strokeDash.pathEffect)

                    // Bounding Box Rectangles
                    boundingBoxes.forEach { box ->
                        val left = box.normX * w
                        val top = box.normY * h
                        val boxWidth = box.normWidth * w
                        val boxHeight = box.normHeight * h

                        val boxColor = when (box.category) {
                            "Worker" -> if (box.isHazard) StatusError else Color(0xFF38BDF8)
                            "Material" -> Color(0xFFA855F7)
                            "Equipment" -> StatusSuccess
                            "Hazard" -> StatusError
                            else -> StatusWarning
                        }

                        val isSelected = selectedBox?.label == box.label

                        drawRect(
                            color = boxColor,
                            topLeft = Offset(left, top),
                            size = Size(boxWidth, boxHeight),
                            style = Stroke(width = if (isSelected) 4f else 2f)
                        )
                    }
                }

                // Interactive Clickable Badges overlaying normalized coordinates
                boundingBoxes.forEach { box ->
                    val categoryColor = when (box.category) {
                        "Worker" -> if (box.isHazard) StatusError else Color(0xFF38BDF8)
                        "Material" -> Color(0xFFA855F7)
                        "Equipment" -> StatusSuccess
                        "Hazard" -> StatusError
                        else -> StatusWarning
                    }

                    val isSelected = selectedBox?.label == box.label

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 8.dp, vertical = 8.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (isSelected) categoryColor else categoryColor.copy(0.85f),
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(
                                    start = (box.normX * 280).dp,
                                    top = (box.normY * 180).dp
                                )
                                .clickable { onBoxSelect(box) }
                                .testTag("bounding_box_chip_${box.label.take(8).lowercase().replace(" ", "_")}")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (box.isHazard) Icons.Default.Warning else Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(10.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${box.label} • ${(box.confidence * 100).toInt()}%",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }

                // Top Status Bar Overlay inside Canvas
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter)
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color.Black.copy(0.7f)
                    ) {
                        Text(
                            text = "LIVE VISION BINDING (${boundingBoxes.size} OBJECTS)",
                            fontSize = 9.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = Color.Green,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }

                    if (isCapturing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color = MetaBlue,
                            strokeWidth = 2.dp
                        )
                    }
                }

                // Bottom Action Button Overlay inside Canvas
                Button(
                    onClick = onCaptureClick,
                    enabled = !isCapturing,
                    colors = ButtonDefaults.buttonColors(containerColor = MetaBlue),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(12.dp)
                        .testTag("capture_screenshot_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "Capture Screenshot",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isCapturing) "CAPTURING FRAME..." else "CAPTURE SCREENSHOT",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
