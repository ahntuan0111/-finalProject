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

    // --- Reset ---
    suspend fun clearData() {
        context.dataStore.edit { it.clear() }
    }

    suspend fun clearToken() {
        context.dataStore.edit { it.remove(TOKEN_KEY) }
    }
}