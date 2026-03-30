package com.example.finalproject.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import com.example.finalproject.ui.components.AppNotificationType
import com.example.finalproject.ui.components.CustomTextField
import com.example.finalproject.ui.components.PrimaryButton
import com.example.finalproject.ui.components.SocialButton
import com.example.finalproject.ui.theme.PrimaryBlue
import com.example.finalproject.ui.viewmodel.AuthState
import com.example.finalproject.ui.viewmodel.AuthViewModel
import com.example.finalproject.ui.navigation.AppRoutes

// --- Helpers validate ---
private fun isValidEmail(email: String): Boolean =
    android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()

private fun isValidUsername(username: String): Boolean =
    username.length >= 4 && !username.contains(" ")

private fun isStrongPassword(password: String): Boolean =
    password.length >= 8 && password.any { it.isDigit() || !it.isLetterOrDigit() }

@Composable
fun RegisterScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var agreedToTerms by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }

    // Inline validation errors
    var usernameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var confirmPasswordError by remember { mutableStateOf<String?>(null) }

    val state by viewModel.registerState.collectAsState()

    LaunchedEffect(state) {
        when (state) {
            is AuthState.OtpSent -> {
                AppNotificationManager.info("📧 Mã OTP đã gửi đến email của bạn!")
                navController.navigate(AppRoutes.createOtpVerificationRoute(email))
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
        // Top Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "MaSci",
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = PrimaryBlue
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Đã có tài khoản? ", fontSize = 12.sp, color = Color(0xFF64748B))
                Text(
                    text = "Đăng nhập",
                    fontSize = 12.sp,
                    color = PrimaryBlue,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { navController.navigate("login") }
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Badge
        Surface(
            color = Color(0xFF00FF88).copy(alpha = 0.2f),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(
                text = "CHÀO MỪNG BẠN MỚI",
                color = Color(0xFF047857),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(id = R.string.register_title),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E293B),
            textAlign = TextAlign.Center,
            lineHeight = 32.sp
        )
        Text(
            text = stringResource(id = R.string.register_subtitle),
            fontSize = 14.sp,
            color = Color(0xFF64748B),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 12.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Registration Card
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            shadowElevation = 2.dp
        ) {
            Column(modifier = Modifier.padding(20.dp)) {

                // --- Username ---
                CustomTextField(
                    value = username,
                    onValueChange = {
                        username = it
                        usernameError = null // clear error on type
                    },
                    label = "Tên đăng nhập",
                    showLabelOutside = true,
                    leadingIcon = { Icon(Icons.Default.Person, null, tint = PrimaryBlue) },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    isError = usernameError != null
                )
                FieldError(usernameError)

                // --- Email ---
                CustomTextField(
                    value = email,
                    onValueChange = {
                        email = it
                        emailError = null
                    },
                    label = "Email",
                    showLabelOutside = true,
                    leadingIcon = { Icon(Icons.Default.Email, null, tint = PrimaryBlue) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                    isError = emailError != null
                )
                FieldError(emailError)

                // --- Password ---
                CustomTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        passwordError = null
                    },
                    label = "Mật khẩu",
                    showLabelOutside = true,
                    leadingIcon = { Icon(Icons.Default.Lock, null, tint = PrimaryBlue) },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                null, tint = Color(0xFF94A3B8)
                            )
                        }
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next),
                    isError = passwordError != null
                )
                FieldError(passwordError)

                // --- Confirm Password ---
                CustomTextField(
                    value = confirmPassword,
                    onValueChange = {
                        confirmPassword = it
                        confirmPasswordError = null
                    },
                    label = "Xác nhận mật khẩu",
                    showLabelOutside = true,
                    leadingIcon = { Icon(Icons.Default.History, null, tint = PrimaryBlue) },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                    isError = confirmPasswordError != null
                )
                FieldError(confirmPasswordError)

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 12.dp)
                ) {
                    Checkbox(
                        checked = agreedToTerms,
                        onCheckedChange = { agreedToTerms = it }
                    )
                    Text(
                        text = stringResource(id = R.string.register_terms_accept),
                        fontSize = 11.sp,
                        color = Color(0xFF64748B),
                        lineHeight = 16.sp,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }

                PrimaryButton(
                    text = "Đăng ký",
                    onClick = {
                        // 1. Validate fields
                        var hasError = false

                        if (!isValidUsername(username)) {
                            usernameError = "Tên đăng nhập phải 4+ ký tự, không chứa khoảng trắng"
                            hasError = true
                        }
                        if (!isValidEmail(email)) {
                            emailError = "Email không đúng định dạng"
                            hasError = true
                        }
                        if (!isStrongPassword(password)) {
                            passwordError = "Mật khẩu 8+ ký tự, phải có số hoặc ký tự đặc biệt"
                            hasError = true
                        }
                        if (password != confirmPassword) {
                            confirmPasswordError = "Mật khẩu xác nhận không khớp"
                            hasError = true
                        }
                        if (!agreedToTerms) {
                            AppNotificationManager.error("Vui lòng đồng ý với điều khoản sử dụng")
                            hasError = true
                        }

                        if (!hasError) {
                            viewModel.requestRegisterOtp(username, email, password)
                        }
                    },
                    isLoading = state is AuthState.Loading
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Social Register
                Row(verticalAlignment = Alignment.CenterVertically) {
                    HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFE2E8F0))
                    Text(" HOẶC ĐĂNG KÝ BẰNG ", fontSize = 10.sp, color = Color(0xFF94A3B8), fontWeight = FontWeight.Bold)
                    HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFE2E8F0))
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    SocialButton(
                        text = "Google",
                        icon = R.drawable.ic_launcher_foreground,
                        onClick = {},
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    SocialButton(
                        text = "Facebook",
                        icon = R.drawable.ic_launcher_foreground,
                        onClick = {},
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Bạn gặp khó khăn khi đăng ký? Liên hệ hỗ trợ",
            color = PrimaryBlue,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.clickable { /* Handle Help */ }
        )

        Spacer(modifier = Modifier.height(20.dp))
    }
}

// Helper composable — hiển thị lỗi inline bên dưới field
@Composable
private fun FieldError(message: String?) {
    if (message != null) {
        Text(
            text = "⚠ $message",
            color = Color(0xFFFF4D4F),
            fontSize = 12.sp,
            modifier = Modifier.padding(start = 4.dp, bottom = 6.dp)
        )
    }
}