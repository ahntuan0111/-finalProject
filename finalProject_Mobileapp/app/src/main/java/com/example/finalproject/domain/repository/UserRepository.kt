package com.example.finalproject.domain.repository

import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun updateGrade(grade: Int): Flow<Result<Unit>>
    suspend fun fetchAndSaveCurrentGrade(): Flow<Result<Int?>> // Trả về lớp (hoặc null)
}