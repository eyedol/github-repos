// Copyright 2025, Livefront sample app project contributors
// SPDX-License-Identifier: Apache-2.0

package com.addhen.livefront.screen.githubrepolist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.addhen.livefront.data.model.GithubRepo
import com.addhen.livefront.data.respository.GithubRepoRepository
import com.addhen.livefront.NetworkConnectivityRepository
import com.addhen.livefront.ConnectionState
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@HiltViewModel
class GithubRepoListViewModel @Inject constructor(
    private val repository: GithubRepoRepository,
    networkConnectivityRepository: NetworkConnectivityRepository
) : ViewModel() {
    private val searchQuery = MutableStateFlow("stars:>0")

    val connectivityState = networkConnectivityRepository.connectivity
        .distinctUntilChanged()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ConnectionState.Available)

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val searchResults: Flow<PagingData<GithubRepo>> = searchQuery
        .filterNot { it.isEmpty() }
        .flatMapLatest { query -> repository.searchRepos(query) }
        .cachedIn(viewModelScope)
}
