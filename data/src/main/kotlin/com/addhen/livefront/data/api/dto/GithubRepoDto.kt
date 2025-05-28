// Copyright 2025, Livefront sample app project contributors
// SPDX-License-Identifier: Apache-2.0

package com.addhen.livefront.data.api.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GithubRepoDto(
    @SerialName("id")
    val id: Long,
    @SerialName("description")
    val description: String? = null,
    @SerialName("full_name")
    val full_name: String,
    @SerialName("stargazers_count")
    val stargazers_count: Int,
    @SerialName("owner")
    val owner: OwnerDto,
    @SerialName("html_url")
    val html_url: String,
) {
    @Serializable
    data class OwnerDto(
        @SerialName("id")
        val id: Long,
        @SerialName("login")
        val login: String,
        @SerialName("avatar_url")
        val avatar_url: String,
    )

    @Serializable
    data class ContributorDto(
        @SerialName("id")
        val id: Long?,
        @SerialName("login")
        val login: String,
        @SerialName("contributions")
        val contributions: Int,
        @SerialName("avatar_url")
        val avatar_url: String?,
        @SerialName("html_url")
        val html_url: String,
    ) {
        companion object
    }

    companion object
}
