package com.example.aos.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.aos.service.GithubApi
import com.example.aos.service.GithubApiFactory

class ViewModelFactory(
    private val application: Application
) : ViewModelProvider.Factory {
    private val api: GithubApi = GithubApiFactory.githubApi

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> 
                LoginViewModel(application, api) as T
            modelClass.isAssignableFrom(PopularReposViewModel::class.java) -> 
                PopularReposViewModel(api) as T
            modelClass.isAssignableFrom(ProfileViewModel::class.java) -> 
                ProfileViewModel(api) as T
            modelClass.isAssignableFrom(RepoDetailViewModel::class.java) -> 
                RepoDetailViewModel(api) as T
            modelClass.isAssignableFrom(SearchViewModel::class.java) -> 
                SearchViewModel(api) as T
            modelClass.isAssignableFrom(RaiseIssueViewModel::class.java) ->
                RaiseIssueViewModel(application, api) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
} 