package com.example.finalproject.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector,
    val activeIcon: ImageVector
) {
    object Home : BottomNavItem(AppRoutes.HOME, "Home", Icons.Outlined.Home, Icons.Filled.Home)
    object Dashboard : BottomNavItem(AppRoutes.DASHBOARD, "Thống kê", Icons.Outlined.BarChart, Icons.Filled.BarChart)
    object AiChat : BottomNavItem(AppRoutes.AI_CHAT, "AI Chat", Icons.Outlined.SmartToy, Icons.Filled.SmartToy)
    object Reminder : BottomNavItem(AppRoutes.REMINDER, "Nhắc nhở", Icons.Outlined.NotificationsNone, Icons.Filled.Notifications)
    object User : BottomNavItem(AppRoutes.USER, "Cá nhân", Icons.Outlined.Person, Icons.Filled.Person)
}