package com.example.finalproject.data.repository

import com.example.finalproject.data.remote.ApiService
import com.example.finalproject.data.remote.model.ExerciseDto
import com.example.finalproject.data.remote.model.SaveSubmissionRequest
import com.example.finalproject.data.remote.model.UploadUrlResponse
import com.example.finalproject.data.remote.model.ValidationSubmissionRequest
import com.example.finalproject.data.remote.model.ValidationSubmissionResponse
import com.example.finalproject.domain.repository.ExerciseRepository
import com.example.finalproject.utils.AppLogger
import com.example.finalproject.utils.ErrorParser
import com.example.finalproject.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ExerciseRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : ExerciseRepository {

    companion object { private const val TAG = "ExerciseRepo" }

    override suspend fun getExercisesByLesson(lessonId: Long): Flow<Resource<List<ExerciseDto>>> = flow {
        emit(Resource.Loading())
        AppLogger.network(TAG, "getExercisesByLesson(lessonId=$lessonId)")
        try {
            val response = apiService.getExercisesByLesson(lessonId)
            if (response.success) {
                AppLogger.d(TAG, "getExercisesByLesson OK — ${response.data?.size ?: 0} items")
                emit(Resource.Success(response.data ?: emptyList()))
            } else {
                val msg = ErrorParser.friendlyMessage(response.message.orEmpty().ifBlank { "Lỗi tải bài tập" })
                AppLogger.w(TAG, "getExercisesByLesson failed: ${response.message}")
                emit(Resource.Error(msg))
            }
        } catch (e: Exception) {
            val msg = ErrorParser.fromException(e, "Không thể tải bài tập")
            emit(Resource.Error(msg))
        }
    }

    override suspend fun validateImage(
        exerciseId: Long,
        request: ValidationSubmissionRequest
    ): Resource<ValidationSubmissionResponse> {
        AppLogger.network(TAG, "validateImage(exerciseId=$exerciseId)")
        return try {
            val response = apiService.validateImage(exerciseId, request)
            if (response.success && response.data != null) {
                AppLogger.d(TAG, "validateImage OK")
                Resource.Success(response.data)
            } else {
                val msg = ErrorParser.friendlyMessage(response.message.orEmpty().ifBlank { "Ảnh không hợp lệ, vui lòng chụp lại" })
                AppLogger.w(TAG, "validateImage failed: ${response.message}")
                Resource.Error(msg)
            }
        } catch (e: retrofit2.HttpException) {
            val aiMessage = try {
                val errorBody = e.response()?.errorBody()?.string()
                AppLogger.network(TAG, "validateImage HTTP ${e.code()} body: $errorBody")
                val json = org.json.JSONObject(errorBody ?: "")
                ErrorParser.friendlyMessage(json.optString("message", "Ảnh không hợp lệ, vui lòng chụp lại"))
            } catch (parseEx: Exception) {
                AppLogger.e(TAG, "Parse HttpException body failed", parseEx)
                "Ảnh không hợp lệ, vui lòng chụp lại"
            }
            Resource.Error(aiMessage)
        } catch (e: Exception) {
            Resource.Error(ErrorParser.fromException(e, "Không thể đánh giá bài làm"))
        }
    }

    override suspend fun saveSubmission(
        exerciseId: Long,
        request: SaveSubmissionRequest
    ): Resource<Any> {
        AppLogger.network(TAG, "saveSubmission(exerciseId=$exerciseId)")
        return try {
            val response = apiService.saveSubmission(exerciseId, request)
            if (response.success) {
                AppLogger.d(TAG, "saveSubmission OK")
                Resource.Success(response.data ?: Any())
            } else {
                val msg = ErrorParser.friendlyMessage(response.message.orEmpty().ifBlank { "Lưu bài làm thất bại" })
                AppLogger.w(TAG, "saveSubmission failed: ${response.message}")
                Resource.Error(msg)
            }
        } catch (e: Exception) {
            Resource.Error(ErrorParser.fromException(e, "Không thể nộp bài"))
        }
    }

    override suspend fun getUploadUrl(exerciseId: Long): Resource<UploadUrlResponse> {
        AppLogger.network(TAG, "getUploadUrl(exerciseId=$exerciseId)")
        return try {
            val response = apiService.getUploadUrl(exerciseId)
            if (response.success && response.data != null) {
                AppLogger.d(TAG, "getUploadUrl OK → ${response.data.uploadUrl}")
                Resource.Success(response.data)
            } else {
                val msg = ErrorParser.friendlyMessage(response.message.orEmpty().ifBlank { "Lỗi chuẩn bị tải ảnh" })
                AppLogger.w(TAG, "getUploadUrl failed: ${response.message}")
                Resource.Error(msg)
            }
        } catch (e: Exception) {
            Resource.Error(ErrorParser.fromException(e, "Không thể chuẩn bị tải ảnh"))
        }
    }
}