package com.example.finalproject.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.finalproject.ui.components.AppNotificationManager
import com.example.finalproject.ui.components.PrimaryButton
import com.example.finalproject.ui.theme.PrimaryBlue
import com.example.finalproject.ui.viewmodel.AuthState
import com.example.finalproject.ui.viewmodel.AuthViewModel

@Composable
fun OtpVerificationScreen(
    navController: NavController,
    email: String,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val state by viewModel.registerState.collectAsState()
    val otpValues = remember { mutableStateListOf("", "", "", "", "", "") }
    val focusRequesters = remember { List(6) { FocusRequester() } }

    LaunchedEffect(state) {
        when (state) {
            is AuthState.Success -> {
                AppNotificationManager.achievement("🏆 Tạo tài khoản thành công! Chào mừng bạn!")
                viewModel.resetState()
                navController.navigate("login") {
                    popUpTo("welcome") { inclusive = false }
                }
            }
            is AuthState.Error -> {
                AppNotificationManager.error((state as AuthState.Error).message)
                viewModel.resetState()
            }
            else -> {}
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Back Button
        IconButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier.align(Alignment.Start)
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color(0xFF1E293B))
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Xác thực tài khoản",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E293B)
        )

        Text(
            text = "Chúng tôi đã gửi mã xác thực đến\n$email",
            fontSize = 14.sp,
            color = Color(0xFF64748B),
            textAlign = TextAlign.Center,
            // ĐÃ SỬA: Tách thành 2 lệnh padding nối tiếp nhau
            modifier = Modifier
                .padding(top = 12.dp)
                .padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(48.dp))

        // 6-Digit OTP Input
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            otpValues.forEachIndexed { index, value ->
                OtpBox(
                    value = value,
                    onValueChange = { newValue ->
                        if (newValue.length <= 1) {
                            otpValues[index] = newValue
                            if (newValue.isNotEmpty() && index < 5) {
                                focusRequesters[index + 1].requestFocus()
                            }
                        }
                    },
                    onBackspace = {
                        if (value.isEmpty() && index > 0) {
                            focusRequesters[index - 1].requestFocus()
                        }
                    },
                    focusRequester = focusRequesters[index],
                    modifier = Modifier.weight(1f).padding(horizontal = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(48.dp))

        PrimaryButton(
            text = "Xác nhận",
            onClick = {
                val fullOtp = otpValues.joinToString("")
                if (fullOtp.length == 6) {
                    viewModel.verifyRegisterOtp(email, fullOtp)
                } else {
                    AppNotificationManager.error("Vui lòng nhập đủ 6 số")
                }
            },
            isLoading = state is AuthState.Loading
        )

        Spacer(modifier = Modifier.height(32.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "Bạn chưa nhận được mã? ", fontSize = 14.sp, color = Color(0xFF64748B))
            Text(
                text = "Gửi lại ngay",
                fontSize = 14.sp,
                color = PrimaryBlue,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable {
                    AppNotificationManager.info("📧 Đã gửi lại mã OTP đến email của bạn")
                }
            )
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun OtpBox(
    value: String,
    onValueChange: (String) -> Unit,
    onBackspace: () -> Unit,
    focusRequester: FocusRequester,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .aspectRatio(1f)
            .focusRequester(focusRequester)
            .onKeyEvent {
                if (it.key == Key.Backspace && it.type == KeyEventType.KeyDown) {
                    onBackspace()
                    false
                } else {
                    false
                }
            },
        textStyle = TextStyle(
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = Color(0xFF1E293B)
        ),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = PrimaryBlue,
            unfocusedBorderColor = Color(0xFFE2E8F0),
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White
        )
    )
}