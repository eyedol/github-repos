// Copyright 2025, Livefront sample app project contributors
// SPDX-License-Identifier: Apache-2.0

package com.addhen.livefront.data.di

import com.addhen.livefront.data.BuildConfig
import com.addhen.livefront.data.api.GithubApiService
import com.addhen.livefront.data.cache.MemoryStorage
import com.addhen.livefront.data.cache.StorageInterface
import com.addhen.livefront.data.model.GithubRepo
import com.addhen.livefront.data.respository.GithubRepoDataRepository
import com.addhen.livefront.data.respository.GithubRepoRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import javax.inject.Singleton

private const val BASE_URL = "https://api.github.com/"

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        logging: HttpLoggingInterceptor,
        authInterceptor: Interceptor,
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor(authInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient, json: Json): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(
                json.asConverterFactory("application/json; charset=UTF8".toMediaType()),
            )
            .client(client)
            .build()
    }

    @Provides
    @Singleton
    fun provideGithubApiService(retrofit: Retrofit): GithubApiService {
        return retrofit.create(GithubApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideGithubRepoRepository(
        apiService: GithubApiService,
        storage: StorageInterface<GithubRepo>,
    ): GithubRepoRepository {
        return GithubRepoDataRepository(apiService, storage)
    }

    @Provides
    @Singleton
    fun provideGithubRepoStorage(): StorageInterface<GithubRepo> = MemoryStorage()

    @Provides
    @Singleton
    fun provideJson(): Json {
        return Json {
            ignoreUnknownKeys = true
            isLenient = true
        }
    }

    @Provides
    @Singleton
    fun provideAuthInterceptor(): Interceptor {
        return Interceptor { chain ->
            val token = BuildConfig.GITHUB_API_KEY
            val originalRequest = chain.request()
            if (token.isBlank() || originalRequest.header("Authorization") != null) {
                chain.proceed(originalRequest)
            } else {
                val newRequest = originalRequest.newBuilder()
                    .header("Authorization", "Bearer $token")
                    .build()
                chain.proceed(newRequest)
            }
        }
    }
}
