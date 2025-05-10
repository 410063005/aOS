package com.example.aos.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aos.model.GithubRepo
import com.example.aos.service.GithubApi
import com.example.aos.service.GithubApiFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PopularReposViewModel(
    private val api: GithubApi = GithubApiFactory.githubApi
) : ViewModel() {
    private val _repos = MutableStateFlow<List<GithubRepo>>(emptyList())
    val repos: StateFlow<List<GithubRepo>> = _repos.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _hasMoreItems = MutableStateFlow(true)
    val hasMoreItems: StateFlow<Boolean> = _hasMoreItems.asStateFlow()

    private var currentPage = 1
    private var totalCount = 0

    init {
        fetchPopularRepos()
    }

    fun fetchPopularRepos(date: String? = null) {
        if (_isLoading.value || !_hasMoreItems.value) return

        val query = date?.let { "stars:>1000 created:>$it" } ?: "stars:>1000"
        Log.i("cmcmcm", date.toString())
        Log.i("cmcmcm", query)
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                val response = api.searchRepositories(
                    query = query,
                    page = currentPage
                )

                totalCount = response.total_count
                _repos.value += response.items
                currentPage++

                // Check if we've loaded all items
                _hasMoreItems.value = _repos.value.size < totalCount
            } catch (e: Exception) {
                _error.value = e.message ?: "An error occurred"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun reset(date: String? = null) {
        currentPage = 1
        _repos.value = emptyList()
        _hasMoreItems.value = true
        _error.value = null
        fetchPopularRepos(date)
    }
} 