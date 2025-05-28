// Copyright 2025, Livefront sample app project contributors
// SPDX-License-Identifier: Apache-2.0

package com.addhen.livefront.fakes

import com.addhen.livefront.connectivity.ConnectionState
import com.addhen.livefront.connectivity.ConnectivityRepository
import kotlinx.coroutines.flow.Flow

class FakeNetworkConnectivity(
    override val connectivity: Flow<ConnectionState>,
) : ConnectivityRepository
