package com.addhen.livefront.data.model

data class GithubRepo(
    val id: Long,
    val name: String,
    val fullName: String,
    val description: String? = null,
    val stargazersCount: Int,
    val htmlUrl: String,
    val owner: Owner,
    val contributor: Contributor? = null,
    val contributors: List<Contributor> = emptyList()
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
        val htmlUrl: String
    ) {
        companion object
    }

    companion object
}