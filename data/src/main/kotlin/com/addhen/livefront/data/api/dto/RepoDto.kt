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
    ) {
        companion object
    }

    companion object
}

fun RepoDto.Companion.fakes(id : Long = 1): RepoDto {
    return RepoDto(
        id = id,
        description = "Fake repo description $id",
        full_name = "Fake repo full name $id",
        stargazers_count = 1,
        owner = RepoDto.OwnerDto(
            id = id,
            login = "Fake owner login $id",
            avatar_url = "Fake owner avatar url",
        ),
        contributor = RepoDto.ContributorDto(
            id = id,
            login = "Fake contributor login",
            contributions = 2,
            avatar_url = "Fake contributor avatar url",
        )
    )
}

fun RepoDto.ContributorDto.Companion.fakes(id: Long = 1): RepoDto.ContributorDto {
    return RepoDto.ContributorDto(
        id = id,
        login = "Fake contributor login",
        contributions = 2,
        avatar_url = "Fake contributor avatar url"
    )
}