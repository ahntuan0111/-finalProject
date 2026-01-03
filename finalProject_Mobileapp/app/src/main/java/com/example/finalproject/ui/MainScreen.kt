package com.example.finalproject.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
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
        bottomBar = {
            if (showBottomBar) {
                BottomNavigationBar(navController = navController)
            }
        }
    ) { innerPadding ->
        // Truyền padding vào AppNavHost để nội dung không bị BottomBar che mất
        AppNavHost(
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        )
    }
}