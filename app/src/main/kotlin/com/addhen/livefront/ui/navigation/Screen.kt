package com.addhen.livefront.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
data object GithubRepoListRoute

@Serializable
data class GithubRepoDetailRoute(val id: Long)