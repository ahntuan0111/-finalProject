package com.example.finalproject.data.remote.api

import com.example.finalproject.data.remote.model.ApiResponse
import com.example.finalproject.data.remote.model.ReminderSettingRequest
import com.example.finalproject.data.remote.model.ReminderSettingResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT

interface ReminderApiService {
    @GET("users/reminders")
    suspend fun getMySettings(): ApiResponse<ReminderSettingResponse>

    @PUT("users/reminders")
    suspend fun updateMySettings(@Body request: ReminderSettingRequest): ApiResponse<ReminderSettingResponse>
}
