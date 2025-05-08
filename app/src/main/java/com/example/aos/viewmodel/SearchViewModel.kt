package com.example.aos.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aos.model.GithubRepo
import com.example.aos.service.GithubApiFactory
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SearchViewModel : ViewModel() {
    private val _repos = MutableStateFlow<List<GithubRepo>>(emptyList())
    val repos: StateFlow<List<GithubRepo>> = _repos.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val api = GithubApiFactory.githubApi

    private var searchJob: Job? = null

    fun searchRepos(query: String, language: String?) {
        if (query.isEmpty()) {
            _repos.value = emptyList()
            return
        }
        searchJob?.cancel()
        searchJob = viewModelScope.launch {

            _isLoading.value = true
            _error.value = null

            delay(300)

            try {
                val searchQuery = buildString {
                    if (query.isNotEmpty()) {
                        append(query)
                        append(" in:name")
                    }
                    if (language != null) {
                        if (isNotEmpty()) append(" ")
                        append("language:$language")
                    }
                }

                val response = api.searchRepositories(searchQuery)
                _repos.value = response.items
            } catch (e: Exception) {
                _error.value = e.message ?: "An error occurred"
            } finally {
                _isLoading.value = false
            }
        }
    }
}

