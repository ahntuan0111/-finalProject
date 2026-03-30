package com.example.finalproject.data.remote

import com.example.finalproject.data.remote.model.*
import com.example.finalproject.data.remote.model.BaseResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
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

    @POST("auth/register-otp")
    suspend fun requestRegisterOtp(@Body request: RegisterRequest): Response<ApiResponse<String>>

    @POST("auth/verify-register")
    suspend fun verifyRegister(@Body request: VerifyRegisterRequest): Response<AuthResponse>

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

    @POST("exercises/{exerciseId}/validate-image")
    suspend fun validateImage(
        @Path("exerciseId") exerciseId: Long,
        @Body request: ValidationSubmissionRequest
    ): ApiResponse<ValidationSubmissionResponse>

    @POST("exercises/{exerciseId}/save-submission")
    suspend fun saveSubmission(
        @Path("exerciseId") exerciseId: Long,
        @Body request: SaveSubmissionRequest
    ): ApiResponse<Any>

    @GET("exercises/{exerciseId}/upload-url")
    suspend fun getUploadUrl(
        @Path("exerciseId") exerciseId: Long
    ): ApiResponse<UploadUrlResponse>

    @GET("documents")
    suspend fun getDocuments(
        @Query("subjectCode") subjectCode: String,
        @Query("grade") grade: Int
    ): Response<ApiResponse<List<DocumentDto>>>

    @GET("quizzes") // Đổi lại đúng endpoint lấy danh sách quiz cho user
    suspend fun getAllQuizzes(): ApiResponse<List<QuizResponse>>

    @GET("quizzes/filter")
    suspend fun getQuizzesBySubjectAndGrade(
        @Query("subjectCode") subjectCode: String,
        @Query("grade") grade: Int
    ): ApiResponse<List<QuizResponse>>

    @POST("quizzes/{quizId}/submit")
    suspend fun submitQuiz(
        @Path("quizId") quizId: Int,
        @Body request: QuizSubmitRequest
    ): ApiResponse<Any>

    @GET("users/profile")
    suspend fun getProfile(): ApiResponse<UserProfileResponse>

    @PUT("users/profile")
    suspend fun updateProfile(@Body request: UserProfileUpdateRequest): ApiResponse<UserProfileResponse>

    @POST("progress/mark-complete")
    suspend fun markLessonAsCompleted(
        @Body request: LessonCompletionRequest
    ): ApiResponse<Any> // Trả về Any hoặc kiểu object tương ứng của bạn

    @GET("progress/my-progress")
    suspend fun getMyProgress(
        @Query("grade") grade: Int
    ): ApiResponse<List<ProgressResponse>>

    @GET("progress/overview")
    suspend fun getSubjectOverview(
        @Query("subjectCode") subjectCode: String,
        @Query("grade") grade: Int
    ): ApiResponse<SubjectOverviewResponse>

    @GET("statistics/dashboard")
    suspend fun getDashboardData(): ApiResponse<DashboardResponse>

    // Trong interface ApiService của bạn, thêm hàm sau:
    @POST("users/update-fcm-token") // Đổi lại URL cho khớp với mapping trong UserController của bạn
    suspend fun updateFcmToken(@Body request: Map<String, String>): Response<Any>
// Lưu ý: Response<Any> hoặc Response<BaseResponse<Any>> tuỳ thuộc vào cấu trúc trả về của BE bạn

    @GET("notifications")
    suspend fun getNotifications(): Response<BaseResponse<List<NotificationDto>>>

    @GET("notifications/unread-count")
    suspend fun getUnreadNotificationCount(): Response<BaseResponse<Int>>

    @PUT("notifications/{id}/read")
    suspend fun markNotificationAsRead(@Path("id") id: Long): Response<BaseResponse<Any>>

    @PUT("notifications/read-all")
    suspend fun markAllNotificationsAsRead(): Response<BaseResponse<Any>>

    @GET("student/search")
    suspend fun searchContent(
        @Query("keyword") keyword: String,
        @Query("grade") grade: Int
    ): Response<ApiResponse<List<SearchResponse>>>
}