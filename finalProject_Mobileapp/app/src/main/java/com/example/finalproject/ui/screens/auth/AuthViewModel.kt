package com.example.finalproject.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finalproject.data.remote.model.AuthResponse
import com.example.finalproject.data.remote.model.LoginRequest
import com.example.finalproject.data.remote.model.RegisterRequest
import com.example.finalproject.data.remote.model.ResetPasswordRequest
import com.example.finalproject.domain.repository.AuthRepository
import com.example.finalproject.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// Định nghĩa các trạng thái
sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Error(val message: String) : AuthState()

    // State dùng cho Register/Forgot/Reset (Chỉ cần biết thành công)
    data class Success(val data: AuthResponse?) : AuthState()

    // State đặc biệt cho Login (Cần biết đã có lớp hay chưa)
    data class LoginSuccess(val hasGrade: Boolean) : AuthState()
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    // --- STATES ---
    private val _loginState = MutableStateFlow<AuthState>(AuthState.Idle)
    val loginState = _loginState.asStateFlow()

    private val _registerState = MutableStateFlow<AuthState>(AuthState.Idle)
    val registerState = _registerState.asStateFlow()

    private val _forgotPassState = MutableStateFlow<AuthState>(AuthState.Idle)
    val forgotPassState = _forgotPassState.asStateFlow()

    private val _resetPassState = MutableStateFlow<AuthState>(AuthState.Idle)
    val resetPassState = _resetPassState.asStateFlow()

    // --- LOGIN FLOW ---
    fun login(username: String, pass: String) {
        viewModelScope.launch {
            _loginState.value = AuthState.Loading
            authRepository.login(LoginRequest(username, pass)).collect { result ->
                result.onSuccess {
                    checkUserGrade()
                }.onFailure {
                    _loginState.value = AuthState.Error(it.message ?: "Lỗi đăng nhập")
                }
            }
        }
    }

    private fun checkUserGrade() {
        viewModelScope.launch {
            userRepository.fetchAndSaveCurrentGrade().collect { result ->
                result.onSuccess { grade ->
                    _loginState.value = AuthState.LoginSuccess(hasGrade = grade != null)
                }.onFailure {
                    _loginState.value = AuthState.LoginSuccess(hasGrade = false)
                }
            }
        }
    }

    // --- REGISTER FLOW ---
    fun register(username: String, email: String, pass: String) {
        viewModelScope.launch {
            _registerState.value = AuthState.Loading
            authRepository.register(RegisterRequest(username, email, pass)).collect { result ->
                result.onSuccess {
                    _registerState.value = AuthState.Success(it)
                }.onFailure {
                    _registerState.value = AuthState.Error(it.message ?: "Đăng ký thất bại")
                }
            }
        }
    }

    // --- FORGOT PASSWORD ---
    fun forgotPassword(email: String) {
        viewModelScope.launch {
            _forgotPassState.value = AuthState.Loading
            authRepository.forgotPassword(email).collect { result ->
                result.onSuccess {
                    _forgotPassState.value = AuthState.Success(null)
                }.onFailure {
                    _forgotPassState.value = AuthState.Error(it.message ?: "Lỗi gửi yêu cầu")
                }
            }
        }
    }

    // --- RESET PASSWORD ---
    fun resetPassword(email: String, otp: String, newPass: String) {
        viewModelScope.launch {
            _resetPassState.value = AuthState.Loading
            authRepository.resetPassword(ResetPasswordRequest(email, otp, newPass)).collect { result ->
                result.onSuccess {
                    _resetPassState.value = AuthState.Success(null)
                }.onFailure {
                    _resetPassState.value = AuthState.Error(it.message ?: "Lỗi đặt lại mật khẩu")
                }
            }
        }
    }

    // Reset toàn bộ State (Dùng khi logout hoặc rời màn hình auth chính)
    fun resetState() {
        _loginState.value = AuthState.Idle
        _registerState.value = AuthState.Idle
        _forgotPassState.value = AuthState.Idle
        _resetPassState.value = AuthState.Idle
    }

    // --- THÊM HÀM NÀY ĐỂ SỬA LỖI ---
    // Reset riêng state của luồng Forgot/Reset Password
    fun resetForgotStates() {
        _forgotPassState.value = AuthState.Idle
        _resetPassState.value = AuthState.Idle
    }
}