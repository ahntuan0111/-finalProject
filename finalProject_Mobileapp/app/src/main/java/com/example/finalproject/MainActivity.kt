package com.example.finalproject

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.finalproject.ui.MainScreen
import com.example.finalproject.ui.navigation.AppRoutes // IMPORT THÊM DÒNG NÀY
import com.example.finalproject.ui.theme.FinalProjectTheme
import com.example.finalproject.utils.DataStoreManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking // IMPORT THÊM DÒNG NÀY
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var dataStoreManager: DataStoreManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Đọc token đồng bộ ngay khi mở App
        val initialToken = runBlocking { dataStoreManager.getTokenSync() }

        // 2. Quyết định màn hình đầu tiên (Có token -> HOME, Không token -> WELCOME)
        val startDestination = if (initialToken.isNullOrEmpty()) {
            AppRoutes.WELCOME
        } else {
            AppRoutes.HOME
        }

        setContent {
            FinalProjectTheme {
                MainScreen(
                    dataStoreManager = dataStoreManager,
                    startDestination = startDestination // 3. Truyền biến này vào MainScreen
                )
            }
        }
    }
}