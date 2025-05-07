package com.example.aos.data.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aos.data.repo.SearchRepo
import kotlinx.coroutines.launch

class SearchViewModel(private val searchRepo: SearchRepo): ViewModel() {
    fun getPopularRepos() {
        viewModelScope.launch {
            val result = searchRepo.getPopularRepos()
//            when (result) {
//            }
        }
    }
}