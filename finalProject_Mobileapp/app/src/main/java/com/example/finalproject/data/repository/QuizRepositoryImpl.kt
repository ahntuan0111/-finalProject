package com.example.finalproject.data.repository

import com.example.finalproject.data.remote.ApiService
import com.example.finalproject.data.remote.model.ApiResponse
import com.example.finalproject.data.remote.model.QuizResponse
import com.example.finalproject.data.remote.model.QuizSubmitRequest
import com.example.finalproject.domain.repository.QuizRepository
import javax.inject.Inject

class QuizRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : QuizRepository {

    override suspend fun getQuizzesBySubjectAndGrade(
        subjectCode: String,
        grade: Int
    ): ApiResponse<List<QuizResponse>> {
        return apiService.getQuizzesBySubjectAndGrade(subjectCode, grade)
    }

    override suspend fun submitQuiz(
        quizId: Int,
        request: QuizSubmitRequest
    ): ApiResponse<Any> {
        return apiService.submitQuiz(quizId, request)
    }
}