package com.example.finalproject.data.remote.model

import com.google.gson.annotations.SerializedName

data class ReminderSettingRequest(
    val isStudyReminderEnabled: Boolean,
    val studyDays: String?,
    val studyTime: String?,
    
    val isStreakReminderEnabled: Boolean,
    val streakTime: String?,
    
    val isGoalReminderEnabled: Boolean,
    val targetDate: String?,
    val targetName: String?
)

data class ReminderSettingResponse(
    val id: Long?,
    val isStudyReminderEnabled: Boolean,
    val studyDays: String?,
    val studyTime: String?,
    
    val isStreakReminderEnabled: Boolean,
    val streakTime: String?,
    
    val isGoalReminderEnabled: Boolean,
    val targetDate: String?,
    val targetName: String?
)
