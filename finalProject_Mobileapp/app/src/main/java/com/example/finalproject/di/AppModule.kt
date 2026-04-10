package com.example.finalproject.di

import com.example.finalproject.data.remote.ApiService
import com.example.finalproject.data.repository.AuthRepositoryImpl
import com.example.finalproject.domain.repository.AuthRepository
import com.example.finalproject.domain.repository.UserRepository
import com.example.finalproject.data.repository.UserRepositoryImpl
import com.example.finalproject.domain.repository.QuizRepository
import com.example.finalproject.data.repository.QuizRepositoryImpl
import com.example.finalproject.data.repository.StatisticsRepositoryImpl
import com.example.finalproject.data.repository.TheoryRepositoryImpl
import com.example.finalproject.domain.repository.StatisticsRepository
import com.example.finalproject.domain.repository.TheoryRepository
import com.example.finalproject.utils.DataStoreManager // Import DataStoreManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // 1. Cung cấp AuthInterceptor
    @Provides
    @Singleton
    fun provideAuthInterceptor(dataStoreManager: DataStoreManager): AuthInterceptor {
        return AuthInterceptor(dataStoreManager)
    }

    // 2. Cung cấp OkHttpClient đã gắn Interceptor
    @Provides
    @Singleton
    fun provideOkHttpClient(
        authInterceptor: AuthInterceptor,
        languageInterceptor: LanguageInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(languageInterceptor) // Thêm LanguageInterceptor để gắn Accept-Language
            .connectTimeout(30, TimeUnit.SECONDS) // Tăng thời gian chờ
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    // 3. Cập nhật Retrofit để dùng OkHttpClient trên
    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://192.168.0.144:8080/api/")
            .client(okHttpClient) // <--- QUAN TRỌNG
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideReminderApiService(retrofit: Retrofit): com.example.finalproject.data.remote.api.ReminderApiService {
        return retrofit.create(com.example.finalproject.data.remote.api.ReminderApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideAiTutorApiService(retrofit: Retrofit): com.example.finalproject.data.remote.api.AiTutorApiService {
        return retrofit.create(com.example.finalproject.data.remote.api.AiTutorApiService::class.java)
    }

    // ... Các phần provide Repository giữ nguyên
    @Provides
    @Singleton
    fun provideAuthRepository(impl: AuthRepositoryImpl): AuthRepository = impl

    @Provides
    @Singleton
    fun provideUserRepository(impl: UserRepositoryImpl): UserRepository = impl

    @Provides
    @Singleton
    fun provideQuizRepository(impl: QuizRepositoryImpl): QuizRepository = impl

    @Provides
    @Singleton
    fun provideTheoryRepository(apiService: ApiService): TheoryRepository {
        return TheoryRepositoryImpl(apiService)
    }
}