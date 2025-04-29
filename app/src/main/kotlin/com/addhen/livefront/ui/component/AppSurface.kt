// Copyright 2025, Livefront sample app project contributors
// SPDX-License-Identifier: Apache-2.0

package com.addhen.livefront.ui.component

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun AppSurface(
    modifier: Modifier = Modifier,
    appNavHost: @Composable () -> Unit,
) {
    // A surface container using the 'background' color from the theme
    AppBackground(modifier = modifier.fillMaxSize()) {
        appNavHost()
    }
}

@Composable
private fun AppBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = modifier,
        content = content,
    )
}
