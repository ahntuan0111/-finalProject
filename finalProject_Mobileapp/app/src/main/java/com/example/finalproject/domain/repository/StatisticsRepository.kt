package com.example.finalproject.domain.repository

import com.example.finalproject.data.remote.model.ApiResponse
import com.example.finalproject.data.remote.model.DashboardResponse

interface StatisticsRepository {
    suspend fun getDashboardData(): ApiResponse<DashboardResponse>
}