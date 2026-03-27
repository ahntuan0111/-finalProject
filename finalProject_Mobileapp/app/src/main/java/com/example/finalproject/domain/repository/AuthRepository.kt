package com.example.finalproject.domain.repository

import com.example.finalproject.data.remote.model.AuthResponse
import com.example.finalproject.data.remote.model.LoginRequest
import com.example.finalproject.data.remote.model.RegisterRequest
import com.example.finalproject.data.remote.model.ResetPasswordRequest
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun login(request: LoginRequest): Flow<Result<AuthResponse>>
    suspend fun register(request: RegisterRequest): Flow<Result<AuthResponse>>

    suspend fun forgotPassword(email: String): Flow<Result<Unit>> // Dùng Unit vì chỉ cần biết thành công hay thất bại
    suspend fun resetPassword(request: ResetPasswordRequest): Flow<Result<Unit>>

    // OTP Registration
    suspend fun requestRegisterOtp(request: RegisterRequest): Flow<Result<Unit>>
    suspend fun verifyRegisterOtp(request: com.example.finalproject.data.remote.model.VerifyRegisterRequest): Flow<Result<AuthResponse>>
}