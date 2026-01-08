package com.example.finalproject.data.remote.model

import com.google.gson.annotations.SerializedName

// Model trả về từ API
data class ChapterDto(
    val id: Long,
    val title: String,
    @SerializedName("orderNo") val orderNo: Int,
    val lessons: List<LessonDto>? = emptyList() // List bài học bên trong
)

data class LessonDto(
    val id: Long,
    val title: String,
    @SerializedName("orderNo") val orderNo: Int,
    // videoUrl, contents... nếu cần
    val isCompleted: Boolean = false // Giả lập trạng thái đã học
)