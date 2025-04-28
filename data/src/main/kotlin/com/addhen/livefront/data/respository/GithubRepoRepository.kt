package com.addhen.livefront.data.respository

import androidx.paging.PagingData
import com.addhen.livefront.data.model.GithubRepo
import kotlinx.coroutines.flow.Flow

interface GithubRepoRepository {
    fun searchRepos(query: String): Flow<PagingData<GithubRepo>>

    fun getRepoDetails(id: Long): Flow<GithubRepo?>
}