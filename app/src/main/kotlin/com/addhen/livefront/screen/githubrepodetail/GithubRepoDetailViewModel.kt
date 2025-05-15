// Copyright 2025, Livefront sample app project contributors
// SPDX-License-Identifier: Apache-2.0

package com.addhen.livefront.screen.githubrepodetail

import androidx.compose.runtime.Stable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.addhen.livefront.data.model.DataError
import com.addhen.livefront.data.model.DataResult
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
        .map { result ->
            when (result) {
                is DataResult.Success -> {
                    val githubRepo = result.data as GithubRepo
                    savedStateHandle[REPO_SAVED_STATE_KEY] = githubRepo.encodeToString()
                    GithubRepoDetailUiState.Success(githubRepo)
                }
                is DataResult.Error -> when (val error = result.error) {
                    DataError.NotFound -> {
                        // Try restoring from SavedStateHandle
                        when {
                            restoredRepo != null -> {
                                val repo = restoredRepo.decodeToGithubRepo()
                                GithubRepoDetailUiState.Success(repo)
                            }
                            else -> {
                                GithubRepoDetailUiState.Empty
                            }
                        }
                    }
                    DataError.Network -> GithubRepoDetailUiState.Error("Network error, please try again.")
                    is DataError.Unknown -> GithubRepoDetailUiState.Error(error.message)
                }
            }
        }
        .catch { e ->
            // There can be an error from de/serialization of the [GithubRepo] object
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
