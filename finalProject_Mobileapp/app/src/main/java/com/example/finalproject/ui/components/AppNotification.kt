package com.example.finalproject.ui.components

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

// ============================================================
// Data Models
// ============================================================

sealed class AppNotificationType {
    object Success     : AppNotificationType()
    object Error       : AppNotificationType()
    object Info        : AppNotificationType()
    object Achievement : AppNotificationType()
}

data class AppNotification(
    val message: String,
    val type: AppNotificationType,
    val durationMs: Long = 3000L
)

// ============================================================
// Global Singleton Manager — gọi từ bất kỳ màn hình nào
// ============================================================

object AppNotificationManager {

    private val _current = MutableStateFlow<AppNotification?>(null)
    val current = _current.asStateFlow()

    fun show(
        message: String,
        type: AppNotificationType,
        durationMs: Long = 3000L
    ) {
        _current.value = AppNotification(message, type, durationMs)
    }

    fun success(message: String, durationMs: Long = 3000L) =
        show(message, AppNotificationType.Success, durationMs)

    fun error(message: String, durationMs: Long = 3500L) =
        show(message, AppNotificationType.Error, durationMs)

    fun info(message: String, durationMs: Long = 3000L) =
        show(message, AppNotificationType.Info, durationMs)

    fun achievement(message: String, durationMs: Long = 4000L) =
        show(message, AppNotificationType.Achievement, durationMs)

    fun dismiss() {
        _current.value = null
    }
}
