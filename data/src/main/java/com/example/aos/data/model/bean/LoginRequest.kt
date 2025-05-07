package com.example.aos.data.model.bean

import com.google.gson.annotations.SerializedName

class LoginRequest {
    var scopes: List<String>? = null
        private set
    var note: String? = null
        private set
    @SerializedName("client_id")
    var clientId: String? = null
        private set
    @SerializedName("client_secret")
    var clientSecret: String? = null
        private set

    companion object {
        fun generate(): LoginRequest {
            val model = LoginRequest()
            model.scopes = listOf("user", "repo", "gist", "notifications")
            model.note = ""//BuildConfig.APPLICATION_ID
            model.clientId = ""//BuildConfig.CLIENT_ID
            model.clientSecret = ""//BuildConfig.CLIENT_SECRET
            return model
        }
    }
}