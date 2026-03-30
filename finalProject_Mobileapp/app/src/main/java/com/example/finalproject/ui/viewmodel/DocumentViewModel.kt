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
                android.util.Log.d("DocumentVM", "Fetching documents: subjectCode=$subjectCode, grade=$grade")
                val response = apiService.getDocuments(subjectCode, grade)
                android.util.Log.d("DocumentVM", "HTTP Status: ${response.code()}, Body: ${response.body()?.success}, Count: ${response.body()?.data?.size}")
                if (response.isSuccessful && response.body()?.success == true) {
                    val items = response.body()?.data ?: emptyList()
                    _documents.value = items
                    android.util.Log.d("DocumentVM", "Documents loaded: ${items.size} items")
                } else {
                    android.util.Log.w("DocumentVM", "Response not successful or success=false")
                    _documents.value = emptyList()
                }
            } catch (e: Exception) {
                android.util.Log.e("DocumentVM", "Error fetching documents", e)
            } finally {
                _isLoading.value = false
            }
        }
    }
}