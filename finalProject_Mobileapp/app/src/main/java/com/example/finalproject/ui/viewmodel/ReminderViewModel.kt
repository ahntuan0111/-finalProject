package com.example.finalproject.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Data
import com.example.finalproject.utils.DataStoreManager
import com.example.finalproject.utils.ReminderWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class ReminderViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dataStoreManager: DataStoreManager
) : ViewModel() {

    private val workManager = WorkManager.getInstance(context)

    val dailyEnabled = dataStoreManager.getReminderDailyEnabledFlow()
        .stateIn(viewModelScope, SharingStarted.Lazily, false)
    val dailyTime = dataStoreManager.getReminderDailyTimeFlow()
        .stateIn(viewModelScope, SharingStarted.Lazily, Pair(7, 30))

    val streakEnabled = dataStoreManager.getReminderStreakEnabledFlow()
        .stateIn(viewModelScope, SharingStarted.Lazily, false)
    val streakTime = dataStoreManager.getReminderStreakTimeFlow()
        .stateIn(viewModelScope, SharingStarted.Lazily, Pair(21, 0))

    val examEnabled = dataStoreManager.getReminderExamEnabledFlow()
        .stateIn(viewModelScope, SharingStarted.Lazily, false)

    fun toggleDaily(enabled: Boolean, hour: Int, minute: Int) {
        viewModelScope.launch {
            dataStoreManager.saveReminderDailyEnabled(enabled)
            dataStoreManager.saveReminderDailyTime(hour, minute)
            if (enabled) scheduleReminder("daily", hour, minute)
            else workManager.cancelUniqueWork("reminder_daily")
        }
    }

    fun toggleStreak(enabled: Boolean, hour: Int, minute: Int) {
        viewModelScope.launch {
            dataStoreManager.saveReminderStreakEnabled(enabled)
            dataStoreManager.saveReminderStreakTime(hour, minute)
            if (enabled) scheduleReminder("streak", hour, minute)
            else workManager.cancelUniqueWork("reminder_streak")
        }
    }

    fun toggleExam(enabled: Boolean) {
        viewModelScope.launch {
            dataStoreManager.saveReminderExamEnabled(enabled)
            if (enabled) scheduleReminder("exam", 8, 0) // Giả định nhắc vào 8h sáng
            else workManager.cancelUniqueWork("reminder_exam")
        }
    }

    private fun scheduleReminder(type: String, hour: Int, minute: Int) {
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
        val data = Data.Builder().putString("type", type).build()

        val request = PeriodicWorkRequestBuilder<ReminderWorker>(24, TimeUnit.HOURS)
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(data)
            .build()

        workManager.enqueueUniquePeriodicWork(
            "reminder_$type",
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
    }
}
