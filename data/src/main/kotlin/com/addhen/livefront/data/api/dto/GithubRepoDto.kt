// Copyright 2025, Livefront sample app project contributors
// SPDX-License-Identifier: Apache-2.0

package com.addhen.livefront.data.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class GithubRepoDto(
    val id: Long,
    val description: String? = null,
    val full_name: String,
    val stargazers_count: Int,
    val owner: OwnerDto,
    val contributor: ContributorDto? = null,
    val html_url: String,
) {
    @Serializable
    data class OwnerDto(
        val id: Long,
        val login: String,
        val avatar_url: String,
    )

    @Serializable
    data class ContributorDto(
        val id: Long?,
        val login: String,
        val contributions: Int,
        val avatar_url: String?,
        val html_url: String,
    ) {
        companion object
    }

    companion object
}
