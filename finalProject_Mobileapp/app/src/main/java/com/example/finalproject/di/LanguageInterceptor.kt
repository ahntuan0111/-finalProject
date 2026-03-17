package com.example.finalproject.di

import com.example.finalproject.utils.DataStoreManager
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class LanguageInterceptor @Inject constructor(
    private val dataStoreManager: DataStoreManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        // Lấy ngôn ngữ hiện hành (Sử dụng runBlocking vì interceptor chạy đồng bộ)
        val currentLang = runBlocking { dataStoreManager.getLanguageSync() }

        // Nhét Header Accept-Language vào Request
        val requestWithLanguage = chain.request().newBuilder()
            .addHeader("Accept-Language", currentLang)
            .build()

        return chain.proceed(requestWithLanguage)
    }
}