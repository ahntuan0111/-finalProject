package com.example.finalproject.data.repository

import com.example.finalproject.data.remote.ApiService
import com.example.finalproject.data.remote.model.UpdateGradeRequest
import com.example.finalproject.domain.repository.UserRepository
import com.example.finalproject.utils.DataStoreManager
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val dataStoreManager: DataStoreManager // Class quản lý DataStore của bạn
) : UserRepository {

    override suspend fun updateGrade(grade: Int) = flow {
        try {
            val response = apiService.updateGrade(UpdateGradeRequest(grade))
            if (response.isSuccessful) {
                // Lưu ngay vào máy để dùng luôn
                dataStoreManager.saveGrade(grade)
                emit(Result.success(Unit))
            } else {
                emit(Result.failure(Exception("Lỗi cập nhật lớp")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override suspend fun fetchAndSaveCurrentGrade() = flow {
        try {
            val response = apiService.getCurrentGrade()
            if (response.isSuccessful) {
                val grade = response.body()?.data
                if (grade != null) {
                    dataStoreManager.saveGrade(grade)
                }
                emit(Result.success(grade))
            } else {
                emit(Result.failure(Exception("Lỗi lấy thông tin lớp")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
}