package com.example.finalproject.data.repository

import com.example.finalproject.data.remote.ApiService
import com.example.finalproject.data.remote.model.ApiResponse
import com.example.finalproject.data.remote.model.DashboardResponse
import com.example.finalproject.domain.repository.StatisticsRepository // Nhớ import Interface này
import javax.inject.Inject

class StatisticsRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : StatisticsRepository {
    override suspend fun getDashboardData(): ApiResponse<DashboardResponse> {
        return apiService.getDashboardData()
    }
}