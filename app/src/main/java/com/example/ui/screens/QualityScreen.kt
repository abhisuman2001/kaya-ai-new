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
import com.example.data.model.QualityCategory
import com.example.ui.components.QualityCategoryGridCard
import com.example.ui.components.QualityHistoryCard
import com.example.ui.components.QualityInspectionItemCard
import com.example.ui.components.QualityRecommendationsCard
import com.example.ui.components.QualityScoreCard
import com.example.ui.theme.MetaBlue
import com.example.ui.viewmodel.KayaViewModel

@Composable
fun QualityScreen(
    viewModel: KayaViewModel,
    modifier: Modifier = Modifier
) {
    val qualityState by viewModel.qualityInspectionState.collectAsStateWithLifecycle()

    val filteredItems = if (qualityState.selectedCategory == QualityCategory.ALL) {
        qualityState.inspectionItems
    } else {
        qualityState.inspectionItems.filter { it.category == qualityState.selectedCategory }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp)
            .testTag("quality_screen_list"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(8.dp)) }

        // Header Title
        item {
            Column {
                Text(
                    text = "PHASE 10 — QUALITY INSPECTION",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MetaBlue,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "QA / QC Inspection & Defect Engine",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        // 1. Quality Score & Category Breakdown Card
        item {
            QualityScoreCard(
                overallScore = qualityState.overallQualityScore,
                qualityGrade = qualityState.qualityGrade,
                crackScore = qualityState.crackScore,
                surfaceScore = qualityState.surfaceScore,
                alignmentScore = qualityState.alignmentScore,
                concreteScore = qualityState.concreteScore,
                pipeScore = qualityState.pipeScore,
                boltScore = qualityState.boltScore,
                isScanning = qualityState.isScanning,
                onRunScan = { viewModel.runAiQualityScan() }
            )
        }

        // 2. Inspection Category Selector (Cracks, Surface, Alignment, Concrete, Pipe, Bolt)
        item {
            QualityCategoryGridCard(
                selectedCategory = qualityState.selectedCategory,
                onCategorySelected = { category -> viewModel.selectQualityCategory(category) }
            )
        }

        // 3. Filtered Inspection Checkpoints List
        items(
            items = filteredItems,
            key = { it.id }
        ) { item ->
            QualityInspectionItemCard(
                item = item,
                onRemediateToggle = { viewModel.toggleQualityRemediated(item.id) }
            )
        }

        // 4. AI Site Remediation Action Plan Recommendations
        item {
            QualityRecommendationsCard(
                recommendations = qualityState.recommendations,
                onCompleteRecommendation = { recId -> viewModel.completeQualityRecommendation(recId) }
            )
        }

        // 5. QA/QC Audit History Logs
        item {
            QualityHistoryCard(historyLogs = qualityState.historyLogs)
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}
