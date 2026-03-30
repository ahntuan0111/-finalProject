package com.example.finalproject.ui.screens.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.finalproject.R
import com.example.finalproject.ui.components.AppNotificationManager
import com.example.finalproject.ui.components.CustomTextField
import com.example.finalproject.ui.components.PrimaryButton
import com.example.finalproject.ui.components.SocialButton
import com.example.finalproject.ui.theme.PrimaryBlue
import com.example.finalproject.ui.viewmodel.AuthState
import com.example.finalproject.ui.viewmodel.AuthViewModel

@Composable
fun LoginScreen(navController: NavController, viewModel: AuthViewModel = hiltViewModel()) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    // Inline validation errors
    var usernameError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }

    val state by viewModel.loginState.collectAsState()

    LaunchedEffect(state) {
        when (state) {
            is AuthState.LoginSuccess -> {
                val hasGrade = (state as AuthState.LoginSuccess).hasGrade
                AppNotificationManager.success("Chào mừng trở lại! 🎉")
                if (hasGrade) {
                    navController.navigate("home") { popUpTo("welcome") { inclusive = true } }
                } else {
                    navController.navigate("class_selection") { popUpTo("welcome") { inclusive = true } }
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
            .background(Color.White)
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        // Branding
        Text(
            text = "MaSci",
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold,
            color = PrimaryBlue,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = stringResource(id = R.string.login_title),
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E293B)
        )
        Text(
            text = stringResource(id = R.string.login_subtitle),
            fontSize = 15.sp,
            color = Color(0xFF64748B),
            modifier = Modifier.padding(top = 8.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Circular Illustration
        Box(
            modifier = Modifier
                .size(180.dp)
                .clip(CircleShape)
                .background(Color(0xFFF1F5F9)),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.study_time),
                contentDescription = "Login Illustration",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // --- Username field ---
        CustomTextField(
            value = username,
            onValueChange = {
                username = it
                usernameError = null
            },
            label = stringResource(id = R.string.login_username_hint),
            showLabelOutside = false,
            leadingIcon = {
                Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = PrimaryBlue)
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            isError = usernameError != null
        )
        if (usernameError != null) {
            Text(
                text = "⚠ $usernameError",
                color = Color(0xFFFF4D4F),
                fontSize = 12.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 4.dp, bottom = 6.dp)
            )
        }

        // --- Password field ---
        CustomTextField(
            value = password,
            onValueChange = {
                password = it
                passwordError = null
            },
            label = stringResource(id = R.string.login_password_hint),
            showLabelOutside = false,
            leadingIcon = {
                Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = PrimaryBlue)
            },
            trailingIcon = {
                val image = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = image, contentDescription = null, tint = Color(0xFF94A3B8))
                }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
            isError = passwordError != null
        )
        if (passwordError != null) {
            Text(
                text = "⚠ $passwordError",
                color = Color(0xFFFF4D4F),
                fontSize = 12.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 4.dp, bottom = 6.dp)
            )
        }

        Text(
            text = stringResource(id = R.string.login_forgot_password),
            color = PrimaryBlue,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.End)
                .padding(bottom = 24.dp)
                .clickable { navController.navigate("forgot_password") }
        )

        PrimaryButton(
            text = stringResource(id = R.string.login_title),
            onClick = {
                var hasError = false
                if (username.isBlank()) {
                    usernameError = "Vui lòng nhập tên đăng nhập"
                    hasError = true
                }
                if (password.isBlank()) {
                    passwordError = "Vui lòng nhập mật khẩu"
                    hasError = true
                }
                if (!hasError) {
                    viewModel.login(username, password)
                }
            },
            isLoading = state is AuthState.Loading
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Social Login
        Row(verticalAlignment = Alignment.CenterVertically) {
            HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFE2E8F0))
            Text(
                text = " " + stringResource(id = R.string.social_login_or) + " ",
                fontSize = 12.sp,
                color = Color(0xFF94A3B8)
            )
            HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFE2E8F0))
        }

        Spacer(modifier = Modifier.height(24.dp))

        SocialButton(
            text = stringResource(id = R.string.social_login_google),
            icon = R.drawable.ic_launcher_foreground,
            onClick = { /* Handle Google Login */ }
        )

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text = stringResource(id = R.string.login_no_account), color = Color(0xFF64748B))
            Text(
                text = stringResource(id = R.string.login_btn_signup),
                color = PrimaryBlue,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { navController.navigate("register") }
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "© 2024 MaSci Learning Platform. All rights reserved.",
            fontSize = 11.sp,
            color = Color(0xFF94A3B8),
            textAlign = TextAlign.Center
        )
    }
}