package com.addhen.livefront.di

import android.content.Context
import com.addhen.livefront.ConnectivityRepository
import com.addhen.livefront.NetworkConnectivityRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideGithubRepoRepository(@ApplicationContext context: Context): ConnectivityRepository {
        return NetworkConnectivityRepository(context)
    }
}