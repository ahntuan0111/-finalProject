package com.example.finalproject.ui.screens.auth

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.finalproject.ui.components.CustomTextField
import com.example.finalproject.ui.components.PrimaryButton
import com.example.finalproject.ui.theme.PrimaryBlue
import com.example.finalproject.ui.theme.TextGray

@Composable
fun ForgotPasswordScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    var email by remember { mutableStateOf("") }
    val state by viewModel.forgotPassState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(state) {
        if (state is AuthState.Success) {
            Toast.makeText(context, "Đã gửi mã OTP! Kiểm tra email.", Toast.LENGTH_LONG).show()
            viewModel.resetForgotStates()
            // Chuyển sang màn nhập OTP, truyền theo email
            navController.navigate("reset_password/$email")
        } else if (state is AuthState.Error) {
            Toast.makeText(context, (state as AuthState.Error).message, Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(60.dp))

        Text(
            text = "Quên mật khẩu",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = PrimaryBlue
        )
        Text(
            text = "Nhập email đã đăng ký để nhận mã OTP xác thực.",
            color = TextGray,
            fontSize = 14.sp,
            modifier = Modifier.padding(top = 8.dp, bottom = 40.dp)
        )

        CustomTextField(
            value = email,
            onValueChange = { email = it },
            label = "Email đăng ký"
        )

        Spacer(modifier = Modifier.height(24.dp))

        PrimaryButton(
            text = "Gửi mã OTP",
            onClick = {
                if (email.isNotBlank()) viewModel.forgotPassword(email)
                else Toast.makeText(context, "Vui lòng nhập email", Toast.LENGTH_SHORT).show()
            },
            isLoading = state is AuthState.Loading
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = { navController.popBackStack() }) {
            Text("Quay lại Đăng nhập", color = TextGray)
        }
    }
}