package com.addhen.livefront.testing

import android.annotation.SuppressLint
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.internalToRoute
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import org.junit.jupiter.api.extension.AfterEachCallback
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.ExtensionContext

/**
 * A JUnit 5 test extension that mocks and configures the behavior of a [SavedStateHandle]
 * instance within a test environment. The rule provides a mocked [SavedStateHandle]
 * and ensures proper setup and teardown for related static methods.
 *
 * This rule is designed to facilitate testing ViewModels and other classes that rely
 * on [SavedStateHandle] for state management, particularly for scenarios involving
 * navigation route handling.
 *
 * This is a workaround for the issue bug in the type navigation compose library.
 *
 * See https://issuetracker.google.com/issues/349807172?pli=1 for the bug report
 *
 * All credits goes to the comment here: https://issuetracker.google.com/issues/349807172#comment7
 *
 * Just made it a Junit5 extension.
 *
 * Note: Once the bug is fixed, this won't be used anymore and can be removed.
 *
 */
class SavedStateHandleRule(
    private val route: Any,
): BeforeEachCallback, AfterEachCallback {

    val savedStateHandleMock: SavedStateHandle = mockk(relaxed = true)

    @SuppressLint("RestrictedApi")
    override fun beforeEach(context: ExtensionContext?) {
        mockkStatic("androidx.navigation.SavedStateHandleKt")
        every { savedStateHandleMock.internalToRoute<Any>(any(), any()) } returns route
    }

    override fun afterEach(context: ExtensionContext?) {
        unmockkStatic("androidx.navigation.SavedStateHandleKt")
    }
}