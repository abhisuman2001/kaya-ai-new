package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.BluetoothConnected
import androidx.compose.material.icons.filled.BluetoothSearching
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SdStorage
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SignalCellularAlt
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WifiTethering
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.AuthScreenState
import com.example.data.model.DiscoveredGlassDevice
import com.example.data.model.GlassAiState
import com.example.data.model.PairingStep
import com.example.ui.components.RayBanGlassesHero
import com.example.ui.theme.BorderDark
import com.example.ui.theme.MetaBlue
import com.example.ui.theme.StatusError
import com.example.ui.theme.StatusSuccess
import com.example.ui.theme.StatusWarning
import com.example.ui.viewmodel.KayaViewModel

@Composable
fun DeviceScreen(
    viewModel: KayaViewModel,
    onNavigateToRoute: ((String) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val glassState by viewModel.glassState.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val isScanning by viewModel.isScanningBle.collectAsStateWithLifecycle()
    val discoveredDevices by viewModel.discoveredDevices.collectAsStateWithLifecycle()
    val pairingStep by viewModel.pairingStep.collectAsStateWithLifecycle()
    val pairingProgress by viewModel.pairingProgress.collectAsStateWithLifecycle()
    val pairingStatusMsg by viewModel.pairingStatusMsg.collectAsStateWithLifecycle()
    val permissions by viewModel.permissionsState.collectAsStateWithLifecycle()
    val connQuality by viewModel.connectionQuality.collectAsStateWithLifecycle()
    val isReconnecting by viewModel.isReconnecting.collectAsStateWithLifecycle()
    val isCheckingFirmware by viewModel.isCheckingFirmware.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(8.dp)) }

        // User Profile Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, BorderDark, RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(MetaBlue.copy(0.15f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Person, contentDescription = null, tint = MetaBlue, modifier = Modifier.size(22.dp))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(currentUser.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Text(currentUser.email, color = Color.White.copy(0.6f), fontSize = 11.sp)
                            }
                        }

                        Box(
                            modifier = Modifier
                                .background(MetaBlue.copy(0.2f), RoundedCornerShape(10.dp))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(currentUser.role.title, color = MetaBlue, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { viewModel.navigateToAuth(AuthScreenState.ROLE_SELECTION) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("switch_role_button"),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark)
                        ) {
                            Icon(Icons.Default.Shield, contentDescription = null, modifier = Modifier.size(14.dp), tint = MetaBlue)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Switch Role", fontSize = 11.sp, color = Color.White)
                        }

                        OutlinedButton(
                            onClick = { viewModel.logout() },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("logout_button"),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, StatusError.copy(0.4f))
                        ) {
                            Icon(Icons.Default.ExitToApp, contentDescription = null, modifier = Modifier.size(14.dp), tint = StatusError)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Sign Out", fontSize = 11.sp, color = StatusError)
                        }
                    }
                }
            }
        }

        // Section Title
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "RAY-BAN META DEVICE PAIRING & STATUS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MetaBlue,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = glassState.deviceName,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "SN: ${glassState.serialNumber} • FW: ${glassState.firmwareVersion}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Device connection status pill
                Box(
                    modifier = Modifier
                        .background(
                            if (glassState.connectionState == GlassAiState.OFFLINE) StatusError.copy(0.2f)
                            else StatusSuccess.copy(0.2f),
                            RoundedCornerShape(20.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = if (glassState.connectionState == GlassAiState.OFFLINE) "OFFLINE" else "CONNECTED",
                        color = if (glassState.connectionState == GlassAiState.OFFLINE) StatusError else StatusSuccess,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }
            }
        }

        // Animated Glass Visualizer Card
        item {
            RayBanGlassesHero(
                deviceState = glassState,
                onStateSelect = { newState -> viewModel.setGlassState(newState) }
            )
        }

        // Smartphone as Meta Glass Bridge Card (For Testing Without Physical Glasses)
        item {
            PhoneGlassBridgeCard(
                glassState = glassState,
                viewModel = viewModel,
                onNavigateToRoute = onNavigateToRoute
            )
        }

        // Connection Quality & Reconnect Bar
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "BLE & WI-FI DIRECT STREAM QUALITY",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            letterSpacing = 1.sp
                        )

                        Text(
                            text = connQuality.signalRating,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = StatusSuccess
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        SubMetric(icon = Icons.Default.SignalCellularAlt, label = "Signal RSSI", value = "${connQuality.rssiDbm} dBm")
                        SubMetric(icon = Icons.Default.WifiTethering, label = "Bandwidth", value = "${connQuality.bandwidthMbps} Mbps")
                        SubMetric(icon = Icons.Default.GraphicEq, label = "Stream Latency", value = "${connQuality.latencyMs} ms")
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { viewModel.reconnectDevice() },
                            enabled = !isReconnecting,
                            colors = ButtonDefaults.buttonColors(containerColor = MetaBlue),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("reconnect_button")
                        ) {
                            if (isReconnecting) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Reconnecting...", fontSize = 12.sp)
                            } else {
                                Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Reconnect BLE", fontSize = 12.sp)
                            }
                        }

                        OutlinedButton(
                            onClick = {
                                if (glassState.connectionState == GlassAiState.OFFLINE) {
                                    viewModel.setGlassState(GlassAiState.CONNECTED)
                                } else {
                                    viewModel.disconnectDevice()
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (glassState.connectionState == GlassAiState.OFFLINE) StatusSuccess else StatusError.copy(0.5f)
                            ),
                            modifier = Modifier.testTag("toggle_connection_button")
                        ) {
                            Text(
                                text = if (glassState.connectionState == GlassAiState.OFFLINE) "Connect" else "Disconnect",
                                fontSize = 12.sp,
                                color = if (glassState.connectionState == GlassAiState.OFFLINE) StatusSuccess else StatusError
                            )
                        }
                    }
                }
            }
        }

        // Bluetooth Discovery & Nearby Ray-Ban Device Scanning Section
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, BorderDark, RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.BluetoothSearching, contentDescription = null, tint = MetaBlue, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "BLUETOOTH LE DISCOVERY",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                letterSpacing = 1.sp
                            )
                        }

                        OutlinedButton(
                            onClick = { viewModel.startBleScan() },
                            enabled = !isScanning,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("scan_ble_button")
                        ) {
                            if (isScanning) {
                                CircularProgressIndicator(modifier = Modifier.size(14.dp), color = MetaBlue, strokeWidth = 2.dp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Scanning...", fontSize = 11.sp)
                            } else {
                                Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(14.dp), tint = MetaBlue)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Scan Nearby", fontSize = 11.sp, color = MetaBlue)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = pairingStatusMsg,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Animated Radar Scan pulse if scanning
                    if (isScanning) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(80.dp)
                                .background(MetaBlue.copy(0.08f), RoundedCornerShape(16.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            val infiniteTransition = rememberInfiniteTransition(label = "pulse")
                            val pulseScale by infiniteTransition.animateFloat(
                                initialValue = 0.8f,
                                targetValue = 1.2f,
                                animationSpec = infiniteRepeatable(tween(800, easing = LinearEasing), RepeatMode.Reverse),
                                label = "scale"
                            )
                            Icon(
                                imageVector = Icons.Default.BluetoothSearching,
                                contentDescription = null,
                                tint = MetaBlue,
                                modifier = Modifier
                                    .size(36.dp)
                                    .scale(pulseScale)
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    // Discovered Devices List
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        discoveredDevices.forEach { device ->
                            DiscoveredDeviceItem(
                                device = device,
                                onPairClick = { viewModel.selectDeviceToPair(device) }
                            )
                        }
                    }
                }
            }
        }

        // Pairing Handshake Progress Animation Card (shows when pairing in progress)
        item {
            AnimatedVisibility(
                visible = pairingStep == PairingStep.HANDSHAKE || pairingStep == PairingStep.FIRMWARE_CHECK || pairingStep == PairingStep.SUCCESS,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, if (pairingStep == PairingStep.SUCCESS) StatusSuccess else MetaBlue, RoundedCornerShape(20.dp)),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (pairingStep == PairingStep.SUCCESS) Icons.Default.CheckCircle else Icons.Default.BluetoothConnected,
                                contentDescription = null,
                                tint = if (pairingStep == PairingStep.SUCCESS) StatusSuccess else MetaBlue,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (pairingStep == PairingStep.SUCCESS) "RAY-BAN PAIRING COMPLETE" else "PAIRING IN PROGRESS",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = pairingStatusMsg,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        LinearProgressIndicator(
                            progress = { pairingProgress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(CircleShape),
                            color = if (pairingStep == PairingStep.SUCCESS) StatusSuccess else MetaBlue,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )

                        if (pairingStep == PairingStep.SUCCESS) {
                            Spacer(modifier = Modifier.height(12.dp))
                            OutlinedButton(
                                onClick = { viewModel.resetPairingFlow() },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("dismiss_pairing_success_button"),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Done & Close Flow", fontSize = 12.sp, color = StatusSuccess)
                            }
                        }
                    }
                }
            }
        }

        // Permissions Request & Configuration Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        1.dp,
                        if (!permissions.allGranted) StatusWarning.copy(0.6f) else BorderDark,
                        RoundedCornerShape(20.dp)
                    ),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "SYSTEM PERMISSIONS & HARDWARE ACCESS",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = if (permissions.allGranted) "All required permissions granted" else "Action needed: Enable spatial sensors",
                                fontSize = 11.sp,
                                color = if (permissions.allGranted) StatusSuccess else StatusWarning
                            )
                        }

                        if (!permissions.allGranted) {
                            Button(
                                onClick = { viewModel.grantAllPermissions() },
                                colors = ButtonDefaults.buttonColors(containerColor = MetaBlue),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.testTag("grant_all_permissions_button")
                            ) {
                                Text("Grant All", fontSize = 11.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    PermissionRow(
                        icon = Icons.Default.Videocam,
                        title = "Camera Access (1080p HUD Feed)",
                        subtitle = "Required for AI Vision inspection & Blueprint spatial anchoring",
                        granted = permissions.cameraGranted,
                        onToggle = { viewModel.toggleCameraPermission() },
                        tag = "camera_permission_switch"
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    PermissionRow(
                        icon = Icons.Default.Mic,
                        title = "Microphone Access (Voice AI)",
                        subtitle = "Captures 3-mic spatial array voice queries for RAG lookup",
                        granted = permissions.microphoneGranted,
                        onToggle = { viewModel.toggleMicPermission() },
                        tag = "mic_permission_switch"
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    PermissionRow(
                        icon = Icons.Default.Notifications,
                        title = "OSHA Safety Hazard Alerts",
                        subtitle = "Pushes real-time audio & HUD notifications during active hazard detection",
                        granted = permissions.notificationGranted,
                        onToggle = { viewModel.toggleNotificationPermission() },
                        tag = "notification_permission_switch"
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    PermissionRow(
                        icon = Icons.Default.BluetoothConnected,
                        title = "Bluetooth LE & Location Access",
                        subtitle = "Discovers Ray-Ban frames & handles Wi-Fi Direct peer streaming",
                        granted = permissions.bluetoothGranted,
                        onToggle = { viewModel.toggleBluetoothPermission() },
                        tag = "bluetooth_permission_switch"
                    )
                }
            }
        }

        // Battery & Firmware Diagnostics Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "BATTERY & FIRMWARE MANAGEMENT",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    MetricRow(
                        icon = Icons.Default.BatteryChargingFull,
                        tint = StatusSuccess,
                        title = "Mobile Device Battery",
                        value = "${glassState.batteryPercent}% • ${glassState.chargingStatusText}",
                        progress = (glassState.batteryPercent.coerceIn(0, 100)) / 100f
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    MetricRow(
                        icon = Icons.Default.BatteryChargingFull,
                        tint = MetaBlue,
                        title = "Battery Health Status",
                        value = "${glassState.batteryHealth} Health • ${glassState.tempCelsius}°C",
                        progress = 1.0f
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Current Firmware", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(glassState.firmwareVersion, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }

                        OutlinedButton(
                            onClick = { viewModel.checkFirmwareUpdate() },
                            enabled = !isCheckingFirmware,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("check_firmware_button")
                        ) {
                            if (isCheckingFirmware) {
                                CircularProgressIndicator(modifier = Modifier.size(14.dp), color = MetaBlue, strokeWidth = 2.dp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Checking...", fontSize = 11.sp)
                            } else {
                                Icon(Icons.Default.SystemUpdate, contentDescription = null, modifier = Modifier.size(14.dp), tint = MetaBlue)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Check Updates", fontSize = 11.sp, color = MetaBlue)
                            }
                        }
                    }
                }
            }
        }

        // Diagnostic Action Buttons
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { viewModel.runAiQuery("Test microphone array and open-ear spatial audio") },
                    colors = ButtonDefaults.buttonColors(containerColor = MetaBlue),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("test_audio_button")
                ) {
                    Icon(imageVector = Icons.Default.GraphicEq, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Test Spatial Audio", fontSize = 11.sp)
                }

                OutlinedButton(
                    onClick = { viewModel.setGlassState(GlassAiState.CONNECTED) },
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("re_sync_ble_button")
                ) {
                    Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Re-Sync Frame BLE", fontSize = 11.sp)
                }
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}

@Composable
private fun DiscoveredDeviceItem(
    device: DiscoveredGlassDevice,
    onPairClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, if (device.isPaired) MetaBlue else BorderDark, RoundedCornerShape(14.dp)),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(0.4f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(if (device.isSmartphoneBridge) StatusSuccess.copy(0.2f) else MetaBlue.copy(0.15f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (device.isSmartphoneBridge) Icons.Default.Smartphone else Icons.Default.BluetoothConnected,
                        contentDescription = null,
                        tint = if (device.isSmartphoneBridge) StatusSuccess else MetaBlue,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(device.name, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
                        Spacer(modifier = Modifier.width(6.dp))
                        if (device.isSmartphoneBridge) {
                            Box(
                                modifier = Modifier
                                    .background(StatusSuccess.copy(0.2f), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text("PHONE BRIDGE", color = StatusSuccess, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                        } else if (device.isPaired) {
                            Box(
                                modifier = Modifier
                                    .background(StatusSuccess.copy(0.2f), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text("PAIRED", color = StatusSuccess, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Text(
                        text = "MAC: ${device.macAddress} • Signal: ${device.rssiDbm} dBm",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Button(
                onClick = onPairClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (device.isSmartphoneBridge) StatusSuccess else if (device.isPaired) MaterialTheme.colorScheme.surfaceVariant else MetaBlue
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("pair_device_${device.id}_button")
            ) {
                Text(
                    text = if (device.isSmartphoneBridge) "Connect Bridge" else if (device.isPaired) "Re-Pair" else "Pair Device",
                    fontSize = 11.sp,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun PermissionRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    granted: Boolean,
    onToggle: () -> Unit,
    tag: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (granted) MetaBlue else StatusWarning,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .background(
                                if (granted) StatusSuccess.copy(0.15f) else StatusError.copy(0.15f),
                                RoundedCornerShape(6.dp)
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if (granted) "GRANTED" else "PENDING",
                            color = if (granted) StatusSuccess else StatusError,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Text(text = subtitle, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        Switch(
            checked = granted,
            onCheckedChange = { onToggle() },
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = MetaBlue,
                uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            modifier = Modifier.testTag(tag)
        )
    }
}

@Composable
private fun MetricRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tint: Color,
    title: String,
    value: String,
    progress: Float
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            }
            Text(text = value, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Spacer(modifier = Modifier.height(6.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(CircleShape),
            color = tint,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}

@Composable
private fun SubMetric(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(30.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = MetaBlue, modifier = Modifier.size(15.dp))
        }
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(text = label, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(text = value, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}

@Composable
private fun PhoneGlassBridgeCard(
    glassState: com.example.data.model.GlassDeviceState,
    viewModel: KayaViewModel,
    onNavigateToRoute: ((String) -> Unit)?
) {
    val isBridgeActive = glassState.isPhoneBridgeMode
    var isGuideExpanded by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // MAIN PHONE BRIDGE STATUS CARD
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    1.dp,
                    if (isBridgeActive) StatusSuccess else MetaBlue.copy(0.6f),
                    RoundedCornerShape(20.dp)
                ),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                // Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(if (isBridgeActive) StatusSuccess.copy(0.2f) else MetaBlue.copy(0.2f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Smartphone,
                                contentDescription = null,
                                tint = if (isBridgeActive) StatusSuccess else MetaBlue,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "DEMO MODE / SIMULATE META GLASSES",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isBridgeActive) StatusSuccess else MetaBlue,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = if (isBridgeActive) "Demo Mode Active (Phone Camera & Mic)" else "Simulate Glasses on This Phone",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .background(
                                if (isBridgeActive) StatusSuccess.copy(0.2f) else MaterialTheme.colorScheme.surfaceVariant,
                                RoundedCornerShape(12.dp)
                            )
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .background(if (isBridgeActive) StatusSuccess else StatusWarning, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isBridgeActive) "DEMO ACTIVE" else "STANDBY",
                                color = if (isBridgeActive) StatusSuccess else StatusWarning,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Activate Demo Mode to use this smartphone's built-in camera, microphone, and speaker as a full Ray-Ban Meta Glasses hardware proxy. Live video frames, ambient voice queries, and spoken AI responses function instantly without needing physical smart glasses.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                // REALTIME STATUS TELEMETRY GRID
                Text(
                    text = "REAL-TIME PHONE TELEMETRY & HARDWARE STATUS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(0.4f), RoundedCornerShape(16.dp))
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Row 1: Battery & Connection
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Phone Battery Telemetry
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.BatteryChargingFull, contentDescription = null, tint = StatusSuccess, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Phone Battery", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${glassState.batteryPercent}% • ${glassState.chargingStatusText} • Health: ${glassState.batteryHealth}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            LinearProgressIndicator(
                                progress = { (glassState.batteryPercent.coerceIn(0, 100)) / 100f },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(4.dp)
                                    .clip(RoundedCornerShape(2.dp)),
                                color = StatusSuccess,
                                trackColor = MaterialTheme.colorScheme.surface
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        // Connection Status Telemetry
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.BluetoothConnected, contentDescription = null, tint = MetaBlue, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Bluetooth & Wireless", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (isBridgeActive) "BT 5.3 + Wi-Fi Direct" else "Ready to Pair",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isBridgeActive) StatusSuccess else MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (isBridgeActive) "Signal: -42 dBm (Ultra Low Latency)" else "MAC: ${glassState.bluetoothMacAddress}",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Divider line
                    Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(MaterialTheme.colorScheme.outlineVariant.copy(0.3f)))

                    // Row 2: Camera Sensor & Flash Torch
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Camera Sensor Status
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Videocam, contentDescription = null, tint = MetaBlue, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Camera Sensor", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (glassState.cameraFacing == "REAR") "Rear Cam • 1080p 60fps" else "Front Cam • 1080p 30fps",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        // Torch Light Status
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (glassState.isTorchActive) Icons.Default.FlashOn else Icons.Default.FlashOff,
                                    contentDescription = null,
                                    tint = if (glassState.isTorchActive) StatusSuccess else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Flashlight Torch", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (glassState.isTorchActive) "TORCH ACTIVE" else "Standby (OFF)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (glassState.isTorchActive) StatusSuccess else Color.White
                            )
                        }
                    }

                    // Divider line
                    Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(MaterialTheme.colorScheme.outlineVariant.copy(0.3f)))

                    // Row 3: Audio Array & Thermal Health
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Microphone Array
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Mic, contentDescription = null, tint = StatusSuccess, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Phone Mic Array", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Capturing 48kHz Voice",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        // Speaker Output
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.VolumeUp, contentDescription = null, tint = MetaBlue, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Audio Speaker", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Volume 80% • HUD Voice",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Interactive Controls when Active
                if (isBridgeActive) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Camera Flip Toggle
                        OutlinedButton(
                            onClick = { viewModel.togglePhoneBridgeCameraFacing() },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("toggle_camera_facing_button")
                        ) {
                            Icon(Icons.Default.Cameraswitch, contentDescription = null, modifier = Modifier.size(14.dp), tint = MetaBlue)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (glassState.cameraFacing == "REAR") "Rear Cam" else "Front Cam", fontSize = 11.sp, color = Color.White)
                        }

                        // Torch Toggle
                        OutlinedButton(
                            onClick = { viewModel.togglePhoneBridgeTorch() },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("toggle_torch_button")
                        ) {
                            Icon(
                                imageVector = if (glassState.isTorchActive) Icons.Default.FlashOn else Icons.Default.FlashOff,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = if (glassState.isTorchActive) StatusSuccess else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (glassState.isTorchActive) "Torch ON" else "Torch OFF", fontSize = 11.sp, color = Color.White)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Testing Feature Launchpad
                    Text(
                        text = "FEATURE TESTING LAUNCHPAD",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { onNavigateToRoute?.invoke(com.example.ui.navigation.Screen.LiveAi.route) },
                            colors = ButtonDefaults.buttonColors(containerColor = MetaBlue),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("test_live_ai_button")
                        ) {
                            Icon(Icons.Default.Videocam, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Live Vision", fontSize = 11.sp)
                        }

                        Button(
                            onClick = { onNavigateToRoute?.invoke(com.example.ui.navigation.Screen.Assistant.route) },
                            colors = ButtonDefaults.buttonColors(containerColor = MetaBlue),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("test_voice_ai_button")
                        ) {
                            Icon(Icons.Default.Psychology, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Voice AI", fontSize = 11.sp)
                        }

                        Button(
                            onClick = { onNavigateToRoute?.invoke(com.example.ui.navigation.Screen.Tasks.route) },
                            colors = ButtonDefaults.buttonColors(containerColor = MetaBlue),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("test_tasks_button")
                        ) {
                            Icon(Icons.Default.Assignment, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Tasks", fontSize = 11.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedButton(
                        onClick = { viewModel.disablePhoneBridgeMode() },
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, StatusError.copy(0.5f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("disconnect_phone_bridge_button")
                    ) {
                        Text("Exit Demo Mode / Stop Phone Simulation", fontSize = 12.sp, color = StatusError)
                    }
                } else {
                    // Connect Button
                    Button(
                        onClick = { viewModel.enableDemoMode() },
                        colors = ButtonDefaults.buttonColors(containerColor = MetaBlue),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("enable_phone_bridge_button")
                    ) {
                        Icon(Icons.Default.Smartphone, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Start Demo Mode (Simulate Glasses via Phone Cam & Mic)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // BLUETOOTH CONNECTION GUIDE CARD
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, MetaBlue.copy(0.4f), RoundedCornerShape(20.dp)),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isGuideExpanded = !isGuideExpanded },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(MetaBlue.copy(0.15f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.HelpOutline,
                                contentDescription = null,
                                tint = MetaBlue,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "SETUP & BLUETOOTH GUIDE",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MetaBlue,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "How to Connect Mobile Phone via Bluetooth",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    Icon(
                        imageVector = if (isGuideExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null,
                        tint = MetaBlue
                    )
                }

                AnimatedVisibility(visible = isGuideExpanded) {
                    Column(modifier = Modifier.padding(top = 16.dp)) {
                        Text(
                            text = "Follow these simple steps to pair a secondary phone or connect this smartphone as your Ray-Ban Meta Glass hardware emulator over Bluetooth:",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Step 1
                        GuideStepItem(
                            stepNumber = "1",
                            title = "Enable Bluetooth & Wi-Fi",
                            description = "Turn on Bluetooth and Wi-Fi on both your primary device and the mobile phone you wish to use as a smart glass bridge."
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Step 2
                        GuideStepItem(
                            stepNumber = "2",
                            title = "Set Phone in Discovery Mode",
                            description = "Open Bluetooth Settings on the bridge phone and ensure 'Visible to nearby devices' or Pair Mode is enabled."
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Step 3
                        GuideStepItem(
                            stepNumber = "3",
                            title = "Scan & Select Device",
                            description = "In Kaya, scroll to 'Discovered Glasses & Mobile Bridges' on this screen and tap 'Connect Bridge' on 'Smartphone Camera & Mic Bridge'."
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Step 4
                        GuideStepItem(
                            stepNumber = "4",
                            title = "Grant Camera & Mic Permissions",
                            description = "Allow camera and microphone access so the phone can capture visual scenes and ambient voice queries."
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Step 5
                        GuideStepItem(
                            stepNumber = "5",
                            title = "Launch Live Vision & Voice AI",
                            description = "Go to Live Vision to see real-time 1080p camera stream, AI object detection, and spoken HUD audio guidance directly from your phone!"
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                viewModel.enablePhoneBridgeMode("Smartphone Camera & Mic Bridge")
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MetaBlue),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("guide_quick_connect_button")
                        ) {
                            Icon(Icons.Default.BluetoothConnected, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Pair Mobile Phone Bridge Now", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun GuideStepItem(
    stepNumber: String,
    title: String,
    description: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(0.3f), RoundedCornerShape(12.dp))
            .padding(10.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .background(MetaBlue, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stepNumber,
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column {
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = description,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

