package com.example.finalproject.data.remote.model

data class LessonCompletionRequest(
    val lessonId: Long,
    val type: String // Sẽ truyền vào "THEORY" hoặc "EXERCISE"
)