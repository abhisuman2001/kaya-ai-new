package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.ReportFilterCategory
import com.example.ui.components.ComprehensiveReportCard
import com.example.ui.components.ReportCategoryFilterCard
import com.example.ui.components.ReportHeaderCard
import com.example.ui.theme.MetaBlue
import com.example.ui.theme.StatusSuccess
import com.example.ui.viewmodel.KayaViewModel

@Composable
fun ReportScreen(
    viewModel: KayaViewModel,
    modifier: Modifier = Modifier
) {
    val reportState by viewModel.comprehensiveReportState.collectAsStateWithLifecycle()

    val filteredReports = if (reportState.selectedFilter == ReportFilterCategory.ALL) {
        reportState.reportsList
    } else {
        val filterCode = when (reportState.selectedFilter) {
            ReportFilterCategory.DAILY -> "DAILY"
            ReportFilterCategory.WEEKLY -> "WEEKLY"
            ReportFilterCategory.INCIDENT -> "INCIDENT"
            ReportFilterCategory.SAFETY -> "SAFETY"
            ReportFilterCategory.QUALITY -> "QUALITY"
            else -> ""
        }
        reportState.reportsList.filter { it.typeCode == filterCode }
    }

    // Share Dialog Popup
    reportState.shareDialogMessage?.let { msg ->
        AlertDialog(
            onDismissRequest = { viewModel.dismissReportNotifications() },
            title = {
                Text("SHARE REPORT", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MetaBlue)
            },
            text = {
                Text(msg, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
            },
            confirmButton = {
                TextButton(
                    onClick = { viewModel.dismissReportNotifications() },
                    modifier = Modifier.testTag("dismiss_share_dialog_btn")
                ) {
                    Text("OK", fontWeight = FontWeight.Bold, color = MetaBlue)
                }
            }
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp)
            .testTag("report_screen_list"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(8.dp)) }

        // Title Header
        item {
            Column {
                Text(
                    text = "PHASE 12 — AUTOMATED SITE REPORTING",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MetaBlue,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Daily, Weekly, Incident & Quality AI Reports",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        // PDF Toast Banner
        reportState.pdfExportToast?.let { toast ->
            item {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = StatusSuccess.copy(0.15f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = toast,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = StatusSuccess,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }

        // 1. Report Header Card with AI Triggers
        item {
            ReportHeaderCard(
                totalReportsCount = reportState.reportsList.size,
                isGenerating = reportState.isGeneratingAiReport,
                onGenerateAiDpr = { viewModel.generateAiReport("DAILY") },
                onGenerateAiWeekly = { viewModel.generateAiReport("WEEKLY") }
            )
        }

        // 2. Report Category Filter Pills
        item {
            ReportCategoryFilterCard(
                selectedCategory = reportState.selectedFilter,
                onCategorySelected = { cat -> viewModel.selectReportFilterCategory(cat) }
            )
        }

        // 3. Filtered Reports List
        items(
            items = filteredReports,
            key = { it.id }
        ) { report ->
            ComprehensiveReportCard(
                report = report,
                onExportPdf = { viewModel.exportReportPdf(report.title) },
                onShare = { viewModel.shareReport(report.title) }
            )
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}
