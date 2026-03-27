package com.example.finalproject.ui.screens.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.finalproject.R
import com.example.finalproject.ui.components.PrimaryButton
import com.example.finalproject.ui.components.SecondaryButton
import com.example.finalproject.ui.theme.PrimaryBlue

@Composable
fun AuthSelectionScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(
                colors = listOf(Color(0xFFE0F2FE), Color.White)
            ))
    ) {
        // App Logo/Name at the top
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "MaSci",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = PrimaryBlue
            )
            Text(
                text = "Học Tập Thú Vị",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E293B),
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Center Illustration
        Image(
            painter = painterResource(id = R.drawable.study_time), // Placeholder for Robot
            contentDescription = "Welcome Illustration",
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
                .padding(horizontal = 24.dp),
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.weight(1f))

        // Bottom Action Card
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
            color = Color.White,
            shadowElevation = 16.dp
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(id = R.string.welcome_brand_desc),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B),
                    textAlign = TextAlign.Center,
                    lineHeight = 30.sp
                )

                Text(
                    text = stringResource(id = R.string.welcome_brand_sub),
                    fontSize = 14.sp,
                    color = Color(0xFF64748B),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 12.dp, bottom = 32.dp)
                )

                PrimaryButton(
                    text = stringResource(id = R.string.welcome_btn_login_short),
                    onClick = { navController.navigate("login") }
                )

                Spacer(modifier = Modifier.height(16.dp))

                SecondaryButton(
                    text = stringResource(id = R.string.welcome_btn_register_new),
                    onClick = { navController.navigate("register") }
                )

                Text(
                    text = stringResource(id = R.string.welcome_help_link),
                    color = Color(0xFF64748B),
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 24.dp)
                )
            }
        }
    }
}