package com.example.finalproject.ui.screens.theory

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finalproject.data.remote.ApiService
import com.example.finalproject.data.remote.model.ChapterDto
import com.example.finalproject.data.remote.model.LessonDto
import com.example.finalproject.utils.DataStoreManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TheoryViewModel @Inject constructor(
    private val apiService: ApiService,
    private val dataStoreManager: DataStoreManager
) : ViewModel() {

    // Danh sách chương
    private val _chapters = MutableStateFlow<List<ChapterDto>>(emptyList())
    val chapters = _chapters.asStateFlow()

    // Danh sách ID các chương đang mở
    private val _expandedChapterIds = MutableStateFlow<Set<Long>>(emptySet())
    val expandedChapterIds = _expandedChapterIds.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun fetchChapters(subjectCode: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // 3. Lấy grade từ DataStore
                val currentGrade = dataStoreManager.getGradeSync()

                // Log để kiểm tra
                Log.d("TheoryViewModel", "Fetching $subjectCode for Grade: $currentGrade")

                // 4. Gọi API với grade vừa lấy được
                val response = apiService.getChaptersBySubject(subjectCode, currentGrade)

                if (response.isSuccessful && response.body()?.success == true) {
                    val data = response.body()?.data ?: emptyList()
                    _chapters.value = data
                    if (data.isNotEmpty()) {
                        _expandedChapterIds.value = setOf(data.first().id)
                    }
                }
                // ... (Xử lý lỗi giữ nguyên)
            } catch (e: Exception) {
                // ...
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Hàm toggle đóng mở chương
    fun toggleChapter(chapterId: Long) {
        val currentIds = _expandedChapterIds.value.toMutableSet()
        if (currentIds.contains(chapterId)) {
            currentIds.remove(chapterId)
        } else {
            currentIds.add(chapterId)
        }
        _expandedChapterIds.value = currentIds
    }
}