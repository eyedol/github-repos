// Copyright 2025, Livefront sample app project contributors
// SPDX-License-Identifier: Apache-2.0

package com.addhen.livefront.data.respository

import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import app.cash.turbine.test
import com.addhen.livefront.data.api.FakeGithubApiService
import com.addhen.livefront.data.api.dto.GithubRepoDto
import com.addhen.livefront.data.api.dto.GithubRepoResponseDto
import com.addhen.livefront.data.cache.MemoryStorage
import com.addhen.livefront.data.fakes.fakes
import com.addhen.livefront.data.model.GithubRepo
import com.addhen.livefront.testing.CoroutineTestRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import retrofit2.HttpException
import java.net.HttpURLConnection

@OptIn(ExperimentalCoroutinesApi::class)
@DisplayName("Github Repo Paging Source Tests")
class GithubRepoPagingSourceTest {

    @JvmField
    @RegisterExtension
    val coroutineTestRule = CoroutineTestRule()

    private lateinit var pagingSource: GithubRepoPagingSource
    private lateinit var fakeApiService: FakeGithubApiService
    private lateinit var memoryStorage: MemoryStorage<GithubRepo>
    private val testQuery = "android"

    @BeforeEach
    fun setup() {
        fakeApiService = FakeGithubApiService()
        fakeApiService.start()

        memoryStorage = MemoryStorage<GithubRepo>(coroutineTestRule.dispatcher)
        pagingSource = GithubRepoPagingSource(fakeApiService, memoryStorage, testQuery)
    }

    @AfterEach
    fun tearDown() {
        fakeApiService.shutDown()
    }

    @Nested
    @DisplayName("getRefreshKey() Tests")
    inner class GetRefreshKeyTests {

        @Test
        @DisplayName("When PagingState has no anchor position, getRefreshKey returns null")
        fun `getRefreshKey with no anchor position`() {
            val pagingState = PagingState<Int, GithubRepo>(
                pages = listOf(),
                anchorPosition = null,
                config = PagingConfig(pageSize = 20),
                leadingPlaceholderCount = 0,
            )

            val result = pagingSource.getRefreshKey(pagingState)

            assertEquals(null, result)
        }

        @Test
        @DisplayName("When PagingState has anchor position but no closest page, getRefreshKey returns null")
        fun `getRefreshKey with anchor position but no closest page`() {
            val pagingState = PagingState<Int, GithubRepo>(
                pages = listOf(),
                anchorPosition = 0,
                config = PagingConfig(pageSize = 20),
                leadingPlaceholderCount = 0,
            )

            val result = pagingSource.getRefreshKey(pagingState)

            assertEquals(null, result)
        }

        @Test
        @DisplayName("When closest page has prevKey, getRefreshKey returns prevKey + 1")
        fun `getRefreshKey with prev key`() = runTest {
            val fakeRepoResponse = GithubRepoResponseDto.fakes()
            val jsonResponse = fakeApiService.json.encodeToString(fakeRepoResponse)
            val mockResponse = MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_OK)
                .setBody(jsonResponse)
            fakeApiService.enqueue(mockResponse)
            val githubRepo = fakeApiService.getRepos("android", 1, 2)
            val page = PagingSource.LoadResult.Page(
                data = githubRepo.items.toGithubRepoList(),
                prevKey = 3,
                nextKey = 5,
            )
            val pagingState = PagingState(
                pages = listOf(page),
                anchorPosition = 0,
                config = PagingConfig(pageSize = 20),
                leadingPlaceholderCount = 0,
            )

            val result = pagingSource.getRefreshKey(pagingState)

            assertEquals(4, result)
        }

        @Test
        @DisplayName("When closest page has only nextKey, getRefreshKey returns nextKey - 1")
        fun `getRefreshKey with next key`() = runTest {
            val fakeRepoResponse = GithubRepoResponseDto.fakes()
            val jsonResponse = fakeApiService.json.encodeToString(fakeRepoResponse)
            val mockResponse = MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_OK)
                .setBody(jsonResponse)
            fakeApiService.enqueue(mockResponse)
            val githubRepo = fakeApiService.getRepos("android", 1, 2)

            val page = PagingSource.LoadResult.Page(
                data = githubRepo.items.toGithubRepoList(),
                prevKey = null,
                nextKey = 5,
            )
            val pagingState = PagingState(
                pages = listOf(page),
                anchorPosition = 0,
                config = PagingConfig(pageSize = 20),
                leadingPlaceholderCount = 0,
            )

            val result = pagingSource.getRefreshKey(pagingState)

            assertEquals(4, result)
        }
    }

    @Nested
    @DisplayName("load() Tests")
    inner class LoadTests {

        @Test
        @DisplayName("When load is called with null key, it uses page 1 and returns success")
        fun `load with null key returns first page`() = runTest {
            fakeApiService.setDispatcher(LoadDispatcher())
            val loadSize = 20
            val loadParams = PagingSource.LoadParams.Refresh<Int>(
                key = null,
                loadSize = loadSize,
                placeholdersEnabled = false,
            )

            val result = pagingSource.load(loadParams)

            assertTrue(result is PagingSource.LoadResult.Page)
            result as PagingSource.LoadResult.Page<Int, GithubRepo>

            assertEquals(3, result.data.size)
            assertEquals(null, result.prevKey) // First page has no prev key
            assertEquals(2, result.nextKey) // Next key should be current page + 1
            assertEquals("fakeOwner1", result.data[0].contributor?.login)
            assertEquals("fakeOwner1", result.data[1].contributor?.login)

            val request = fakeApiService.takeRequest()
            assertEquals("/search/repositories?sort=stars&order=desc&q=android&page=1&per_page=20", request.path)
            memoryStorage.all().test { assertEquals(result.data, awaitItem()) }
        }

        @Test
        @DisplayName("When load is called with a specific page key, it uses that page")
        fun `load with specific page`() = runTest {
            fakeApiService.setDispatcher(LoadDispatcher())
            val page = 3
            val loadSize = 10
            val loadParams = PagingSource.LoadParams.Refresh(
                key = page,
                loadSize = loadSize,
                placeholdersEnabled = false,
            )

            val result = pagingSource.load(loadParams)

            assertTrue(result is PagingSource.LoadResult.Page)
            result as PagingSource.LoadResult.Page

            assertEquals(3, result.data.size)
            assertEquals(2, result.prevKey) // Previous page
            assertEquals(4, result.nextKey) // Next page

            val request = fakeApiService.takeRequest()
            assertEquals("/search/repositories?sort=stars&order=desc&q=android&page=3&per_page=10", request.path)
            memoryStorage.all().test {
                assertEquals(result.data, awaitItem())
            }
        }

        @Test
        @DisplayName("When API returns empty items list, nextKey should be null")
        fun `load with empty response`() = runTest {
            val mockResponse = MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_OK)
                .setBody("""{"items": []}""")

            fakeApiService.enqueue(mockResponse)
            val loadParams = PagingSource.LoadParams.Refresh(
                key = 1,
                loadSize = 10,
                placeholdersEnabled = false,
            )

            val result = pagingSource.load(loadParams)

            assertTrue(result is PagingSource.LoadResult.Page)
            result as PagingSource.LoadResult.Page

            assertEquals(0, result.data.size)
            assertEquals(null, result.nextKey)
            memoryStorage.all().test {
                assertEquals(result.data, awaitItem())
            }
        }

        @Test
        @DisplayName("When getContributors fails for a repo, repo should still be included without contributor")
        fun `load with contributor failure`() = runTest {
            fakeApiService.setDispatcher(LoadDispatcher(contributorFailed = true))
            val loadParams = PagingSource.LoadParams.Refresh<Int>(
                key = null,
                loadSize = 10,
                placeholdersEnabled = false,
            )

            val result = pagingSource.load(loadParams)

            assertTrue(result is PagingSource.LoadResult.Page)
            result as PagingSource.LoadResult.Page

            assertEquals(3, result.data.size)
            assertEquals(null, result.data[0].contributor?.login)
            assertEquals(null, result.data[1].contributor)
            memoryStorage.all().test {
                assertEquals(result.data, awaitItem())
            }
        }

        @Test
        @DisplayName("When getRepos API call fails, load should return Error result")
        fun `load with api failure`() = runTest {
            fakeApiService.setDispatcher(LoadDispatcher(reposFailed = true))
            val loadParams = PagingSource.LoadParams.Refresh<Int>(
                key = null,
                loadSize = 10,
                placeholdersEnabled = false,
            )

            val result = pagingSource.load(loadParams)

            assertTrue(result is PagingSource.LoadResult.Error)
            result as PagingSource.LoadResult.Error

            assertTrue(result.throwable is HttpException)
            assertEquals("HTTP 500 Server Error", result.throwable.message)
            memoryStorage.all().test { assertEquals(emptyList<GithubRepo>(), awaitItem()) }
        }
    }

    inner class LoadDispatcher(val contributorFailed: Boolean = false, val reposFailed: Boolean = false) : Dispatcher() {
        override fun dispatch(request: RecordedRequest): MockResponse {
            return when {
                request.path?.startsWith("/search/repositories") == true -> {
                    if (reposFailed) {
                        return MockResponse().setResponseCode(HttpURLConnection.HTTP_INTERNAL_ERROR)
                    }

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
                        html_url = "Fake repo url"
                    )

                    val fakeRepoResponse = GithubRepoResponseDto(
                        items = (1..3).map { githubRepo.copy(id = it.toLong()) },
                    )
                    val jsonResponse = fakeApiService.json.encodeToString(fakeRepoResponse)
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
                    val contributorJsonResponse = fakeApiService.json.encodeToString(fakeContributorResponse)
                    MockResponse()
                        .setResponseCode(HttpURLConnection.HTTP_OK)
                        .setBody(contributorJsonResponse)
                }
                else -> {
                    MockResponse().setResponseCode(404)
                }
            }
        }
    }
}
