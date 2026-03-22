package com.example.finalproject.ui.screens.auth

import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.ui.res.stringResource
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

import com.example.finalproject.ui.viewmodel.AuthState
import com.example.finalproject.ui.viewmodel.AuthViewModel

@Composable
fun RegisterScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    val state by viewModel.registerState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(state) {
        when (state) {
            is AuthState.Success -> {
                Toast.makeText(context, context.getString(R.string.register_toast_success), Toast.LENGTH_LONG).show()
                viewModel.resetState()
                navController.navigate("login") { popUpTo("auth_selection") { inclusive = false } }
            }
            is AuthState.Error -> {
                Toast.makeText(context, (state as AuthState.Error).message, Toast.LENGTH_SHORT).show()
            }
            else -> {}
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = stringResource(id = R.string.register_title),
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = PrimaryBlue,
            modifier = Modifier.align(Alignment.Start)
        )
        Text(
            text = stringResource(id = R.string.register_subtitle),
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.align(Alignment.Start).padding(top = 8.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        CustomTextField(
            value = username,
            onValueChange = { username = it },
            label = stringResource(id = R.string.register_username_hint),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
        )

        CustomTextField(
            value = email,
            onValueChange = { email = it },
            label = stringResource(id = R.string.register_email_hint),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next)
        )

        CustomTextField(
            value = password,
            onValueChange = { password = it },
            label = stringResource(id = R.string.register_password_hint),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next)
        )

        CustomTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = stringResource(id = R.string.register_confirm_password_hint),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done)
        )

        Spacer(modifier = Modifier.height(24.dp))

        PrimaryButton(
            text = stringResource(id = R.string.register_btn_signup),
            onClick = {
                if (username.isBlank() || email.isBlank() || password.isBlank()) {
                    Toast.makeText(context, context.getString(R.string.register_toast_empty), Toast.LENGTH_SHORT).show()
                } else if (password != confirmPassword) {
                    Toast.makeText(context, context.getString(R.string.register_toast_pass_mismatch), Toast.LENGTH_SHORT).show()
                } else {
                    viewModel.register(username, email, password)
                }
            },
            isLoading = state is AuthState.Loading
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text = stringResource(id = R.string.register_already_have_account), color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(
                text = stringResource(id = R.string.register_btn_login),
                color = PrimaryBlue,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { navController.navigate("login") }
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
    }
}