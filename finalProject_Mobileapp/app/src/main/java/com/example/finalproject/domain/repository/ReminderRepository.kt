package com.example.finalproject.domain.repository

import com.example.finalproject.data.remote.model.ReminderSettingRequest
import com.example.finalproject.data.remote.model.ReminderSettingResponse
import com.example.finalproject.utils.Resource
import kotlinx.coroutines.flow.Flow

interface ReminderRepository {
    fun getMySettings(): Flow<Resource<ReminderSettingResponse>>
    fun updateMySettings(request: ReminderSettingRequest): Flow<Resource<ReminderSettingResponse>>
}
