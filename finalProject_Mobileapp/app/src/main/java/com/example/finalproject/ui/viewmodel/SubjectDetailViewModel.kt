package com.example.finalproject.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finalproject.data.remote.model.SubjectOverviewResponse
import com.example.finalproject.domain.repository.ProgressRepository
import com.example.finalproject.utils.DataStoreManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SubjectDetailViewModel @Inject constructor(
    private val progressRepository: ProgressRepository,
    dataStoreManager: DataStoreManager
) : ViewModel() {

    // Lấy grade hiện tại
    val currentGrade: StateFlow<Int> = dataStoreManager.getGrade()
        .map { it ?: 6 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 6)

    private val _overview = MutableStateFlow<SubjectOverviewResponse?>(null)
    val overview: StateFlow<SubjectOverviewResponse?> = _overview.asStateFlow()

    fun fetchOverview(subjectCode: String, grade: Int) {
        viewModelScope.launch {
            try {
                val response = progressRepository.getSubjectOverview(subjectCode, grade)
                if (response.success) {
                    _overview.value = response.data
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}