package com.example.finalproject.ui.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.rounded.HourglassEmpty
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.RocketLaunch
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.finalproject.data.remote.model.AiPredictionDto
import com.example.finalproject.data.remote.model.ChartPointDto
import com.example.finalproject.data.remote.model.DashboardResponse
import com.example.finalproject.data.remote.model.HeatmapPointDto
import com.example.finalproject.data.remote.model.SubjectProgressDto
import com.example.finalproject.ui.viewmodel.DashboardViewModel
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.compose.chart.line.lineSpec
import com.patrykandpatrick.vico.compose.component.shape.shader.fromBrush // Import quan trọng để sửa lỗi fromBrush
import com.patrykandpatrick.vico.core.chart.values.AxisValuesOverrider
import com.patrykandpatrick.vico.core.component.shape.shader.DynamicShaders
import com.patrykandpatrick.vico.core.entry.FloatEntry
import com.patrykandpatrick.vico.core.entry.entryModelOf
import androidx.compose.material.icons.rounded.ChevronLeft
import androidx.compose.material.icons.rounded.ChevronRight
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

// --- MÀU SẮC CHỦ ĐẠO ---
val PrimaryBlue = Color(0xFF2B58C4)
val CardBg = Color.White
val BgOffWhite = Color(0xFFFAFBFF)
val OrangeFlame = Color(0xFFFF6D00)
val OrangeLight = Color(0xFFFFF3E0)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    navController: NavController,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val dashboardData by viewModel.dashboardData.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val scrollState = rememberScrollState()

    // Quản lý trạng thái Bottom Sheet (Lịch Streak 30 ngày)
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    var showStreakSheet by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = BgOffWhite,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .statusBarsPadding(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    modifier = Modifier.clickable { navController.popBackStack() }
                )
                Text("DashBoard", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1C2A3A))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Notifications, contentDescription = "Bell", tint = Color.Gray, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Box(modifier = Modifier.size(36.dp).clip(CircleShape).background(Color.LightGray))
                }
            }
        }
    ) { innerPadding ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PrimaryBlue)
            }
        } else {
            dashboardData?.let { data ->
                Box(modifier = Modifier.fillMaxSize()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .verticalScroll(scrollState)
                            .padding(horizontal = 20.dp, vertical = 8.dp)
                    ) {
                        // 1. TOP CARDS (TỔNG THỜI GIAN & STREAK)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            TopStatCard(
                                modifier = Modifier.weight(1f),
                                icon = Icons.Rounded.HourglassEmpty,
                                iconTint = PrimaryBlue,
                                title = "TỔNG THỜI GIAN",
                                value = (data.topStats.totalMinutes / 60).toString(),
                                unit = "Giờ",
                                badgeText = "Học tập chăm chỉ",
                                badgeColor = Color(0xFFE8F5E9),
                                badgeTextColor = Color(0xFF4CAF50),
                                onClick = {}
                            )
                            TopStatCard(
                                modifier = Modifier.weight(1f),
                                icon = Icons.Rounded.LocalFireDepartment,
                                iconTint = OrangeFlame,
                                title = "STREAK HIỆN TẠI",
                                value = data.topStats.currentStreak.toString(),
                                unit = "Ngày",
                                badgeText = "Chạm để xem lịch",
                                badgeColor = OrangeLight,
                                badgeTextColor = OrangeFlame,
                                onClick = { showStreakSheet = true } // Mở Bottom Sheet
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // 2. ĐƯỜNG ĐUA TIẾN ĐỘ
                        ProgressTrackCard(data.progressTrack)

                        Spacer(modifier = Modifier.height(24.dp))

                        // 3. BIỂU ĐỒ HIỆU SUẤT (VICO CHART)
                        PerformanceChartCard(data.performanceChart)

                        Spacer(modifier = Modifier.height(24.dp))

                        // 4. DỰ ĐOÁN ĐIỂM AI
                        AiPredictionCard(data.aiPrediction)

                        Spacer(modifier = Modifier.height(24.dp))

                        // 5. HEATMAP - MẬT ĐỘ HỌC TẬP
                        HeatmapCard(data.heatmap)

                        Spacer(modifier = Modifier.height(40.dp))
                    }

                    // ================= BOTTOM SHEET LỊCH STREAK 30 NGÀY =================
                    if (showStreakSheet) {
                        ModalBottomSheet(
                            onDismissRequest = { showStreakSheet = false },
                            sheetState = sheetState,
                            containerColor = Color.White,
                            dragHandle = { BottomSheetDefaults.DragHandle() },
                            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
                        ) {
                            StreakCalendarContent(
                                data = data,
                                onClose = { showStreakSheet = false }
                            )
                        }
                    }
                }
            }
        }
    }
}

// ================= GIAO DIỆN BÊN TRONG BOTTOM SHEET LỊCH =================
@Composable
fun StreakCalendarContent(data: DashboardResponse, onClose: () -> Unit) {
    // Biến trạng thái để lùi tháng: 0 là tháng hiện tại, -1 là tháng trước, -2 là 2 tháng trước
    var monthOffset by remember { mutableIntStateOf(0) }

    val today = LocalDate.now()
    val currentViewMonth = YearMonth.now().plusMonths(monthOffset.toLong())
    val daysInMonth = currentViewMonth.lengthOfMonth()
    // Lấy ngày đầu tiên của tháng để biết nó rơi vào thứ mấy (1 = T2, 7 = CN)
    val firstDayOfWeek = currentViewMonth.atDay(1).dayOfWeek.value
    val emptyCellsBefore = firstDayOfWeek - 1 // Số ô trống trước mùng 1

    // Chuyển đổi dữ liệu Heatmap từ BE thành Set<LocalDate> để check O(1)
    val studiedDates = remember(data.heatmap) {
        data.heatmap
            .filter { it.intensity > 0 } // Chỉ lấy những ngày có học
            .mapNotNull {
                try {
                    LocalDate.parse(it.date, DateTimeFormatter.ISO_DATE)
                } catch (e: Exception) {
                    null
                }
            }
            .toSet()
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(bottom = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Hành trình giữ lửa", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF1C2A3A))
        Spacer(modifier = Modifier.height(24.dp))

        // 2 Thẻ Chỉ số phía trên
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            StreakInfoBox(
                modifier = Modifier.weight(1f),
                title = "STREAK HIỆN TẠI",
                value = "${data.topStats.currentStreak}",
                bgColor = OrangeLight,
                valueColor = OrangeFlame
            )
            val longest = maxOf(data.topStats.currentStreak, 25) // Mock longest
            StreakInfoBox(
                modifier = Modifier.weight(1f),
                title = "STREAK DÀI NHẤT",
                value = "$longest",
                bgColor = Color(0xFFF0F5FF),
                valueColor = PrimaryBlue
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Thanh điều hướng Tháng
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Nút lùi tháng (Chỉ cho phép lùi tối đa 2 tháng)
            IconButton(
                onClick = { monthOffset-- },
                enabled = monthOffset > -2
            ) {
                Icon(
                    Icons.Rounded.ChevronLeft,
                    contentDescription = "Tháng trước",
                    tint = if (monthOffset > -2) Color.Black else Color.LightGray
                )
            }

            Text(
                text = "Tháng ${currentViewMonth.monthValue} năm ${currentViewMonth.year}",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1C2A3A)
            )

            // Nút tiến tháng (Không cho phép vượt quá tháng hiện tại)
            IconButton(
                onClick = { monthOffset++ },
                enabled = monthOffset < 0
            ) {
                Icon(
                    Icons.Rounded.ChevronRight,
                    contentDescription = "Tháng sau",
                    tint = if (monthOffset < 0) Color.Black else Color.LightGray
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Header T2 -> CN
        Row(modifier = Modifier.fillMaxWidth()) {
            val daysOfWeek = listOf("T2", "T3", "T4", "T5", "T6", "T7", "CN")
            daysOfWeek.forEach { day ->
                Text(
                    text = day,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Lưới Lịch 7 cột
        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.heightIn(max = 280.dp) // Auto chiều cao
        ) {
            // Ô trống đầu tháng
            items(emptyCellsBefore) {
                Box(modifier = Modifier.aspectRatio(1f))
            }

            // Các ngày trong tháng
            items(daysInMonth) { dayIndex ->
                val dayNumber = dayIndex + 1
                val cellDate = currentViewMonth.atDay(dayNumber)

                val isStudied = studiedDates.contains(cellDate)
                val isToday = cellDate == today
                val isFuture = cellDate.isAfter(today)

                CalendarDayCell(
                    dayNumber = dayNumber,
                    isStudied = isStudied,
                    isToday = isToday,
                    isFuture = isFuture
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Card động viên
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(Color.White), contentAlignment = Alignment.Center) {
                    Icon(Icons.Rounded.RocketLaunch, contentDescription = null, tint = PrimaryBlue)
                }
                Spacer(modifier = Modifier.width(12.dp))

                val daysLeft = if (data.topStats.currentStreak < 30) 30 - data.topStats.currentStreak else 0
                Column {
                    Row {
                        Text("Cố lên! Bạn chỉ còn ", fontSize = 13.sp, color = Color(0xFF1C2A3A))
                        Text("$daysLeft ngày", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                    }
                    Text("nữa để đạt mục tiêu tháng!", fontSize = 13.sp, color = Color(0xFF1C2A3A))
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Nút bấm
        Button(
            onClick = { onClose() },
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("Tiếp tục học ngay", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun CalendarDayCell(dayNumber: Int, isStudied: Boolean, isToday: Boolean, isFuture: Boolean) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(CircleShape)
            // Màu nền: Nếu đã học -> Cam. Nếu chưa học (quá khứ) -> Xám. Tương lai -> Xám siêu nhạt.
            .background(
                when {
                    isStudied -> OrangeFlame
                    isFuture -> Color.Transparent
                    else -> Color(0xFFE2E8F0) // Đứt chuỗi
                }
            )
            // Viền chỉ dành cho ngày hôm nay
            .border(
                width = if (isToday) 2.dp else 0.dp,
                color = if (isToday && !isStudied) PrimaryBlue else Color.Transparent,
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        if (isStudied) {
            Icon(Icons.Rounded.LocalFireDepartment, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
        } else {
            Text(
                text = dayNumber.toString(),
                fontSize = 14.sp,
                fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                color = if (isFuture) Color.LightGray else Color(0xFF64748B)
            )
        }
    }
}

@Composable
fun StreakInfoBox(modifier: Modifier, title: String, value: String, bgColor: Color, valueColor: Color) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(bgColor)
            .border(1.dp, valueColor.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
            .padding(vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(title, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = valueColor)
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(value, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = valueColor)
                Text(" ngày", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = valueColor, modifier = Modifier.padding(bottom = 3.dp, start = 2.dp))
            }
        }
    }
}
// ================= CÁC COMPONENT GIAO DIỆN CHÍNH =================

@Composable
fun TopStatCard(modifier: Modifier, icon: androidx.compose.ui.graphics.vector.ImageVector, iconTint: Color, title: String, value: String, unit: String, badgeText: String, badgeColor: Color, badgeTextColor: Color, onClick: () -> Unit) {
    Card(
        modifier = modifier.height(140.dp).clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(iconTint.copy(alpha = 0.1f)), contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(title, fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
            Row(verticalAlignment = Alignment.Bottom) {
                Text(value, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF1C2A3A))
                Text(" $unit", fontSize = 14.sp, color = Color.Gray, modifier = Modifier.padding(bottom = 4.dp))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Box(modifier = Modifier.background(badgeColor, RoundedCornerShape(50)).padding(horizontal = 8.dp, vertical = 4.dp)) {
                Text(badgeText, fontSize = 10.sp, color = badgeTextColor, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun ProgressTrackCard(progressTrack: List<SubjectProgressDto>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Đường Đua Tiến Độ", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF1C2A3A))
                Icon(Icons.Default.Notifications, contentDescription = "Flag", tint = Color.LightGray)
            }
            Spacer(modifier = Modifier.height(20.dp))

            progressTrack.forEachIndexed { index, subject ->
                val color = try { Color(android.graphics.Color.parseColor(subject.colorHex)) } catch (e: Exception) { PrimaryBlue }
                val iconStr = if (subject.subjectName.contains("Toán", ignoreCase = true)) "Σ" else "∆"

                SubjectProgressRow(
                    iconText = iconStr,
                    name = subject.subjectName,
                    completed = subject.completedLessons,
                    total = subject.totalLessons,
                    progress = (subject.progressPercent / 100).toFloat(),
                    color = color
                )

                if (index < progressTrack.size - 1) Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun SubjectProgressRow(iconText: String, name: String, completed: Int, total: Int, progress: Float, color: Color) {
    Column {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(32.dp).clip(CircleShape).background(color.copy(alpha = 0.1f)), contentAlignment = Alignment.Center) {
                    Text(iconText, color = color, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(name, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1C2A3A))
            }
            Row(verticalAlignment = Alignment.Bottom) {
                Text("$completed/$total Bài ", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = color)
                Text("(${if(total > 0) (completed*100/total) else 0}%)", fontSize = 10.sp, color = Color.Gray)
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = { progress.coerceIn(0f, 1f) },
            modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
            color = color,
            trackColor = Color(0xFFF0F0F0)
        )
    }
}

@Composable
fun PerformanceChartCard(chartData: List<ChartPointDto>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text("Hiệu suất học tập", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF1C2A3A))
                    Text("Tiến độ bài kiểm tra theo tuần", fontSize = 12.sp, color = Color.Gray)
                }
                Box(modifier = Modifier.border(1.dp, Color(0xFFF0F0F0), RoundedCornerShape(50)).padding(horizontal = 12.dp, vertical = 6.dp)) {
                    Text("Tháng này ▾", fontSize = 12.sp, color = Color.DarkGray)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (chartData.isNotEmpty()) {
                val entries = chartData.mapIndexed { index, point ->
                    FloatEntry(x = index.toFloat(), y = point.score.toFloat())
                }
                val chartEntryModel = entryModelOf(entries)
                val xLabels = chartData.map { it.dateLabel }

                val gradientBrush = Brush.verticalGradient(listOf(PrimaryBlue.copy(alpha = 0.4f), Color.Transparent))

                Chart(
                    chart = lineChart(
                        lines = listOf(
                            lineSpec(
                                lineColor = PrimaryBlue,
                                lineThickness = 3.dp,
                                lineBackgroundShader = DynamicShaders.fromBrush(gradientBrush) // Đã import và sửa lỗi thành công
                            )
                        ),
                        axisValuesOverrider = AxisValuesOverrider.fixed(minY = 0f, maxY = 10f)
                    ),
                    model = chartEntryModel,
                    startAxis = rememberStartAxis(valueFormatter = { value, _ -> value.toInt().toString() }),
                    bottomAxis = rememberBottomAxis(valueFormatter = { value, _ ->
                        xLabels.getOrNull(value.toInt()) ?: ""
                    }),
                    modifier = Modifier.fillMaxWidth().height(180.dp)
                )
            } else {
                Box(modifier = Modifier.fillMaxWidth().height(180.dp), contentAlignment = Alignment.Center) {
                    Text("Chưa có dữ liệu bài thi", color = Color.Gray)
                }
            }
        }
    }
}

@Composable
fun AiPredictionCard(aiData: AiPredictionDto) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = PrimaryBlue),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text("Dự đoán điểm AI", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                    Text("Kỳ thi học kỳ I sắp tới", fontSize = 13.sp, color = Color.White.copy(alpha = 0.8f))
                }
                Box(modifier = Modifier.background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(50)).padding(horizontal = 12.dp, vertical = 6.dp)) {
                    Text("${aiData.predictedScore} / 10", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
            Spacer(modifier = Modifier.height(24.dp))

            val progressPercent = if (aiData.targetMinutes > 0) {
                (aiData.studiedMinutesThisWeek.toFloat() / aiData.targetMinutes.toFloat()).coerceIn(0f, 1f)
            } else 0f

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("MỤC TIÊU TUẦN: ${aiData.targetMinutes / 60}H HỌC", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text("${(progressPercent * 100).toInt()}%", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { progressPercent },
                modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                color = Color.White,
                trackColor = Color.White.copy(alpha = 0.3f)
            )
        }
    }
}

@Composable
fun HeatmapCard(heatmapData: List<HeatmapPointDto>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Mật độ học tập", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF1C2A3A))
                Text("3 tháng qua", fontSize = 12.sp, color = Color.Gray)
            }

            Spacer(modifier = Modifier.height(20.dp))

            val columns = heatmapData.chunked(7)

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(columns) { columnData ->
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        columnData.forEach { point ->
                            val boxColor = when (point.intensity) {
                                0 -> Color(0xFFF0F0F0)
                                1 -> PrimaryBlue.copy(alpha = 0.2f)
                                2 -> PrimaryBlue.copy(alpha = 0.5f)
                                3 -> PrimaryBlue.copy(alpha = 0.8f)
                                else -> PrimaryBlue
                            }
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(boxColor)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("ÍT HƠN", fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Box(modifier = Modifier.size(10.dp).clip(RoundedCornerShape(2.dp)).background(Color(0xFFF0F0F0)))
                    Box(modifier = Modifier.size(10.dp).clip(RoundedCornerShape(2.dp)).background(PrimaryBlue.copy(alpha = 0.2f)))
                    Box(modifier = Modifier.size(10.dp).clip(RoundedCornerShape(2.dp)).background(PrimaryBlue.copy(alpha = 0.5f)))
                    Box(modifier = Modifier.size(10.dp).clip(RoundedCornerShape(2.dp)).background(PrimaryBlue.copy(alpha = 0.8f)))
                    Box(modifier = Modifier.size(10.dp).clip(RoundedCornerShape(2.dp)).background(PrimaryBlue))
                }
                Text("NHIỀU HƠN", fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
            }
        }
    }
}