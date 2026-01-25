package com.example.finalproject.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finalproject.data.remote.model.ExerciseDto
import com.example.finalproject.domain.repository.ExerciseRepository
import com.example.finalproject.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExerciseViewModel @Inject constructor(
    private val exerciseRepository: ExerciseRepository
) : ViewModel() {

    private val _exercisesState = MutableStateFlow<Resource<List<ExerciseDto>>>(Resource.Loading())
    val exercisesState: StateFlow<Resource<List<ExerciseDto>>> = _exercisesState

    fun getExercises(lessonId: Long) {
        viewModelScope.launch {
            exerciseRepository.getExercisesByLesson(lessonId).collect { result ->
                _exercisesState.value = result
            }
        }
    }
}