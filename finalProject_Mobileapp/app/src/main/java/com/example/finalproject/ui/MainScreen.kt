package com.example.finalproject.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.finalproject.ui.components.AppNotificationHost
import com.example.finalproject.ui.components.BottomNavigationBar
import com.example.finalproject.ui.navigation.AppNavHost
import com.example.finalproject.ui.navigation.AppRoutes
import com.example.finalproject.utils.DataStoreManager

@Composable
fun MainScreen(
    dataStoreManager: DataStoreManager,
    startDestination: String
) {
    val navController = rememberNavController()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = currentRoute in listOf(
        AppRoutes.HOME, AppRoutes.DASHBOARD, AppRoutes.AI_CHAT, AppRoutes.REMINDER, AppRoutes.USER
    )

    Scaffold(
        containerColor = Color(0xFFFAFAFA),
        bottomBar = {
            if (showBottomBar) {
                BottomNavigationBar(navController = navController)
            }
        }
    ) { innerPadding ->
        // Dùng Box để overlay banner thông báo lên trên mọi màn hình
        Box(modifier = Modifier.fillMaxSize()) {
            AppNavHost(
                navController = navController,
                dataStoreManager = dataStoreManager,
                startDestination = startDestination,
                modifier = Modifier.padding(innerPadding)
            )
            // Banner nổi trên cùng — không bị che bởi BottomBar hay bàn phím
            AppNotificationHost()
        }
    }
}