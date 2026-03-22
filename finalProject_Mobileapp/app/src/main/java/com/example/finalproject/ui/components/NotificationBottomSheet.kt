package com.example.finalproject.ui.components

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.finalproject.data.remote.model.NotificationDto
import com.example.finalproject.utils.TimeUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationBottomSheet(
    notifications: List<NotificationDto>,
    isLoading: Boolean,
    onDismiss: () -> Unit,
    onMarkAllRead: () -> Unit,
    onItemClick: (NotificationDto) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f) // Chiếm 85% màn hình khi mở
        ) {
            // --- HEADER ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Thông báo",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                TextButton(onClick = onMarkAllRead) {
                    Text("Đánh dấu đã đọc", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Medium)
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

            // --- LIST ---
            if (isLoading && notifications.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            } else if (notifications.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Bạn chưa có thông báo nào.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn {
                    items(notifications) { notif ->
                        NotificationItem(
                            notification = notif,
                            onClick = { onItemClick(notif) }
                        )
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    }
                }
            }
        }
    }
}

@Composable
fun NotificationItem(notification: NotificationDto, onClick: () -> Unit) {
    // Styling tuỳ thuộc vào Đã đọc/Chưa đọc
    val bgColor = if (notification.isRead) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.primaryContainer
    val titleWeight = if (notification.isRead) FontWeight.Medium else FontWeight.Bold
    val titleColor = if (notification.isRead) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface

    // Cấu hình Icon và Màu sắc dựa theo Type
    val (icon, iconColor, iconBg) = when (notification.type) {
        "REMINDER" -> Triple(Icons.Default.Notifications, MaterialTheme.colorScheme.onSecondaryContainer, MaterialTheme.colorScheme.secondaryContainer)
        "GAMIFICATION" -> Triple(Icons.Default.Star, MaterialTheme.colorScheme.onTertiaryContainer, MaterialTheme.colorScheme.tertiaryContainer)
        "SYSTEM" -> Triple(Icons.Default.Info, MaterialTheme.colorScheme.onPrimaryContainer, MaterialTheme.colorScheme.primaryContainer)
        else -> Triple(Icons.Default.Notifications, MaterialTheme.colorScheme.onSurfaceVariant, MaterialTheme.colorScheme.surfaceVariant)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(bgColor)
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.Top
    ) {
        // 1. Icon bên trái
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(iconBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(24.dp))
        }

        Spacer(modifier = Modifier.width(16.dp))

        // 2. Nội dung text ở giữa
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = notification.title,
                fontSize = 15.sp,
                fontWeight = titleWeight,
                color = titleColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = notification.message,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(6.dp))

            // Format Time (5 phút trước)
            val timeText = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                TimeUtils.getRelativeTime(notification.createdAt)
            } else {
                notification.createdAt.substringBefore("T")
            }

            Text(
                text = timeText,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // 3. Dấu chấm đỏ/xanh (Chưa đọc) ở góc phải
        if (!notification.isRead) {
            Box(
                modifier = Modifier
                    .padding(start = 8.dp, top = 6.dp)
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
            )
        }
    }
}