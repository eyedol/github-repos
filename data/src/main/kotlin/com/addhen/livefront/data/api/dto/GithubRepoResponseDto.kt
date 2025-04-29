// Copyright 2025, Livefront sample app project contributors
// SPDX-License-Identifier: Apache-2.0

package com.addhen.livefront.data.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class GithubRepoResponseDto(
    val items: List<GithubRepoDto>,
) {
    companion object
}

fun GithubRepoResponseDto.Companion.fakes(): GithubRepoResponseDto {
    return GithubRepoResponseDto(
        items = (1..10).map { GithubRepoDto.fakes(it.toLong()) },
    )
}
