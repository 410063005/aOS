package com.example.aos.model

import com.google.gson.annotations.SerializedName

data class GithubRepo(
    val id: Int,
    val name: String,
    @SerializedName("full_name")
    val fullName: String?,
    val description: String?,
    val owner: Owner,
    @SerializedName("stargazers_count")
    val stars: Int,
    val language: String?,
    val forks: Int = 0,
    val watchers: Int = 0,
    val openIssues: Int = 0,
    val defaultBranch: String = "main",
    val createdAt: String? = null,
    val updatedAt: String? = null,
    val homepage: String? = null,
    val topics: List<String> = emptyList()
)

data class Owner(
    val login: String,
    @SerializedName("avatar_url")
    val avatarUrl: String
) 