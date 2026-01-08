package com.example.finalproject.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finalproject.utils.DataStoreManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val dataStoreManager: DataStoreManager
) : ViewModel() {

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
}