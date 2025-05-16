package com.example.aos.data

import android.content.Context
import android.content.SharedPreferences
import com.example.aos.service.UserResponse
import com.google.gson.Gson

class UserPreferences(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val gson = Gson()

    companion object {
        private const val PREFS_NAME = "user_prefs"
        private const val KEY_TOKEN = "token"
        private const val KEY_USER = "user"
        private const val KEY_SELECTED_DATE = "popular_repo.selected_date"
        private const val KEY_EXPAND_DATE_FILTER = "popular_repo.expand_date_filter"
    }

    fun saveToken(token: String) {
        prefs.edit().putString(KEY_TOKEN, token).apply()
    }

    fun getToken(): String? {
        return prefs.getString(KEY_TOKEN, null)
    }

    fun saveUser(user: UserResponse) {
        val userJson = gson.toJson(user)
        prefs.edit().putString(KEY_USER, userJson).apply()
    }

    fun getUser(): UserResponse? {
        val userJson = prefs.getString(KEY_USER, null) ?: return null
        return try {
            gson.fromJson(userJson, UserResponse::class.java)
        } catch (e: Exception) {
            null
        }
    }

    fun saveSelectedDate(date: String?) {
        prefs.edit().putString(KEY_SELECTED_DATE, date).apply()
    }

    fun getSelectedDate(): String? {
        return prefs.getString(KEY_SELECTED_DATE, null)
    }

    fun saveExpandDateFilter(expand: Boolean) {
        prefs.edit().putBoolean(KEY_EXPAND_DATE_FILTER, expand).apply()
    }

    fun getExpandDateFilter(): Boolean {
        return prefs.getBoolean(KEY_EXPAND_DATE_FILTER, false)
    }

    fun clearUserData() {
        prefs.edit().clear().apply()
    }

    fun isLoggedIn(): Boolean {
        return getToken() != null && getUser() != null
    }
} 