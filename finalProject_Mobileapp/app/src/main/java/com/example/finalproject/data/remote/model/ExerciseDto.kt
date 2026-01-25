package com.example.finalproject.data.remote.model

import com.google.gson.annotations.SerializedName

data class ExerciseDto(
    val id: Long,
    val questionType: String, // "TEXT" hoặc "IMAGE"
    val questionValue: String,
    val orderNo: Int,
    val solutions: List<ExerciseSolutionDto> = emptyList() // Danh sách lời giải
)

data class ExerciseSolutionDto(
    val id: Long,
    val solutionType: String, // "TEXT" hoặc "IMAGE"
    val solutionValue: String,
    val solutionOrder: Int
)