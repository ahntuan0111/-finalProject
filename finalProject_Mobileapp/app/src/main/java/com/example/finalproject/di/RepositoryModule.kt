package com.example.finalproject.di


import com.example.finalproject.data.repository.ExerciseRepositoryImpl
import com.example.finalproject.data.repository.ProgressRepositoryImpl
import com.example.finalproject.data.repository.StatisticsRepositoryImpl
import com.example.finalproject.domain.repository.ExerciseRepository
import com.example.finalproject.domain.repository.ProgressRepository
import com.example.finalproject.domain.repository.StatisticsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindExerciseRepository(
        exerciseRepositoryImpl: ExerciseRepositoryImpl
    ): ExerciseRepository

    @Binds
    @Singleton // Optional: Use this if you want the same instance throughout the app
    abstract fun bindProgressRepository(
        progressRepositoryImpl: ProgressRepositoryImpl
    ): ProgressRepository

    @Binds
    @Singleton
    abstract fun bindStatisticsRepository(
        statisticsRepositoryImpl: StatisticsRepositoryImpl
    ): StatisticsRepository

    @Binds
    @Singleton
    abstract fun bindReminderRepository(
        reminderRepositoryImpl: com.example.finalproject.data.repository.ReminderRepositoryImpl
    ): com.example.finalproject.domain.repository.ReminderRepository
}