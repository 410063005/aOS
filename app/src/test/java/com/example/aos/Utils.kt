package com.example.aos

import com.example.aos.service.GithubApi
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

fun githubApiForTest(accessToken: String? = null): GithubApi {
    val okHttpClientForTest = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .apply {
            if (accessToken != null) {
                addInterceptor(Interceptor { chain ->
                    var request = chain.request()
                    val url = request.url.toString()
                    request = request.newBuilder()
                        .addHeader("Authorization", "Bearer $accessToken")
                        .url(url)
                        .build()
                    chain.proceed(request)
                })
            }
        }
        .build()
    val retrofitForTest = Retrofit.Builder()
        .baseUrl("https://api.github.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttpClientForTest)
        .build()
    return retrofitForTest.create(GithubApi::class.java)
}