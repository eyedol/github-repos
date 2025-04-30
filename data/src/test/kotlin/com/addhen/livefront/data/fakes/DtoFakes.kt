package com.addhen.livefront.data.fakes

import com.addhen.livefront.data.api.dto.GithubRepoDto
import com.addhen.livefront.data.api.dto.GithubRepoResponseDto

internal fun GithubRepoDto.Companion.fakes(id: Long = 1): GithubRepoDto {
    return GithubRepoDto(
        id = id,
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
            html_url = "Fake contributor url$id",
        ),
    )
}

internal fun GithubRepoDto.ContributorDto.Companion.fakes(id: Long = 1): GithubRepoDto.ContributorDto {
    return GithubRepoDto.ContributorDto(
        id = id,
        login = "fakeOwner$id",
        contributions = 2,
        avatar_url = "Fake contributor avatar url",
        html_url = "Fake contributor url",
    )
}

internal fun GithubRepoResponseDto.Companion.fakes(): GithubRepoResponseDto {
    return GithubRepoResponseDto(
        items = (1..10).map { GithubRepoDto.fakes(it.toLong()) },
    )
}
