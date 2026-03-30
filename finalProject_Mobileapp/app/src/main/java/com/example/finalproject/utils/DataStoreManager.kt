package com.example.finalproject.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_settings")

@Singleton
class DataStoreManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    companion object {
        val GRADE_KEY = intPreferencesKey("USER_GRADE")
        val TOKEN_KEY = stringPreferencesKey("USER_TOKEN")
        val LANGUAGE_KEY = stringPreferencesKey("USER_LANGUAGE") // THÊM KEY NGÔN NGỮ
        val THEME_KEY = androidx.datastore.preferences.core.booleanPreferencesKey("USER_THEME")

        // REMINDER KEYS
        val REMINDER_DAILY_ENABLED = androidx.datastore.preferences.core.booleanPreferencesKey("reminder_daily_enabled")
        val REMINDER_DAILY_HOUR = intPreferencesKey("reminder_daily_hour")
        val REMINDER_DAILY_MINUTE = intPreferencesKey("reminder_daily_minute")
        val REMINDER_STREAK_ENABLED = androidx.datastore.preferences.core.booleanPreferencesKey("reminder_streak_enabled")
        val REMINDER_STREAK_HOUR = intPreferencesKey("reminder_streak_hour")
        val REMINDER_STREAK_MINUTE = intPreferencesKey("reminder_streak_minute")
        val REMINDER_EXAM_ENABLED = androidx.datastore.preferences.core.booleanPreferencesKey("reminder_exam_enabled")

        // SEARCH HISTORY
        val SEARCH_HISTORY_KEY = stringPreferencesKey("search_history")
    }

    // --- Xử lý Grade ---
    suspend fun saveGrade(grade: Int) {
        context.dataStore.edit { it[GRADE_KEY] = grade }
    }

    fun getGrade(): Flow<Int?> {
        return context.dataStore.data.map { it[GRADE_KEY] }
    }

    suspend fun getGradeSync(): Int {
        val preferences = context.dataStore.data.first()
        return preferences[GRADE_KEY] ?: 6 // Mặc định lớp 6
    }

    // --- Xử lý Token ---
    suspend fun saveToken(token: String) {
        context.dataStore.edit { it[TOKEN_KEY] = token }
    }

    fun getToken(): Flow<String?> {
        return context.dataStore.data.map { it[TOKEN_KEY] }
    }

    suspend fun getTokenSync(): String? {
        return context.dataStore.data.map { it[TOKEN_KEY] }.first()
    }

    // --- Xử lý NGÔN NGỮ (MỚI) ---
    suspend fun saveLanguage(langCode: String) {
        context.dataStore.edit { it[LANGUAGE_KEY] = langCode }
    }

    fun getLanguageFlow(): Flow<String> {
        return context.dataStore.data.map { it[LANGUAGE_KEY] ?: "vi" } // Mặc định tiếng Việt
    }

    suspend fun getLanguageSync(): String {
        val preferences = context.dataStore.data.first()
        return preferences[LANGUAGE_KEY] ?: "vi"
    }

    // --- Xử lý GIAO DIỆN DARK THEME (MỚI) ---
    suspend fun saveTheme(isDark: Boolean) {
        context.dataStore.edit { it[THEME_KEY] = isDark }
    }

    fun getDarkThemeFlow(): Flow<Boolean?> {
        return context.dataStore.data.map { it[THEME_KEY] } // Trả về null nếu chưa cấu hình
    }

    // --- Xử lý NHẮC NHỞ HỌC TẬP (REMINDERS) ---
    fun getReminderDailyEnabledFlow() = context.dataStore.data.map { it[REMINDER_DAILY_ENABLED] ?: false }
    suspend fun saveReminderDailyEnabled(enabled: Boolean) = context.dataStore.edit { it[REMINDER_DAILY_ENABLED] = enabled }

    fun getReminderDailyTimeFlow() = context.dataStore.data.map { Pair(it[REMINDER_DAILY_HOUR] ?: 7, it[REMINDER_DAILY_MINUTE] ?: 30) }
    suspend fun saveReminderDailyTime(hour: Int, minute: Int) = context.dataStore.edit {
        it[REMINDER_DAILY_HOUR] = hour
        it[REMINDER_DAILY_MINUTE] = minute
    }

    fun getReminderStreakEnabledFlow() = context.dataStore.data.map { it[REMINDER_STREAK_ENABLED] ?: false }
    suspend fun saveReminderStreakEnabled(enabled: Boolean) = context.dataStore.edit { it[REMINDER_STREAK_ENABLED] = enabled }

    fun getReminderStreakTimeFlow() = context.dataStore.data.map { Pair(it[REMINDER_STREAK_HOUR] ?: 21, it[REMINDER_STREAK_MINUTE] ?: 0) }
    suspend fun saveReminderStreakTime(hour: Int, minute: Int) = context.dataStore.edit {
        it[REMINDER_STREAK_HOUR] = hour
        it[REMINDER_STREAK_MINUTE] = minute
    }

    fun getReminderExamEnabledFlow() = context.dataStore.data.map { it[REMINDER_EXAM_ENABLED] ?: false }
    suspend fun saveReminderExamEnabled(enabled: Boolean) = context.dataStore.edit { it[REMINDER_EXAM_ENABLED] = enabled }

    // --- Xử lý Lịch sử Tìm Kiếm ---
    fun getSearchHistoryFlow(): Flow<List<String>> {
        return context.dataStore.data.map { preferences ->
            val historyString = preferences[SEARCH_HISTORY_KEY] ?: ""
            if (historyString.isBlank()) emptyList() else historyString.split(",,,")
        }
    }

    suspend fun saveSearchKeyword(keyword: String) {
        if (keyword.isBlank()) return
        context.dataStore.edit { preferences ->
            val currentHistory = preferences[SEARCH_HISTORY_KEY] ?: ""
            val historyList = if (currentHistory.isBlank()) mutableListOf() else currentHistory.split(",,,").toMutableList()
            
            // Bỏ từ khoá cũ nếu bị trùng và thêm vào đầu
            historyList.remove(keyword)
            historyList.add(0, keyword)
            
            // Giữ lại 10 từ khóa mới nhất
            if (historyList.size > 10) {
                historyList.removeAt(historyList.size - 1)
            }
            
            preferences[SEARCH_HISTORY_KEY] = historyList.joinToString(",,,")
        }
    }

    suspend fun clearSearchHistory() {
        context.dataStore.edit { it.remove(SEARCH_HISTORY_KEY) }
    }

    // --- Reset ---
    suspend fun clearData() {
        context.dataStore.edit { it.clear() }
    }

    suspend fun clearToken() {
        context.dataStore.edit { it.remove(TOKEN_KEY) }
    }
}