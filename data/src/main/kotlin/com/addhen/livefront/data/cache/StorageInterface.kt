package com.addhen.livefront.data.cache

import kotlinx.coroutines.flow.Flow

interface StorageInterface<E> {

    fun all(): Flow<List<E>>

    suspend fun append(elements: List<E>)
}
