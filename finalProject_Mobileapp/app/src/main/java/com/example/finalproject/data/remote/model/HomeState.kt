package com.example.finalproject.data.remote.model

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color

// Model cho thẻ Thống kê (Dashboard)
data class DashboardStat(
    @DrawableRes val imageRes: Int, // Đổi từ ImageVector sang Int (Drawable Resource)
    val value: String,
    val label: String,
    val color: Color
)

// Model cho Môn học
data class SubjectItem(
    val id: String,
    val name: String,
    @DrawableRes val imageRes: Int, // Thêm trường ảnh cho môn học
    val progress: Float,
    val totalLessons: Int,
    val finishedLessons: Int,
    val color: Color
)