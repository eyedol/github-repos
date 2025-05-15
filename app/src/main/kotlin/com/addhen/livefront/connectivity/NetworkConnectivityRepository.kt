// Copyright 2025, Livefront sample app project contributors
// SPDX-License-Identifier: Apache-2.0

package com.addhen.livefront.connectivity

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class NetworkConnectivityRepository @Inject constructor(
    @ApplicationContext private val context: Context,
) : ConnectivityRepository {
    @OptIn(ExperimentalCoroutinesApi::class)
    override val connectivity: Flow<ConnectionState> = context.observeConnectivityAsFlow()
}

interface ConnectivityRepository {
    val connectivity: Flow<ConnectionState>
}
