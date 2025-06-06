package com.example.aos.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aos.model.GithubRepo
import com.example.aos.service.GithubApi
import com.example.aos.service.GithubApiFactory
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SearchViewModel(
    private val api: GithubApi = GithubApiFactory.githubApi
) : ViewModel() {
    private val _repos = MutableStateFlow<List<GithubRepo>>(emptyList())
    val repos: StateFlow<List<GithubRepo>> = _repos.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _expandLanguageFilter = MutableStateFlow(false)
    val expandLanguageFilter: StateFlow<Boolean> = _expandLanguageFilter.asStateFlow()

    private var searchJob: Job? = null

    fun toggleLanguageFilter() {
        _expandLanguageFilter.value = !_expandLanguageFilter.value
    }

    fun searchRepos(query: String, language: String?) {
        if (language.isNullOrBlank()) {
            this.searchRepos(query, emptyList())
        } else {
            this.searchRepos(query, listOf(language))
        }
    }

    fun searchRepos(query: String, languages: Collection<String>) {
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
                    if (languages.isNotEmpty()) {
                        append(" ")
                        append("language:${languages.joinToString(" language:")}".lowercase())
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

