package com.example.finalproject.data.repository

import com.example.finalproject.data.remote.ApiService
import com.example.finalproject.data.remote.model.ApiResponse
import com.example.finalproject.data.remote.model.LessonCompletionRequest
import com.example.finalproject.domain.repository.TheoryRepository
import javax.inject.Inject

class TheoryRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : TheoryRepository {
    override suspend fun markLessonAsCompleted(request: LessonCompletionRequest): ApiResponse<Any> {
        return apiService.markLessonAsCompleted(request)
    }
}