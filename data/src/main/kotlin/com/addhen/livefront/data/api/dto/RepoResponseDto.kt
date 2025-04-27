package com.addhen.livefront.data.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class RepoResponseDto(
    val items: List<RepoDto>,
) {
    companion object
}

fun RepoResponseDto.Companion.fakes(): RepoResponseDto {
    return RepoResponseDto(
        items = (1..10).map { RepoDto.fakes(it.toLong()) }
    )
}