// Copyright 2025, Livefront sample app project contributors
// SPDX-License-Identifier: Apache-2.0

package com.addhen.livefront.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
data object GithubRepoListRoute

@Serializable
data class GithubRepoDetailRoute(val id: Long)
