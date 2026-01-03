package com.example.finalproject.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.finalproject.ui.navigation.BottomNavItem
import com.example.finalproject.ui.theme.PrimaryBlue
import com.example.finalproject.ui.theme.TextGray

@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Dashboard,
        BottomNavItem.AiChat,
        BottomNavItem.User
    )

    // Lấy route hiện tại để biết item nào đang được chọn
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        containerColor = Color.White, // Nền trắng sạch sẽ
        tonalElevation = 8.dp
    ) {
        items.forEach { item ->
            val isSelected = currentRoute == item.route

            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title
                    )
                },
                label = { Text(text = item.title) },
                selected = isSelected,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = PrimaryBlue, // Icon khi chọn
                    selectedTextColor = PrimaryBlue, // Chữ khi chọn
                    indicatorColor = Color(0xFFE3F2FD), // Nền tròn bao quanh icon khi chọn (Xanh rất nhạt)
                    unselectedIconColor = TextGray,
                    unselectedTextColor = TextGray
                ),
                onClick = {
                    navController.navigate(item.route) {
                        // 1. Pop up đến đích bắt đầu để tránh chồng chất stack khi bấm Back
                        navController.graph.startDestinationRoute?.let { route ->
                            popUpTo(route) {
                                saveState = true
                            }
                        }
                        // 2. Tránh mở lại cùng 1 màn hình khi bấm nhiều lần
                        launchSingleTop = true
                        // 3. Khôi phục trạng thái (scroll position...) khi quay lại
                        restoreState = true
                    }
                }
            )
        }
    }
}