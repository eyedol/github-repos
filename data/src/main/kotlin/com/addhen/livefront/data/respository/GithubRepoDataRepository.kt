package com.addhen.livefront.data.respository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.addhen.livefront.data.api.GithubApiService
import com.addhen.livefront.data.model.GithubRepo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GithubRepoDataRepository @Inject constructor(
    private val apiService: GithubApiService,
): GithubRepoRepository {

    override fun searchRepos(query: String): Flow<PagingData<GithubRepo>> {
        return Pager(
            config = PagingConfig(
                pageSize = 30,
                enablePlaceholders = false,
                maxSize = 100,
            ),
            pagingSourceFactory = { GithubRepoPagingSource(apiService, query) },
        ).flow
    }
}