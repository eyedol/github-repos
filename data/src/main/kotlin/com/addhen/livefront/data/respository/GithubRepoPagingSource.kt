package com.addhen.livefront.data.respository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.addhen.livefront.data.api.GithubApiService
import com.addhen.livefront.data.api.dto.GithubRepoDto
import com.addhen.livefront.data.api.dto.GithubRepoDto.ContributorDto
import com.addhen.livefront.data.api.dto.GithubRepoDto.OwnerDto
import com.addhen.livefront.data.model.GithubRepo
import com.addhen.livefront.data.model.GithubRepo.Contributor
import com.addhen.livefront.data.model.GithubRepo.Owner
import timber.log.Timber

class GithubRepoPagingSource(
    private val apiService: GithubApiService,
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
                    val contributors = apiService.getContributors(owner, repoName)
                    repo.copy(contributor = contributors.firstOrNull())
                } catch (e: Exception) {
                    // If we fail to fetch contributors, return repo without a contributor
                    Timber.e(e, "Failed to fetch contributors for repo: ${repo.full_name}")
                    repo
                }
            }

            LoadResult.Page(
                data = reposWithContributors.map { it.toGithubRepo() },
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (response.items.isEmpty()) null else page + 1,
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}

private fun GithubRepoDto.toGithubRepo(): GithubRepo {
    return GithubRepo(
        id = id,
        fullName = full_name,
        description = description,
        stargazersCount = stargazers_count,
        owner = owner.toOwner(),
        contributor = contributor?.toContributor(),
    )
}

private fun OwnerDto.toOwner(): Owner {
    return Owner(
        id = id,
        login = login,
        avatarUrl = avatar_url,
    )
}

private fun ContributorDto.toContributor(): Contributor {
    return Contributor(
        id = id,
        login = login,
        contributions = contributions,
        avatarUrl = avatar_url,
    )
}
