package com.example.finalproject

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.finalproject.ui.MainScreen // Import MainScreen
import com.example.finalproject.ui.theme.FinalProjectTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint // Bắt buộc để Hilt hoạt động
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FinalProjectTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Thay vì gọi trực tiếp AppNavHost, ta gọi MainScreen
                    // MainScreen đã chứa logic Scaffold, BottomBar và AppNavHost bên trong
                    MainScreen()
                }
            }
        }
    }
}