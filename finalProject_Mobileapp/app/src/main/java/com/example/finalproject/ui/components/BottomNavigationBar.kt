package com.example.finalproject.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
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

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Sử dụng Surface để tạo nền nổi và bo góc
    // Chúng ta sẽ padding bên ngoài để nó không dính sát viền màn hình
    Surface(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 16.dp) // Cách lề để tạo hiệu ứng "nổi"
            .height(64.dp) // Chiều cao cố định
            .shadow(elevation = 8.dp, shape = RoundedCornerShape(32.dp)) // Đổ bóng mềm
            .clip(RoundedCornerShape(32.dp)), // Bo tròn mạnh (hình viên thuốc)
        color = Color.White
    ) {
        NavigationBar(
            containerColor = Color.White, // Nền trắng
            tonalElevation = 0.dp, // Tắt bóng mặc định của NavigationBar để dùng bóng của Surface
            modifier = Modifier.padding(0.dp)
        ) {
            items.forEach { item ->
                val isSelected = currentRoute == item.route

                NavigationBarItem(
                    icon = {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.title,
                            modifier = Modifier.padding(bottom = 2.dp) // Căn chỉnh chút cho đẹp
                        )
                    },
                    // Có thể ẩn label nếu muốn style tối giản, hoặc giữ lại
                    label = {
                        if (isSelected) { // Chỉ hiện chữ khi được chọn (Option style hiện đại)
                            Text(text = item.title, style = MaterialTheme.typography.labelSmall)
                        }
                    },
                    selected = isSelected,
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = PrimaryBlue,
                        selectedTextColor = PrimaryBlue,
                        indicatorColor = Color(0xFFE3F2FD), // Nền xanh nhạt khi chọn
                        unselectedIconColor = TextGray.copy(alpha = 0.6f),
                        unselectedTextColor = TextGray
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
                    alwaysShowLabel = false // Tắt luôn hiện nhãn để UI thoáng hơn
                )
            }
        }
    }
}