package com.example.finalproject.data.repository

import com.example.finalproject.data.remote.ApiService
import com.example.finalproject.data.remote.model.ExerciseDto
import com.example.finalproject.data.remote.model.SaveSubmissionRequest
import com.example.finalproject.data.remote.model.UploadUrlResponse
import com.example.finalproject.data.remote.model.ValidationSubmissionRequest
import com.example.finalproject.data.remote.model.ValidationSubmissionResponse
import com.example.finalproject.domain.repository.ExerciseRepository
import com.example.finalproject.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ExerciseRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : ExerciseRepository {

    override suspend fun getExercisesByLesson(lessonId: Long): Flow<Resource<List<ExerciseDto>>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.getExercisesByLesson(lessonId)

            if (response.success) {
                val data = response.data ?: emptyList()
                emit(Resource.Success(data))
            } else {
                emit(Resource.Error(response.message ?: "Lỗi tải dữ liệu"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Lỗi kết nối: ${e.message}"))
        }
    }

    override suspend fun validateImage(exerciseId: Long, request: ValidationSubmissionRequest): Resource<ValidationSubmissionResponse> {
        return try {
            val response = apiService.validateImage(exerciseId, request)
            if (response.success && response.data != null) {
                Resource.Success(response.data)
            } else {
                Resource.Error(response.message ?: "Lỗi đánh giá AI")
            }
        } catch (e: retrofit2.HttpException) {
            // HTTP 400: Backend trả về lý do từ chối của AI trong body JSON
            // Cần parse body để lấy message thay vì hiện "Lỗi kết nối"
            val aiMessage = try {
                val errorBody = e.response()?.errorBody()?.string()
                val json = org.json.JSONObject(errorBody ?: "")
                json.optString("message", "Ảnh không hợp lệ, vui lòng chụp lại")
            } catch (parseEx: Exception) {
                "Ảnh không hợp lệ, vui lòng chụp lại"
            }
            Resource.Error(aiMessage)
        } catch (e: Exception) {
            Resource.Error("Lỗi kết nối: ${e.message}")
        }
    }

    override suspend fun saveSubmission(exerciseId: Long, request: SaveSubmissionRequest): Resource<Any> {
        return try {
            val response = apiService.saveSubmission(exerciseId, request)
            if (response.success) {
                Resource.Success(response.data ?: Any())
            } else {
                Resource.Error(response.message ?: "Lỗi lưu dữ liệu")
            }
        } catch (e: Exception) {
            Resource.Error("Lỗi kết nối: ${e.message}")
        }
    }

    override suspend fun getUploadUrl(exerciseId: Long): Resource<UploadUrlResponse> {
        return try {
            val response = apiService.getUploadUrl(exerciseId)
            if (response.success && response.data != null) {
                Resource.Success(response.data)
            } else {
                Resource.Error(response.message ?: "Lỗi lấy URL upload")
            }
        } catch (e: Exception) {
            Resource.Error("Lỗi kết nối: ${e.message}")
        }
    }
}