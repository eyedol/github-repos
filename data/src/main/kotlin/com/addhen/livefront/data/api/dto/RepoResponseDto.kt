package com.addhen.livefront.data.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class RepoResponseDto(
    val items: List<RepoDto>,
)