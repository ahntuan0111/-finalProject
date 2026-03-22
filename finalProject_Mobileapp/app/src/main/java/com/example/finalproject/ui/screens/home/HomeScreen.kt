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
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.finalproject.R
import com.example.finalproject.data.remote.model.DashboardStat
import com.example.finalproject.data.remote.model.SubjectItem
import com.example.finalproject.ui.components.NotificationBottomSheet
import com.example.finalproject.ui.navigation.AppRoutes
import com.example.finalproject.ui.theme.PrimaryBlue
import com.example.finalproject.ui.theme.TextGray
import com.example.finalproject.ui.viewmodel.HomeViewModel
import com.example.finalproject.ui.viewmodel.NotificationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel(),
    notificationViewModel: NotificationViewModel = hiltViewModel()
) {
    val userName by viewModel.userName.collectAsState()
    val subjects by viewModel.subjects.collectAsState()
    val scrollState = rememberScrollState()

    // --- State cho Thông báo ---
    val notifications by notificationViewModel.notifications.collectAsState()
    val unreadCount by notificationViewModel.unreadCount.collectAsState()
    val isNotifLoading by notificationViewModel.isLoading.collectAsState()
    var showNotificationSheet by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.fetchProgress()
        notificationViewModel.fetchData()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        // 1. Khu vực chào mừng
        HomeHeader(
            userName = userName,
            unreadCount = unreadCount,
            onNotificationClick = { showNotificationSheet = true }
        )

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
        // Lấy tên môn học fallback từ strings.xml
        val fallbackSubjectName = stringResource(id = R.string.home_fallback_subject)

        HomeSubjects(subjects = subjects) { subjectIdString ->
            val dbSubjectCode = when(subjectIdString) {
                "math" -> "toan"
                "science" -> "khtn"
                else -> subjectIdString
            }

            val subjectName = subjects.find { it.id == subjectIdString }?.name ?: fallbackSubjectName
            navController.navigate(AppRoutes.createSubjectDetailRoute(dbSubjectCode, subjectName))
        }

        Spacer(modifier = Modifier.height(32.dp))

        // 5. Trợ lý AI
        HomeAiAssistant(onAiClick = {
            // TODO: Navigate to Chat Screen
        })

        Spacer(modifier = Modifier.height(80.dp))
    }

    // --- HIỂN THỊ BOTTOM SHEET THÔNG BÁO ---
    if (showNotificationSheet) {
        NotificationBottomSheet(
            notifications = notifications,
            isLoading = isNotifLoading,
            onDismiss = { showNotificationSheet = false },
            onMarkAllRead = { notificationViewModel.markAllAsRead() },
            onItemClick = { notif ->
                if (!notif.isRead) {
                    notificationViewModel.markAsRead(notif.id)
                }
                showNotificationSheet = false
                if (!notif.actionUrl.isNullOrEmpty()) {
                    try {
                        navController.navigate(notif.actionUrl)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        )
    }
}

// --- CÁC COMPONENT CON ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeHeader(
    userName: String,
    unreadCount: Int,
    onNotificationClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(id = R.string.home_welcome_user, userName),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Text(
                text = stringResource(id = R.string.home_what_to_learn),
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                onClick = onNotificationClick,
                modifier = Modifier.padding(end = 8.dp)
            ) {
                BadgedBox(
                    badge = {
                        if (unreadCount > 0) {
                            Badge(
                                containerColor = Color(0xFFF04438),
                                contentColor = Color.White
                            ) {
                                Text(if (unreadCount > 99) "99+" else unreadCount.toString())
                            }
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = stringResource(id = R.string.home_content_desc_notification),
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE8F0FE))
                    .border(2.dp, Color(0xFFEFEFEF), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = stringResource(id = R.string.home_content_desc_avatar),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(28.dp)
                )
            }
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
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 0.5.dp,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = stringResource(id = R.string.home_content_desc_search),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = stringResource(id = R.string.home_search_hint),
                color = Color.LightGray,
                fontSize = 15.sp
            )
        }
    }
}

// ================= GIAO DIỆN DASHBOARD MỚI =================
@Composable
fun HomeDashboard(stats: List<DashboardStat>) {
    // Đọc String từ resources để làm fallback nếu index không tồn tại
    val progressLabel = stringResource(id = R.string.home_dashboard_progress)
    val completedLabel = stringResource(id = R.string.home_dashboard_completed)
    val daysLabel = stringResource(id = R.string.home_dashboard_days)
    val streakLabel = stringResource(id = R.string.home_dashboard_streak)

    val progressStat = stats.getOrNull(0) ?: DashboardStat(0, "0%", progressLabel, Color.White)
    val completedStat = stats.getOrNull(1) ?: DashboardStat(0, "0", completedLabel, Color.White)
    val streakStat = stats.getOrNull(2) ?: DashboardStat(0, "0", daysLabel, Color.White)

    val progressValue = progressStat.value.replace("%", "").toFloatOrNull()?.div(100f) ?: 0f

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    progress = { progressValue },
                    modifier = Modifier.size(110.dp),
                    color = PrimaryBlue,
                    trackColor = Color(0xFFF4F6FB),
                    strokeWidth = 12.dp,
                    strokeCap = StrokeCap.Round
                )
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = progressStat.value,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 24.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = progressStat.label.uppercase(),
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier
                    .weight(1.1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                MiniStatCard(
                    modifier = Modifier.weight(1f),
                    iconRes = completedStat.imageRes,
                    value = completedStat.value,
                    label = completedStat.label,
                    bgColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha=0.5f),
                    iconBg = MaterialTheme.colorScheme.surface
                )

                MiniStatCard(
                    modifier = Modifier.weight(1f),
                    iconRes = streakStat.imageRes,
                    value = "${streakStat.value} $daysLabel", // Ép chuỗi động: "0 Ngày" / "0 Days"
                    label = streakLabel,
                    bgColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha=0.5f),
                    iconBg = MaterialTheme.colorScheme.surface
                )
            }
        }
    }
}

@Composable
fun MiniStatCard(iconRes: Int, value: String, label: String, bgColor: Color, iconBg: Color, modifier: Modifier = Modifier) {
    Card(
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

            Column {
                Text(
                    text = value,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = label,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
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
            Text("📚 ", fontSize = 20.sp)
            Text(
                text = stringResource(id = R.string.home_your_subjects),
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface
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
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
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
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(10.dp))

                LinearProgressIndicator(
                    progress = { subject.progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = PrimaryBlue,
                    trackColor = Color(0xFFF0F0F0),
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = if (subject.totalLessons == 0)
                        stringResource(id = R.string.home_no_data)
                    else
                        stringResource(id = R.string.home_lesson_count_format, subject.finishedLessons, subject.totalLessons),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
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
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surface),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.SmartToy,
                        contentDescription = stringResource(id = R.string.home_content_desc_ai),
                        tint = PrimaryBlue,
                        modifier = Modifier.size(26.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = stringResource(id = R.string.home_ai_assistant),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 17.sp,
                        color = PrimaryBlue
                    )
                    Text(
                        text = stringResource(id = R.string.home_ai_desc),
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Surface(
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.surface
            ) {
                Row(
                    modifier = Modifier.fillMaxSize().padding(start = 16.dp, end = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(id = R.string.home_ai_hint),
                        color = Color.LightGray,
                        fontSize = 14.sp
                    )

                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(PrimaryBlue),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = stringResource(id = R.string.home_content_desc_send),
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}