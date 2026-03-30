package com.example.finalproject.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.finalproject.R
import com.example.finalproject.ui.components.AppNotificationManager
import com.example.finalproject.ui.components.CustomTextField
import com.example.finalproject.ui.components.PrimaryButton
import com.example.finalproject.ui.theme.PrimaryBlue
import com.example.finalproject.ui.viewmodel.AuthState
import com.example.finalproject.ui.viewmodel.AuthViewModel

@Composable
fun ResetPasswordScreen(
    navController: NavController,
    email: String,
    viewModel: AuthViewModel = hiltViewModel()
) {
    var otp by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    val state by viewModel.resetPassState.collectAsState()

    LaunchedEffect(state) {
        if (state is AuthState.Success) {
            AppNotificationManager.success("✅ Đặt lại mật khẩu thành công!")
            viewModel.resetForgotStates()
            navController.navigate("login") { popUpTo("auth_selection") { inclusive = false } }
        } else if (state is AuthState.Error) {
            AppNotificationManager.error((state as AuthState.Error).message)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp)
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        Text(stringResource(id = R.string.reset_title), fontSize = 28.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)

        // Truyền biến vào String động
        Text(stringResource(id = R.string.reset_email_label, email), fontSize = 14.sp, modifier = Modifier.padding(vertical = 12.dp))

        Spacer(modifier = Modifier.height(24.dp))

        CustomTextField(
            value = otp,
            onValueChange = { otp = it },
            label = stringResource(id = R.string.reset_otp_hint),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        CustomTextField(
            value = newPassword,
            onValueChange = { newPassword = it },
            label = stringResource(id = R.string.reset_new_password_hint),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )

        CustomTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = stringResource(id = R.string.reset_confirm_password_hint),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )

        Spacer(modifier = Modifier.height(24.dp))

        PrimaryButton(
            text = stringResource(id = R.string.reset_btn_confirm),
            onClick = {
                if (otp.isBlank() || newPassword.isBlank()) {
                    AppNotificationManager.error("Vui lòng nhập đầy đủ thông tin")
                } else if (newPassword != confirmPassword) {
                    AppNotificationManager.error("Mật khẩu xác nhận không khớp")
                } else {
                    viewModel.resetPassword(email, otp, newPassword)
                }
            },
            isLoading = state is AuthState.Loading
        )
    }
}