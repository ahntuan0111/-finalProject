package com.example.finalproject.ui.screens.auth

import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
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
import com.example.finalproject.ui.components.CustomTextField
import com.example.finalproject.ui.components.PrimaryButton
import com.example.finalproject.ui.components.SocialButton
import com.example.finalproject.ui.theme.PrimaryBlue
import com.example.finalproject.ui.viewmodel.AuthState
import com.example.finalproject.ui.viewmodel.AuthViewModel
import com.example.finalproject.ui.navigation.AppRoutes
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

    val state by viewModel.registerState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(state) {
        when (state) {
            is AuthState.OtpSent -> {
                navController.navigate(AppRoutes.createOtpVerificationRoute(email))
            }
            is AuthState.Error -> {
                Toast.makeText(context, (state as AuthState.Error).message, Toast.LENGTH_SHORT).show()
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
                CustomTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = "Tên đăng nhập",
                    showLabelOutside = true,
                    leadingIcon = { Icon(Icons.Default.Person, null, tint = PrimaryBlue) },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                )

                CustomTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = "Email",
                    showLabelOutside = true,
                    leadingIcon = { Icon(Icons.Default.Email, null, tint = PrimaryBlue) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next)
                )

                CustomTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = "Mật khẩu",
                    showLabelOutside = true,
                    leadingIcon = { Icon(Icons.Default.Lock, null, tint = PrimaryBlue) },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff, null, tint = Color(0xFF94A3B8))
                        }
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next)
                )

                CustomTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = "Xác nhận mật khẩu",
                    showLabelOutside = true,
                    leadingIcon = { Icon(Icons.Default.History, null, tint = PrimaryBlue) },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done)
                )

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
                        if (username.isBlank() || email.isBlank() || password.isBlank()) {
                            Toast.makeText(context, context.getString(R.string.register_toast_empty), Toast.LENGTH_SHORT).show()
                        } else if (password != confirmPassword) {
                            Toast.makeText(context, context.getString(R.string.register_toast_pass_mismatch), Toast.LENGTH_SHORT).show()
                        } else if (!agreedToTerms) {
                            Toast.makeText(context, "Vui lòng đồng ý với điều khoản", Toast.LENGTH_SHORT).show()
                        } else {
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