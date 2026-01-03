package com.example.finalproject.ui.screens.class_selection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finalproject.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ClassSelectionState {
    object Idle : ClassSelectionState()
    object Loading : ClassSelectionState()
    object Success : ClassSelectionState()
    data class Error(val message: String) : ClassSelectionState()
}

@HiltViewModel
class ClassSelectionViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _state = MutableStateFlow<ClassSelectionState>(ClassSelectionState.Idle)
    val state = _state.asStateFlow()

    fun selectGrade(grade: Int) {
        viewModelScope.launch {
            _state.value = ClassSelectionState.Loading
            userRepository.updateGrade(grade).collect { result ->
                result.onSuccess {
                    _state.value = ClassSelectionState.Success
                }.onFailure {
                    _state.value = ClassSelectionState.Error(it.message ?: "Lỗi kết nối")
                }
            }
        }
    }
}