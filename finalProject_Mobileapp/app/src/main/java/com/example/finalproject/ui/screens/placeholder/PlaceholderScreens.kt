package com.example.finalproject.ui.screens.placeholder

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.finalproject.R
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun DashboardScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(stringResource(id = R.string.placeholder_dashboard))
    }
}

@Composable
fun AiChatScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(stringResource(id = R.string.placeholder_ai_chat))
    }
}

@Composable
fun UserProfileScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(stringResource(id = R.string.placeholder_user))
    }
}