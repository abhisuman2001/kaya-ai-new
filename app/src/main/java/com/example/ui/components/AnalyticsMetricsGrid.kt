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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AnalyticsMetric
import com.example.ui.theme.MetaBlue
import com.example.ui.theme.StatusSuccess
import com.example.ui.theme.StatusWarning

@Composable
fun AnalyticsMetricsGrid(
    productivity: AnalyticsMetric,
    hazardsPrevented: AnalyticsMetric,
    projectProgress: AnalyticsMetric,
    qualityScore: AnalyticsMetric,
    modifier: Modifier = Modifier
) {
    androidx.compose.foundation.layout.BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val width = maxWidth
        when {
            width >= 680.dp -> {
                // Large screen (tablet / foldable unfolded): 4 cards in a single row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MetricCardItem(metric = productivity, icon = Icons.Default.Speed, badgeColor = MetaBlue, modifier = Modifier.weight(1f), testTag = "metric_productivity")
                    MetricCardItem(metric = hazardsPrevented, icon = Icons.Default.Shield, badgeColor = StatusSuccess, modifier = Modifier.weight(1f), testTag = "metric_hazards")
                    MetricCardItem(metric = projectProgress, icon = Icons.Default.TrendingUp, badgeColor = MetaBlue, modifier = Modifier.weight(1f), testTag = "metric_progress")
                    MetricCardItem(metric = qualityScore, icon = Icons.Default.FactCheck, badgeColor = StatusSuccess, modifier = Modifier.weight(1f), testTag = "metric_quality")
                }
            }
            width >= 360.dp -> {
                // Standard phone screen: 2x2 grid
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        MetricCardItem(metric = productivity, icon = Icons.Default.Speed, badgeColor = MetaBlue, modifier = Modifier.weight(1f), testTag = "metric_productivity")
                        MetricCardItem(metric = hazardsPrevented, icon = Icons.Default.Shield, badgeColor = StatusSuccess, modifier = Modifier.weight(1f), testTag = "metric_hazards")
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        MetricCardItem(metric = projectProgress, icon = Icons.Default.TrendingUp, badgeColor = MetaBlue, modifier = Modifier.weight(1f), testTag = "metric_progress")
                        MetricCardItem(metric = qualityScore, icon = Icons.Default.FactCheck, badgeColor = StatusSuccess, modifier = Modifier.weight(1f), testTag = "metric_quality")
                    }
                }
            }
            else -> {
                // Small phone screen (< 360dp): 1 card per row stacked
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MetricCardItem(metric = productivity, icon = Icons.Default.Speed, badgeColor = MetaBlue, modifier = Modifier.fillMaxWidth(), testTag = "metric_productivity")
                    MetricCardItem(metric = hazardsPrevented, icon = Icons.Default.Shield, badgeColor = StatusSuccess, modifier = Modifier.fillMaxWidth(), testTag = "metric_hazards")
                    MetricCardItem(metric = projectProgress, icon = Icons.Default.TrendingUp, badgeColor = MetaBlue, modifier = Modifier.fillMaxWidth(), testTag = "metric_progress")
                    MetricCardItem(metric = qualityScore, icon = Icons.Default.FactCheck, badgeColor = StatusSuccess, modifier = Modifier.fillMaxWidth(), testTag = "metric_quality")
                }
            }
        }
    }
}

@Composable
private fun MetricCardItem(
    metric: AnalyticsMetric,
    icon: ImageVector,
    badgeColor: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier,
    testTag: String
) {
    Card(
        modifier = modifier
            .border(1.dp, badgeColor.copy(0.3f), RoundedCornerShape(18.dp))
            .testTag(testTag),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = badgeColor, modifier = Modifier.size(20.dp))
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = badgeColor.copy(0.12f)
                ) {
                    Text(
                        text = metric.changePercent,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = badgeColor,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = metric.title.uppercase(),
                fontSize = 9.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = metric.value,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = badgeColor
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = metric.unit,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 3.dp)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = metric.description,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 14.sp
            )
        }
    }
}
