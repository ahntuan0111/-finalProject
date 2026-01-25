package com.example.finalproject.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finalproject.data.remote.ApiService
import com.example.finalproject.data.remote.model.LessonDetailResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TheoryDetailViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {

    private val _lessonDetail = MutableStateFlow<LessonDetailResponse?>(null)
    val lessonDetail = _lessonDetail.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

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
}