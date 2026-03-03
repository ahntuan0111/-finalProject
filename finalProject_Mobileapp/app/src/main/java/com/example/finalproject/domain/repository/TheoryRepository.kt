package com.example.finalproject.domain.repository

import com.example.finalproject.data.remote.model.ApiResponse
import com.example.finalproject.data.remote.model.LessonCompletionRequest
import com.example.finalproject.data.remote.model.ProgressResponse

interface TheoryRepository {
    suspend fun markLessonAsCompleted(request: LessonCompletionRequest): ApiResponse<Any>

}