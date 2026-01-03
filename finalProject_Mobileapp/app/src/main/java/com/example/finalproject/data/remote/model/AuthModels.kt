package com.example.finalproject.data.remote.model

import com.google.gson.annotations.SerializedName

// --- 1. Class Generic dùng chung cho toàn bộ App (QUAN TRỌNG) ---
// Class này sẽ hứng JSON dạng: { "success": true, "message": "...", "data": ... }
data class ApiResponse<T>(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: T?
)

// --- 2. Các Model Request ---

data class LoginRequest(
    @SerializedName("username") val username: String,
    @SerializedName("password") val password: String
)

data class RegisterRequest(
    @SerializedName("username") val username: String,
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)

data class ForgotPasswordRequest(
    val email: String
)

data class ResetPasswordRequest(
    val email: String,
    val otp: String,
    val newPassword: String
)

// --- 3. Model Data ---

// Đây là data bên trong của phản hồi đăng nhập
data class TokenData(
    @SerializedName("token") val token: String,
    @SerializedName("type") val type: String = "Bearer",
    @SerializedName("username") val username: String,
    @SerializedName("role") val role: String,
    // Thêm các trường khác nếu cần, ví dụ: id, grade
    @SerializedName("id") val id: Long? = null,
    @SerializedName("grade") val grade: Int? = null
)

// --- 4. AuthResponse (Wrapper cũ) ---
// Bạn có thể giữ class này nếu code Repository cũ đang dùng nó.
// Tuy nhiên, bản chất nó chính là ApiResponse<TokenData>
data class AuthResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: TokenData?
)