package com.example.finalproject.domain.repository

import com.example.finalproject.data.remote.model.ApiResponse
import com.example.finalproject.data.remote.model.ProgressResponse
import com.example.finalproject.data.remote.model.SubjectOverviewResponse

interface ProgressRepository {

    /**
     * Lấy danh sách tiến trình học tập của người dùng theo Khối lớp (Grade)
     * @param grade Khối lớp hiện tại (Ví dụ: 6, 7, 8, 9)
     */
    suspend fun getMyProgress(grade: Int): ApiResponse<List<ProgressResponse>>

    suspend fun getSubjectOverview(subjectCode: String, grade: Int): ApiResponse<SubjectOverviewResponse>
}