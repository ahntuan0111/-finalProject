package com.example.finalproject.ui.screens.auth

import android.widget.Toast
import androidx.compose.foundation.Image
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
import androidx.compose.ui.res.painterResource
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
fun LoginScreen(navController: NavController, viewModel: AuthViewModel = hiltViewModel()) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val state by viewModel.loginState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(state) {
        when (state) {
            is AuthState.LoginSuccess -> {
                val hasGrade = (state as AuthState.LoginSuccess).hasGrade
                Toast.makeText(context, context.getString(R.string.login_toast_success), Toast.LENGTH_SHORT).show()

                if (hasGrade) {
                    navController.navigate("home") { popUpTo("welcome") { inclusive = true } }
                } else {
                    navController.navigate("class_selection") { popUpTo("welcome") { inclusive = true } }
                }
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
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = stringResource(id = R.string.login_title),
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = PrimaryBlue,
            modifier = Modifier.align(Alignment.Start)
        )
        Text(
            text = stringResource(id = R.string.login_subtitle),
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.align(Alignment.Start).padding(top = 8.dp)
        )

        Spacer(modifier = Modifier.height(40.dp))

        CustomTextField(
            value = username,
            onValueChange = { username = it },
            label = stringResource(id = R.string.login_username_hint),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
        )

        CustomTextField(
            value = password,
            onValueChange = { password = it },
            label = stringResource(id = R.string.login_password_hint),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done)
        )

        Text(
            text = stringResource(id = R.string.login_forgot_password),
            color = PrimaryBlue,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.End)
                .padding(top = 8.dp, bottom = 24.dp)
                .clickable { navController.navigate("forgot_password") }
        )

        PrimaryButton(
            text = stringResource(id = R.string.login_title),
            onClick = { viewModel.login(username, password) },
            isLoading = state is AuthState.Loading
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text = stringResource(id = R.string.login_no_account), color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(
                text = stringResource(id = R.string.login_btn_signup),
                color = PrimaryBlue,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { navController.navigate("register") }
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Image(
            painter = painterResource(id = R.drawable.study_time),
            contentDescription = "Login Illustration",
            modifier = Modifier.fillMaxWidth().height(200.dp)
        )
    }
}