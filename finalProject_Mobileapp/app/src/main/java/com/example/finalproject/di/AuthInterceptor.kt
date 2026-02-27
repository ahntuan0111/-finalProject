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

        // 1. Lấy token và gắn vào request
        val token = runBlocking {
            dataStoreManager.getTokenSync()
        }

        if (!token.isNullOrEmpty()) {
            requestBuilder.addHeader("Authorization", "Bearer $token")
        }

        // 2. Thực thi Request
        val response = chain.proceed(requestBuilder.build())

        // 3. XỬ LÝ AUTO LOGOUT (Khi Token hết hạn hoặc sai)
        if (response.code == 401) {
            runBlocking {
                // Xóa token khỏi máy
                dataStoreManager.clearToken()
            }
        }

        return response
    }
}