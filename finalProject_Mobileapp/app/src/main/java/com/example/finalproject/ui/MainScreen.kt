package com.example.finalproject.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.finalproject.ui.components.BottomNavigationBar
import com.example.finalproject.ui.navigation.AppNavHost
import com.example.finalproject.ui.navigation.AppRoutes

@Composable
fun MainScreen() {
    val navController = rememberNavController()

    // Lấy route hiện tại để kiểm tra xem có nên hiện BottomBar không
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Danh sách các màn hình CÓ BottomBar
    val showBottomBar = currentRoute in listOf(
        AppRoutes.HOME,
        AppRoutes.DASHBOARD,
        AppRoutes.AI_CHAT,
        AppRoutes.USER
    )

    Scaffold(
        // Đặt màu nền container trùng với màu nền của các màn hình con (ví dụ Home xám nhạt)
        // Để phần padding của BottomBar không bị lệch màu
        containerColor = Color(0xFFFAFAFA),
        bottomBar = {
            if (showBottomBar) {
                // BottomBar của chúng ta giờ đã có padding tự thân bên trong component
                BottomNavigationBar(navController = navController)
            }
        }
    ) { innerPadding ->
        AppNavHost(
            navController = navController,
            // Chỉ cần padding bottom một chút nếu cần,
            // nhưng vì BottomBar nổi nên nội dung có thể cuộn xuống dưới nó.
            // Tuy nhiên, để an toàn không bị che nội dung cuối:
            modifier = Modifier.padding(innerPadding)
        )
    }
}