// Copyright 2025, Livefront sample app project contributors
// SPDX-License-Identifier: Apache-2.0

package com.addhen.livefront.fakes

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.testing.asPagingSourceFactory
import com.addhen.livefront.data.model.GithubRepo
import com.addhen.livefront.data.respository.GithubRepoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeGithubRepoRepository : GithubRepoRepository {
    private val queryResultsMap = mutableMapOf<String, List<GithubRepo>>()
    val results = mutableListOf<GithubRepo>()
    var shouldTriggerError = false

    fun setReposForQuery(query: String, repos: List<GithubRepo>) {
        queryResultsMap[query] = repos
    }

    override fun searchRepos(query: String): Flow<PagingData<GithubRepo>> {
        if (shouldTriggerError) {
            return flow { throw Exception("Fake error") } // Simulate error
        }
        this.results.addAll(queryResultsMap[query] ?: emptyList())

        return Pager(
            config = PagingConfig(
                pageSize = 10,
            ),
        ) {
            results.asPagingSourceFactory().invoke()
        }.flow
    }

    override fun getRepoDetails(id: Long): Flow<GithubRepo?> {
        TODO("Not yet implemented as it's not supported in this test case")
    }
}
