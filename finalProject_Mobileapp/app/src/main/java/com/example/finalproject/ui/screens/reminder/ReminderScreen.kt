package com.example.finalproject.ui.screens.reminder

import android.app.TimePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.MenuBook
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.finalproject.ui.viewmodel.ReminderViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderScreen(
    navController: NavController,
    viewModel: ReminderViewModel = hiltViewModel()
) {
    val dailyEnabled by viewModel.dailyEnabled.collectAsState()
    val dailyTime by viewModel.dailyTime.collectAsState()

    val streakEnabled by viewModel.streakEnabled.collectAsState()
    val streakTime by viewModel.streakTime.collectAsState()

    val examEnabled by viewModel.examEnabled.collectAsState()

    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cài đặt Nhắc nhở", fontWeight = FontWeight.Bold, fontSize = 22.sp) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Daily Reminder Card
            ReminderCard(
                title = "Nhắc học lý thuyết",
                description = "Nhận thông báo để ôn tập bài học mỗi ngày",
                icon = Icons.Rounded.MenuBook,
                time = dailyTime,
                isEnabled = dailyEnabled,
                onCheckedChange = { isChecked ->
                    viewModel.toggleDaily(isChecked, dailyTime.first, dailyTime.second)
                },
                onTimeClick = {
                    TimePickerDialog(
                        context,
                        { _, hour, minute ->
                            viewModel.toggleDaily(dailyEnabled, hour, minute)
                        },
                        dailyTime.first,
                        dailyTime.second,
                        true
                    ).show()
                }
            )

            // Streak Reminder Card
            ReminderCard(
                title = "Bảo vệ Streak",
                description = "Nhắc bạn hoàn thành bài học để giữ chuỗi ngày",
                icon = Icons.Rounded.LocalFireDepartment,
                time = streakTime,
                isEnabled = streakEnabled,
                onCheckedChange = { isChecked ->
                    viewModel.toggleStreak(isChecked, streakTime.first, streakTime.second)
                },
                onTimeClick = {
                    TimePickerDialog(
                        context,
                        { _, hour, minute ->
                            viewModel.toggleStreak(streakEnabled, hour, minute)
                        },
                        streakTime.first,
                        streakTime.second,
                        true
                    ).show()
                }
            )

            // Exam Reminder Card
            ReminderCard(
                title = "Nhắc trước kỳ thi",
                description = "Nhận thông báo tự động khi sắp có lịch thi diễn ra",
                icon = Icons.Rounded.CalendarMonth,
                time = null,
                isEnabled = examEnabled,
                onCheckedChange = { isChecked ->
                    viewModel.toggleExam(isChecked)
                },
                onTimeClick = null
            )
        }
    }
}

@Composable
fun ReminderCard(
    title: String,
    description: String,
    icon: ImageVector,
    time: Pair<Int, Int>?,
    isEnabled: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    onTimeClick: (() -> Unit)?
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier
                            .padding(12.dp)
                            .size(24.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
                    Text(text = description, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Switch(
                    checked = isEnabled,
                    onCheckedChange = onCheckedChange,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MaterialTheme.colorScheme.primary,
                        checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
                    )
                )
            }

            if (time != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Thời gian nhắc nhở", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
                    Button(
                        onClick = { if (onTimeClick != null) onTimeClick() },
                        enabled = isEnabled,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                        ),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        val timeString = String.format(Locale.getDefault(), "%02d:%02d", time.first, time.second)
                        Text(text = timeString, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    }
                }
            }
        }
    }
}
