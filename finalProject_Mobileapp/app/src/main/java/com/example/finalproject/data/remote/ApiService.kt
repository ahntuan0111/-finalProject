package com.example.finalproject.data.remote

import com.example.finalproject.data.remote.model.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    // Login trả về Token -> Dùng AuthResponse (hoặc ApiResponse<TokenData>)
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    // Register: Nếu BE trả về Token để login luôn -> Dùng AuthResponse
    // Nếu BE chỉ trả về text "Đăng ký thành công" -> Dùng ApiResponse<String>
    // Ở đây tôi giữ AuthResponse theo code cũ của bạn
    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    // Forgot Password trả về message -> ApiResponse<String>
    @POST("auth/forgot-password")
    suspend fun forgotPassword(@Body request: ForgotPasswordRequest): Response<ApiResponse<String>>

    // Reset Password trả về message -> ApiResponse<String>
    @POST("auth/reset-password")
    suspend fun resetPassword(@Body request: ResetPasswordRequest): Response<ApiResponse<String>>

    // Update Grade trả về message hoặc User object mới -> ApiResponse<String>
    @POST("users/update-grade")
    suspend fun updateGrade(@Body request: UpdateGradeRequest): Response<ApiResponse<String>>

    // Get Current Grade trả về số Int (có thể null) -> ApiResponse<Int?>
    @GET("users/current-grade")
    suspend fun getCurrentGrade(): Response<ApiResponse<Int?>>

    @GET("student/subjects/{subjectId}/chapters")
    suspend fun getChaptersBySubject(
        @Path("subjectId") subjectId: Int
    ): Response<ApiResponse<List<ChapterDto>>>

    @GET("student/chapters")
    suspend fun getChaptersBySubject(
        @Query("code") code: String, // "toan", "khtn"
        @Query("grade") grade: Int   // 7
    ): Response<ApiResponse<List<ChapterDto>>>

    // Lấy chi tiết nội dung bài học
    @GET("student/lessons/{id}")
    suspend fun getLessonDetail(
        @Path("id") lessonId: Long
    ): Response<ApiResponse<LessonDetailResponse>>

    // Thêm API call này vào interface ApiService
    @GET("exercises/by-lesson/{lessonId}")
    suspend fun getExercisesByLesson(@Path("lessonId") lessonId: Long): ApiResponse<List<ExerciseDto>>

    @GET("documents")
    suspend fun getDocuments(
        @Query("subjectCode") subjectCode: String,
        @Query("grade") grade: Int
    ): Response<ApiResponse<List<DocumentDto>>>


}