package com.addhen.livefront.data.api

import com.addhen.livefront.data.api.dto.RepoResponseDto
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
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

@OptIn(ExperimentalCoroutinesApi::class)
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

    @Test
    fun `getContributors successful retrieval`() = runTest {
        val fakeResponse = RepoResponseDto.fakes()
        val jsonResponse = json.encodeToString(fakeResponse)
        mockWebServer.enqueue(MockResponse().setBody(jsonResponse))

        val result = sut.getContributors(
            owner = "addhen",
            repo = "livefront",
            perPage = 1
        )

        val request = mockWebServer.takeRequest()
        assertEquals("/repos/addhen/livefront/contributors", request.path)
    }
}