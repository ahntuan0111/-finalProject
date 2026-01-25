package com.example.finalproject.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finalproject.data.remote.ApiService
import com.example.finalproject.data.remote.model.DocumentDto
import com.example.finalproject.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DocumentViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {

    private val _documents = MutableStateFlow<List<DocumentDto>>(emptyList())
    val documents = _documents.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun fetchDocuments(subjectCode: String, grade: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = apiService.getDocuments(subjectCode, grade)
                if (response.isSuccessful && response.body()?.success == true) {
                    _documents.value = response.body()?.data ?: emptyList()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
}