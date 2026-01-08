package com.example.finalproject.data.remote.model

import com.google.gson.annotations.SerializedName

data class LessonDetailResponse(
    val id: Long,
    val title: String,
    val videoUrl: String?,
    @SerializedName("orderNo") val orderNo: Int,
    val contents: List<LessonContentDto>
)

data class LessonContentDto(
    val id: Long,
    val contentType: String, // "TEXT", "IMAGE"
    val contentValue: String, // Chứa HTML hoặc LaTeX: "$$ x^2 $$"
    val contentOrder: Int
)