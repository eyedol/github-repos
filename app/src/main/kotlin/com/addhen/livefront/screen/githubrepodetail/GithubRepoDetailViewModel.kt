// Copyright 2025, Livefront sample app project contributors
// SPDX-License-Identifier: Apache-2.0

package com.addhen.livefront.screen.githubrepodetail

import androidx.compose.runtime.Stable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.addhen.livefront.data.model.GithubRepo
import com.addhen.livefront.data.respository.GithubRepoRepository
import com.addhen.livefront.ui.navigation.GithubRepoDetailRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class GithubRepoDetailViewModel @Inject constructor(
    repository: GithubRepoRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val route = savedStateHandle.toRoute<GithubRepoDetailRoute>()
    private val _uiState = MutableStateFlow(RepoDetailUiState())
    val uiState: StateFlow<RepoDetailUiState> = _uiState.asStateFlow()

    val githubRepo: StateFlow<GithubRepo?> = repository.getRepoDetails(route.id)
        .onStart { _uiState.update { it.copy(isLoadingRepo = true, error = null) } }
        .catch { e ->
            Timber.d(e)
            _uiState.update { it.copy(isLoadingRepo = false, error = "Failed to load repo details: ${e.message}") }
        }
        .onEach {
            Timber.d("Repo details: $it")
            _uiState.update { it.copy(isLoadingRepo = false, error = null) }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null,
        )

    @Stable
    data class RepoDetailUiState(
        val isLoadingRepo: Boolean = true,
        val error: String? = null,
    )
}
