package com.example.aos.model

import com.google.gson.annotations.SerializedName

data class Issue(
    val id: Long,
    val number: Int,
    val title: String,
    val body: String?,
    val state: String,
    val labels: List<Label>,
    val user: Owner,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("updated_at")
    val updatedAt: String,
    @SerializedName("html_url")
    val htmlUrl: String
)

data class Label(
    val id: Long,
    val name: String,
    val color: String,
    val description: String?
) 