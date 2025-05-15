// Copyright 2025, Livefront sample app project contributors
// SPDX-License-Identifier: Apache-2.0

package com.addhen.livefront.data.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class GithubRepo(
    val id: Long,
    val fullName: String,
    val description: String? = null,
    val stargazersCount: Int,
    val htmlUrl: String,
    val owner: Owner,
    val contributor: Contributor? = null,
    val contributors: List<Contributor> = emptyList(),
) {
    @Serializable
    data class Owner(
        val login: String,
        val id: Long,
        val avatarUrl: String,
    )

    @Serializable
    data class Contributor(
        val id: Long?,
        val login: String,
        val contributions: Int,
        val avatarUrl: String?,
        val htmlUrl: String,
    ) {
        companion object
    }

    companion object
}

/**
 * Decodes a string representation of a [GithubRepo] into a [GithubRepo] object.
 * A helper extension function for use with [SavedStateHandle].
 *
 *
 * @throws SerializationException if the given value cannot be serialized to JSON.
 * @return The decoded [GithubRepo] object.
 *
 */
fun GithubRepo.encodeToString() = Json.encodeToString(this)

/**
 * Decodes a string representation of a [GithubRepo] into a [GithubRepo] object.
 * A helper extension function for use with [SavedStateHandle].
 *
 * @throws SerializationException - in case of any decoding-specific error
 *         IllegalArgumentException - if the decoded input is not a valid instance of [GithubRepo]
 * @return The decoded [GithubRepo] object.
 */
fun String.decodeToGithubRepo() = Json.decodeFromString<GithubRepo>(this)