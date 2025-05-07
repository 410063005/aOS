package com.example.aos.data.repo

import com.example.aos.data.model.bean.Repo
import com.example.aos.data.model.bean.SearchResult
import com.example.aos.data.service.SearchService
import retrofit2.Retrofit

class SearchRepo constructor(private val retrofit: Retrofit) {

    suspend fun getPopularRepos() : Result<SearchResult<Repo>> {
        val service = retrofit.create(SearchService::class.java)
        val resp = service.getPopularRepos("stars:>=1000", "stars", "desc")
        if (resp.isSuccessful) {
            return Result.Success(resp.body()!!)
        }
        return Result.Error(Exception(resp.message()))
    }
}