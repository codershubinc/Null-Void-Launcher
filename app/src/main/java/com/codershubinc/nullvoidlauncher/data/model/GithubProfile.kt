package com.codershubinc.nullvoidlauncher.data.model

data class GithubProfile(
    val name: String,
    val login: String,
    val bio: String,
    val company: String,
    val publicRepos: Int,
    val avatarUrl: String,
    val followers: Int = 0,
    val following: Int = 0,
    val location: String = "",
    val blog: String = "",
    val publicGists: Int = 0,
    val hireable: Boolean = false,
    val createdAt: String = ""
)
