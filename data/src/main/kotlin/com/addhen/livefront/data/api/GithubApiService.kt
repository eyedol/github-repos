// Copyright 2025, Livefront sample app project contributors
// SPDX-License-Identifier: Apache-2.0

package com.addhen.livefront.data.api

import com.addhen.livefront.data.api.dto.GithubRepoDto.ContributorDto
import com.addhen.livefront.data.api.dto.GithubRepoResponseDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * A [retrofit2.Retrofit] service interface for interacting with the GitHub API.
 * Provides methods to fetch contributors of a repository and search for repositories.
 */
interface GithubApiService {

    /**
     * Fetches a list of contributors for a specific GitHub repository.
     *
     * @param owner The username or organization name that owns the repository.
     * @param repo The name of the repository.
     * @param perPage The number of contributors to return per page. Defaults to 1.
     * @return A list of contributor details as [ContributorDto] objects.
     */
    @GET("repos/{owner}/{repo}/contributors")
    suspend fun getContributors(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Query("per_page") perPage: Int = 1,
    ): List<ContributorDto>

    /**
     * Retrieves a list of GitHub repositories sorted by stars in descending order.
     *
     * @param query The search query string for repositories.
     * @param page The page number to retrieve, used for pagination.
     * @param perPage The number of repositories to fetch per page.
     * @return A [GithubRepoResponseDto] containing a list of repositories matching the search criteria.
     */
    @GET("search/repositories?sort=stars&order=desc")
    suspend fun getRepos(
        @Query("q") query: String,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int,
    ): GithubRepoResponseDto
}
