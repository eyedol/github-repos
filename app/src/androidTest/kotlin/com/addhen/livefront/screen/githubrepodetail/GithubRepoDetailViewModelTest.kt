// Copyright 2025, Livefront sample app project contributors
// SPDX-License-Identifier: Apache-2.0

package com.addhen.livefront.screen.githubrepodetail

import androidx.lifecycle.SavedStateHandle
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import com.addhen.livefront.data.model.GithubRepo
import com.addhen.livefront.screen.fakes.FakeGithubRepoRepository
import com.addhen.livefront.screen.fakes.fakes
import com.addhen.livefront.testing.CoroutineTestRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNull
import org.junit.jupiter.api.extension.RegisterExtension
import org.junit.runner.RunWith

/**
 * Using instrumented test for [GithubRepoDetailViewModel] due to a bug in
 * type navigation compose. Technically this can be a pure unit test.
 *
 * See https://issuetracker.google.com/issues/349807172?pli=1 for the bug repo
 */
@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
@DisplayName("Github Repo Detail ViewModel Tests")
class GithubRepoDetailViewModelTest {

    @JvmField
    @RegisterExtension
    val coroutineTestRule = CoroutineTestRule()

    private lateinit var fakeRepository: FakeGithubRepoRepository
    private lateinit var fakeSavedStateHandle: SavedStateHandle
    private lateinit var viewModel: GithubRepoDetailViewModel

    @BeforeEach
    fun setup() {
        val initialState = mapOf("id" to 1234L)
        fakeSavedStateHandle = SavedStateHandle(initialState)
        fakeRepository = FakeGithubRepoRepository()
    }

    @Test
    @DisplayName("When Repo ID is provided, emit loading state and then emit the fetched repo ")
    fun uiStateEmitsLoadingThenSuccess() = runTest {
        val expectedRepo = GithubRepo.fakes(1234)
        fakeRepository.setRepoDetails(1234, expectedRepo)
        viewModel = GithubRepoDetailViewModel(
            repository = fakeRepository,
            savedStateHandle = fakeSavedStateHandle,
        )
        advanceUntilIdle()

        viewModel.uiState.test {
            assertEquals(
                GithubRepoDetailViewModel.GithubRepoDetailUiState(isLoadingRepo = true),
                awaitItem(),
            )
        }

        viewModel.githubRepo.test {
            assertEquals(expectedRepo, awaitItem())
        }
    }

    @Test
    @DisplayName("When repo ID is provided, but repository throws exception, emit error state")
    fun uiStateEmitsErrorMessageWhenRepositoryThrowsException() = runTest {
        val expectedRepo = GithubRepo.fakes(1234)
        fakeRepository.setRepoDetails(1234, expectedRepo)
        fakeRepository.setShouldError(shouldError = true)
        viewModel = GithubRepoDetailViewModel(
            repository = fakeRepository,
            savedStateHandle = fakeSavedStateHandle,
        )

        viewModel.githubRepo.test {
            assertNull(awaitItem())
        }

        viewModel.uiState.test {
            assertEquals(
                GithubRepoDetailViewModel.GithubRepoDetailUiState(
                    isLoadingRepo = false,
                    error = "Failed to load repo details: Fake error",
                ),
                awaitItem(),
            )
        }
    }
}
