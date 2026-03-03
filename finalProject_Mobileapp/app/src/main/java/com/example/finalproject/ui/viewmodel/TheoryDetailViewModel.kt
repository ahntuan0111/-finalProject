package com.example.finalproject.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finalproject.data.remote.ApiService
import com.example.finalproject.data.remote.model.LessonCompletionRequest
import com.example.finalproject.data.remote.model.LessonDetailResponse
import com.example.finalproject.domain.repository.TheoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TheoryDetailViewModel @Inject constructor(
    private val apiService: ApiService,
    private val repository: TheoryRepository
) : ViewModel() {

    private val _lessonDetail = MutableStateFlow<LessonDetailResponse?>(null)
    val lessonDetail = _lessonDetail.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _isSubmitting = MutableStateFlow(false)
    val isSubmitting: StateFlow<Boolean> = _isSubmitting.asStateFlow()

    // Biến lưu trạng thái đã hoàn thành (Màu xanh)
    private val _isCompleted = MutableStateFlow(false)
    val isCompleted: StateFlow<Boolean> = _isCompleted.asStateFlow()

    fun fetchLessonDetail(lessonId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = apiService.getLessonDetail(lessonId)
                if (response.isSuccessful && response.body()?.success == true) {
                    _lessonDetail.value = response.body()?.data
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun markAsCompleted(lessonId: Long) {
        viewModelScope.launch {
            _isSubmitting.value = true
            try {
                val request = LessonCompletionRequest(
                    lessonId = lessonId,
                    type = "THEORY" // Code cứng là THEORY vì đây là màn hình Lý thuyết
                )
                val response = repository.markLessonAsCompleted(request)

                if (response.success) {
                    _isCompleted.value = true // Chuyển nút sang trạng thái đã hoàn thành
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isSubmitting.value = false
            }
        }
    }
}