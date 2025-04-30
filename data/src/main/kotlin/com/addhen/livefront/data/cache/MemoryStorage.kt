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

/**
 * An in-memory implementation of the [StorageInterface] for managing elements of a specific type.
 *
 * This class uses a [MutableStateFlow] to maintain an internal list of elements stored in memory.
 * It allows retrieving all current elements as a reactive stream and adding multiple elements
 * asynchronously to the existing collection.
 *
 * @param E The type of elements that the storage handles.
 * @property dispatcher The [CoroutineDispatcher] used for executing asynchronous operations. Defaults to [Dispatchers.IO].
 */
@Singleton
class MemoryStorage<E> @Inject constructor(
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : StorageInterface<E> {

    private val emitter = MutableStateFlow<List<E>>(emptyList())

    /**
     * Retrieves a reactive flow of the current list of all elements stored in memory.
     *
     * @return A [Flow] emitting the current list of elements of type [E] stored in memory.
     */
    override fun all(): Flow<List<E>> = emitter.asSharedFlow()

    /**
     * Adds a list of elements of type [E] to the in-memory storage and updates the emitted state.
     *
     * This method appends the provided elements to the current list stored in memory and emits the updated list
     * to all subscribers of the reactive flow returned by the `MemoryStorage#all` method.
     *
     * @param elements A list of elements to be added to the storage. This can be an empty list, in which case
     * no changes are made and no emissions occur.
     */
    override suspend fun addAll(elements: List<E>) = withContext(dispatcher) {
        val newList = emitter.value.toMutableList()
        newList.addAll(elements)
        emitter.emit(newList)
    }
}
