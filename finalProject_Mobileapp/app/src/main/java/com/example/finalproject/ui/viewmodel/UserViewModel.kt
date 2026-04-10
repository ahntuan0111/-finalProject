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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val dataStoreManager: DataStoreManager
) : ViewModel() {

    var profile = mutableStateOf<UserProfileResponse?>(null)
        private set

    var isLoading = mutableStateOf(false)
        private set

    private val _events = MutableSharedFlow<UserEvent>()
    val events: SharedFlow<UserEvent> = _events.asSharedFlow()

    sealed class UserEvent {
        data class ShowMessage(val message: String) : UserEvent()
        object UpdateSuccess : UserEvent()
    }

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
                _events.emit(UserEvent.ShowMessage("Lỗi tải thông tin: ${e.message}"))
            } finally {
                isLoading.value = false
            }
        }
    }

    fun updateProfile(avatarUrlOrUri: String?, dateOfBirth: String?) {
        viewModelScope.launch {
            isLoading.value = true
            try {
                var finalAvatarUrl: String? = avatarUrlOrUri
                
                // Nếu avatar là URI content:// (từ thiết bị), tiến hành upload lên Firebase
                if (avatarUrlOrUri != null && avatarUrlOrUri.startsWith("content://")) {
                    _events.emit(UserEvent.ShowMessage("Đang tải ảnh lên..."))
                    val uri = android.net.Uri.parse(avatarUrlOrUri)
                    val downloadUrl = com.example.finalproject.utils.FirebaseStorageHelper.uploadAvatar(uri)
                    if (downloadUrl != null) {
                        finalAvatarUrl = downloadUrl
                    } else {
                        _events.emit(UserEvent.ShowMessage("Lỗi tải ảnh. Sẽ giữ lại ảnh cũ."))
                        finalAvatarUrl = profile.value?.avatarUrl
                    }
                }

                val request = UserProfileUpdateRequest(finalAvatarUrl, dateOfBirth)
                val response = userRepository.updateProfile(request)
                if (response.success) {
                    profile.value = response.data
                    _events.emit(UserEvent.UpdateSuccess)
                    _events.emit(UserEvent.ShowMessage("Cập nhật hồ sơ thành công!"))
                } else {
                    _events.emit(UserEvent.ShowMessage("Cập nhật thất bại: ${response.message}"))
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _events.emit(UserEvent.ShowMessage("Lỗi hệ thống: ${e.message}"))
            } finally {
                isLoading.value = false
            }
        }
    }

    fun saveGrade(newGrade: Int) {
        viewModelScope.launch {
            dataStoreManager.saveGrade(newGrade)
            _events.emit(UserEvent.ShowMessage("Đã chuyển sang Lớp $newGrade"))
        }
    }

    // ── LANGUAGE CHANGE (safe recreation flag) ───────────────────────────
    // KHÔNG gọi AppCompatDelegate trực tiếp trong Composable/Coroutine vì
    // setApplicationLocales() sẽ trigger Activity.recreate() ngay lập tức,
    // destroy NavController và xóa toàn bộ back stack.
    // Thay vào đó: lưu locale vào DataStore → emit flag → MainActivity quan sát
    // và gọi recreate() an toàn SAU KHI composition kết thúc.
    private val _needsRecreate = MutableStateFlow(false)
    val needsRecreate: StateFlow<Boolean> = _needsRecreate.asStateFlow()

    fun changeLanguage(langCode: String) {
        viewModelScope.launch {
            // 1. Lưu ngôn ngữ vào DataStore
            dataStoreManager.saveLanguage(langCode)

            // 2. Áp dụng locale ngay (không gây recreate trên Android 13+ Grammatical Inflection)
            val appLocale = LocaleListCompat.forLanguageTags(langCode)
            AppCompatDelegate.setApplicationLocales(appLocale)
            // AppCompatDelegate.setApplicationLocales tự xử lý recreate ở API 33+
            // Trên API < 33 nó cũng tự recreate qua AppLocalesStorageHelper
            // → Không cần gọi thêm gì, nhưng ta cần đảm bảo startDestination
            // sau recreate vẫn trỏ về HOME chứ không phải WELCOME.

            _events.emit(
                UserEvent.ShowMessage(
                    if (langCode == "vi") "Đã đổi sang Tiếng Việt" else "Switched to English"
                )
            )
        }
    }

    fun onRecreateHandled() {
        _needsRecreate.value = false
    }

    fun logout() {
        viewModelScope.launch {
            dataStoreManager.clearToken()
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