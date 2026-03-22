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
import com.example.finalproject.utils.DataStoreManager // <-- THÊM IMPORT NÀY

@Composable
fun MainScreen(
    dataStoreManager: DataStoreManager,
    startDestination: String // <-- THÊM THAM SỐ NÀY
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
        AppNavHost(
            navController = navController,
            dataStoreManager = dataStoreManager,
            startDestination = startDestination, // <-- TRUYỀN XUỐNG ĐÂY
            modifier = Modifier.padding(innerPadding)
        )
    }
}