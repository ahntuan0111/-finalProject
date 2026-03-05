package com.example.finalproject.data.remote.model

import com.google.gson.annotations.SerializedName

data class DashboardResponse(
    @SerializedName("topStats") val topStats: TopStats,
    @SerializedName("progressTrack") val progressTrack: List<SubjectProgressDto>,
    @SerializedName("performanceChart") val performanceChart: List<ChartPointDto>,
    @SerializedName("aiPrediction") val aiPrediction: AiPredictionDto,
    @SerializedName("heatmap") val heatmap: List<HeatmapPointDto>
)

data class TopStats(
    val totalMinutes: Int,
    val currentStreak: Int
)

data class SubjectProgressDto(
    val subjectName: String,
    val completedLessons: Int,
    val totalLessons: Int,
    val progressPercent: Double,
    val colorHex: String
)

data class ChartPointDto(
    val dateLabel: String,
    val score: Double
)

data class AiPredictionDto(
    val predictedScore: Double,
    val targetMinutes: Int,
    val studiedMinutesThisWeek: Int
)

data class HeatmapPointDto(
    val date: String,
    val intensity: Int
)