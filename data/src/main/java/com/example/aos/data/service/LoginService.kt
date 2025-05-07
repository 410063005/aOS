package com.example.aos.data.service

import com.example.aos.data.model.bean.AccessToken
import com.example.aos.data.model.bean.LoginRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

interface LoginService {
    @POST("authorizations")
    @Headers("Accept: application/json")
    suspend fun authorizations(@Body authRequestModel: LoginRequest): Response<AccessToken>


    @GET("https://github.com/login/oauth/access_token")
    @Headers("Accept: application/json")
    fun authorizationsCode(@Query("client_id") clientId: String,
                           @Query("client_secret") clientSecret: String,
                           @Query("code") code: String): Response<AccessToken>
}