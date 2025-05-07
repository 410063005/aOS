package com.example.aos.data.viewmodel

sealed class ViewModelResult<out R> {
    data class Success<out T>(val data: T) : ViewModelResult<T>()
    data class Error(val exception: Exception) : ViewModelResult<Nothing>()
}