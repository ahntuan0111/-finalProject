package com.example.finalproject.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finalproject.data.remote.model.DashboardResponse
import com.example.finalproject.domain.repository.StatisticsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

import com.example.finalproject.data.remote.model.SubjectProgressDto

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val repository: StatisticsRepository,
    private val apiService: com.example.finalproject.data.remote.ApiService,
    private val dataStoreManager: com.example.finalproject.utils.DataStoreManager
) : ViewModel() {

    private val _dashboardData = MutableStateFlow<DashboardResponse?>(null)
    val dashboardData: StateFlow<DashboardResponse?> = _dashboardData.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        fetchDashboard()
    }

    fun fetchDashboard() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.getDashboardData()
                if (response.success) {
                    val dashboard = response.data
                    if (dashboard != null) {
                        try {
                            val grade = dataStoreManager.getGradeSync()
                            val progressResp = apiService.getMyProgress(grade)
                            
                            if (progressResp.success && progressResp.data != null) {
                                val realProgressTrack = progressResp.data.map {
                                    val isMath = it.subjectCode.contains("toan", ignoreCase = true)
                                    SubjectProgressDto(
                                        subjectName = if (isMath) "Toán học" else "Khoa học Tự nhiên",
                                        completedLessons = it.completedLessons,
                                        totalLessons = it.totalLessons,
                                        progressPercent = it.progressPercent,
                                        colorHex = if (isMath) "#2B58C4" else "#52C41A"
                                    )
                                }
                                _dashboardData.value = dashboard.copy(progressTrack = realProgressTrack)
                            } else {
                                _dashboardData.value = dashboard
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            _dashboardData.value = dashboard
                        }
                    } else {
                        _dashboardData.value = null
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
}