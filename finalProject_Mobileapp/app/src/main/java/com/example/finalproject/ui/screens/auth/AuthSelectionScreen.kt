package com.example.finalproject.ui.screens.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.finalproject.R // Đảm bảo import R
import com.example.finalproject.ui.components.PrimaryButton
import com.example.finalproject.ui.theme.PrimaryBlue

@Composable
fun AuthSelectionScreen(navController: NavController) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Logo hoặc Ảnh minh họa
        // Image(painter = painterResource(id = R.drawable.logo), contentDescription = "Logo")

        Spacer(modifier = Modifier.height(48.dp))

        PrimaryButton(text = "Đăng nhập", onClick = { navController.navigate("login") })

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = { navController.navigate("register") },
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text("Đăng ký tài khoản mới", color = PrimaryBlue)
        }
    }
}