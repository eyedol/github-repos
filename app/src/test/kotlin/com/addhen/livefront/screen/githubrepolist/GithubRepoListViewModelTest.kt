// Copyright 2025, Livefront sample app project contributors
// SPDX-License-Identifier: Apache-2.0

package com.addhen.livefront.screen.githubrepolist

import androidx.paging.testing.asSnapshot
import com.addhen.livefront.data.model.GithubRepo
import com.addhen.livefront.fakes.FakeGithubRepoRepository
import com.addhen.livefront.fakes.fakes
import com.addhen.livefront.testing.CoroutineTestRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

@OptIn(ExperimentalCoroutinesApi::class)
@DisplayName("Github Repo List View Model Tests")
class GithubRepoListViewModelTest {

    @JvmField
    @RegisterExtension
    val coroutineTestRule = CoroutineTestRule()

    private lateinit var fakeRepository: FakeGithubRepoRepository
    private lateinit var viewModel: GithubRepoListViewModel

    @BeforeEach
    fun setup() {
        fakeRepository = FakeGithubRepoRepository()
    }

    @Test
    @DisplayName("When searchQuery changes, searchResults should update")
    fun `searchResults should update when searchQuery changes`() = runTest {
        fakeRepository.setReposForQuery("stars:>0", (1..5).map { GithubRepo.fakes(it.toLong()) })
        val expected = fakeRepository.results
        viewModel = GithubRepoListViewModel(repository = fakeRepository)

        val actual = viewModel.searchResults.asSnapshot()
        assertEquals(expected, actual)
    }

    @Test
    @DisplayName("When searchResults errors, searchResults should error")
    fun `searchResults should error when repository errors`() = runTest {
        fakeRepository.shouldTriggerError = true
        viewModel = GithubRepoListViewModel(repository = fakeRepository)

        val runTestBlock: () -> Unit = {
            runTest { viewModel.searchResults.asSnapshot { refresh() } }
        }

        assertThrows<Exception>(Exception::class.java, runTestBlock)
    }
}
