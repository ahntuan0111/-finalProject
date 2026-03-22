package com.example.finalproject.ui.viewmodel

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finalproject.data.remote.model.UserProfileResponse
import com.example.finalproject.data.remote.model.UserProfileUpdateRequest
import com.example.finalproject.domain.repository.UserRepository
import com.example.finalproject.utils.DataStoreManager
import androidx.core.os.LocaleListCompat
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
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

    // ĐÃ SỬA LỖI: Dùng map { it ?: 6 } để đảm bảo luôn trả về Int thay vì Int?
    val currentGrade: StateFlow<Int> = dataStoreManager.getGrade()
        .map { it ?: 6 }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 6
        )


    init {
        fetchProfile()
    }

    val currentLanguage = dataStoreManager.getLanguageFlow().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = "vi"
    )

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

    fun saveGrade(newGrade: Int) {
        viewModelScope.launch {
            dataStoreManager.saveGrade(newGrade)
        }
    }

    fun changeLanguage(langCode: String) {
        viewModelScope.launch {
            // 1. Lưu cấu hình vào DataStore
            dataStoreManager.saveLanguage(langCode)

            // 2. Ép Android đổi ngôn ngữ UI (Cần Android 13+ hoặc thư viện AppCompat)
            val appLocale = LocaleListCompat.forLanguageTags(langCode)
            AppCompatDelegate.setApplicationLocales(appLocale)
        }
    }

    val isDarkTheme = dataStoreManager.getDarkThemeFlow().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null // Trả về null để UI dùng `isSystemInDarkTheme()` làm fallback
    )

    fun toggleDarkMode(isDark: Boolean) {
        viewModelScope.launch {
            dataStoreManager.saveTheme(isDark)
        }
    }
}