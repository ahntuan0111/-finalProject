package com.example.finalproject.ui.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finalproject.data.remote.model.UserProfileResponse
import com.example.finalproject.data.remote.model.UserProfileUpdateRequest
import com.example.finalproject.domain.repository.UserRepository
import com.example.finalproject.utils.DataStoreManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val dataStoreManager: DataStoreManager
) : ViewModel() {
    var profile = mutableStateOf<UserProfileResponse?>(null)
        private set

    var isLoading = mutableStateOf(false)
        private set

    init {
        fetchProfile()
    }
    // Tự động theo dõi Grade từ DataStore
    // Nếu DataStore đổi, biến này tự cập nhật theo
    val userGrade: StateFlow<Int?> = dataStoreManager.getGrade()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    // Tương tự cho UserName nếu cần
    // val userName = ...

    fun fetchProfile() {
        viewModelScope.launch {
            isLoading.value = true
            try {
                val response = userRepository.getProfile()
                if (response.success) profile.value = response.data
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isLoading.value = false
            }
        }
    }

    fun updateProfile(avatarUrl: String?, dateOfBirth: String?) {
        viewModelScope.launch {
            isLoading.value = true
            try {
                val request = UserProfileUpdateRequest(avatarUrl, dateOfBirth)
                val response = userRepository.updateProfile(request)
                if (response.success) profile.value = response.data
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isLoading.value = false
            }
        }
    }
}