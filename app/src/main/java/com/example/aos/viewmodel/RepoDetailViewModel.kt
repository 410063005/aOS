package com.example.aos.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aos.model.GithubRepo
import com.example.aos.service.GithubApi
import com.example.aos.service.GithubApiFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RepoDetailViewModel(
    private val api: GithubApi = GithubApiFactory.githubApi
) : ViewModel() {

    private val _repo = MutableStateFlow<GithubRepo?>(null)
    val repo: StateFlow<GithubRepo?> = _repo.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun loadRepo(owner: String, repo: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                _repo.value = api.getRepo(owner, repo)
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to load repository"
            } finally {
                _isLoading.value = false
            }
        }
    }
} 