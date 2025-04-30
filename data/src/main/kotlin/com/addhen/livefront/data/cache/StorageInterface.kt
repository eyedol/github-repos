// Copyright 2025, Livefront sample app project contributors
// SPDX-License-Identifier: Apache-2.0

package com.addhen.livefront.data.cache

import kotlinx.coroutines.flow.Flow

/**
 * A generic interface that represents a storage mechanism for managing elements of a specific type.
 * It provides methods for retrieving all stored elements and adding multiple elements to the storage.
 *
 * @param E the type of elements that the storage mechanism handles.
 */
interface StorageInterface<E> {

    /**
     * Retrieves a flow that emits the current list of all stored elements of type [E].
     *
     * @return A [Flow] emitting a list of all elements of type [E] currently stored in the storage.
     */
    fun all(): Flow<List<E>>

    /**
     * Adds a list of elements of type [E] to the storage.
     *
     * @param elements A list of elements to be added to the storage.
     */
    suspend fun addAll(elements: List<E>)
}
