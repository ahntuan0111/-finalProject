package com.example.finalproject.di

import com.example.finalproject.utils.DataStoreManager
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val dataStoreManager: DataStoreManager
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()

        // Lấy token từ DataStore (Dùng runBlocking vì Interceptor chạy đồng bộ)
        val token = runBlocking {
            dataStoreManager.getTokenSync()
        }

        // Nếu có token thì gắn vào Header Authorization
        if (!token.isNullOrEmpty()) {
            requestBuilder.addHeader("Authorization", "Bearer $token")
        }

        return chain.proceed(requestBuilder.build())
    }
}