package com.example.githubapiapp

import retrofit2.http.GET
import retrofit2.http.Query

interface GithHubApiService {
    @GET("search/repositories")
    suspend fun searchRepositories(
        @Query("q") query: String,
        @Query("sort") sort: String = "stars"
    ): GitHubResponse
}