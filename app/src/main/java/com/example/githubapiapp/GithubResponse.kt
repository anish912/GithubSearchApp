package com.example.githubapiapp

data class GitHubResponse(
    val total_count: Int,
    val items: List<Repository>
)
