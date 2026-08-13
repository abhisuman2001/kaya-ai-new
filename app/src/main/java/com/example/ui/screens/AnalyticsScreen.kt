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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.AnalyticsChartsCard
import com.example.ui.components.AnalyticsFilterCard
import com.example.ui.components.AnalyticsHeaderCard
import com.example.ui.components.AnalyticsMetricsGrid
import com.example.ui.theme.MetaBlue
import com.example.ui.theme.StatusSuccess
import com.example.ui.viewmodel.KayaViewModel

@Composable
fun AnalyticsScreen(
    viewModel: KayaViewModel,
    modifier: Modifier = Modifier
) {
    val analyticsState by viewModel.analyticsState.collectAsStateWithLifecycle()

    // Predictive Simulation Popup Dialog
    analyticsState.simulationResult?.let { result ->
        AlertDialog(
            onDismissRequest = { viewModel.dismissAnalyticsSimulationResult() },
            title = {
                Text("AI PREDICTIVE SIMULATION", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MetaBlue)
            },
            text = {
                Text(result, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface, lineHeight = 18.sp)
            },
            confirmButton = {
                TextButton(
                    onClick = { viewModel.dismissAnalyticsSimulationResult() },
                    modifier = Modifier.testTag("dismiss_sim_dialog_btn")
                ) {
                    Text("DONE", fontWeight = FontWeight.Bold, color = MetaBlue)
                }
            }
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp)
            .testTag("analytics_screen_list"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(8.dp)) }

        // Title Header
        item {
            Column {
                Text(
                    text = "PHASE 13 — SITE MIND ANALYTICS",
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = MetaBlue,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Productivity, Hazards & Quality Telemetry",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Real-time AI metrics tracking efficiency, safety near-misses & BIM progress.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // 1. Header Card with Predictive AI Trigger
        item {
            AnalyticsHeaderCard(
                timeframeName = analyticsState.timeframe.displayName,
                isRunningSim = analyticsState.isRunningPredictiveSim,
                onRunPredictiveSim = { viewModel.runPredictiveAnalyticsSimulation() }
            )
        }

        // 2. Core Analytics Metrics Grid (Productivity, Hazards Prevented, Project Progress, Quality Score)
        item {
            AnalyticsMetricsGrid(
                productivity = analyticsState.productivityMetric,
                hazardsPrevented = analyticsState.hazardsPreventedMetric,
                projectProgress = analyticsState.projectProgressMetric,
                qualityScore = analyticsState.qualityScoreMetric
            )
        }

        // 3. Timeframe & Trade Filters
        item {
            AnalyticsFilterCard(
                selectedTimeframe = analyticsState.timeframe,
                onTimeframeSelected = { tf -> viewModel.setAnalyticsTimeframe(tf) },
                selectedTrade = analyticsState.selectedTrade,
                onTradeSelected = { trade -> viewModel.setAnalyticsTradeFilter(trade) }
            )
        }

        // 4. Interactive Trend & Category Breakdown Charts
        item {
            AnalyticsChartsCard(
                weeklyTrend = analyticsState.weeklyTrend,
                hazardBreakdown = analyticsState.hazardBreakdown
            )
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}
