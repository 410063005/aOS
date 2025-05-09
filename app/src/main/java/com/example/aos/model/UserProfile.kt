package com.example.aos.model

import com.google.gson.annotations.SerializedName

data class UserProfile(
    val login: String,
    val name: String?,
    val avatarUrl: String,
    val bio: String?,
    @SerializedName("public_repos")
    val publicRepos: Int,
    val followers: Int,
    val following: Int,
    val location: String?,
    val company: String?,
    val blog: String?
) 