package com.example.finalproject.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SmartToy // Robot Icon
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.finalproject.ui.theme.PrimaryBlue
import com.example.finalproject.ui.theme.TextGray

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val userName by viewModel.userName.collectAsState()
    val scrollState = rememberScrollState()

    // Cấu trúc chính: Scroll dọc
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFAFAFA)) // Nền xám rất nhạt
            .verticalScroll(scrollState)
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        // 1. Khu vực chào mừng
        HomeHeader(userName = userName)

        Spacer(modifier = Modifier.height(24.dp))

        // 2. Thanh tìm kiếm
        HomeSearchBar(onSearchClick = {
            // TODO: Navigate to Search Screen
        })

        Spacer(modifier = Modifier.height(24.dp))

        // 3. Dashboard Thống kê
        HomeDashboard(stats = viewModel.stats)

        Spacer(modifier = Modifier.height(32.dp))

        // 4. Môn học
        HomeSubjects(subjects = viewModel.subjects) { subjectId ->
            // TODO: Navigate to Lesson List
        }

        Spacer(modifier = Modifier.height(32.dp))

        // 5. Trợ lý AI
        HomeAiAssistant(onAiClick = {
            // TODO: Navigate to Chat Screen
        })

        Spacer(modifier = Modifier.height(80.dp)) // Bottom padding cho thoáng
    }
}

// --- CÁC COMPONENT CON ---

@Composable
fun HomeHeader(userName: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "👋 Xin chào, $userName!",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Text(
                text = "Hôm nay bạn muốn học gì?",
                fontSize = 14.sp,
                color = TextGray,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
        // Avatar giả lập (Hình tròn xám)
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color.LightGray)
        )
    }
}

@Composable
fun HomeSearchBar(onSearchClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .clickable { onSearchClick() }, // Bấm vào để chuyển trang
        shape = RoundedCornerShape(50), // Bo tròn viên thuốc
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = TextGray
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Tìm bài học, chương, từ khoá...",
                color = TextGray,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun HomeDashboard(stats: List<DashboardStat>) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(stats) { stat ->
            StatCard(stat)
        }
    }
}

@Composable
fun StatCard(stat: DashboardStat) {
    Card(
        modifier = Modifier.size(width = 100.dp, height = 110.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = stat.color),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(imageVector = stat.icon, contentDescription = null, tint = Color.DarkGray)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stat.value,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color.Black
            )
            Text(
                text = stat.label,
                fontSize = 12.sp,
                color = TextGray
            )
        }
    }
}

@Composable
fun HomeSubjects(subjects: List<SubjectItem>, onClick: (String) -> Unit) {
    Column {
        Text(
            text = "📚 Môn học của bạn",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        subjects.forEach { subject ->
            SubjectCard(subject = subject, onClick = { onClick(subject.id) })
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun SubjectCard(subject: SubjectItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon môn học (Box màu nền)
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(subject.color),
                contentAlignment = Alignment.Center
            ) {
                // Tạm thời dùng chữ cái đầu, sau này thay bằng Icon/Image
                Text(
                    text = subject.name.first().toString(),
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = PrimaryBlue
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Thông tin text
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = subject.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(4.dp))

                // Progress Bar
                LinearProgressIndicator(
                    progress = { subject.progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = PrimaryBlue,
                    trackColor = Color(0xFFEEEEEE),
                )

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${subject.finishedLessons}/${subject.totalLessons} bài học",
                    fontSize = 12.sp,
                    color = TextGray
                )
            }
        }
    }
}

@Composable
fun HomeAiAssistant(onAiClick: () -> Unit) {
    // Card AI nổi bật hơn một chút với Gradient hoặc màu riêng
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onAiClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8EAF6)), // Màu xanh tím nhạt
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.SmartToy,
                    contentDescription = "AI",
                    tint = PrimaryBlue,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Trợ lý học tập AI",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = PrimaryBlue
                    )
                    Text(
                        text = "Hỏi bài - Giải thích - Gợi ý",
                        fontSize = 12.sp,
                        color = TextGray
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Giả lập ô input
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = Color.White
            ) {
                Text(
                    text = "Hỏi AI: Giải giúp mình bài này...",
                    color = TextGray,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }
    }
}