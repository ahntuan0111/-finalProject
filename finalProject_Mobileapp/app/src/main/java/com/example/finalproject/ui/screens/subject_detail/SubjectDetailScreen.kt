package com.example.finalproject.ui.screens.subject_detail

import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.finalproject.di.TheoryMode
import com.example.finalproject.ui.navigation.AppRoutes
import com.example.finalproject.ui.theme.PrimaryBlue
import com.example.finalproject.ui.viewmodel.SubjectDetailViewModel

// --- MÀU SẮC ĐỊNH NGHĨA DỰA THEO MOCKUP ---
val BgOffWhite = Color(0xFFFAFBFF)
val BannerStart = Color(0xFF4A85F6)
val BannerEnd = Color(0xFF2B58C4)

// Màu thẻ thực hành
val GreenTint = Color(0xFFF2FCF5)
val GreenIcon = Color(0xFF4CAF50)
val OrangeTint = Color(0xFFFFF9F0)
val OrangeIcon = Color(0xFFFF9800)
val PurpleTint = Color(0xFFFDF7FF)
val PurpleIcon = Color(0xFF9C27B0)

@Composable
fun SubjectDetailScreen(
    navController: NavController,
    subjectCode: String,
    subjectName: String,
    viewModel: SubjectDetailViewModel = hiltViewModel() // ĐÃ SỬA: Dùng SubjectDetailViewModel
) {
    val currentGrade by viewModel.currentGrade.collectAsState()
    val overview by viewModel.overview.collectAsState() // Lấy data overview từ API
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    // Gọi API mỗi khi currentGrade hoặc subjectCode thay đổi
    LaunchedEffect(currentGrade, subjectCode) {
        viewModel.fetchOverview(subjectCode, currentGrade)
    }

    // Lấy data an toàn (mặc định 0% và id = -1 nếu đang tải hoặc chưa có)
    val progressValue = overview?.progressPercent?.div(100f)?.toFloat() ?: 0f
    val nextLessonId = overview?.nextLessonId ?: -1L

    Scaffold(
        containerColor = BgOffWhite,
        topBar = {
            CustomTopBar(
                title = "Khối $currentGrade - $subjectName",
                onBack = { navController.popBackStack() }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            // 1. HERO BANNER
            HeroBanner(subjectName = subjectName, grade = currentGrade)

            Spacer(modifier = Modifier.height(28.dp))

            // 2. HEADER LỘ TRÌNH & XEM TẤT CẢ
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Lộ trình học tập",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF1C2A3A)
                )
                Text(
                    text = "Xem tất cả",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryBlue,
                    modifier = Modifier.clickable {
                        // BẤM VÀO ĐÂY ĐỂ XEM DANH SÁCH CHƯƠNG/BÀI HỌC
                        navController.navigate(
                            AppRoutes.createTheoryRoute(
                                code = subjectCode,
                                name = subjectName,
                                mode = TheoryMode.THEORY
                            )
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 3. THẺ LÝ THUYẾT (MAIN CARD - FULL WIDTH)
            PrimaryTheoryCard(
                progress = progressValue, // ĐÃ SỬA: Lấy progress từ API truyền vào
                onContinueClick = {
                    if (nextLessonId != -1L) {
                        // ĐIỀU HƯỚNG VÀO BÀI HỌC TIẾP THEO CHƯA HỌC
                        navController.navigate(AppRoutes.createLessonDetailRoute(nextLessonId))
                    } else {
                        Toast.makeText(context, "Môn học này chưa có nội dung hoặc đã học xong!", Toast.LENGTH_SHORT).show()
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 4. GRID 2 CỘT: GIẢI BÀI TẬP & QUIZ
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                BounceCard(
                    modifier = Modifier.weight(1f).aspectRatio(1f),
                    bgColor = GreenTint,
                    icon = Icons.Rounded.CheckCircle,
                    iconColor = GreenIcon,
                    title = "Giải bài tập",
                    subtitle = "Lời giải chi tiết SGK & SBT",
                    onClick = {
                        navController.navigate(
                            AppRoutes.createTheoryRoute(subjectCode, subjectName, TheoryMode.EXERCISE)
                        )
                    }
                )

                BounceCard(
                    modifier = Modifier.weight(1f).aspectRatio(1f),
                    bgColor = OrangeTint,
                    icon = Icons.Rounded.Bolt,
                    iconColor = OrangeIcon,
                    title = "Quiz",
                    subtitle = "Kiểm tra nhanh 15p hàng ngày",
                    onClick = {
                        navController.navigate(AppRoutes.createQuizFlowRoute(subjectCode, currentGrade))
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 5. THẺ BỘ ĐỀ THI (TERTIARY CARD - FULL WIDTH)
            BounceCard(
                modifier = Modifier.fillMaxWidth().height(90.dp),
                bgColor = PurpleTint,
                icon = Icons.Rounded.Assignment,
                iconColor = PurpleIcon,
                title = "Bộ đề thi",
                subtitle = "Sẵn sàng thi học kỳ • Đề thi thử mới nhất",
                isHorizontal = true, // Layout xoay ngang
                onClick = {
                    navController.navigate(AppRoutes.createDocumentListRoute(subjectCode, currentGrade))
                }
            )

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

// ======================= CÁC COMPONENT GIAO DIỆN =======================

@Composable
fun CustomTopBar(title: String, onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .statusBarsPadding(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Nút Back tròn
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.White)
                .border(1.dp, Color(0xFFF0F0F0), CircleShape)
                .clickable { onBack() },
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Rounded.ArrowBackIosNew, contentDescription = "Back", tint = PrimaryBlue, modifier = Modifier.size(18.dp))
        }

        Text(text = title, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1C2A3A))

        // Nút Dark/Light mode (Giả lập UI)
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.White)
                .border(1.dp, Color(0xFFF0F0F0), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Rounded.DarkMode, contentDescription = "Theme", tint = PrimaryBlue, modifier = Modifier.size(20.dp))
        }
    }
}

@Composable
fun HeroBanner(subjectName: String, grade: Int) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(Brush.linearGradient(listOf(BannerStart, BannerEnd)))
            .padding(24.dp)
    ) {
        // Họa tiết mờ (Math Symbols)
        Text("∑", color = Color.White.copy(alpha = 0.1f), fontSize = 60.sp, modifier = Modifier.align(Alignment.TopEnd).offset(x = (-40).dp, y = (-10).dp))
        Text("π", color = Color.White.copy(alpha = 0.1f), fontSize = 70.sp, modifier = Modifier.align(Alignment.CenterEnd).offset(x = 10.dp, y = (-20).dp))
        Text("√", color = Color.White.copy(alpha = 0.05f), fontSize = 80.sp, modifier = Modifier.align(Alignment.BottomEnd).offset(x = (-80).dp, y = 10.dp))

        Column {
            // Tag
            Box(
                modifier = Modifier
                    .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(50))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text("CHƯƠNG TRÌNH MỚI 2024", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "$subjectName\ncho Khối $grade",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                lineHeight = 34.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Sẵn sàng chinh phục các con số chưa?\nHãy bắt đầu ngay hôm nay nhé!",
                fontSize = 13.sp,
                color = Color.White.copy(alpha = 0.8f),
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Avatar những người đang học
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(28.dp).clip(CircleShape).background(Color.Black).border(2.dp, BannerEnd, CircleShape))
                Box(modifier = Modifier.size(28.dp).offset(x = (-10).dp).clip(CircleShape).background(Color.White).border(2.dp, BannerEnd, CircleShape))
                Box(
                    modifier = Modifier.size(28.dp).offset(x = (-20).dp).clip(CircleShape).background(PrimaryBlue).border(2.dp, BannerEnd, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("+12", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                }
                Text("Đang học cùng bạn", color = Color.White.copy(alpha = 0.9f), fontSize = 12.sp, modifier = Modifier.offset(x = (-10).dp))
            }
        }
    }
}

@Composable
fun PrimaryTheoryCard(progress: Float, onContinueClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, Color(0xFFF0F0F0))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Icon Box
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFFE8F0FE)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Rounded.MenuBook, contentDescription = null, tint = PrimaryBlue)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text("Lý thuyết trọng tâm", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1C2A3A))
                        Text("12 chương • 48 bài học", fontSize = 12.sp, color = Color.Gray)
                    }
                }

                // Nút Tiếp Tục (Vào thẳng bài học tiếp theo)
                Box(
                    modifier = Modifier
                        .background(Color(0xFFEEF2FF), RoundedCornerShape(50))
                        .clickable { onContinueClick() }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text("Tiếp tục", color = PrimaryBlue, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Progress Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Tiến độ hoàn thành", fontSize = 12.sp, color = Color.Gray)
                Text("${(progress * 100).toInt()}%", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
            }
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                color = PrimaryBlue,
                trackColor = Color(0xFFF0F0F0)
            )
        }
    }
}

// Thẻ thực hành với hiệu ứng Bounce & Soft Tint Color
@Composable
fun BounceCard(
    modifier: Modifier = Modifier,
    bgColor: Color,
    icon: ImageVector,
    iconColor: Color,
    title: String,
    subtitle: String,
    isHorizontal: Boolean = false,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(targetValue = if (isPressed) 0.95f else 1f, label = "scale")

    Card(
        modifier = modifier
            .scale(scale) // Hiệu ứng lún xuống khi bấm
            .clickable(interactionSource = interactionSource, indication = LocalIndication.current, onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, bgColor.copy(alpha = 0.5f)) // Viền mờ tệp màu nền
    ) {
        if (isHorizontal) {
            // Bố cục ngang cho thẻ "Bộ đề thi"
            Row(
                modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.size(48.dp).clip(CircleShape).background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = iconColor)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1C2A3A))
                    Text(subtitle, fontSize = 12.sp, color = Color.Gray)
                }
                Icon(Icons.Rounded.ChevronRight, contentDescription = null, tint = Color.LightGray)
            }
        } else {
            // Bố cục dọc cho thẻ vuông (Grid)
            Column(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier.size(44.dp).clip(CircleShape).background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = iconColor)
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1C2A3A))
                Spacer(modifier = Modifier.height(4.dp))
                Text(subtitle, fontSize = 11.sp, color = Color.Gray, lineHeight = 16.sp)
            }
        }
    }
}