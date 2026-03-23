package com.example.finalproject.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finalproject.data.remote.model.ReminderSettingRequest
import com.example.finalproject.data.remote.model.ReminderSettingResponse
import com.example.finalproject.domain.repository.ReminderRepository
import com.example.finalproject.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import android.content.Context
import androidx.work.WorkManager
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.Data
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Calendar
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class ReminderViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: ReminderRepository
) : ViewModel() {

    private val _settings = MutableStateFlow<ReminderSettingResponse?>(null)
    val settings = _settings.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    init {
        fetchSettings()
    }

    fun fetchSettings() {
        viewModelScope.launch {
            repository.getMySettings().collect { result ->
                when (result) {
                    is Resource.Loading -> _isLoading.value = true
                    is Resource.Success -> {
                        _isLoading.value = false
                        _settings.value = result.data
                        syncAlarms(result.data)
                    }
                    is Resource.Error -> {
                        _isLoading.value = false
                        _error.value = result.message
                    }
                }
            }
        }
    }

    fun updateSettings(request: ReminderSettingRequest) {
        viewModelScope.launch {
            repository.updateMySettings(request).collect { result ->
                when (result) {
                    is Resource.Loading -> _isLoading.value = true
                    is Resource.Success -> {
                        _isLoading.value = false
                        _settings.value = result.data
                        syncAlarms(result.data)
                    }
                    is Resource.Error -> {
                        _isLoading.value = false
                        _error.value = result.message
                    }
                }
            }
        }
    }

    private fun syncAlarms(settings: ReminderSettingResponse?) {
        val workManager = WorkManager.getInstance(context)
        if (settings == null) {
            workManager.cancelAllWork()
            return
        }

        if (settings.isStudyReminderEnabled && !settings.studyTime.isNullOrEmpty()) {
            val parts = settings.studyTime.split(":")
            if (parts.size == 2) {
                scheduleReminder(workManager, "daily", parts[0].toInt(), parts[1].toInt(), settings.studyDays)
            }
        } else {
            workManager.cancelUniqueWork("reminder_daily")
        }

        if (settings.isStreakReminderEnabled && !settings.streakTime.isNullOrEmpty()) {
            val parts = settings.streakTime.split(":")
            if (parts.size == 2) {
                scheduleReminder(workManager, "streak", parts[0].toInt(), parts[1].toInt(), null)
            }
        } else {
            workManager.cancelUniqueWork("reminder_streak")
        }

        if (settings.isGoalReminderEnabled && !settings.targetDate.isNullOrEmpty()) {
            scheduleReminder(workManager, "exam", 8, 0, null)
        } else {
            workManager.cancelUniqueWork("reminder_exam")
        }
    }

    private fun scheduleReminder(workManager: WorkManager, type: String, hour: Int, minute: Int, days: String?) {
        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
        }

        if (target.before(now)) {
            target.add(Calendar.DAY_OF_YEAR, 1)
        }

        val delay = target.timeInMillis - now.timeInMillis
        val dataBuilder = Data.Builder().putString("type", type)
        if (days != null) dataBuilder.putString("days", days)

        val request = PeriodicWorkRequestBuilder<com.example.finalproject.utils.ReminderWorker>(24, TimeUnit.HOURS)
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(dataBuilder.build())
            .build()

        workManager.enqueueUniquePeriodicWork(
            "reminder_$type",
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
    }
}
