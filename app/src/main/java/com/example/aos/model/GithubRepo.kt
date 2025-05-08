package com.example.aos.model

import com.google.gson.annotations.SerializedName

data class GithubRepo(
    val id: Long,
    val name: String,
    @SerializedName("full_name")
    val fullName: String?,
    val description: String?,
    @SerializedName("stargazers_count")
    val stars: Int,
    val language: String?,
    val owner: Owner
)

data class Owner(
    val login: String,
    @SerializedName("avatar_url")
    val avatarUrl: String
) 