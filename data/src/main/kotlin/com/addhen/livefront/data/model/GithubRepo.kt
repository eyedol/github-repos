package com.addhen.livefront.data.model

data class GithubRepo(
    val id: Long,
    val fullName: String,
    val description: String? = null,
    val stargazersCount: Int,
    val owner: Owner,
    val contributor: Contributor? = null,
) {
    data class Owner(
        val login: String,
        val id: Long,
        val avatarUrl: String,
    )

    data class Contributor(
        val id: Long?,
        val login: String,
        val contributions: Int,
        val avatarUrl: String?,
    ) {
        companion object
    }

    companion object
}