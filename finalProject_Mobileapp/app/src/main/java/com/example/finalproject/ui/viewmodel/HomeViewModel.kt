package com.example.finalproject.ui.viewmodel

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finalproject.R
import com.example.finalproject.data.remote.model.DashboardStat
import com.example.finalproject.data.remote.model.SubjectItem
import com.example.finalproject.domain.repository.ProgressRepository
import com.example.finalproject.utils.DataStoreManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val progressRepository: ProgressRepository,
    private val dataStoreManager: DataStoreManager
) : ViewModel() {

    private val _userName = MutableStateFlow("Minh")
    val userName: StateFlow<String> = _userName.asStateFlow()

    // Danh sách thống kê dùng ảnh (Có thể gọi API cập nhật sau nếu cần)
    val stats = listOf(
        DashboardStat(R.drawable.trend, "75%", "Tiến độ", Color(0xFFE0F7FA)),
        DashboardStat(R.drawable.check, "12", "Đã xong", Color(0xFFF1F8E9)),
        DashboardStat(R.drawable.fire, "5", "Ngày", Color(0xFFFFF3E0)),
        DashboardStat(R.drawable.medal, "350", "Điểm XP", Color(0xFFFFF8E1))
    )

    // Khởi tạo môn học với thông số mặc định là 0 (Đổi ID thành "toan" và "khtn" để khớp với DB)
    private val _subjects = MutableStateFlow(
        listOf(
            SubjectItem("toan", "Toán Học", R.drawable.math, 0f, 0, 0, Color(0xFFE3F2FD)),
            SubjectItem("khtn", "Khoa học Tự nhiên", R.drawable.science, 0f, 0, 0, Color(0xFFE8F5E9))
        )
    )
    val subjects: StateFlow<List<SubjectItem>> = _subjects.asStateFlow()

    // Hàm gọi API lấy tiến độ học tập
    fun fetchProgress() {
        viewModelScope.launch {
            try {
                // 1. Lấy khối lớp hiện tại từ DataStore (Mặc định là 6 nếu chưa có)
                val currentGrade = dataStoreManager.getGrade().first() ?: 6

                // 2. Gọi API lấy danh sách tiến trình theo lớp
                val response = progressRepository.getMyProgress(currentGrade)

                if (response.success) {
                    val progressList = response.data

                    // 3. Đắp dữ liệu API vào danh sách hiển thị
                    _subjects.update { currentSubjects ->
                        currentSubjects.map { subject ->
                            // Tìm data khớp với mã môn học (toan/khtn) VÀ khớp grade
                            val remoteData = progressList?.find {
                                it.subjectCode == subject.id && it.gradeId == currentGrade
                            }

                            if (remoteData != null) {
                                subject.copy(
                                    finishedLessons = remoteData.completedLessons,
                                    totalLessons = remoteData.totalLessons,
                                    // Backend trả về 0.0 - 100.0, Compose cần 0.0f - 1.0f
                                    progress = (remoteData.progressPercent / 100.0).toFloat()
                                )
                            } else {
                                // Nếu người dùng chưa học môn này bao giờ, trả về 0
                                subject.copy(
                                    finishedLessons = 0,
                                    totalLessons = 0,
                                    progress = 0f
                                )
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}