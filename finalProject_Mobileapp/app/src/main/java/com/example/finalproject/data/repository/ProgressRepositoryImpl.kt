package com.example.finalproject.data.repository

import com.example.finalproject.data.remote.ApiService
import com.example.finalproject.data.remote.model.ApiResponse
import com.example.finalproject.data.remote.model.ProgressResponse
import com.example.finalproject.domain.repository.ProgressRepository
import javax.inject.Inject

class ProgressRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : ProgressRepository {

    override suspend fun getMyProgress(grade: Int): ApiResponse<List<ProgressResponse>> {
        // Gọi ApiService và truyền grade vào
        return apiService.getMyProgress(grade)
    }
}