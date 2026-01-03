package com.example.finalproject.ui.screens.home

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

// Model cho thẻ Thống kê (Dashboard)
data class DashboardStat(
    val icon: ImageVector,
    val value: String,
    val label: String,
    val color: Color
)

// Model cho Môn học
data class SubjectItem(
    val id: String,
    val name: String,
    val progress: Float, // 0.0 -> 1.0
    val totalLessons: Int,
    val finishedLessons: Int,
    val color: Color
)