// Copyright 2025, Livefront sample app project contributors
// SPDX-License-Identifier: Apache-2.0

package com.addhen.livefront.screen.githubrepodetail

import androidx.compose.runtime.Stable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.addhen.livefront.data.model.GithubRepo
import com.addhen.livefront.data.model.decodeToGithubRepo
import com.addhen.livefront.data.model.encodeToString
import com.addhen.livefront.data.respository.GithubRepoRepository
import com.addhen.livefront.ui.navigation.GithubRepoDetailRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import timber.log.Timber
import javax.inject.Inject

private const val REPO_SAVED_STATE_KEY = "GitHubRepo"

@HiltViewModel
class GithubRepoDetailViewModel @Inject constructor(
    repository: GithubRepoRepository,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val route = savedStateHandle.toRoute<GithubRepoDetailRoute>()
    private val restoredRepo: String? = savedStateHandle.get<String>(REPO_SAVED_STATE_KEY)
    private val _uiState = MutableStateFlow(GithubRepoDetailUiState())
    val uiState: StateFlow<GithubRepoDetailUiState> = _uiState.asStateFlow()

    val githubRepo: StateFlow<GithubRepo?> = repository.getRepoDetails(route.id)
        .onStart { _uiState.update { it.copy(isLoadingRepo = true, error = null) } }
        .catch { e ->
            Timber.e(e)
            _uiState.update { it.copy(isLoadingRepo = false, error = "Failed to load repo details: ${e.message}") }
        }
        .map {
            Timber.d("Repo details: $it")
            it ?: restoredRepo?.decodeToGithubRepo()
        }
        .onEach {
            /*
             * Save the `GithubRepo` object(serialized to a string) in `SavedStateHandle` as a quick
             * way to persist the data across configuration changes and process deaths.
             *
             * In a real world application this won't be done but the data would be retrieved from
             * a persistent storage like Room or DataStore. Using this approach as a quick win to
             * fix the issue at hand and for this exercise.
             *
             */
            savedStateHandle[REPO_SAVED_STATE_KEY] = it?.encodeToString()
            _uiState.update { it.copy(isLoadingRepo = false, error = null) }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null,
        )

    override fun onCleared() {
        super.onCleared()
        savedStateHandle.remove<String>(REPO_SAVED_STATE_KEY)
    }

    @Stable
    data class GithubRepoDetailUiState(
        val isLoadingRepo: Boolean = true,
        val error: String? = null,
    )
}
