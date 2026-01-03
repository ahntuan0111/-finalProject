package com.example.finalproject.ui.screens.auth

import androidx.annotation.DrawableRes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image // Import Image thay vì Icon
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
import androidx.compose.ui.res.painterResource // Quan trọng: Để load ảnh từ drawable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.finalproject.R // Import R để truy cập tài nguyên
import com.example.finalproject.ui.components.PrimaryButton
import com.example.finalproject.ui.theme.InactiveGray
import com.example.finalproject.ui.theme.PrimaryBlue
import kotlinx.coroutines.launch

// 1. Sửa Data Class: Đổi từ ImageVector -> Int (Resource ID)
data class OnboardingPage(
    val title: String,
    val desc: String,
    @DrawableRes val imageRes: Int // @DrawableRes giúp gợi ý chỉ điền ID ảnh
)

// 2. Cập nhật danh sách Pages với hình ảnh trong res/drawable
// Giả sử bạn đã copy 3 ảnh tên là: ic_study, ic_chart, ic_community vào drawable
val pages = listOf(
    OnboardingPage(
        "Học tập mọi lúc",
        "Truy cập bài giảng và làm bài tập bất cứ đâu, bất cứ khi nào.",
        R.drawable.book // Thay tên file ảnh của bạn vào đây
    ),
    OnboardingPage(
        "Theo dõi tiến độ",
        "Biểu đồ trực quan giúp bạn nắm bắt kết quả học tập từng ngày.",
        R.drawable.trending_up // Thay tên file ảnh của bạn vào đây
    ),
    OnboardingPage(
        "Trao đổi dễ dàng",
        "Kết nối với giáo viên và bạn bè để giải đáp thắc mắc.",
        R.drawable.school // Thay tên file ảnh của bạn vào đây
    )
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
        // Nút Bỏ qua
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            TextButton(onClick = { navController.navigate("auth_selection") }) {
                Text("Bỏ qua", color = InactiveGray)
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Pager Content
        HorizontalPager(state = pagerState) { page ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {

                // 3. Thay thế Icon bằng Image
                Image(
                    painter = painterResource(id = pages[page].imageRes), // Load ảnh từ ID
                    contentDescription = null,
                    modifier = Modifier
                        .size(250.dp) // Tăng kích thước lên chút vì ảnh minh họa thường to
                        .padding(16.dp),
                    contentScale = ContentScale.Fit // Giữ tỷ lệ ảnh không bị méo
                )

                Spacer(modifier = Modifier.height(32.dp))
                Text(
                    text = pages[page].title,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryBlue
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = pages[page].desc,
                    textAlign = TextAlign.Center,
                    color = InactiveGray,
                    fontSize = 16.sp
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Indicators
        Row(
            modifier = Modifier
                .height(50.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(pages.size) { iteration ->
                val color = if (pagerState.currentPage == iteration) PrimaryBlue else InactiveGray
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

        // Button
        PrimaryButton(
            text = if (pagerState.currentPage == 2) "Bắt đầu ngay" else "Tiếp tục",
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