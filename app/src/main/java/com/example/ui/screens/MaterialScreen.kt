package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.MaterialCategory
import com.example.ui.components.MaterialCategoryFilterCard
import com.example.ui.components.MaterialHeaderCard
import com.example.ui.components.MaterialInventoryAuditCard
import com.example.ui.components.MaterialItemCard
import com.example.ui.theme.MetaBlue
import com.example.ui.theme.StatusSuccess
import com.example.ui.viewmodel.KayaViewModel

@Composable
fun MaterialScreen(
    viewModel: KayaViewModel,
    modifier: Modifier = Modifier
) {
    val materialState by viewModel.materialVerificationState.collectAsStateWithLifecycle()

    val filteredMaterials = if (materialState.selectedCategory == MaterialCategory.ALL) {
        materialState.materials
    } else {
        materialState.materials.filter { it.category == materialState.selectedCategory }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp)
            .testTag("material_screen_list"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(8.dp)) }

        // Screen Title Header
        item {
            Column {
                Text(
                    text = "PHASE 11 — MATERIAL VERIFICATION",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MetaBlue,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Brand, Spec, Expiry & Batch AI Verification",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        // Scan Toast / Banner if present
        materialState.scanSuccessMessage?.let { msg ->
            item {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = StatusSuccess.copy(0.15f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = msg,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = StatusSuccess,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }

        // 1. Material Summary Header Card
        item {
            MaterialHeaderCard(
                totalMaterials = materialState.totalMaterialsInspected,
                compliantCount = materialState.compliantMaterialsCount,
                complianceRate = materialState.complianceRatePercent,
                nonCompliantCount = materialState.expiredOrNonCompliantCount,
                isScanning = materialState.isScanningMaterial,
                onScanMaterials = { viewModel.scanMaterialWithGlasses() }
            )
        }

        // 2. Material Category Filter Pills
        item {
            MaterialCategoryFilterCard(
                selectedCategory = materialState.selectedCategory,
                onCategorySelected = { cat -> viewModel.selectMaterialCategory(cat) }
            )
        }

        // 3. Filtered Material Items
        items(
            items = filteredMaterials,
            key = { it.id }
        ) { material ->
            MaterialItemCard(
                material = material,
                onToggleCompliance = { viewModel.toggleMaterialCompliance(material.id) },
                onUpdateStockDelta = { delta -> viewModel.updateMaterialInventoryStock(material.id, delta) }
            )
        }

        // 4. ERP & BIM Inventory Audit Integration
        item {
            MaterialInventoryAuditCard()
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}
