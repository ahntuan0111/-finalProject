package com.example.finalproject.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finalproject.data.remote.model.ExerciseDto
import com.example.finalproject.domain.repository.ExerciseRepository
import com.example.finalproject.data.remote.model.LessonCompletionRequest
import com.example.finalproject.domain.repository.TheoryRepository
import com.example.finalproject.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExerciseDetailViewModel @Inject constructor(
    private val exerciseRepository: ExerciseRepository,
    private val repository: TheoryRepository
) : ViewModel() {

    // State chứa danh sách bài tập
    private val _exercises = MutableStateFlow<List<ExerciseDto>>(emptyList())
    val exercises: StateFlow<List<ExerciseDto>> = _exercises.asStateFlow()

    // State chứa trạng thái loading
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // State chứa thông báo lỗi (nếu cần hiển thị Toast/Snackbar)
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _isSubmitting = MutableStateFlow(false)
    val isSubmitting: StateFlow<Boolean> = _isSubmitting.asStateFlow()

    private val _isCompleted = MutableStateFlow(false)
    val isCompleted: StateFlow<Boolean> = _isCompleted.asStateFlow()

    fun fetchExercises(lessonId: Long) {
        viewModelScope.launch {
            exerciseRepository.getExercisesByLesson(lessonId).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _isLoading.value = true
                        _errorMessage.value = null
                    }
                    is Resource.Success -> {
                        _isLoading.value = false
                        _exercises.value = result.data ?: emptyList()
                    }
                    is Resource.Error -> {
                        _isLoading.value = false
                        _errorMessage.value = result.message ?: "Đã xảy ra lỗi không xác định"
                    }
                }
            }
        }
    }

    fun markAsCompleted(lessonId: Long) {
        viewModelScope.launch {
            _isSubmitting.value = true
            try {
                val request = LessonCompletionRequest(
                    lessonId = lessonId,
                    type = "EXERCISE" // Bắt buộc là EXERCISE
                )
                // Lưu ý: repository này phải là file chứa hàm markLessonAsCompleted
                // giống như bên Theory.
                val response = repository.markLessonAsCompleted(request)

                if (response.success) {
                    _isCompleted.value = true
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isSubmitting.value = false
            }
        }
    }
}