package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.VisionBoundingBox
import com.example.ui.components.SceneBoundingBoxOverlay
import com.example.ui.components.SceneCategoryTabs
import com.example.ui.components.SceneRiskScoreCard
import com.example.ui.components.SceneBottomSheet
import com.example.ui.theme.MetaBlue
import com.example.ui.viewmodel.KayaViewModel

@Composable
fun SceneAnalysisScreen(
    viewModel: KayaViewModel,
    modifier: Modifier = Modifier
) {
    val sceneData by viewModel.sceneAnalysis.collectAsStateWithLifecycle()
    val isCapturing by viewModel.isCapturingScene.collectAsStateWithLifecycle()
    val isSaved by viewModel.isAnalysisSaved.collectAsStateWithLifecycle()
    val selectedBox by viewModel.selectedBoundingBox.collectAsStateWithLifecycle()
    val showBottomSheet by viewModel.showBottomSheet.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp)
            .testTag("scene_analysis_screen_list"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(8.dp)) }

        // Screen Title Header
        item {
            Column {
                Text(
                    text = "GEMINI 1.5 PRO VISION ENGINE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MetaBlue,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Scene Analysis & Object Matrix",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        // Image Preview & Interactive Bounding Boxes Overlay
        item {
            SceneBoundingBoxOverlay(
                imageUrl = sceneData.imageUrl,
                boundingBoxes = sceneData.boundingBoxes,
                selectedBox = selectedBox,
                isCapturing = isCapturing,
                onBoxSelect = { box ->
                    viewModel.selectBoundingBox(box)
                },
                onCaptureClick = {
                    viewModel.captureNewSceneScreenshot()
                }
            )
        }

        // Risk Score Index & Save Analysis Action
        item {
            SceneRiskScoreCard(
                sceneData = sceneData,
                isSaved = isSaved,
                onSaveClick = {
                    viewModel.saveSceneAnalysis()
                }
            )
        }

        // Category Breakdown Tabs (Materials, Workers, Equipment, Hazards, AI Suggestions)
        item {
            SceneCategoryTabs(
                sceneData = sceneData,
                onInspectItem = { itemTag ->
                    val matchingBox = sceneData.boundingBoxes.find { itemTag.contains(it.label, ignoreCase = true) }
                    viewModel.selectBoundingBox(
                        matchingBox ?: VisionBoundingBox(
                            label = itemTag,
                            confidence = 0.95f,
                            isHazard = itemTag.contains("Hazard", ignoreCase = true),
                            normX = 0.2f,
                            normY = 0.3f,
                            normWidth = 0.4f,
                            normHeight = 0.4f,
                            riskLevel = "HIGH",
                            category = "General"
                        )
                    )
                }
            )
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }

    // Modal Bottom Sheet when object or item is selected
    if (showBottomSheet) {
        SceneBottomSheet(
            selectedBox = selectedBox,
            onDismiss = {
                viewModel.toggleBottomSheet(false)
            }
        )
    }
}
