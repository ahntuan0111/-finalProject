package com.example.finalproject.ui.screens.auth

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.finalproject.R
import com.example.finalproject.ui.components.CustomTextField
import com.example.finalproject.ui.components.PrimaryButton
import com.example.finalproject.ui.theme.PrimaryBlue
import com.example.finalproject.ui.theme.TextGray

@Composable
fun LoginScreen(navController: NavController, viewModel: AuthViewModel = hiltViewModel()) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val state by viewModel.loginState.collectAsState()
    val context = LocalContext.current

    // --- LOGIC XỬ LÝ TRẠNG THÁI ---
    LaunchedEffect(state) {
        when (state) {
            is AuthState.LoginSuccess -> {
                val hasGrade = (state as AuthState.LoginSuccess).hasGrade
                Toast.makeText(context, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show()

                if (hasGrade) {
                    // Đã có lớp -> Vào Home
                    navController.navigate("home") {
                        popUpTo("welcome") { inclusive = true }
                    }
                } else {
                    // Chưa có lớp -> Vào chọn lớp
                    navController.navigate("class_selection") {
                        popUpTo("welcome") { inclusive = true }
                    }
                }
            }
            is AuthState.Error -> {
                Toast.makeText(context, (state as AuthState.Error).message, Toast.LENGTH_SHORT).show()
                viewModel.resetState() // Reset để tránh Toast hiện lại khi xoay màn hình
            }
            else -> {}
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "Login",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = PrimaryBlue,
            modifier = Modifier.align(Alignment.Start)
        )
        Text(
            text = "Login to your account",
            fontSize = 16.sp,
            color = TextGray,
            modifier = Modifier.align(Alignment.Start).padding(top = 8.dp)
        )

        Spacer(modifier = Modifier.height(40.dp))

        CustomTextField(
            value = username,
            onValueChange = { username = it },
            label = "Username",
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
        )

        CustomTextField(
            value = password,
            onValueChange = { password = it },
            label = "Password",
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done)
        )

        Text(
            text = "Forgot Password?",
            color = PrimaryBlue,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.End)
                .padding(top = 8.dp, bottom = 24.dp)
                .clickable {
                    navController.navigate("forgot_password")
                }
        )

        PrimaryButton(
            text = "Login",
            onClick = { viewModel.login(username, password) },
            isLoading = state is AuthState.Loading
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text = "Don't have an account? ", color = TextGray)
            Text(
                text = "Sign up",
                color = PrimaryBlue,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable {
                    navController.navigate("register")
                }
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Image(
            painter = painterResource(id = R.drawable.study_time), // Đảm bảo bạn có ảnh này
            contentDescription = "Login Illustration",
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        )
    }
}