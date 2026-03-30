package com.example.finalproject.data.repository

import com.example.finalproject.data.remote.ApiService
import com.example.finalproject.data.remote.model.ApiResponse
import com.example.finalproject.data.remote.model.UpdateGradeRequest
import com.example.finalproject.data.remote.model.UserProfileResponse
import com.example.finalproject.data.remote.model.UserProfileUpdateRequest
import com.example.finalproject.domain.repository.UserRepository
import com.example.finalproject.utils.AppLogger
import com.example.finalproject.utils.DataStoreManager
import com.example.finalproject.utils.ErrorParser
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val dataStoreManager: DataStoreManager
) : UserRepository {

    companion object { private const val TAG = "UserRepo" }

    override suspend fun updateGrade(grade: Int) = flow {
        AppLogger.network(TAG, "updateGrade(grade=$grade)")
        try {
            val response = apiService.updateGrade(UpdateGradeRequest(grade))
            if (response.isSuccessful) {
                dataStoreManager.saveGrade(grade)
                AppLogger.d(TAG, "updateGrade OK — grade=$grade saved")
                emit(Result.success(Unit))
            } else {
                val msg = ErrorParser.fromResponse(response, "Cập nhật lớp không thành công")
                AppLogger.w(TAG, "updateGrade failed: $msg")
                emit(Result.failure(Exception(msg)))
            }
        } catch (e: Exception) {
            emit(Result.failure(Exception(ErrorParser.fromException(e))))
        }
    }

    override suspend fun fetchAndSaveCurrentGrade() = flow {
        AppLogger.network(TAG, "fetchAndSaveCurrentGrade()")
        try {
            val response = apiService.getCurrentGrade()
            if (response.isSuccessful) {
                val grade = response.body()?.data
                if (grade != null) {
                    dataStoreManager.saveGrade(grade)
                    AppLogger.d(TAG, "fetchAndSaveCurrentGrade OK — grade=$grade")
                }
                emit(Result.success(grade))
            } else {
                val msg = ErrorParser.fromResponse(response, "Lỗi lấy thông tin lớp")
                AppLogger.w(TAG, "fetchAndSaveCurrentGrade failed: $msg")
                emit(Result.failure(Exception(msg)))
            }
        } catch (e: Exception) {
            emit(Result.failure(Exception(ErrorParser.fromException(e))))
        }
    }

    override suspend fun getProfile(): ApiResponse<UserProfileResponse> {
        AppLogger.network(TAG, "getProfile()")
        return try {
            apiService.getProfile()
        } catch (e: Exception) {
            AppLogger.e(TAG, "getProfile exception", e)
            throw e
        }
    }

    override suspend fun updateProfile(request: UserProfileUpdateRequest): ApiResponse<UserProfileResponse> {
        AppLogger.network(TAG, "updateProfile()")
        return try {
            apiService.updateProfile(request)
        } catch (e: Exception) {
            AppLogger.e(TAG, "updateProfile exception", e)
            throw e
        }
    }
}