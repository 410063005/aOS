package com.example.aos.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aos.data.UserPreferences
import com.example.aos.service.GithubApi
import com.example.aos.service.IssueRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

data class IssueFormState(
    val title: String = "",
    val body: String = "",
    val labels: List<String> = emptyList()
)

class RaiseIssueViewModel(application: Application) : AndroidViewModel(application) {
    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .addInterceptor(headerInterceptor())
        .build()

    private val api = Retrofit.Builder()
        .baseUrl("https://api.github.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttpClient)
        .build()
        .create(GithubApi::class.java)

    private val userPreferences = UserPreferences(application)

    private fun headerInterceptor(): Interceptor {
        return Interceptor { chain ->
            var request = chain.request()

            //add access token
            val accessToken = userPreferences.getToken() ?: "" //getAuthorization()

            if (accessToken.isNotEmpty()) {
                val url = request.url.toString()
                request = request.newBuilder()
                    .addHeader("Authorization", "Bearer $accessToken")
                    .url(url)
                    .build()
            }

            chain.proceed(request)
        }

    }

    private val _formState = MutableStateFlow(IssueFormState())
    val formState: StateFlow<IssueFormState> = _formState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _isSuccess = MutableStateFlow(false)
    val isSuccess: StateFlow<Boolean> = _isSuccess.asStateFlow()

    fun updateTitle(title: String) {
        _formState.value = _formState.value.copy(title = title)
    }

    fun updateBody(body: String) {
        _formState.value = _formState.value.copy(body = body)
    }

    fun updateLabels(labels: List<String>) {
        _formState.value = _formState.value.copy(labels = labels)
    }

    fun createIssue(owner: String, repo: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                _isSuccess.value = false

                val issue = api.createIssue(
                    owner = owner,
                    repo = repo,
//                    title = _formState.value.title,
//                    body = _formState.value.body,
//                    labels = _formState.value.labels
                    issue = IssueRequest(
                        title = _formState.value.title,
                        body = _formState.value.body,
                        labels = _formState.value.labels
                    )
                )

                _isSuccess.value = true
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to create issue"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun reset() {
        _formState.value = IssueFormState()
        _error.value = null
        _isSuccess.value = false
    }
} 