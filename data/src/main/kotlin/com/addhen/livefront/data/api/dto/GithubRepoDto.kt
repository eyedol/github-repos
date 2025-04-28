package com.addhen.livefront.data.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class GithubRepoDto(
    val id: Long,
    val name: String,
    val description: String? = null,
    val full_name: String,
    val stargazers_count: Int,
    val owner: OwnerDto,
    val contributor: ContributorDto? = null,
    val html_url: String
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
        val html_url: String
    ) {
        companion object
    }

    companion object
}

internal fun GithubRepoDto.Companion.fakes(id : Long = 1): GithubRepoDto {
    return GithubRepoDto(
        id = id,
        name = "Fake repo $id",
        description = "Fake repo description $id",
        full_name = "Fake repo/full name $id",
        html_url = "Fake repo url $id",
        stargazers_count = 1,
        owner = GithubRepoDto.OwnerDto(
            id = id,
            login = "fakeOwner$id",
            avatar_url = "Fake owner avatar url",
        ),
        contributor = GithubRepoDto.ContributorDto(
            id = id,
            login = "fakeOwner$id",
            contributions = 2,
            avatar_url = "Fake contributor avatar url$id",
            html_url = "Fake contributor url$id"
        )
    )
}

internal fun GithubRepoDto.ContributorDto.Companion.fakes(id: Long = 1): GithubRepoDto.ContributorDto {
    return GithubRepoDto.ContributorDto(
        id = id,
        login = "fakeOwner$id",
        contributions = 2,
        avatar_url = "Fake contributor avatar url",
        html_url = "Fake contributor url"
    )
}