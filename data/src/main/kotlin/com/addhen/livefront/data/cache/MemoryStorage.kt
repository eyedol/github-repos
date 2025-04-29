// Copyright 2025, Livefront sample app project contributors
// SPDX-License-Identifier: Apache-2.0

package com.addhen.livefront.data.cache

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MemoryStorage<E> @Inject constructor(
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : StorageInterface<E> {

    private val emitter = MutableStateFlow<List<E>>(emptyList())

    override fun all(): Flow<List<E>> = emitter.asSharedFlow()

    override suspend fun addAll(elements: List<E>) = withContext(dispatcher) {
        val newList = emitter.value.toMutableList()
        newList.addAll(elements)
        emitter.emit(newList)
    }
}
