package com.example.finalproject.ui.screens.auth

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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

import com.example.finalproject.ui.theme.PrimaryBlue
import kotlinx.coroutines.launch

data class OnboardingPage(
    @StringRes val titleRes: Int,
    @StringRes val descRes: Int,
    @DrawableRes val imageRes: Int
)

val pages = listOf(
    OnboardingPage(R.string.welcome_page1_title, R.string.welcome_page1_desc, R.drawable.book),
    OnboardingPage(R.string.welcome_page2_title, R.string.welcome_page2_desc, R.drawable.trending_up),
    OnboardingPage(R.string.welcome_page3_title, R.string.welcome_page3_desc, R.drawable.school)
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WelcomeScreen(navController: NavController) {
    val pagerState = rememberPagerState(pageCount = { 3 })
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            TextButton(onClick = { navController.navigate("auth_selection") }) {
                Text(stringResource(id = R.string.welcome_skip), color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        HorizontalPager(state = pagerState) { page ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    painter = painterResource(id = pages[page].imageRes),
                    contentDescription = null,
                    modifier = Modifier
                        .size(250.dp)
                        .padding(16.dp),
                    contentScale = ContentScale.Fit
                )

                Spacer(modifier = Modifier.height(32.dp))
                Text(
                    text = stringResource(id = pages[page].titleRes),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryBlue
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(id = pages[page].descRes),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 16.sp
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier
                .height(50.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(pages.size) { iteration ->
                val color = if (pagerState.currentPage == iteration) PrimaryBlue else MaterialTheme.colorScheme.onSurfaceVariant
                Box(
                    modifier = Modifier
                        .padding(4.dp)
                        .clip(CircleShape)
                        .background(color)
                        .size(if (pagerState.currentPage == iteration) 12.dp else 8.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        PrimaryButton(
            text = if (pagerState.currentPage == 2) stringResource(id = R.string.welcome_btn_start) else stringResource(id = R.string.welcome_btn_next),
            onClick = {
                if (pagerState.currentPage < 2) {
                    scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                } else {
                    navController.navigate("auth_selection")
                }
            }
        )
    }
}