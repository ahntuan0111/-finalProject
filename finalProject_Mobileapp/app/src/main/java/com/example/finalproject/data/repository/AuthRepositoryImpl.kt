package com.example.finalproject.data.repository

import com.example.finalproject.data.remote.ApiService
import com.example.finalproject.data.remote.model.ForgotPasswordRequest
import com.example.finalproject.data.remote.model.LoginRequest
import com.example.finalproject.data.remote.model.RegisterRequest
import com.example.finalproject.data.remote.model.ResetPasswordRequest
import com.example.finalproject.domain.repository.AuthRepository
import com.example.finalproject.utils.DataStoreManager
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val dataStoreManager: DataStoreManager // Thay TokenManager bằng DataStoreManager
) : AuthRepository {

    override suspend fun login(request: LoginRequest) = flow {
        try {
            val response = apiService.login(request)

            if (response.isSuccessful && response.body() != null) {
                val authResponse = response.body()!! // Lấy body ra biến authResponse

                // 1. Lấy token từ response
                val token = authResponse.data?.token

                // 2. Lưu token vào DataStoreManager (QUAN TRỌNG: Để Interceptor đọc được)
                if (!token.isNullOrEmpty()) {
                    dataStoreManager.saveToken(token)
                }

                // 3. Trả về kết quả thành công
                emit(Result.success(authResponse))
            } else {
                // Xử lý lỗi từ server (ví dụ 401, 400)
                val errorMsg =
                    response.errorBody()?.string() ?: "Đăng nhập thất bại: ${response.code()}"
                emit(Result.failure(Exception(errorMsg)))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override suspend fun register(request: RegisterRequest) = flow {
        try {
            val response = apiService.register(request)
            if (response.isSuccessful && response.body() != null) {
                // Đăng ký thành công
                emit(Result.success(response.body()!!))
            } else {
                val errorMsg =
                    response.errorBody()?.string() ?: "Đăng ký thất bại: ${response.code()}"
                emit(Result.failure(Exception(errorMsg)))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override suspend fun forgotPassword(email: String) = flow {
        try {
            val response = apiService.forgotPassword(ForgotPasswordRequest(email))
            if (response.isSuccessful) {
                emit(Result.success(Unit))
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Gửi yêu cầu thất bại"
                emit(Result.failure(Exception(errorMsg)))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override suspend fun resetPassword(request: ResetPasswordRequest) = flow {
        try {
            val response = apiService.resetPassword(request)
            if (response.isSuccessful) {
                emit(Result.success(Unit))
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Đặt lại mật khẩu thất bại"
                emit(Result.failure(Exception(errorMsg)))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override suspend fun requestRegisterOtp(request: RegisterRequest) = flow {
        try {
            val response = apiService.requestRegisterOtp(request)
            if (response.isSuccessful) {
                emit(Result.success(Unit))
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Gửi mã xác thực thất bại"
                emit(Result.failure(Exception(errorMsg)))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override suspend fun verifyRegisterOtp(request: com.example.finalproject.data.remote.model.VerifyRegisterRequest) = flow {
        try {
            val response = apiService.verifyRegister(request)
            if (response.isSuccessful && response.body() != null) {
                val authResponse = response.body()!!
                val token = authResponse.data?.token
                if (!token.isNullOrEmpty()) {
                    dataStoreManager.saveToken(token)
                }
                emit(Result.success(authResponse))
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Xác nhận OTP thất bại"
                emit(Result.failure(Exception(errorMsg)))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
}