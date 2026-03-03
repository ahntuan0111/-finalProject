package com.example.finalproject.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Home : BottomNavItem(AppRoutes.HOME, "Home", Icons.Default.Home)
    object Dashboard : BottomNavItem("dashboard", "Thống kê", Icons.Default.BarChart)
    object AiChat : BottomNavItem("ai_chat", "AI Chat", Icons.Default.SmartToy) // Hoặc icon Chat
    object User : BottomNavItem(AppRoutes.USER, "Cá nhân", Icons.Default.Person)
}