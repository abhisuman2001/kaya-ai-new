package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.data.model.CadFileType
import com.example.ui.components.BimAlignmentControls
import com.example.ui.components.BimMeasurementsCard
import com.example.ui.components.CadDeviationCard
import com.example.ui.components.CadFileUploadSection
import com.example.ui.components.DifferenceVisualizationCard
import com.example.ui.theme.MetaBlue
import com.example.ui.viewmodel.KayaViewModel

@Composable
fun BlueprintScreen(
    viewModel: KayaViewModel,
    modifier: Modifier = Modifier
) {
    val cadBimState by viewModel.cadBimState.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp)
            .testTag("blueprint_cad_screen_list"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(8.dp)) }

        // Header Title
        item {
            Column {
                Text(
                    text = "CAD / BIM BLUEPRINT & AR COMPARISON",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MetaBlue,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "CAD/BIM Quality Verification",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        // 1. Difference Visualization Card (Overlay, Heatmap, Split View, Redline Wireframe)
        item {
            DifferenceVisualizationCard(
                selectedViewMode = cadBimState.selectedViewMode,
                xOffsetMm = cadBimState.alignmentXOffsetMm,
                yOffsetMm = cadBimState.alignmentYOffsetMm,
                rotationDeg = cadBimState.alignmentRotationDeg,
                isComparing = cadBimState.isComparing,
                onViewModeChange = { mode -> viewModel.setCadViewMode(mode) },
                onRunCompare = { viewModel.runCadComparison() }
            )
        }

        // 2. CAD File Upload & Project Mapping Section (.dwg, .dxf, .ifc)
        item {
            CadFileUploadSection(
                files = cadBimState.files,
                activeFileId = cadBimState.activeFileId,
                onSelectFile = { fileId -> viewModel.selectActiveCadFile(fileId) },
                onUploadClick = { type ->
                    val ext = if (type == CadFileType.IFC) "IFC_Architectural_S18.ifc" else "CAD_Structural_GridB4.dwg"
                    viewModel.uploadCadBimFile(
                        fileName = ext,
                        fileType = type,
                        gridMapping = "Level 18 Deck • Grid B-4"
                    )
                }
            )
        }

        // 3. AR Matrix Alignment & Calibration Controls
        item {
            BimAlignmentControls(
                xOffsetMm = cadBimState.alignmentXOffsetMm,
                yOffsetMm = cadBimState.alignmentYOffsetMm,
                rotationDeg = cadBimState.alignmentRotationDeg,
                onAdjustAlignment = { dx, dy, dRot -> viewModel.adjustAlignment(dx, dy, dRot) },
                onResetAlignment = { viewModel.resetAlignment() }
            )
        }

        // 4. Laser & AR Measurements Table
        item {
            BimMeasurementsCard(measurements = cadBimState.measurements)
        }

        // 5. CAD / BIM Deviations & Voice Feedback Section Header
        item {
            Text(
                text = "CAD / BIM DEVIATION DISCREPANCIES & VOICE HUD",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 0.5.sp
            )
        }

        // Deviation Cards
        items(
            items = cadBimState.deviations,
            key = { it.id }
        ) { deviation ->
            CadDeviationCard(
                deviation = deviation,
                isVoicePlaying = cadBimState.activeVoicePlayingId == deviation.id,
                onVoiceFeedbackClick = { viewModel.playCadVoiceFeedback(deviation.id) },
                onResolveClick = { viewModel.resolveCadDeviation(deviation.id) }
            )
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}
