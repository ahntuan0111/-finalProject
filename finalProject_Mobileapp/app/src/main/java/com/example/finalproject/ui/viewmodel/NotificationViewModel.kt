package com.example.finalproject.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finalproject.data.remote.ApiService
import com.example.finalproject.data.remote.model.NotificationDto
import com.example.finalproject.data.remote.model.BaseResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {

    private val _notifications = MutableStateFlow<List<NotificationDto>>(emptyList())
    val notifications: StateFlow<List<NotificationDto>> = _notifications.asStateFlow()

    private val _unreadCount = MutableStateFlow(0)
    val unreadCount: StateFlow<Int> = _unreadCount.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun fetchData() {
        fetchNotifications()
        fetchUnreadCount()
    }

    private fun fetchNotifications() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = apiService.getNotifications()
                if (response.isSuccessful) {
                    // Giả sử API trả về BaseResponse có field 'data'
                    _notifications.value = response.body()?.data ?: emptyList()
                }
            } catch (e: Exception) {
                // Xử lý lỗi
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun fetchUnreadCount() {
        viewModelScope.launch {
            try {
                val response = apiService.getUnreadNotificationCount()
                if (response.isSuccessful) {
                    _unreadCount.value = response.body()?.data ?: 0
                }
            } catch (e: Exception) {}
        }
    }

    fun markAsRead(id: Long) {
        viewModelScope.launch {
            try {
                // Update UI ngay lập tức để tạo cảm giác mượt (Optimistic Update)
                _notifications.value = _notifications.value.map {
                    if (it.id == id) it.copy(isRead = true) else it
                }
                if (_unreadCount.value > 0) _unreadCount.value -= 1

                // Gọi API ngầm dưới background
                apiService.markNotificationAsRead(id)
            } catch (e: Exception) {}
        }
    }

    fun markAllAsRead() {
        viewModelScope.launch {
            try {
                _notifications.value = _notifications.value.map { it.copy(isRead = true) }
                _unreadCount.value = 0
                apiService.markAllNotificationsAsRead()
            } catch (e: Exception) {}
        }
    }
}