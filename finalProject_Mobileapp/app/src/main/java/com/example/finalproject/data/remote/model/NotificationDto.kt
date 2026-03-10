package com.example.finalproject.data.remote.model

import com.google.gson.annotations.SerializedName

data class NotificationDto(
    val id: Long,
    val title: String,
    val message: String,
    val type: String, // REMINDER, GAMIFICATION, SYSTEM
    @SerializedName("read") val isRead: Boolean,
    val actionUrl: String?,
    val createdAt: String
)