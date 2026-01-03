package com.example.finalproject.ui.screens.home

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {

    // Giả lập tên user (Lấy từ User Profile sau này)
    private val _userName = MutableStateFlow("Minh")
    val userName = _userName.asStateFlow()

    // Giả lập thống kê
    val stats = listOf(
        DashboardStat(Icons.Default.Timeline, "75%", "Tiến độ", Color(0xFFE0F7FA)),
        DashboardStat(Icons.Default.CheckCircle, "12", "Đã xong", Color(0xFFF1F8E9)),
        DashboardStat(Icons.Default.LocalFireDepartment, "5", "Ngày", Color(0xFFFFF3E0)),
        DashboardStat(Icons.Default.Star, "350", "Điểm XP", Color(0xFFFFF8E1))
    )

    // Giả lập môn học
    val subjects = listOf(
        SubjectItem("math", "Toán Học", 0.45f, 20, 9, Color(0xFFE3F2FD)),
        SubjectItem("science", "Khoa học Tự nhiên", 0.2f, 15, 3, Color(0xFFE8F5E9))
    )
}