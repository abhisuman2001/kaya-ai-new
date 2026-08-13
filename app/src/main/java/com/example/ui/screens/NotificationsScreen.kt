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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.NotificationCategory
import com.example.ui.components.NotificationCategoryFilterCard
import com.example.ui.components.NotificationHeaderCard
import com.example.ui.components.NotificationItemCard
import com.example.ui.components.NotificationSettingsCard
import com.example.ui.theme.MetaBlue
import com.example.ui.viewmodel.KayaViewModel

@Composable
fun NotificationsScreen(
    viewModel: KayaViewModel,
    onNavigateToRoute: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val notifState by viewModel.notificationState.collectAsStateWithLifecycle()

    val filteredNotifications = if (notifState.selectedCategory == NotificationCategory.ALL) {
        notifState.notificationsList
    } else {
        notifState.notificationsList.filter { it.category == notifState.selectedCategory }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp)
            .testTag("notifications_screen_list"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(8.dp)) }

        // Screen Header Title
        item {
            Column {
                Text(
                    text = "PHASE 14 — DISPATCH & HUD NOTIFICATIONS",
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = MetaBlue,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Push Alerts, Hazards, Reports & Tasks",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Live telemetry feed synced with Ray-Ban HUD audio chime & mobile push.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // 1. Notification Header Card (Unread badge, Mark All Read, Test Push)
        item {
            NotificationHeaderCard(
                unreadCount = notifState.unreadCount,
                isTestPushSent = notifState.isTestPushSent,
                onMarkAllRead = { viewModel.markAllNotificationsAsRead() },
                onSendTestPush = { viewModel.sendTestPushNotification() }
            )
        }

        // 2. Category Filter Pills (All, AI Vision, Hazards, Reports, Firmware, Tasks)
        item {
            NotificationCategoryFilterCard(
                selectedCategory = notifState.selectedCategory,
                onCategorySelected = { cat -> viewModel.selectNotificationCategory(cat) }
            )
        }

        // 3. Notification Items List
        items(
            items = filteredNotifications,
            key = { it.id }
        ) { notif ->
            NotificationItemCard(
                item = notif,
                onMarkRead = { viewModel.markNotificationAsRead(notif.id) },
                onActionClick = { route -> onNavigateToRoute(route) }
            )
        }

        // 4. Notification Settings Toggle Card
        item {
            NotificationSettingsCard(
                settings = notifState.settings,
                onUpdateSettings = { push, ai, hazards, reports, firmware, tasks ->
                    viewModel.updateNotificationSettings(push, ai, hazards, reports, firmware, tasks)
                }
            )
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}
