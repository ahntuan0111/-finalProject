package com.example.finalproject.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.finalproject.ui.navigation.BottomNavItem
import com.example.finalproject.ui.theme.PrimaryBlue

@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Dashboard,
        BottomNavItem.AiChat,
        BottomNavItem.Reminder,
        BottomNavItem.User
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Tính năng Glassmorphism: màu trong suốt kết hợp blur
    // Tăng chiều cao để chứa nút FAB (AI)
    Surface(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 20.dp)
            .height(76.dp)
            .shadow(elevation = 16.dp, shape = RoundedCornerShape(38.dp), spotColor = Color.Black.copy(alpha = 0.15f))
            .clip(RoundedCornerShape(38.dp)),
        color = MaterialTheme.colorScheme.surface // Nền rõ ràng, không blur
    ) {
        NavigationBar(
            containerColor = Color.Transparent,
            tonalElevation = 0.dp,
            modifier = Modifier.padding(0.dp)
        ) {
            items.forEach { item ->
                val isSelected = currentRoute == item.route

                if (item == BottomNavItem.AiChat) {
                    // Nút AI trung tâm dạng FAB (kẹp ngay giữa tabbar)
                    NavigationBarItem(
                        icon = {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(CircleShape)
                                    .background(Brush.linearGradient(listOf(Color(0xFF2196F3), Color(0xFF9C27B0))))
                                    .offset(y = (-4).dp), // Nổi nhẹ lên trên so với các nút khác
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isSelected) item.activeIcon else item.icon,
                                    contentDescription = item.title,
                                    tint = Color.White,
                                    modifier = Modifier.size(30.dp) // Icon to hơn bình thường
                                )
                            }
                        },
                        selected = isSelected,
                        onClick = {
                            if (currentRoute != item.route) {
                                navController.navigate(item.route) {
                                    navController.graph.startDestinationRoute?.let { route ->
                                        popUpTo(route) { saveState = true }
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = Color.Transparent // Tắt "pill" cho nút AI vì đã có bg gradient
                        ),
                        label = null,
                        alwaysShowLabel = false
                    )
                } else {
                    // Các mục điều hướng bình thường có "Pill"
                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = if (isSelected) item.activeIcon else item.icon,
                                contentDescription = item.title,
                                modifier = Modifier.padding(bottom = 2.dp)
                            )
                        },
                        label = {
                            if (isSelected) {
                                Text(text = item.title, style = MaterialTheme.typography.labelSmall)
                            }
                        },
                        selected = isSelected,
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = PrimaryBlue,
                            selectedTextColor = PrimaryBlue,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer, // Pill indicator từ Compose M3
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        onClick = {
                            if (currentRoute != item.route) {
                                navController.navigate(item.route) {
                                    navController.graph.startDestinationRoute?.let { route ->
                                        popUpTo(route) { saveState = true }
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        alwaysShowLabel = false
                    )
                }
            }
        }
    }
}