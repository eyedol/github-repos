package com.addhen.livefront.screen.githubrepodetail

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.addhen.livefront.data.model.DataError
import com.addhen.livefront.data.model.GithubRepo
import com.addhen.livefront.data.model.encodeToString
import com.addhen.livefront.fakes.FakeGithubRepoRepository
import com.addhen.livefront.fakes.fakes
import com.addhen.livefront.testing.CoroutineTestRule
import com.addhen.livefront.testing.SavedStateHandleRule
import com.addhen.livefront.ui.navigation.GithubRepoDetailRoute
import io.mockk.every
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

/**
 *  You will see the use of mockk here as the entire project prefers the use of
 *  fakes over mocks. This particular unit test is mixing fakes and mockk objects due
 *  to a bug in the type navigation compose library. The workaround for the issue is to
 *  use mockk to mock the SavedStateHandle object.
 *
 *  **NOTE:** Once the bug is fixed, remove the use of mockk and the [SavedStateHandleRule] rule.
 *
 *  See the bug report for more info: https://issuetracker.google.com/issues/349807172?pli=1
 */
@DisplayName("Github Repo Detail ViewModel Tests")
class GithubRepoDetailViewModelTest {
    private val route = GithubRepoDetailRoute(1234L)

    @OptIn(ExperimentalCoroutinesApi::class)
    @JvmField
    @RegisterExtension
    val coroutineTestRule = CoroutineTestRule()

    @JvmField
    @RegisterExtension
    val savedStateHandleRule = SavedStateHandleRule(route)

    private lateinit var fakeRepository: FakeGithubRepoRepository
    private lateinit var fakeSavedStateHandle: SavedStateHandle
    private lateinit var viewModel: GithubRepoDetailViewModel

    @BeforeEach
    fun setup() = runTest {
        fakeSavedStateHandle = savedStateHandleRule.savedStateHandleMock
        fakeRepository = FakeGithubRepoRepository()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    @DisplayName("When Repo ID is provided, emit loading state and then emit the fetched repo ")
    fun uiStateEmitsLoadingThenSuccess() = runTest {
        val expectedRepo = GithubRepo.fakes(1234)
        every { fakeSavedStateHandle.get<Any>(any()) } returns expectedRepo.encodeToString()
        viewModel = GithubRepoDetailViewModel(
            repository = fakeRepository,
            savedStateHandle = fakeSavedStateHandle,
        )

        viewModel.uiState.test {
            fakeRepository.emitRepoDetailsSuccess(expectedRepo)

            assertEquals(
                GithubRepoDetailViewModel.GithubRepoDetailUiState.Loading,
                awaitItem(),
            )

            assertEquals(
                GithubRepoDetailViewModel.GithubRepoDetailUiState.Success(expectedRepo),
                awaitItem()
            )
        }
    }

    @Test
    @DisplayName("When repo ID is provided, but repository throws exception, emit error state")
    fun uiStateEmitsErrorMessageWhenRepositoryThrowsException() = runTest {
        val expectedRepo = GithubRepo.fakes(1234)
        every { fakeSavedStateHandle.get<Any>(any()) } returns expectedRepo.encodeToString()
        viewModel = GithubRepoDetailViewModel(
            repository = fakeRepository,
            savedStateHandle = fakeSavedStateHandle,
        )


        viewModel.uiState.test {
            fakeRepository.emitRepoDetailsError(DataError.Unknown("Fake error"))
            assertEquals(
                GithubRepoDetailViewModel.GithubRepoDetailUiState.Loading,
                awaitItem(),
            )
            assertEquals(
                GithubRepoDetailViewModel.GithubRepoDetailUiState.Error(
                    "Fake error",
                ),
                awaitItem(),
            )
        }
    }
}