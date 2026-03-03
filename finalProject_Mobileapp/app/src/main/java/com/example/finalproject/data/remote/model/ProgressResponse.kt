package com.example.finalproject.data.remote.model

data class ProgressResponse(
    val subjectId: Int,
    val subjectCode: String, // "toan", "khtn"...
    val gradeId: Int,        // Thêm trường này để phân biệt khối lớp
    val completedLessons: Int,
    val totalLessons: Int,
    val progressPercent: Double
)