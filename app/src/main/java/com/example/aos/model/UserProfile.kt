package com.example.aos.model

data class UserProfile(
    val login: String,
    val name: String?,
    val avatarUrl: String,
    val bio: String?,
    val publicRepos: Int,
    val followers: Int,
    val following: Int,
    val location: String?,
    val company: String?,
    val blog: String?
) 