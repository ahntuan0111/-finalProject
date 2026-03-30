package com.example.finalproject.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finalproject.data.remote.ApiService
import com.example.finalproject.data.remote.model.SearchResponse
import com.example.finalproject.utils.DataStoreManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val apiService: ApiService,
    private val dataStoreManager: DataStoreManager
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _searchResults = MutableStateFlow<List<SearchResponse>>(emptyList())
    val searchResults = _searchResults.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _hasFetched = MutableStateFlow(false)
    val hasFetched = _hasFetched.asStateFlow()

    private val _currentGrade = MutableStateFlow(6) // Default 6
    val currentGrade = _currentGrade.asStateFlow()

    val searchHistory: StateFlow<List<String>> = dataStoreManager.getSearchHistoryFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        // Debounce search query
        viewModelScope.launch {
            _searchQuery
                .debounce(500)
                .distinctUntilChanged()
                .filter { it.isNotBlank() }
                .collect { query ->
                    performSearch(query)
                }
        }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
        if (query.isBlank()) {
            _searchResults.value = emptyList()
            _hasFetched.value = false
        }
    }

    private fun performSearch(query: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _hasFetched.value = true
            try {
                val grade = dataStoreManager.getGradeSync()
                _currentGrade.value = grade
                val response = apiService.searchContent(query, grade)
                if (response.isSuccessful && response.body()?.success == true) {
                    _searchResults.value = response.body()?.data ?: emptyList()
                    // Save to history when there are results
                    if (_searchResults.value.isNotEmpty()) {
                        dataStoreManager.saveSearchKeyword(query)
                    }
                } else {
                    _searchResults.value = emptyList()
                }
            } catch (e: Exception) {
                _searchResults.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun saveKeyword(keyword: String) {
        viewModelScope.launch {
            if (keyword.isNotBlank()) {
                dataStoreManager.saveSearchKeyword(keyword)
            }
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            dataStoreManager.clearSearchHistory()
        }
    }

    fun executeSearch(keyword: String) {
        onSearchQueryChange(keyword)
    }
}
