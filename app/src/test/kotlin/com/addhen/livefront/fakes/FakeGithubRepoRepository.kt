// Copyright 2025, Livefront sample app project contributors
// SPDX-License-Identifier: Apache-2.0

package com.addhen.livefront.fakes

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.testing.asPagingSourceFactory
import com.addhen.livefront.data.model.DataError
import com.addhen.livefront.data.model.DataResult
import com.addhen.livefront.data.model.GithubRepo
import com.addhen.livefront.data.respository.GithubRepoRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

class FakeGithubRepoRepository: GithubRepoRepository {
    private val detailsFlow = MutableSharedFlow<DataResult<GithubRepo>>(replay = 1)
    private val searchQueryResults = mutableListOf<GithubRepo>()
    private var searchErrorToReturn: Throwable? = null

    suspend fun emitRepoDetailsSuccess(repo: GithubRepo) {
        detailsFlow.emit(DataResult.Success(repo))
    }

    suspend fun emitRepoDetailsError(error: DataError) {
        detailsFlow.emit(DataResult.Error(error))
    }

    fun emitSearchResults(repos: List<GithubRepo>) {
        searchErrorToReturn = null
        searchQueryResults.addAll(repos)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun emitSearchResultsError(error: Throwable) {
        searchErrorToReturn = error
        searchQueryResults.clear()
    }

    override fun searchRepos(query: String): Flow<PagingData<GithubRepo>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
            ),
            pagingSourceFactory = {
                val currentError = searchErrorToReturn
                if (currentError != null) {
                    object : PagingSource<Int, GithubRepo>() {
                        override suspend fun load(params: LoadParams<Int>): LoadResult<Int, GithubRepo> {
                            return LoadResult.Error(currentError)
                        }
                        override fun getRefreshKey(state: PagingState<Int, GithubRepo>): Int? = null
                    }
                } else {
                    searchQueryResults.asPagingSourceFactory().invoke()
                }
            }
        ).flow
    }

    override fun getRepoDetails(id: Long): Flow<DataResult<GithubRepo?>> {
        return detailsFlow
    }
}
