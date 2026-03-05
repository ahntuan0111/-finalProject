package com.example.finalproject.ui.screens.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.finalproject.R
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
    val subjects by viewModel.subjects.collectAsState() // Quan sát danh sách môn học
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

        // 3. Dashboard Thống kê (UI MỚI)
        HomeDashboard(stats = viewModel.stats)

        Spacer(modifier = Modifier.height(32.dp))

        // 4. Môn học
        HomeSubjects(subjects = subjects) { subjectIdString ->
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
            // Dòng "Xin chào" với icon vẫy tay
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "👋 Xin chào, $userName!",
                    fontSize = 26.sp, // To hơn chút để nổi bật
                    fontWeight = FontWeight.ExtraBold, // Đậm hơn
                    color = Color(0xFF1C2A3A) // Màu xanh đen đậm giống thiết kế
                )
            }
            Text(
                text = "Hôm nay bạn muốn học gì?",
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        // Khu vực Avatar có viền nhẹ và chấm thông báo màu đỏ
        Box(
            modifier = Modifier.padding(top = 4.dp, end = 4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE8F0FE)) // Background nếu không có ảnh
                    .border(2.dp, Color(0xFFEFEFEF), CircleShape), // Viền nhẹ
                contentAlignment = Alignment.Center
            ) {
                // Tạm thời dùng icon hình người nếu không có ảnh thật
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Avatar",
                    tint = Color.Gray,
                    modifier = Modifier.size(32.dp)
                )
            }

            // Chấm đỏ thông báo góc trên cùng bên phải
            Box(
                modifier = Modifier
                    .size(14.dp)
                    .align(Alignment.TopEnd)
                    .offset(x = 2.dp, y = (-2).dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF04438)) // Màu đỏ nổi
                    .border(2.dp, Color(0xFFFAFAFA), CircleShape) // Viền trắng để tách biệt với avatar
            )
        }
    }
}

@Composable
fun HomeSearchBar(onSearchClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp)
            .clickable { onSearchClick() },
        shape = RoundedCornerShape(20.dp), // Bo góc mềm mại
        color = Color.White,
        shadowElevation = 0.5.dp, // Đổ bóng cực nhẹ
        border = BorderStroke(1.dp, Color(0xFFF0F0F0)) // Viền mờ
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = Color.Gray
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Tìm bài học, chương, từ khoá...",
                color = Color.LightGray,
                fontSize = 15.sp
            )
        }
    }
}

// ================= GIAO DIỆN DASHBOARD MỚI =================
@Composable
fun HomeDashboard(stats: List<DashboardStat>) {
    // Lấy an toàn các dữ liệu từ danh sách stats
    val progressStat = stats.getOrNull(0) ?: DashboardStat(0, "0%", "Tiến độ", Color.White)
    val completedStat = stats.getOrNull(1) ?: DashboardStat(0, "0", "Bài đã xong", Color.White)
    val streakStat = stats.getOrNull(2) ?: DashboardStat(0, "0", "Ngày", Color.White)

    // Xử lý chuỗi "75%" thành số thập phân 0.75f để vẽ biểu đồ
    val progressValue = progressStat.value.replace("%", "").toFloatOrNull()?.div(100f) ?: 0f

    // Thẻ (Card) bao bọc toàn bộ Dashboard
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp), // Chiều cao cố định để 2 nửa cân đối
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // --- NỬA TRÁI: BIỂU ĐỒ VÒNG ---
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                // Biểu đồ % vòng tròn
                CircularProgressIndicator(
                    progress = { progressValue },
                    modifier = Modifier.size(110.dp),
                    color = PrimaryBlue, // Màu xanh vệt chạy
                    trackColor = Color(0xFFF4F6FB), // Màu nền xám nhạt của vòng
                    strokeWidth = 12.dp,
                    strokeCap = StrokeCap.Round // Bo tròn 2 đầu vệt chạy
                )
                // Chữ 75% ở giữa
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = progressStat.value,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 24.sp,
                        color = Color(0xFF1C2A3A) // Xanh đen đậm
                    )
                    Text(
                        text = progressStat.label.uppercase(),
                        fontSize = 10.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // --- NỬA PHẢI: 2 THẺ THỐNG KÊ NHỎ ---
            Column(
                modifier = Modifier
                    .weight(1.1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Thẻ 1: Bài đã xong
                MiniStatCard(
                    modifier = Modifier.weight(1f), // <--- TRUYỀN WEIGHT Ở ĐÂY
                    iconRes = completedStat.imageRes,
                    value = completedStat.value,
                    label = completedStat.label,
                    bgColor = Color(0xFFF2FCF5),
                    iconBg = Color.White
                )

                // Thẻ 2: Streak
                MiniStatCard(
                    modifier = Modifier.weight(1f), // <--- TRUYỀN WEIGHT Ở ĐÂY
                    iconRes = streakStat.imageRes,
                    value = "${streakStat.value} Ngày",
                    label = "Streak",
                    bgColor = Color(0xFFFFF9F0),
                    iconBg = Color.White
                )
            }
        }
    }
}

@Composable
fun MiniStatCard(iconRes: Int, value: String, label: String, bgColor: Color, iconBg: Color,modifier: Modifier = Modifier) {
    Card(
        // 2. Gọi modifier từ tham số vào đây, và XÓA dòng .weight(1f) đi
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, bgColor.copy(alpha = 0.8f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Box bọc Icon
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(iconBg)
                    .border(1.dp, Color(0xFFEFEFEF), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = iconRes),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Text
            Column {
                Text(
                    text = value,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 15.sp,
                    color = Color(0xFF1C2A3A)
                )
                Text(
                    text = label,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
    }
}
// ================= KẾT THÚC GIAO DIỆN DASHBOARD MỚI =================

@Composable
fun HomeSubjects(subjects: List<SubjectItem>, onClick: (String) -> Unit) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 16.dp)) {
            // Giả lập icon cuốn sách nhỏ trước chữ Môn học của bạn
            Text("📚 ", fontSize = 20.sp)
            Text(
                text = "Môn học của bạn",
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF1C2A3A)
            )
        }

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
        shape = RoundedCornerShape(20.dp), // Bo góc giống UI
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp) // Đổ bóng nhẹ
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon môn học (Box màu nền)
            Box(
                modifier = Modifier
                    .size(64.dp) // To ra một chút
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

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = subject.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = Color(0xFF1C2A3A)
                )
                Spacer(modifier = Modifier.height(10.dp))

                LinearProgressIndicator(
                    progress = { subject.progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp) // Mỏng lại cho thanh thoát
                        .clip(RoundedCornerShape(3.dp)),
                    color = PrimaryBlue,
                    trackColor = Color(0xFFF0F0F0),
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Hiển thị số bài học
                Text(
                    text = if (subject.totalLessons == 0) "Chưa có dữ liệu"
                    else "${subject.finishedLessons}/${subject.totalLessons} bài học",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun HomeAiAssistant(onAiClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onAiClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF4F6FB)), // Màu xanh xám nhạt
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Background icon AI
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.SmartToy,
                        contentDescription = "AI",
                        tint = PrimaryBlue,
                        modifier = Modifier.size(26.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Trợ lý học tập AI",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 17.sp,
                        color = PrimaryBlue
                    )
                    Text(
                        text = "Hỏi bài - Giải thích - Gợi ý",
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Giả lập ô input có viền bo tròn và icon gửi
            Surface(
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(24.dp),
                color = Color.White
            ) {
                Row(
                    modifier = Modifier.fillMaxSize().padding(start = 16.dp, end = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Hỏi AI: Giải giúp mình bài này...",
                        color = Color.LightGray,
                        fontSize = 14.sp
                    )

                    // Nút gửi AI màu xanh
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(PrimaryBlue),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = "Send",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}