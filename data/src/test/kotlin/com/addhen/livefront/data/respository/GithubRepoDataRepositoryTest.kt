// Copyright 2025, Livefront sample app project contributors
// SPDX-License-Identifier: Apache-2.0

package com.addhen.livefront.data.respository

import androidx.paging.testing.asSnapshot
import app.cash.turbine.test
import com.addhen.livefront.data.api.FakeGithubApiService
import com.addhen.livefront.data.api.dto.GithubRepoDto
import com.addhen.livefront.data.api.dto.GithubRepoResponseDto
import com.addhen.livefront.data.api.dto.fakes
import com.addhen.livefront.data.cache.MemoryStorage
import com.addhen.livefront.data.model.GithubRepo
import com.addhen.livefront.testing.CoroutineTestRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import retrofit2.HttpException
import java.net.HttpURLConnection

@ExperimentalCoroutinesApi
@DisplayName("Github Repository Data Repository Tests")
class GithubRepoDataRepositoryTest {
    @JvmField
    @RegisterExtension
    val coroutineTestRule = CoroutineTestRule()

    private lateinit var apiService: FakeGithubApiService
    private lateinit var repository: GithubRepoDataRepository
    private lateinit var memoryStorage: MemoryStorage<GithubRepo>

    @BeforeEach
    fun setup() {
        apiService = FakeGithubApiService()
        apiService.start()

        memoryStorage = MemoryStorage<GithubRepo>(coroutineTestRule.dispatcher)
        repository = GithubRepoDataRepository(apiService, memoryStorage)
    }

    @AfterEach
    fun tearDown() {
        apiService.shutDown()
    }

    @Test
    @DisplayName("When searchRepos is called with a successful API response, it emits PagingData containing expected repos")
    fun `searchRepos returns flow with correct paging configuration`() = runTest {
        val searchQuery = "kotlin"
        apiService.setDispatcher(LoadDispatcher())

        val result = repository.searchRepos(searchQuery).asSnapshot()

        val request = apiService.takeRequest()
        assertEquals("/search/repositories?sort=stars&order=desc&q=kotlin&page=1&per_page=30", request.path)
        assertTrue(result.isNotEmpty())
        memoryStorage.all().test {
            assertEquals(result, awaitItem())
        }
    }

    @Test
    @DisplayName("When searchRepos is called with an empty API response, it emits PagingData with empty list")
    fun `searchRepos empty response emits empty PagingData`() = runTest {
        val query = "non-existent-repo"
        val mockResponseJson = """{"items": [] }"""

        apiService.enqueue(
            MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_OK)
                .setBody(mockResponseJson),
        )

        val result = repository.searchRepos(query).asSnapshot()

        assertTrue(result.isEmpty())

        val request = apiService.takeRequest()
        assertEquals("/search/repositories?sort=stars&order=desc&q=non-existent-repo&page=1&per_page=30", request.path)
        memoryStorage.all().test {
            assertEquals(result, awaitItem())
        }
    }

    @Test
    @DisplayName("When searchRepos is called with an error API response, its PagingSource load returns Error")
    fun `searchRepos Api error PagingSource returns error`() {
        val query = "some-error-query"
        apiService.enqueue(
            MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_INTERNAL_ERROR)
                .setBody("Server Error"),
        )

        val runTestBlock: () -> Unit = {
            runTest {
                val result = repository.searchRepos(query).asSnapshot()

                memoryStorage.all().test {
                    assertEquals(result, awaitItem())
                }
            }
        }

        val exception = assertThrows(HttpException::class.java, runTestBlock)
        assertEquals("HTTP 500 Server Error", exception.message)

        val request = apiService.takeRequest()
        assertEquals("/search/repositories?sort=stars&order=desc&q=some-error-query&page=1&per_page=30", request.path)
    }

    inner class LoadDispatcher(val contributorFailed: Boolean = false, val reposFailed: Boolean = false) : Dispatcher() {
        override fun dispatch(request: RecordedRequest): MockResponse {
            return when {
                request.path?.startsWith("/search/repositories") == true -> {
                    if (reposFailed) {
                        return MockResponse().setResponseCode(HttpURLConnection.HTTP_INTERNAL_ERROR)
                    }

                    val fakeRepoResponse = getData()
                    val jsonResponse = apiService.json.encodeToString(fakeRepoResponse)
                    MockResponse()
                        .setResponseCode(HttpURLConnection.HTTP_OK)
                        .setBody(jsonResponse)
                }
                request.path?.startsWith("/repos") == true -> {
                    if (contributorFailed) {
                        return MockResponse().setResponseCode(HttpURLConnection.HTTP_INTERNAL_ERROR)
                    }
                    val fakeContributorResponse = (1..10)
                        .map { GithubRepoDto.ContributorDto.fakes(it.toLong()) }
                    val contributorJsonResponse = apiService.json.encodeToString(fakeContributorResponse)
                    MockResponse()
                        .setResponseCode(HttpURLConnection.HTTP_OK)
                        .setBody(contributorJsonResponse)
                }
                else -> {
                    MockResponse().setResponseCode(404)
                }
            }
        }

        fun getData(): GithubRepoResponseDto {
            val contributor = GithubRepoDto.ContributorDto(
                id = 1,
                login = "fakeOwner1",
                contributions = 2,
                avatar_url = "Fake contributor avatar url",
                html_url = "Fake contributor url",
            )

            val githubRepo = GithubRepoDto(
                id = 1,
                description = "Fake repo description",
                full_name = "Fake repo/full name",
                stargazers_count = 1,
                owner = GithubRepoDto.OwnerDto(
                    id = 1,
                    login = "fakeOwner1",
                    avatar_url = "Fake owner avatar url",
                ),
                contributor = if (contributorFailed) null else contributor,
                html_url = "Fake repo url",
                name = "Fake repo",
            )

            return GithubRepoResponseDto(
                items = (1..3).map { githubRepo.copy(id = it.toLong()) },
            )
        }
    }
}
