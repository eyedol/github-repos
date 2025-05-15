// Copyright 2025, Livefront sample app project contributors
// SPDX-License-Identifier: Apache-2.0

package com.addhen.livefront.screen.fakes

import androidx.paging.PagingData
import com.addhen.livefront.data.model.DataResult
import com.addhen.livefront.data.model.GithubRepo
import com.addhen.livefront.data.respository.GithubRepoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeGithubRepoRepository : GithubRepoRepository {

    private val repoDetailsMap = mutableMapOf<Long, GithubRepo?>()
    private var shouldError = false

    fun setRepoDetails(id: Long, repo: GithubRepo?) {
        repoDetailsMap[id] = repo
    }
    fun setShouldError(shouldError: Boolean) {
        this.shouldError = shouldError
    }

    override fun searchRepos(query: String): Flow<PagingData<GithubRepo>> {
        TODO("Not yet implemented as we don't need to test this")
    }

    override fun getRepoDetails(id: Long): Flow<DataResult<GithubRepo?>> {
        if (shouldError) {
            return flow { throw Exception("Fake error") } // Simulate error
        }
        return flow { emit(DataResult.Success(repoDetailsMap[id])) }
    }
}
