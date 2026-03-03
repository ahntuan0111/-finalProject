package com.example.finalproject.ui.screens.home

import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.finalproject.data.remote.model.DashboardStat
import com.example.finalproject.data.remote.model.SubjectItem
import com.example.finalproject.ui.navigation.AppRoutes
import com.example.finalproject.ui.theme.PrimaryBlue
import com.example.finalproject.ui.theme.TextGray
import com.example.finalproject.ui.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val userName by viewModel.userName.collectAsState()
    val subjects by viewModel.subjects.collectAsState() // Thêm dòng này để quan sát danh sách môn học
    val scrollState = rememberScrollState()

    // Gọi API để cập nhật tiến trình mỗi khi HomeScreen hiển thị
    LaunchedEffect(Unit) {
        viewModel.fetchProgress()
    }

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
        HomeSubjects(subjects = subjects) { subjectIdString -> // Sử dụng biến 'subjects' đã collectAsState
            val dbSubjectCode = when(subjectIdString) {
                "math" -> "toan"
                "science" -> "khtn"
                else -> subjectIdString // Fallback: Sử dụng trực tiếp nếu ID khớp (ví dụ "toan")
            }

            val subjectName = subjects.find { it.id == subjectIdString }?.name ?: "Môn học"

            navController.navigate(AppRoutes.createSubjectDetailRoute(dbSubjectCode, subjectName))
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
        shape = RoundedCornerShape(24.dp), // Bo góc mềm hơn (24.dp)
        colors = CardDefaults.cardColors(containerColor = stat.color),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Thay Icon bằng Image để hiển thị màu gốc
            Image(
                painter = painterResource(id = stat.imageRes),
                contentDescription = null,
                modifier = Modifier.size(32.dp)
            )
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
        shape = RoundedCornerShape(24.dp),
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
                    .size(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(subject.color),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = subject.imageRes),
                    contentDescription = null,
                    modifier = Modifier.size(36.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Thông tin text (Đã xóa bỏ cấu trúc lồng lặp)
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = subject.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(6.dp))

                LinearProgressIndicator(
                    progress = { subject.progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp) // Dày hơn chút
                        .clip(RoundedCornerShape(4.dp)),
                    color = PrimaryBlue,
                    trackColor = Color(0xFFF5F5F5),
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Hiển thị số bài học, xử lý trường hợp tổng bài học bằng 0
                Text(
                    text = if (subject.totalLessons == 0) "Chưa có dữ liệu"
                    else "${subject.finishedLessons}/${subject.totalLessons} bài học",
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