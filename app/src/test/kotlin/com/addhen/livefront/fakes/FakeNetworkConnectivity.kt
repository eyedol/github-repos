package com.addhen.livefront.fakes

import com.addhen.livefront.connectivity.ConnectionState
import com.addhen.livefront.connectivity.ConnectivityRepository
import kotlinx.coroutines.flow.Flow

class FakeNetworkConnectivity(
    override val connectivity: Flow<ConnectionState>
) : ConnectivityRepository