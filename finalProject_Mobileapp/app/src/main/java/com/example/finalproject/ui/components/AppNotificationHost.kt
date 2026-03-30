package com.example.finalproject.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.delay

// ============================================================
// Helpers: màu sắc & icon theo loại thông báo
// ============================================================

private data class NotificationStyle(
    val background: Color,
    val border: Color,
    val textColor: Color,
    val icon: String
)

private fun styleFor(type: AppNotificationType) = when (type) {
    is AppNotificationType.Success     -> NotificationStyle(Color(0xFFF6FFED), Color(0xFF52C41A), Color(0xFF389E0D), "✅")
    is AppNotificationType.Error       -> NotificationStyle(Color(0xFFFFF2F0), Color(0xFFFF4D4F), Color(0xFFCF1322), "⚠️")
    is AppNotificationType.Info        -> NotificationStyle(Color(0xFFE6F7FF), Color(0xFF1890FF), Color(0xFF0050B3), "ℹ️")
    is AppNotificationType.Achievement -> NotificationStyle(Color(0xFFFFFBE6), Color(0xFFFAAD14), Color(0xFFAD6800), "🏆")
}

// ============================================================
// Banner Composable — mount ở root (MainScreen)
// ============================================================

@Composable
fun AppNotificationHost() {
    val notification by AppNotificationManager.current.collectAsState()
    var visible by remember { mutableStateOf(false) }
    var currentNotif by remember { mutableStateOf<AppNotification?>(null) }

    // Khi notification thay đổi → animate in/out
    LaunchedEffect(notification) {
        if (notification != null) {
            currentNotif = notification
            visible = true
            delay(notification!!.durationMs)
            visible = false
            delay(400) // chờ animation out
            AppNotificationManager.dismiss()
        } else {
            visible = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .zIndex(999f),
        contentAlignment = Alignment.TopCenter
    ) {
        AnimatedVisibility(
            visible = visible,
            enter = slideInVertically(
                initialOffsetY = { -it - 100 },
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMediumLow
                )
            ) + fadeIn(animationSpec = tween(200)),
            exit = slideOutVertically(
                targetOffsetY = { -it - 100 },
                animationSpec = tween(300, easing = EaseInCubic)
            ) + fadeOut(animationSpec = tween(200))
        ) {
            currentNotif?.let { notif ->
                NotificationBanner(
                    notification = notif,
                    onDismiss = {
                        visible = false
                        AppNotificationManager.dismiss()
                    }
                )
            }
        }
    }
}

// ============================================================
// Banner Card UI
// ============================================================

@Composable
private fun NotificationBanner(
    notification: AppNotification,
    onDismiss: () -> Unit
) {
    val style = styleFor(notification.type)
    var offsetY by remember { mutableFloatStateOf(0f) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .offset { IntOffset(0, offsetY.toInt().coerceAtMost(0)) }
            .pointerInput(Unit) {
                detectVerticalDragGestures(
                    onDragEnd = {
                        if (offsetY < -40f) onDismiss()
                        else offsetY = 0f
                    }
                ) { _, dragAmount ->
                    if (dragAmount < 0) offsetY += dragAmount
                }
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(elevation = 8.dp, shape = RoundedCornerShape(16.dp))
                .clip(RoundedCornerShape(16.dp))
                .background(style.background)
                // Viền trái nhấn màu
                .then(
                    Modifier.background(
                        color = style.border,
                        shape = RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp)
                    ).padding(start = 4.dp)
                )
                .background(style.background, RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp))
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = style.icon,
                fontSize = 22.sp,
                modifier = Modifier.padding(end = 12.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = notification.message,
                    color = style.textColor,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    lineHeight = 20.sp
                )
            }
        }
    }
}
