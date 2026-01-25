package com.example.finalproject.domain.repository

import com.example.finalproject.data.remote.model.ExerciseDto
import com.example.finalproject.utils.Resource
import kotlinx.coroutines.flow.Flow

interface ExerciseRepository {
    suspend fun getExercisesByLesson(lessonId: Long): Flow<Resource<List<ExerciseDto>>>
}