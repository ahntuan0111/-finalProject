package com.example.finalproject.data.remote.model

data class UpdateGradeRequest(
    val grade: Int
)

data class UserResponse(
    val id: Long,
    val username: String,
    val email: String,
    val grade: Int? // Có thể null nếu chưa chọn
)

data class UserProfileResponse(
    val id: Long,
    val username: String,
    val email: String,
    val avatarUrl: String?,
    val dateOfBirth: String? // Trả về dạng YYYY-MM-DD
)

data class UserProfileUpdateRequest(
    val avatarUrl: String?,
    val dateOfBirth: String?
)