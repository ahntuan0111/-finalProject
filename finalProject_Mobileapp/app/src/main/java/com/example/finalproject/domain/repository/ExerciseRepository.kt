package com.example.finalproject.domain.repository

import com.example.finalproject.data.remote.model.ExerciseDto
import com.example.finalproject.data.remote.model.SaveSubmissionRequest
import com.example.finalproject.data.remote.model.UploadUrlResponse
import com.example.finalproject.data.remote.model.ValidationSubmissionRequest
import com.example.finalproject.data.remote.model.ValidationSubmissionResponse
import com.example.finalproject.utils.Resource
import kotlinx.coroutines.flow.Flow

interface ExerciseRepository {
    suspend fun getExercisesByLesson(lessonId: Long): Flow<Resource<List<ExerciseDto>>>
    
    suspend fun validateImage(exerciseId: Long, request: ValidationSubmissionRequest): Resource<ValidationSubmissionResponse>
    
    suspend fun saveSubmission(exerciseId: Long, request: SaveSubmissionRequest): Resource<Any>

    suspend fun getUploadUrl(exerciseId: Long): Resource<UploadUrlResponse>
}