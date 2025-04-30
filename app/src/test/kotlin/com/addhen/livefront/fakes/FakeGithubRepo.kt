// Copyright 2025, Livefront sample app project contributors
// SPDX-License-Identifier: Apache-2.0

package com.addhen.livefront.fakes

import com.addhen.livefront.data.model.GithubRepo

internal fun GithubRepo.Companion.fakes(
    id: Long = 1,
): GithubRepo {
    return GithubRepo(
        id = id,
        description = "Fake repo description $id",
        fullName = "Fake repo/full name $id",
        htmlUrl = "Fake repo url $id",
        stargazersCount = 1,
        owner = GithubRepo.Owner(
            id = id,
            login = "fakeOwner$id",
            avatarUrl = "Fake owner avatar url",
        ),
        contributor = GithubRepo.Contributor.fakes(id),
    )
}

internal fun GithubRepo.Contributor.Companion.fakes(
    id: Long = 1,
): GithubRepo.Contributor {
    return GithubRepo.Contributor(
        id = id,
        login = "fakeOwner$id",
        contributions = 2,
        avatarUrl = "Fake contributor avatar url",
        htmlUrl = "Fake contributor url",
    )
}
