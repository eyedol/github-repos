package com.addhen.livefront.data.cache

import app.cash.turbine.test
import com.addhen.livefront.testing.CoroutineTestRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

@ExperimentalCoroutinesApi
@DisplayName("Memory Storage Tests")
class MemoryStorageTest {

    @JvmField
    @RegisterExtension
    val coroutineTestRule = CoroutineTestRule()

    private lateinit var memoryStorage: MemoryStorage<String>

    @BeforeEach
    fun setup() {
        memoryStorage = MemoryStorage(coroutineTestRule.dispatcher)
    }

    @Test
    @DisplayName("When addAll is called with items to be stored in storage, it stores them and emits them")
    fun `addAll should add all items to the storage and emit`() = runTest {
        val items = listOf("a", "b", "c")

        memoryStorage.all().test {
            memoryStorage.addAll(items)

            assertEquals(emptyList<String>(), awaitItem()) // Initial empty list emitted
            assertEquals(items, awaitItem())
        }
    }

    @Test
    @DisplayName("When addAll is called multiple times with a different list, it should add all items to the storage and emits them")
    fun `addAll is called multiple times with different lists, should add all items to the storage and emits`() = runTest {
        val list1 = listOf("a", "b")
        val list2 = listOf("c", "d", "e")

        memoryStorage.all().test {
            memoryStorage.addAll(list1)
            memoryStorage.addAll(list2)

            assertEquals(emptyList<String>(), awaitItem()) // Initial empty list emitted
            assertEquals(list1, awaitItem())
            assertEquals(list1 + list2, awaitItem())
        }
    }

    @Test
    @DisplayName("When addAll is called with an empty list, it should not emit")
    fun `addAll with empty list should not emit`() = runTest {
        val initialList = listOf("a", "b", "c")
        val emptyList = emptyList<String>()

        memoryStorage.all().test {
            memoryStorage.addAll(initialList)
            memoryStorage.addAll(emptyList)

            assertEquals(emptyList<String>(), awaitItem()) // Initial empty list emitted
            assertEquals(initialList, awaitItem())
        }
    }
}