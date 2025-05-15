package com.addhen.livefront

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class NetworkConnectivityRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    val connectivity: Flow<ConnectionState> = context.observeConnectivityAsFlow()
}