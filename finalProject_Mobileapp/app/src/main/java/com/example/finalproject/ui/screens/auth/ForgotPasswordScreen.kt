package com.example.finalproject.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
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
fun ForgotPasswordScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    var email by remember { mutableStateOf("") }
    val state by viewModel.forgotPassState.collectAsState()

    LaunchedEffect(state) {
        if (state is AuthState.Success) {
            AppNotificationManager.info("📧 Mã OTP đã gửi, hãy kiểm tra email nhé!")
            viewModel.resetForgotStates()
            navController.navigate("reset_password/$email")
        } else if (state is AuthState.Error) {
            AppNotificationManager.error((state as AuthState.Error).message)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(60.dp))

        Text(
            text = stringResource(id = R.string.forgot_title),
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = PrimaryBlue
        )
        Text(
            text = stringResource(id = R.string.forgot_subtitle),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 14.sp,
            modifier = Modifier.padding(top = 8.dp, bottom = 40.dp)
        )

        CustomTextField(
            value = email,
            onValueChange = { email = it },
            label = stringResource(id = R.string.forgot_email_hint)
        )

        Spacer(modifier = Modifier.height(24.dp))

        PrimaryButton(
            text = stringResource(id = R.string.forgot_btn_send_otp),
            onClick = {
                if (email.isNotBlank()) viewModel.forgotPassword(email)
                else AppNotificationManager.error("Vui lòng nhập địa chỉ email")
            },
            isLoading = state is AuthState.Loading
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = { navController.popBackStack() }) {
            Text(stringResource(id = R.string.forgot_back_to_login), color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}