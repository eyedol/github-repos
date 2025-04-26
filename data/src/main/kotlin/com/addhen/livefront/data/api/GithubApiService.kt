package com.addhen.livefront.data.api

import com.addhen.livefront.data.api.dto.RepoDto.ContributorDto
import com.addhen.livefront.data.api.dto.RepoResponseDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GithubApiService {

    @GET("repos/{owner}/{repo}/contributors")
    suspend fun getContributors(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Query("per_page") perPage: Int = 1,
    ): List<ContributorDto>

    @GET("search/repositories?sort=stars&order=desc")
    suspend fun getRepos(
        @Query("q") query: String,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int,
    ): RepoResponseDto
}