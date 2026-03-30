package com.example.finalproject.data.remote.model

data class SearchResponse(
    val id: Long,
    val titleVi: String,
    val titleEn: String?,
    val type: String, // "LESSON", "QUIZ", "DOCUMENT"
    val subjectCode: String,
    val chapterId: Long?
)

enum class SearchResultType {
    LESSON, EXERCISE, QUIZ, DOCUMENT
}
