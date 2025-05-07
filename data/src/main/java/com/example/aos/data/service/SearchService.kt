package com.example.aos.data.service

import com.example.aos.data.model.bean.Repo
import com.example.aos.data.model.bean.SearchResult
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface SearchService {
    // https://api.github.com/search/repositories?q=stars:>=1&sort=stars&order=desc
    // https://stackoverflow.com/questions/30525330/how-to-get-list-of-trending-github-repositories-by-github-api
    // https://gist.github.com/jasonrudolph/6065289
    @GET("search/repositories")
    suspend fun getPopularRepos(@Query("q") query: String,
                               @Query("sort") sort: String,
                               @Query("order") order: String): Response<SearchResult<Repo>>

    @GET("search/repositories")
    fun searchRepos(
        @Query(value = "q", encoded = true) query: String,
        @Query("sort") sort: String = "best%20match",
        @Query("order") order: String = "desc",
        @Query("page") page: Int,
        @Query("per_page") perPage: Int = 20,
    ): Response<SearchResult<Repo>>
}
