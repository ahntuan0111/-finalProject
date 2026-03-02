package com.example.finalproject.data.remote.model

data class QuizResponse(
    val id: Int,
    val code: String,
    val title: String,
    val chapterTitle: String,
    val quizTypeName: String,
    val questions: List<QuestionResponse>
)

data class QuestionResponse(
    val id: Long,
    val explanation: String?,
    val orderNo: Int,
    val contents: List<QuestionContentDto>,
    val choices: List<ChoiceDto>
)

data class QuestionContentDto(
    val id: Long,
    val contentType: String, // "TEXT" or "IMAGE"
    val contentValue: String,
    val contentOrder: Int
)

data class ChoiceDto(
    val id: Long,
    val contentType: String,
    val contentValue: String,
    val correct: Boolean
)

data class QuizSubmitRequest(
    val correctAnswers: Int,
    val totalQuestions: Int,
    val durationSeconds: Int
)