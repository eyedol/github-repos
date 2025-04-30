// Copyright 2025, Livefront sample app project contributors
// SPDX-License-Identifier: Apache-2.0

package com.addhen.livefront.data.respository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.addhen.livefront.data.api.GithubApiService
import com.addhen.livefront.data.api.dto.GithubRepoDto
import com.addhen.livefront.data.api.dto.GithubRepoDto.ContributorDto
import com.addhen.livefront.data.api.dto.GithubRepoDto.OwnerDto
import com.addhen.livefront.data.cache.StorageInterface
import com.addhen.livefront.data.model.GithubRepo
import com.addhen.livefront.data.model.GithubRepo.Contributor
import com.addhen.livefront.data.model.GithubRepo.Owner
import kotlinx.coroutines.CancellationException
import timber.log.Timber

/**
 * A `PagingSource` implementation for loading paginated GitHub repositories
 * based on a specific search query. This class fetches data from the provided
 * GitHub API service and stores it using the given storage interface.
 *
 *
 * @param apiService The API service for fetching GitHub repositories and related data.
 * @param storage The storage interface for saving fetched repositories.
 * @param query The search query string used to retrieve the repositories.
 */
class GithubRepoPagingSource(
    private val apiService: GithubApiService,
    private val storage: StorageInterface<GithubRepo>,
    private val query: String,
) : PagingSource<Int, GithubRepo>() {

    override fun getRefreshKey(state: PagingState<Int, GithubRepo>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, GithubRepo> {
        return try {
            val page = params.key ?: 1
            val response = apiService.getRepos(
                query = query,
                page = page,
                perPage = params.loadSize,
            )

            val reposWithContributors = response.items.map { repo ->
                val (owner, repoName) = repo.full_name.split("/")
                try {
                    val contributors = apiService.getContributors(owner, repoName, 10)
                    repo.toGithubRepo().copy(
                        contributor = contributors.firstOrNull()?.toContributor(),
                        contributors = contributors.toContributorList(),
                    )
                } catch (e: Exception) {
                    // If we fail to fetch contributors, return repo without a contributor
                    Timber.e(e, "Failed to fetch contributors for repo: ${repo.full_name}")
                    repo.toGithubRepo()
                }
            }

            storage.addAll(reposWithContributors)
            LoadResult.Page(
                data = reposWithContributors,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (response.items.isEmpty()) null else page + 1,
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}

internal fun GithubRepoDto.toGithubRepo(): GithubRepo {
    return GithubRepo(
        id = id,
        fullName = full_name,
        description = description,
        stargazersCount = stargazers_count,
        htmlUrl = html_url,
        owner = owner.toOwner(),
        contributor = contributor?.toContributor(),
    )
}

internal fun List<GithubRepoDto>.toGithubRepoList(): List<GithubRepo> {
    return map { it.toGithubRepo() }
}

internal fun OwnerDto.toOwner(): Owner {
    return Owner(
        id = id,
        login = login,
        avatarUrl = avatar_url,
    )
}

internal fun ContributorDto.toContributor(): Contributor {
    return Contributor(
        id = id,
        login = login,
        contributions = contributions,
        avatarUrl = avatar_url,
        htmlUrl = html_url,
    )
}

internal fun List<ContributorDto>.toContributorList(): List<Contributor> {
    return map { it.toContributor() }
}
