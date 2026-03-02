package com.example.finalproject.domain.repository

import com.example.finalproject.data.remote.model.ApiResponse
import com.example.finalproject.data.remote.model.QuizResponse
import com.example.finalproject.data.remote.model.QuizSubmitRequest

interface QuizRepository {
    suspend fun getQuizzesBySubjectAndGrade(subjectCode: String, grade: Int): ApiResponse<List<QuizResponse>>

    suspend fun submitQuiz(quizId: Int, request: QuizSubmitRequest): ApiResponse<Any>
}