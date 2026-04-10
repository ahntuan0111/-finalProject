package com.example.finalproject.data.remote.model

data class SubjectOverviewResponse(
    val progressPercent: Double,
    val nextLessonId: Long,
    val totalChapters: Int = 0,
    val totalLessons: Int = 0
)
