package com.addhen.livefront.data.api

import com.addhen.livefront.data.api.dto.GithubRepoDto
import com.addhen.livefront.data.api.dto.GithubRepoResponseDto
import com.addhen.livefront.data.api.dto.fakes
import com.addhen.livefront.data.di.DataModule
import com.addhen.livefront.testing.CoroutineTestRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import retrofit2.Retrofit
import retrofit2.HttpException
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.net.HttpURLConnection

@OptIn(ExperimentalCoroutinesApi::class)
@DisplayName("Github API Service Tests")
class GithubApiServiceTest {

    @JvmField
    @RegisterExtension
    val coroutineTestRule = CoroutineTestRule()

    private lateinit var mockWebServer: MockWebServer
    private lateinit var sut: GithubApiService
    private lateinit var json: Json

    @BeforeEach
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()
        val contentType = "application/json".toMediaType()
        json = DataModule.provideJson()

        sut = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
            .create(GithubApiService::class.java)
    }

    @AfterEach
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Nested
    @DisplayName("getContributors() Tests")
    inner class GetContributorsTests {

        @Test
        @DisplayName("When getContributors is called with valid parameters, it returns the contributors list")
        fun `getContributors successful retrieval`() = runTest {
            val fakeContributorResponse = (1..10).map { GithubRepoDto.ContributorDto.fakes(it.toLong()) }
            val jsonResponse = json.encodeToString(fakeContributorResponse)
            val mockResponse = MockResponse().setBody(jsonResponse)

            mockWebServer.enqueue(mockResponse)

            val result = sut.getContributors(
                owner = "addhen",
                repo = "livefront",
            )

            val request = mockWebServer.takeRequest()
            assertEquals("/repos/addhen/livefront/contributors?per_page=1", request.path)
            assertEquals(fakeContributorResponse, result)
        }

        @Test
        @DisplayName("When getContributors is called with custom perPage value, it includes that parameter")
        fun `getContributors with custom per page`() = runTest {
            val mockResponse = MockResponse()
                .setBody("[]")

            mockWebServer.enqueue(mockResponse)

            sut.getContributors(
                owner = "addhen",
                repo = "livefront",
                perPage = 10
            )

            val request = mockWebServer.takeRequest()
            assertEquals("/repos/addhen/livefront/contributors?per_page=10", request.path)
        }

        @Test
        @DisplayName("When getContributors receives an error response, it throws an exception")
        fun `getContributors throws error`() {
            val mockResponse = MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_NOT_FOUND)
                .setBody("""{"message": "Not Found"}""")

            mockWebServer.enqueue(mockResponse)

            val runTestBlock: () -> Unit = {
                runTest { sut.getContributors(owner = "addhen", repo = "livefront") }
            }

            val exception = assertThrows(HttpException::class.java, runTestBlock)
            assertEquals("HTTP 404 Client Error", exception.message)
        }

        @Test
        @DisplayName("When getContributors receives a malformed response, it throws an exception")
        fun `getContributors throws due to malformed response`() {
            val malformedErrorMessage = "Unexpected JSON token at offset 0: Expected start of the array '[', but had '{' instead at path: ${'$'}"
            val mockResponse = MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_OK)
                .setBody("""{"invalid": "format"}""")

            mockWebServer.enqueue(mockResponse)

            val runTestBlock: () -> Unit = {
                runTest { sut.getContributors(owner = "addhen", repo = "livefront") }
            }

            assertThrows(Exception::class.java, runTestBlock, malformedErrorMessage )
        }
    }

    @Nested
    @DisplayName("getRepos() Tests")
    inner class GetReposTests {

        @Test
        @DisplayName("When getRepos is called with valid parameters, it returns repositories")
        fun `getRepos successful retrieval`() = runTest {
            val fakeRepoResponse = GithubRepoResponseDto.fakes()
            val jsonResponse = json.encodeToString(fakeRepoResponse)
            val mockResponse = MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_OK)
                .setBody(jsonResponse)

            mockWebServer.enqueue(mockResponse)

            val result =sut.getRepos("android", 1, 2)

            val request = mockWebServer.takeRequest()
            assertEquals("/search/repositories?sort=stars&order=desc&q=android&page=1&per_page=2", request.path)
            assertEquals(10, result.items.size)
            assertEquals(fakeRepoResponse, result)
        }

        @Test
        @DisplayName("When getRepos is called with different parameters, it correctly forms the URL")
        fun `getRepos with different parameters()`() = runTest {
            val mockResponse = MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_OK)
                .setBody("""{"items": []}""")

            mockWebServer.enqueue(mockResponse)

            sut.getRepos("kotlin language:kotlin", 3, 10)

            val request = mockWebServer.takeRequest()
            assertEquals("/search/repositories?sort=stars&order=desc&q=kotlin%20language%3Akotlin&page=3&per_page=10", request.path)
        }

        @Test
        @DisplayName("When getRepos receives an empty response, it returns empty list")
        fun `getRepos receives an empty response`() = runTest {
            val mockResponse = MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_OK)
                .setBody("""{"items": []}""")

            mockWebServer.enqueue(mockResponse)

            val result = sut.getRepos("non-existent-repo", 1, 10)

            assertEquals(0, result.items.size)
        }

        @Test
        @DisplayName("When getRepos receives an error response, it throws an exception")
        fun `getRepos throws error`() {
            val mockResponse = MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_FORBIDDEN)
                .setBody("""{"message": "API rate limit exceeded"}""")

            mockWebServer.enqueue(mockResponse)

            val runTestBlock: () -> Unit = {
                runTest { sut.getRepos("android", 1, 10) }
            }

            val exception = assertThrows(HttpException::class.java, runTestBlock)
            assertEquals("HTTP 403 Client Error", exception.message)
        }
    }
}