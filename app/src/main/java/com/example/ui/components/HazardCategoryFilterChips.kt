package com.example.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.HazardCategory
import com.example.ui.theme.MetaBlue
import com.example.ui.theme.StatusError
import com.example.ui.theme.StatusSuccess
import com.example.ui.theme.StatusWarning

@Composable
fun HazardCategoryFilterChips(
    selectedCategory: HazardCategory?,
    selectedSeverity: String?,
    onCategorySelect: (HazardCategory?) -> Unit,
    onSeveritySelect: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    val severities = listOf("CRITICAL", "HIGH", "MEDIUM", "LOW")

    Column(modifier = modifier.fillMaxWidth()) {
        // Category Filter Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (selectedCategory == null) MetaBlue else MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier
                    .clickable { onCategorySelect(null) }
                    .testTag("filter_category_all")
            ) {
                Text(
                    text = "ALL CATEGORIES",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (selectedCategory == null) Color.White else MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }

            HazardCategory.entries.forEach { cat ->
                val isSelected = selectedCategory == cat
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isSelected) MetaBlue else MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier
                        .clickable { onCategorySelect(cat) }
                        .testTag("filter_category_${cat.name.lowercase()}")
                ) {
                    Text(
                        text = cat.displayName,
                        fontSize = 10.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Severity Filter Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (selectedSeverity == null) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier
                    .clickable { onSeveritySelect(null) }
                    .testTag("filter_severity_all")
            ) {
                Text(
                    text = "ALL SEVERITIES",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (selectedSeverity == null) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                )
            }

            severities.forEach { sev ->
                val isSelected = selectedSeverity == sev
                val sevColor = when (sev) {
                    "CRITICAL", "HIGH" -> StatusError
                    "MEDIUM" -> StatusWarning
                    else -> StatusSuccess
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isSelected) sevColor else MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier
                        .clickable { onSeveritySelect(sev) }
                        .testTag("filter_severity_${sev.lowercase()}")
                ) {
                    Text(
                        text = sev,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }
        }
    }
}
