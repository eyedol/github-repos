// Copyright 2025, Livefront sample app project contributors
// SPDX-License-Identifier: Apache-2.0

package com.addhen.livefront.data.respository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.addhen.livefront.data.api.GithubApiService
import com.addhen.livefront.data.cache.StorageInterface
import com.addhen.livefront.data.model.GithubRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GithubRepoDataRepository @Inject constructor(
    private val apiService: GithubApiService,
    private val storage: StorageInterface<GithubRepo>,
) : GithubRepoRepository {

    override fun searchRepos(query: String): Flow<PagingData<GithubRepo>> {
        return Pager(
            config = PagingConfig(pageSize = 10),
            pagingSourceFactory = { GithubRepoPagingSource(apiService, storage, query) },
        ).flow
    }

    override fun getRepoDetails(id: Long): Flow<GithubRepo?> {
        return storage.all().map {
            it.firstOrNull { it.id == id }
        }
    }
}
