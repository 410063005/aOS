package com.example.aos.service

import android.app.Application
import com.example.aos.data.UserPreferences
import com.example.aos.model.GithubRepo
import com.example.aos.model.Issue
import com.example.aos.model.UserProfile
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

interface GithubApi {
    @GET("search/repositories")
    suspend fun searchRepositories(
        @Query("q", encoded = true) query: String,
        @Query("sort") sort: String = "stars",
        @Query("order") order: String = "desc",
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 30
    ): SearchResponse

    @GET("user")
    suspend fun getCurrentUser(
        @Header("Authorization") token: String
    ): UserResponse

    @GET("users/{username}")
    suspend fun getUserProfile(
        @Path("username") username: String
    ): UserProfile

    @GET("users/{username}/repos")
    suspend fun getUserRepositories(
        @Path("username") username: String,
        @Query("sort") sort: String = "updated",
        @Query("per_page") perPage: Int = 30,
        @Query("page") page: Int = 1
    ): List<GithubRepo>

    @POST("repos/{owner}/{repo}/issues")
    suspend fun createIssue(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Body issue: IssueRequest
    ): Issue

    @GET("repos/{owner}/{repo}")
    suspend fun getRepo(
        @Path("owner") owner: String,
        @Path("repo") repo: String
    ): GithubRepo
}

object GithubApiFactory {

    lateinit var application: Application

    private lateinit var okHttpClient: OkHttpClient
    private lateinit var retrofit: Retrofit
//    private val okHttpClient = OkHttpClient.Builder()
//        .connectTimeout(15, TimeUnit.SECONDS)
//        .addInterceptor(headerInterceptor())
//        .build()
//
//    private val retrofit = Retrofit.Builder()
//        .baseUrl("https://api.github.com/")
//        .addConverterFactory(GsonConverterFactory.create())
//        .client(okHttpClient)
//        .build()

    val githubApi: GithubApi by lazy {
        if (!this::retrofit.isInitialized) {
            okHttpClient = OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .addInterceptor(headerInterceptor())
                .build()
            retrofit = Retrofit.Builder()
                .baseUrl("https://api.github.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build()
        }

        retrofit.create(GithubApi::class.java)
    }

    private fun headerInterceptor(): Interceptor {
        val userPreferences = UserPreferences(application)

        return Interceptor { chain ->
            var request = chain.request()

            //add access token
            val accessToken = userPreferences.getToken() ?: "" //getAuthorization()

            if (accessToken.isNotEmpty()) {
                val url = request.url.toString()
                request = request.newBuilder()
                    .addHeader("Authorization", "Bearer $accessToken")
                    .url(url)
                    .build()
            }

            chain.proceed(request)
        }

    }
}

data class SearchResponse(
    val total_count: Int,
    val items: List<GithubRepo>
)

data class UserResponse(
    val login: String,
    val id: Long,
    val avatar_url: String,
    val name: String?,
    val email: String?,
    val bio: String?,
    val public_repos: Int,
    val followers: Int,
    val following: Int
)

data class IssueRequest(
    val title: String,
    val body: String,
    val labels: List<String>
)