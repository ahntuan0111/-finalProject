package com.example.finalproject.domain.repository

import com.example.finalproject.data.remote.model.ApiResponse
import com.example.finalproject.data.remote.model.UserProfileResponse
import com.example.finalproject.data.remote.model.UserProfileUpdateRequest
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun updateGrade(grade: Int): Flow<Result<Unit>>
    suspend fun fetchAndSaveCurrentGrade(): Flow<Result<Int?>> // Trả về lớp (hoặc null)

    suspend fun getProfile(): ApiResponse<UserProfileResponse>
    suspend fun updateProfile(request: UserProfileUpdateRequest): ApiResponse<UserProfileResponse>
}