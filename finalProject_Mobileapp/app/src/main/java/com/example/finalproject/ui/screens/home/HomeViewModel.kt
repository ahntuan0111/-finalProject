package com.example.finalproject.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.compose.ui.graphics.Color
import com.example.finalproject.R // Import R để lấy ID ảnh
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {

    private val _userName = MutableStateFlow("Minh")
    val userName = _userName.asStateFlow()

    // Cập nhật danh sách thống kê dùng ảnh
    val stats = listOf(
        // Giả sử bạn đã có các ảnh này trong res/drawable
        DashboardStat(R.drawable.trend, "75%", "Tiến độ", Color(0xFFE0F7FA)),
        DashboardStat(R.drawable.check, "12", "Đã xong", Color(0xFFF1F8E9)),
        DashboardStat(R.drawable.fire, "5", "Ngày", Color(0xFFFFF3E0)),
        DashboardStat(R.drawable.medal, "350", "Điểm XP", Color(0xFFFFF8E1))
    )

    // Cập nhật môn học dùng ảnh
    val subjects = listOf(
        SubjectItem("math", "Toán Học", R.drawable.math, 0.45f, 20, 9, Color(0xFFE3F2FD)),
        SubjectItem("science", "Khoa học Tự nhiên", R.drawable.science, 0.2f, 15, 3, Color(0xFFE8F5E9))
    )
}