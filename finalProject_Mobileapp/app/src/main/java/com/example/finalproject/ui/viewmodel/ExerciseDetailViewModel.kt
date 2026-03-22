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
import android.content.Context
import android.net.Uri
import com.example.finalproject.data.remote.model.SaveSubmissionRequest
import com.example.finalproject.data.remote.model.ValidationSubmissionRequest
import com.example.finalproject.data.remote.model.ValidationSubmissionResponse
import com.example.finalproject.utils.FirebaseStorageHelper

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

    private val _validationResult = MutableStateFlow<ValidationSubmissionResponse?>(null)
    val validationResult: StateFlow<ValidationSubmissionResponse?> = _validationResult.asStateFlow()

    private val _isCompleted = MutableStateFlow(false)
    val isCompleted: StateFlow<Boolean> = _isCompleted.asStateFlow()

    fun resetValidationResult() {
        _validationResult.value = null
    }

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

    fun submitExerciseImage(context: Context, exerciseId: Long, lessonId: Long, uri: Uri) {
        viewModelScope.launch {
            _isSubmitting.value = true
            _validationResult.value = null
            _errorMessage.value = null
            try {
                // 1. Chuyển ảnh thành Base64
                val base64 = encodeImageToBase64(context, uri)
                if (base64 == null) {
                    _errorMessage.value = "Lỗi xử lý ảnh, vui lòng chụp lại"
                    _isSubmitting.value = false
                    return@launch
                }

                // 2. Gửi Base64 qua Backend để gọi Gemini API
                val validationResponse = exerciseRepository.validateImage(
                    exerciseId, 
                    ValidationSubmissionRequest(base64Image = base64)
                )

                when (validationResponse) {
                    is Resource.Success -> {
                        val result = validationResponse.data
                        _validationResult.value = result

                        // 3. Nếu AI Passed -> Lấy Signed URL từ Backend
                        if (result?.isValid == true) {
                            val uploadUrlResponse = exerciseRepository.getUploadUrl(exerciseId)

                            if (uploadUrlResponse is Resource.Success && uploadUrlResponse.data != null) {
                                val urlData = uploadUrlResponse.data

                                // 4. Upload ảnh lên Firebase Storage qua Signed URL
                                val uploadSuccess = FirebaseStorageHelper.uploadImageWithSignedUrl(
                                    context, uri, urlData.uploadUrl
                                )

                                if (uploadSuccess) {
                                    // 5. Lưu public URL vào Backend
                                    val saveResponse = exerciseRepository.saveSubmission(
                                        exerciseId,
                                        SaveSubmissionRequest(
                                            imageUrl = urlData.publicUrl,
                                            aiReason = result.reason
                                        )
                                    )

                                    if (saveResponse is Resource.Success) {
                                        markAsCompleted(lessonId)
                                    } else {
                                        _errorMessage.value = saveResponse.message ?: "Lỗi lưu dữ liệu bài nộp"
                                    }
                                } else {
                                    _errorMessage.value = "Upload ảnh lên Cloud thất bại, vui lòng thử lại"
                                }
                            } else {
                                _errorMessage.value = uploadUrlResponse.message ?: "Không thể tạo URL upload"
                            }
                        }
                    }
                    is Resource.Error -> {
                        _errorMessage.value = validationResponse.message ?: "Lỗi xác thực AI"
                    }
                    else -> {}
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _errorMessage.value = "Đã xảy ra lỗi: ${e.message}"
            } finally {
                _isSubmitting.value = false
            }
        }
    }

    private fun encodeImageToBase64(context: Context, uri: Uri): String? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val bytes = inputStream?.readBytes()
            inputStream?.close()
            if (bytes != null) {
                android.util.Base64.encodeToString(bytes, android.util.Base64.NO_WRAP)
            } else null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}