package com.addhen.livefront.data.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class RepoDto(
    val id: Long,
    val description: String? = null,
    val full_name: String,
    val stargazers_count: Int,
    val owner: OwnerDto,
    val contributor: ContributorDto? = null,
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
    )
}