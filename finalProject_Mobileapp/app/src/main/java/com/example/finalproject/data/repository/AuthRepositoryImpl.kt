package com.example.finalproject.data.repository

import com.example.finalproject.data.remote.ApiService
import com.example.finalproject.data.remote.model.ForgotPasswordRequest
import com.example.finalproject.data.remote.model.LoginRequest
import com.example.finalproject.data.remote.model.RegisterRequest
import com.example.finalproject.data.remote.model.ResetPasswordRequest
import com.example.finalproject.data.remote.model.VerifyRegisterRequest
import com.example.finalproject.domain.repository.AuthRepository
import com.example.finalproject.utils.AppLogger
import com.example.finalproject.utils.DataStoreManager
import com.example.finalproject.utils.ErrorParser
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val dataStoreManager: DataStoreManager
) : AuthRepository {

    companion object { private const val TAG = "AuthRepo" }

    override suspend fun login(request: LoginRequest) = flow {
        AppLogger.network(TAG, "login() → username=${request.username}")
        try {
            val response = apiService.login(request)
            if (response.isSuccessful && response.body() != null) {
                val authResponse = response.body()!!
                val token = authResponse.data?.token
                if (!token.isNullOrEmpty()) {
                    dataStoreManager.saveToken(token)
                    AppLogger.d(TAG, "Login OK — token saved")
                }
                emit(Result.success(authResponse))
            } else {
                val msg = ErrorParser.fromResponse(response, "Đăng nhập thất bại")
                AppLogger.w(TAG, "Login failed: $msg")
                emit(Result.failure(Exception(msg)))
            }
        } catch (e: Exception) {
            val msg = ErrorParser.fromException(e)
            emit(Result.failure(Exception(msg)))
        }
    }

    override suspend fun register(request: RegisterRequest) = flow {
        AppLogger.network(TAG, "register() → email=${request.email}")
        try {
            val response = apiService.register(request)
            if (response.isSuccessful && response.body() != null) {
                AppLogger.d(TAG, "Register OK")
                emit(Result.success(response.body()!!))
            } else {
                val msg = ErrorParser.fromResponse(response, "Đăng ký thất bại")
                AppLogger.w(TAG, "Register failed: $msg")
                emit(Result.failure(Exception(msg)))
            }
        } catch (e: Exception) {
            emit(Result.failure(Exception(ErrorParser.fromException(e))))
        }
    }

    override suspend fun forgotPassword(email: String) = flow {
        AppLogger.network(TAG, "forgotPassword() → email=$email")
        try {
            val response = apiService.forgotPassword(ForgotPasswordRequest(email))
            if (response.isSuccessful) {
                AppLogger.d(TAG, "ForgotPassword OK")
                emit(Result.success(Unit))
            } else {
                val msg = ErrorParser.fromResponse(response, "Gửi yêu cầu thất bại")
                AppLogger.w(TAG, "ForgotPassword failed: $msg")
                emit(Result.failure(Exception(msg)))
            }
        } catch (e: Exception) {
            emit(Result.failure(Exception(ErrorParser.fromException(e))))
        }
    }

    override suspend fun resetPassword(request: ResetPasswordRequest) = flow {
        AppLogger.network(TAG, "resetPassword() → email=${request.email}")
        try {
            val response = apiService.resetPassword(request)
            if (response.isSuccessful) {
                AppLogger.d(TAG, "ResetPassword OK")
                emit(Result.success(Unit))
            } else {
                val msg = ErrorParser.fromResponse(response, "Đặt lại mật khẩu thất bại")
                AppLogger.w(TAG, "ResetPassword failed: $msg")
                emit(Result.failure(Exception(msg)))
            }
        } catch (e: Exception) {
            emit(Result.failure(Exception(ErrorParser.fromException(e))))
        }
    }

    override suspend fun requestRegisterOtp(request: RegisterRequest) = flow {
        AppLogger.network(TAG, "requestRegisterOtp() → email=${request.email}")
        try {
            val response = apiService.requestRegisterOtp(request)
            if (response.isSuccessful) {
                AppLogger.d(TAG, "OTP sent OK")
                emit(Result.success(Unit))
            } else {
                val msg = ErrorParser.fromResponse(response, "Gửi mã xác thực thất bại")
                AppLogger.w(TAG, "OTP request failed: $msg")
                emit(Result.failure(Exception(msg)))
            }
        } catch (e: Exception) {
            emit(Result.failure(Exception(ErrorParser.fromException(e))))
        }
    }

    override suspend fun verifyRegisterOtp(request: VerifyRegisterRequest) = flow {
        AppLogger.network(TAG, "verifyRegisterOtp() → email=${request.email}")
        try {
            val response = apiService.verifyRegister(request)
            if (response.isSuccessful && response.body() != null) {
                val authResponse = response.body()!!
                val token = authResponse.data?.token
                if (!token.isNullOrEmpty()) {
                    dataStoreManager.saveToken(token)
                    AppLogger.d(TAG, "OTP verify OK — token saved")
                }
                emit(Result.success(authResponse))
            } else {
                val msg = ErrorParser.fromResponse(response, "Xác nhận OTP thất bại")
                AppLogger.w(TAG, "OTP verify failed: $msg")
                emit(Result.failure(Exception(msg)))
            }
        } catch (e: Exception) {
            emit(Result.failure(Exception(ErrorParser.fromException(e))))
        }
    }
}