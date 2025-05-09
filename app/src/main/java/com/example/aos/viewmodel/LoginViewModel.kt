package com.example.aos.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aos.data.UserPreferences
import com.example.aos.service.GithubApi
import com.example.aos.service.GithubApiFactory
import com.example.aos.service.UserResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
//    application: Application,
    private val api: GithubApi = GithubApiFactory.githubApi,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Initial)
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val _isLoggedIn = MutableStateFlow<Boolean>(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _username = MutableStateFlow("")
    val username: StateFlow<String> = _username.asStateFlow()

    init {
        // Check if user is already logged in
        if (userPreferences.isLoggedIn()) {
            _uiState.value = LoginUiState.Success(userPreferences.getUser()!!)
            _isLoggedIn.value = true
            _username.value = userPreferences.getUser()?.login ?: ""
        }
    }

    fun login(token: String) {
        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading
            try {
                val user = api.getCurrentUser("Bearer $token")
                Log.i("cmcmcm", user.toString())
                
                // Save login state
                userPreferences.saveToken(token)
                userPreferences.saveUser(user)
                
                _uiState.value = LoginUiState.Success(user)
            } catch (e: Exception) {
                _uiState.value = LoginUiState.Error(
                    e.message ?: "Failed to authenticate with GitHub"
                )
            }
        }
    }

    fun logout() {
        userPreferences.clearUserData()
        _uiState.value = LoginUiState.Initial
        _username.value = ""
        _isLoggedIn.value = false
    }
}

sealed class LoginUiState {
    object Initial : LoginUiState()
    object Loading : LoginUiState()
    data class Success(val user: UserResponse) : LoginUiState()
    data class Error(val message: String) : LoginUiState()
} 