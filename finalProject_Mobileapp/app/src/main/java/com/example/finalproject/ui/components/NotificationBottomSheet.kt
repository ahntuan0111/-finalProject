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
        containerColor = Color.White,
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
                    color = Color(0xFF1A1A1A)
                )
                TextButton(onClick = onMarkAllRead) {
                    Text("Đánh dấu đã đọc", color = Color(0xFF1677FF), fontWeight = FontWeight.Medium)
                }
            }

            HorizontalDivider(color = Color(0xFFEEEEEE))

            // --- LIST ---
            if (isLoading && notifications.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF1677FF))
                }
            } else if (notifications.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Bạn chưa có thông báo nào.", color = Color.Gray)
                }
            } else {
                LazyColumn {
                    items(notifications) { notif ->
                        NotificationItem(
                            notification = notif,
                            onClick = { onItemClick(notif) }
                        )
                        HorizontalDivider(color = Color(0xFFF5F5F5))
                    }
                }
            }
        }
    }
}

@Composable
fun NotificationItem(notification: NotificationDto, onClick: () -> Unit) {
    // Styling tuỳ thuộc vào Đã đọc/Chưa đọc
    val bgColor = if (notification.isRead) Color.White else Color(0xFFF0F7FF)
    val titleWeight = if (notification.isRead) FontWeight.Medium else FontWeight.Bold
    val titleColor = if (notification.isRead) Color(0xFF333333) else Color(0xFF1A1A1A)

    // Cấu hình Icon và Màu sắc dựa theo Type
    val (icon, iconColor, iconBg) = when (notification.type) {
        "REMINDER" -> Triple(Icons.Default.Notifications, Color(0xFFFF9800), Color(0xFFFFF3E0))
        "GAMIFICATION" -> Triple(Icons.Default.Star, Color(0xFFFFC107), Color(0xFFFFF8E1))
        "SYSTEM" -> Triple(Icons.Default.Info, Color(0xFF2196F3), Color(0xFFE3F2FD))
        else -> Triple(Icons.Default.Notifications, Color(0xFF9E9E9E), Color(0xFFF5F5F5))
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
                color = Color(0xFF666666),
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
                color = Color(0xFF999999)
            )
        }

        // 3. Dấu chấm đỏ/xanh (Chưa đọc) ở góc phải
        if (!notification.isRead) {
            Box(
                modifier = Modifier
                    .padding(start = 8.dp, top = 6.dp)
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF1677FF))
            )
        }
    }
}