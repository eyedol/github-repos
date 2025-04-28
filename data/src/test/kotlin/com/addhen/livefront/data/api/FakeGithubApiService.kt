package com.addhen.livefront.data.api

import com.addhen.livefront.data.api.dto.GithubRepoDto
import com.addhen.livefront.data.api.dto.GithubRepoResponseDto
import com.addhen.livefront.data.di.DataModule
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

class FakeGithubApiService(
    private val mockWebServer: MockWebServer = MockWebServer()
) : GithubApiService {

    val json = DataModule.provideJson()
    private val githubApiService: GithubApiService by lazy {
        setup()
    }

    override suspend fun getContributors(
        owner: String,
        repo: String,
        perPage: Int
    ): List<GithubRepoDto.ContributorDto> {
        return githubApiService.getContributors(owner, repo, perPage)
    }

    override suspend fun getRepos(
        query: String,
        page: Int,
        perPage: Int
    ): GithubRepoResponseDto {
        return githubApiService.getRepos(query, page, perPage)
    }

    fun enqueue(response: MockResponse) {
        mockWebServer.enqueue(response)
    }

    fun start() {
        mockWebServer.start()
    }

    fun shutDown() {
        mockWebServer.shutdown()
    }

    fun takeRequest(): RecordedRequest {
        return mockWebServer.takeRequest()

    }

    fun setDispatcher(dispatcher: Dispatcher) {
        mockWebServer.dispatcher = dispatcher
    }

    private fun setup(): GithubApiService {
        val contentType = "application/json".toMediaType()
        val json = DataModule.provideJson()

        return Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
            .create(GithubApiService::class.java)
    }
}