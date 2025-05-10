package com.example.aos.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aos.model.GithubRepo
import com.example.aos.model.UserProfile
import com.example.aos.service.GithubApi
import com.example.aos.service.GithubApiFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val api: GithubApi = GithubApiFactory.githubApi
) :  ViewModel() {

    private val _profile = MutableStateFlow<UserProfile?>(null)
    val profile: StateFlow<UserProfile?> = _profile.asStateFlow()

    private val _repos = MutableStateFlow<List<GithubRepo>>(emptyList())
    val repos: StateFlow<List<GithubRepo>> = _repos.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _hasMoreItems = MutableStateFlow(true)
    val hasMoreItems: StateFlow<Boolean> = _hasMoreItems.asStateFlow()

    private var currentPage = 1
    private var currentUsername: String? = null

    fun loadProfile(username: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                
                // Load user profile
                val userProfile = api.getUserProfile(username)
                _profile.value = userProfile
                
                // Reset pagination state
                currentPage = 1
                currentUsername = username
                _repos.value = emptyList()
                _hasMoreItems.value = true
                
                // Load first page of repositories
                val userRepos = api.getUserRepositories(username, page = currentPage)
                _repos.value = userRepos
                _hasMoreItems.value = userRepos.isNotEmpty()
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to load profile"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadMoreRepos() {
        if (!_hasMoreItems.value || _isLoading.value || currentUsername == null) return

        viewModelScope.launch {
            try {
                _isLoading.value = true
                currentPage++
                
                val moreRepos = api.getUserRepositories(currentUsername!!, page = currentPage)
                _repos.value = _repos.value + moreRepos
                _hasMoreItems.value = moreRepos.isNotEmpty()
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to load more repositories"
                currentPage-- // Revert page number on error
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun reset() {
        _profile.value = null
        _repos.value = emptyList()
        _isLoading.value = false
        _error.value = null
        _hasMoreItems.value = true
        currentPage = 1
        currentUsername = null
    }
} 