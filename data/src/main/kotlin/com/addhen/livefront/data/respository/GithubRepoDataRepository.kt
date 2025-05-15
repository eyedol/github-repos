// Copyright 2025, Livefront sample app project contributors
// SPDX-License-Identifier: Apache-2.0

package com.addhen.livefront.data.respository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.addhen.livefront.data.api.GithubApiService
import com.addhen.livefront.data.cache.StorageInterface
import com.addhen.livefront.data.model.DataResult
import com.addhen.livefront.data.model.GithubRepo
import com.addhen.livefront.data.model.DataError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Implementation of [GithubRepoRepository] that provides methods to interact with GitHub repository data.
 *
 * This repository uses the provided [GithubApiService] to fetch repository data from the GitHub API,
 * and the [StorageInterface] to manage local storage of repository details.
 *
 * @param apiService The [GithubApiService] used to fetch repository data from the GitHub API.
 * @param storage The [StorageInterface] used to manage locally stored details about repositories.
 */
class GithubRepoDataRepository @Inject constructor(
    private val apiService: GithubApiService,
    private val storage: StorageInterface<GithubRepo>,
) : GithubRepoRepository {

    /**
     * Searches for GitHub repositories based on the provided query and returns
     * a stream of paginated repository data.
     *
     * @param query The search query string used to filter the repositories.
     * @return A Flow emitting paginated data of GitHub repositories that match the query.
     */
    override fun searchRepos(query: String): Flow<PagingData<GithubRepo>> {
        return Pager(
            config = PagingConfig(pageSize = 10),
            pagingSourceFactory = { GithubRepoPagingSource(apiService, storage, query) },
        ).flow
    }

    /**
     * Retrieves detailed information about a specific GitHub repository based on its ID.
     *
     * @param id The unique identifier of the GitHub repository to retrieve details for.
     * @return A Flow emitting a [GithubRepo] object containing the repository details if found,
     *         or null if no repository matches the given identifier.
     */
    override fun getRepoDetails(id: Long): Flow<DataResult<GithubRepo>> =
        storage.all().map { repos ->
            val repo = repos.firstOrNull { it.id == id }
            if (repo != null) {
                DataResult.Success(repo)
            } else {
                DataResult.Error(DataError.NotFound)
            }
        }
}
