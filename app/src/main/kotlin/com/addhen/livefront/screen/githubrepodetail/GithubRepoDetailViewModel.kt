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
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
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

    val uiState: StateFlow<GithubRepoDetailUiState> = repository.getRepoDetails(route.id)
        .map { githubRepo ->
            when {
                githubRepo != null -> {
                    /*
                     * Save the `GithubRepo` object(serialized to a string) in `SavedStateHandle` as a quick
                     * way to persist the data across configuration changes and process deaths.
                     *
                     * In a real world application this won't be done but the data would be retrieved from
                     * a persistent storage like Room or DataStore. Using this approach as a quick win to
                     * fix the issue at hand and for this exercise.
                     *
                     */
                    savedStateHandle[REPO_SAVED_STATE_KEY] = githubRepo.encodeToString()
                    GithubRepoDetailUiState.Success(githubRepo)
                }
                restoredRepo != null -> {
                    val restored = restoredRepo.decodeToGithubRepo()
                    GithubRepoDetailUiState.Success(restored)
                }
                else -> GithubRepoDetailUiState.Empty
            }
        }
        .catch { e ->
            Timber.e(e)
            emit(GithubRepoDetailUiState.Error(e.message ?: "Unknown error"))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = GithubRepoDetailUiState.Loading,
        )

    override fun onCleared() {
        super.onCleared()
        savedStateHandle.remove<String>(REPO_SAVED_STATE_KEY)
    }

    @Stable
    sealed interface GithubRepoDetailUiState {
        object Empty : GithubRepoDetailUiState
        object Loading : GithubRepoDetailUiState
        data class Success(val repo: GithubRepo) : GithubRepoDetailUiState
        data class Error(val message: String) : GithubRepoDetailUiState
    }
}
