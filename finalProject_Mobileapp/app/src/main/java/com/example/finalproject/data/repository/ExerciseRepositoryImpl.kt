package com.example.finalproject.data.repository

import com.example.finalproject.data.remote.ApiService
import com.example.finalproject.data.remote.model.ExerciseDto
import com.example.finalproject.domain.repository.ExerciseRepository
import com.example.finalproject.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ExerciseRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : ExerciseRepository {

    override suspend fun getExercisesByLesson(lessonId: Long): Flow<Resource<List<ExerciseDto>>> = flow {
        emit(Resource.Loading()) // Báo trạng thái đang tải
        try {
            // Gọi API (Giả sử ApiService trả về Response<ApiResponse<List<ExerciseDto>>>)
            // Nếu ApiService của bạn trả về trực tiếp ApiResponse, hãy bỏ đoạn .body()
            val response = apiService.getExercisesByLesson(lessonId)

            if (response.success) { // Kiểm tra field success từ BE
                val data = response.data ?: emptyList()
                emit(Resource.Success(data))
            } else {
                emit(Resource.Error(response.message ?: "Lỗi tải dữ liệu"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Lỗi kết nối: ${e.message}"))
        }
    }
}