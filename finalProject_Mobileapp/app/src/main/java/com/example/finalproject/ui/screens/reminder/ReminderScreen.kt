package com.example.finalproject.ui.screens.reminder

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.finalproject.data.remote.model.ReminderSettingRequest
import com.example.finalproject.ui.viewmodel.ReminderViewModel
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderScreen(
    navController: NavController,
    viewModel: ReminderViewModel = hiltViewModel()
) {
    val settings by viewModel.settings.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Kế hoạch Học tập", fontWeight = FontWeight.Bold, fontSize = 22.sp) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (isLoading && settings == null) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (error != null && settings == null) {
                Text(
                    text = "Lỗi: $error",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else if (settings != null) {
                val currentSettings = settings!!
                val updateSettings = { newReq: ReminderSettingRequest ->
                    viewModel.updateSettings(newReq)
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    StudyPlanCard(
                        isEnabled = currentSettings.isStudyReminderEnabled,
                        studyTime = currentSettings.studyTime ?: "20:00",
                        studyDays = currentSettings.studyDays ?: "MON,TUE,WED,THU,FRI,SAT,SUN",
                        onUpdate = { enabled, time, days ->
                            updateSettings(
                                ReminderSettingRequest(
                                    isStudyReminderEnabled = enabled,
                                    studyTime = time,
                                    studyDays = days,
                                    isStreakReminderEnabled = currentSettings.isStreakReminderEnabled,
                                    streakTime = currentSettings.streakTime,
                                    isGoalReminderEnabled = currentSettings.isGoalReminderEnabled,
                                    targetDate = currentSettings.targetDate,
                                    targetName = currentSettings.targetName
                                )
                            )
                        }
                    )

                    StreakCard(
                        isEnabled = currentSettings.isStreakReminderEnabled,
                        streakTime = currentSettings.streakTime ?: "21:00",
                        onUpdate = { enabled, time ->
                            updateSettings(
                                ReminderSettingRequest(
                                    isStudyReminderEnabled = currentSettings.isStudyReminderEnabled,
                                    studyTime = currentSettings.studyTime,
                                    studyDays = currentSettings.studyDays,
                                    isStreakReminderEnabled = enabled,
                                    streakTime = time,
                                    isGoalReminderEnabled = currentSettings.isGoalReminderEnabled,
                                    targetDate = currentSettings.targetDate,
                                    targetName = currentSettings.targetName
                                )
                            )
                        }
                    )

                    GoalCard(
                        isEnabled = currentSettings.isGoalReminderEnabled,
                        targetDate = currentSettings.targetDate,
                        targetName = currentSettings.targetName ?: "Kỳ thi quan trọng",
                        onUpdate = { enabled, date, name ->
                            updateSettings(
                                ReminderSettingRequest(
                                    isStudyReminderEnabled = currentSettings.isStudyReminderEnabled,
                                    studyTime = currentSettings.studyTime,
                                    studyDays = currentSettings.studyDays,
                                    isStreakReminderEnabled = currentSettings.isStreakReminderEnabled,
                                    streakTime = currentSettings.streakTime,
                                    isGoalReminderEnabled = enabled,
                                    targetDate = date,
                                    targetName = name
                                )
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun StudyPlanCard(
    isEnabled: Boolean,
    studyTime: String,
    studyDays: String,
    onUpdate: (Boolean, String, String) -> Unit
) {
    val context = LocalContext.current
    val daysOfWeek = listOf("MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN")
    val dayLabels = listOf("T2", "T3", "T4", "T5", "T6", "T7", "CN")
    val selectedDays = studyDays.split(",").toSet()

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Lịch học của tôi", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text("Dành 15 phút mỗi ngày để ôn tập", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Switch(
                    checked = isEnabled,
                    onCheckedChange = { onUpdate(it, studyTime, studyDays) },
                    colors = SwitchDefaults.colors(checkedThumbColor = MaterialTheme.colorScheme.primary)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                daysOfWeek.forEachIndexed { index, day ->
                    val isSelected = selectedDays.contains(day)
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(
                                if (isSelected && isEnabled) MaterialTheme.colorScheme.primary 
                                else if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.5f) 
                                else MaterialTheme.colorScheme.surfaceVariant
                            )
                            .clickable(enabled = isEnabled) {
                                val newDays = if (isSelected) selectedDays - day else selectedDays + day
                                onUpdate(isEnabled, studyTime, newDays.joinToString(","))
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = dayLabels[index],
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    val parts = studyTime.split(":")
                    if(parts.size == 2) {
                        TimePickerDialog(context, { _, hour, minute ->
                            val newTime = String.format(Locale.getDefault(), "%02d:%02d", hour, minute)
                            onUpdate(isEnabled, newTime, studyDays)
                        }, parts[0].toInt(), parts[1].toInt(), true).show()
                    }
                },
                enabled = isEnabled,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer, contentColor = MaterialTheme.colorScheme.onPrimaryContainer),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(text = studyTime, fontWeight = FontWeight.Bold, fontSize = 24.sp, modifier = Modifier.padding(vertical = 4.dp))
            }
        }
    }
}

@Composable
fun StreakCard(
    isEnabled: Boolean,
    streakTime: String,
    onUpdate: (Boolean, String) -> Unit
) {
    val context = LocalContext.current
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha=0.5f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.LocalFireDepartment, contentDescription = null, tint = Color(0xFFFA8C16), modifier = Modifier.size(28.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Cảnh báo mất lửa!", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFFD4380D), modifier = Modifier.weight(1f))
                Switch(
                    checked = isEnabled,
                    onCheckedChange = { onUpdate(it, streakTime) },
                    colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFFFA8C16), checkedTrackColor = Color(0xFFFFD591))
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Nhắc tôi nếu đến ", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.clickable(enabled = isEnabled) {
                        val parts = streakTime.split(":")
                        if(parts.size == 2) {
                            TimePickerDialog(context, { _, hour, minute ->
                                val newTime = String.format(Locale.getDefault(), "%02d:%02d", hour, minute)
                                onUpdate(isEnabled, newTime)
                            }, parts[0].toInt(), parts[1].toInt(), true).show()
                        }
                    }
                ) {
                    Text(streakTime, color = Color(0xFFD4380D), fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                }
                Text(" tối chưa học", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
            }
        }
    }
}

@Composable
fun GoalCard(
    isEnabled: Boolean,
    targetDate: String?,
    targetName: String,
    onUpdate: (Boolean, String?, String) -> Unit
) {
    val context = LocalContext.current
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(Icons.Rounded.CalendarMonth, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(6.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text("Mục tiêu sắp tới", fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.weight(1f))
                Switch(
                    checked = isEnabled,
                    onCheckedChange = { onUpdate(it, targetDate, targetName) },
                    colors = SwitchDefaults.colors(checkedThumbColor = MaterialTheme.colorScheme.primary)
                )
            }

            if (isEnabled) {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        val calendar = Calendar.getInstance()
                        DatePickerDialog(context, { _, year, month, day ->
                            val selectedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, day)
                            onUpdate(isEnabled, selectedDate, targetName)
                        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer, contentColor = MaterialTheme.colorScheme.onSecondaryContainer),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(text = targetDate ?: "Chọn ngày thi / mục tiêu", fontWeight = FontWeight.Bold)
                }

                if (targetDate != null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    var daysText = ""
                    var isInvalid = false
                    try {
                        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        val target = sdf.parse(targetDate)
                        if (target != null) {
                            val today = Calendar.getInstance().apply {
                                set(Calendar.HOUR_OF_DAY, 0)
                                set(Calendar.MINUTE, 0)
                                set(Calendar.SECOND, 0)
                                set(Calendar.MILLISECOND, 0)
                            }.time
                            val targetCal = Calendar.getInstance().apply {
                                time = target
                                set(Calendar.HOUR_OF_DAY, 0)
                                set(Calendar.MINUTE, 0)
                                set(Calendar.SECOND, 0)
                                set(Calendar.MILLISECOND, 0)
                            }.time
                            
                            val diff = targetCal.time - today.time
                            val daysLeft = java.util.concurrent.TimeUnit.DAYS.convert(diff, java.util.concurrent.TimeUnit.MILLISECONDS)
                            
                            daysText = if (daysLeft > 0) "$daysLeft ngày nữa" else if (daysLeft == 0L) "Hôm nay!" else "Đã qua"
                        } else {
                            isInvalid = true
                        }
                    } catch (e: Exception) {
                        isInvalid = true
                    }
                    
                    if (isInvalid) {
                        Text("Ngày không hợp lệ", color = MaterialTheme.colorScheme.error)
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFE6F4EA), RoundedCornerShape(12.dp))
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Chỉ còn khoảng", color = Color(0xFF137333), fontSize = 14.sp)
                                Text(daysText, color = Color(0xFF137333), fontSize = 28.sp, fontWeight = FontWeight.Bold)
                                Text("Đến $targetName", color = Color(0xFF137333), fontSize = 14.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
