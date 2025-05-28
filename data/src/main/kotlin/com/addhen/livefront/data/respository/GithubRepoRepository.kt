// Copyright 2025, Livefront sample app project contributors
// SPDX-License-Identifier: Apache-2.0

package com.addhen.livefront.data.respository

import androidx.paging.PagingData
import com.addhen.livefront.data.model.DataResult
import com.addhen.livefront.data.model.GithubRepo
import kotlinx.coroutines.flow.Flow

/**
 * A repository interface for interacting with GitHub repository data.
 *
 * Provides methods to search for GitHub repositories based on a search query
 * and to retrieve detailed information for a specific repository by its ID.
 */
interface GithubRepoRepository {

    /**
     * Searches for GitHub repositories based on the given query string and returns
     * a stream of paginated repository data.
     *
     * @param query The search query string used to filter repositories.
     * @return A Flow emitting paginated data of GitHub repositories matching the query.
     */
    fun searchRepos(query: String): Flow<PagingData<GithubRepo>>

    /**
     * Retrieves detailed information about a specific GitHub repository by its ID.
     *
     * @param id The unique identifier of the GitHub repository to retrieve details for.
     * @return A Flow emitting the repository details as a [GithubRepo] object if found,
     *         or null if the repository does not exist.
     */
    fun getRepoDetails(id: Long): Flow<DataResult<GithubRepo>>
}
