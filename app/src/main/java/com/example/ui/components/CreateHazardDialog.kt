package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAlert
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.HazardCategory
import com.example.data.model.WorkerItem
import com.example.data.model.sampleWorkerRoster
import com.example.ui.theme.MetaBlue
import com.example.ui.theme.StatusError
import com.example.ui.theme.StatusSuccess
import com.example.ui.theme.StatusWarning

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CreateHazardDialog(
    onDismiss: () -> Unit,
    onCreate: (title: String, category: HazardCategory, severity: String, location: String, oshaStandard: String, description: String, workerId: String?, workerName: String?) -> Unit,
    workerRoster: List<WorkerItem> = sampleWorkerRoster,
    isSupervisor: Boolean = true,
    modifier: Modifier = Modifier
) {
    var title by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(HazardCategory.HELMET) }
    var selectedSeverity by remember { mutableStateOf("HIGH") }
    var location by remember { mutableStateOf("Level 18 Deck • Grid B-4") }
    var oshaStandard by remember { mutableStateOf("OSHA 1926.100(a)") }
    var description by remember { mutableStateOf("") }
    var selectedWorker by remember { mutableStateOf(if (isSupervisor && workerRoster.isNotEmpty()) workerRoster.first() else null) }

    val severities = listOf("CRITICAL", "HIGH", "MEDIUM", "LOW")

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(24.dp))
                .testTag("create_hazard_dialog_card"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AddAlert,
                            contentDescription = null,
                            tint = StatusError,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Log Hazard Observation",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_create_hazard_dialog")
                    ) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                if (!isSupervisor) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = StatusError.copy(alpha = 0.12f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, StatusError.copy(0.4f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = StatusError, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "SUPERVISOR ROLE REQUIRED: Hazard filing and worker assignments are restricted to site supervisors.",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = StatusError
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Hazard Category Selection
                Text("SELECT DETECTION CATEGORY", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MetaBlue)
                Spacer(modifier = Modifier.height(6.dp))

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    HazardCategory.entries.forEach { cat ->
                        val isSelected = selectedCategory == cat
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) MetaBlue else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .clickable { selectedCategory = cat }
                                .testTag("select_category_${cat.name.lowercase()}")
                        ) {
                            Text(
                                text = cat.displayName,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Severity Selection
                Text("RISK SEVERITY LEVEL", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MetaBlue)
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    severities.forEach { sev ->
                        val isSelected = selectedSeverity == sev
                        val sevColor = when (sev) {
                            "CRITICAL", "HIGH" -> StatusError
                            "MEDIUM" -> StatusWarning
                            else -> StatusSuccess
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) sevColor else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedSeverity = sev }
                                .testTag("select_severity_${sev.lowercase()}")
                        ) {
                            Box(
                                modifier = Modifier.padding(vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = sev,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Title Input
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Hazard Title (e.g., Unsafe Deck Edge Access)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("hazard_title_input"),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MetaBlue,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Location & OSHA Standard Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = location,
                        onValueChange = { location = it },
                        label = { Text("Location Tag") },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("hazard_location_input"),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = oshaStandard,
                        onValueChange = { oshaStandard = it },
                        label = { Text("OSHA Reference") },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("hazard_osha_input"),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Description Input
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Hazard Description & Observed Details") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .testTag("hazard_description_input"),
                    maxLines = 3
                )

                if (isSupervisor) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("ASSIGNED WORKER (REQUIRED)", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MetaBlue)
                    Spacer(modifier = Modifier.height(6.dp))

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        workerRoster.forEach { worker ->
                            val isSelected = selectedWorker?.id == worker.id
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) MetaBlue else MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier
                                    .clickable { selectedWorker = worker }
                                    .testTag("select_worker_${worker.id}")
                            ) {
                                Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)) {
                                    Text(
                                        text = worker.name,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = worker.jobTitle,
                                        fontSize = 9.sp,
                                        color = if (isSelected) Color.White.copy(0.8f) else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Submit Button
                val isFormValid = isSupervisor && title.isNotBlank() && selectedWorker != null
                Button(
                    onClick = {
                        if (isFormValid) {
                            val wName = selectedWorker?.let { "${it.name} (${it.jobTitle})" }
                            onCreate(
                                title,
                                selectedCategory,
                                selectedSeverity,
                                location,
                                oshaStandard,
                                description,
                                selectedWorker?.id,
                                wName
                            )
                            onDismiss()
                        }
                    },
                    enabled = isFormValid,
                    colors = ButtonDefaults.buttonColors(containerColor = StatusError),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("submit_create_hazard_button")
                ) {
                    Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("SUBMIT HAZARD OBSERVATION", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }
    }
}
