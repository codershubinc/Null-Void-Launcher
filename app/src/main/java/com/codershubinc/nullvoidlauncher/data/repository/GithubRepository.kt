package com.codershubinc.nullvoidlauncher.data.repository

import android.content.SharedPreferences
import androidx.core.content.edit
import com.codershubinc.nullvoidlauncher.data.model.GithubProfile
import com.codershubinc.nullvoidlauncher.data.prefs.BasePrefs
import com.codershubinc.nullvoidlauncher.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class GithubRepository(prefs: SharedPreferences) : BasePrefs(prefs) {

    var username: String by stringPref("github_username", "CodersHubInc")

    fun saveUserInfo(info: GithubProfile) {
        prefs.edit {
            putString("gh_name", info.name)
            putString("gh_login", info.login)
            putString("gh_bio", info.bio)
            putString("gh_company", info.company)
            putInt("gh_public_repos", info.publicRepos)
            putString("gh_avatar_url", info.avatarUrl)
            putInt("gh_followers", info.followers)
            putInt("gh_following", info.following)
            putString("gh_location", info.location)
            putString("gh_blog", info.blog)
            putInt("gh_public_gists", info.publicGists)
            putBoolean("gh_hireable", info.hireable)
            putString("gh_created_at", info.createdAt)
        }
    }

    fun getUserInfo(): GithubProfile? {
        val name = prefs.getString("gh_name", null) ?: return null
        return GithubProfile(
            name = name,
            login = prefs.getString("gh_login", "") ?: "",
            bio = prefs.getString("gh_bio", "") ?: "",
            company = prefs.getString("gh_company", "") ?: "",
            publicRepos = prefs.getInt("gh_public_repos", 0),
            avatarUrl = prefs.getString("gh_avatar_url", "") ?: "",
            followers = prefs.getInt("gh_followers", 0),
            following = prefs.getInt("gh_following", 0),
            location = prefs.getString("gh_location", "") ?: "",
            blog = prefs.getString("gh_blog", "") ?: "",
            publicGists = prefs.getInt("gh_public_gists", 0),
            hireable = prefs.getBoolean("gh_hireable", false),
            createdAt = prefs.getString("gh_created_at", "") ?: ""
        )
    }

    suspend fun fetchGithubProfile(username: String, force: Boolean = false): GithubProfile? = withContext(Dispatchers.IO) {
        try {
            val cachedUserInfo = getUserInfo()
            if (cachedUserInfo != null && !force && cachedUserInfo.login.equals(username, ignoreCase = true)) {
                return@withContext cachedUserInfo
            }

            val encodedUser = URLEncoder.encode(username.trim(), "UTF-8")
            val url = URL("https://api.github.com/users/$encodedUser")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.setRequestProperty("User-Agent", Constants.Github.USER_AGENT)
            connection.connectTimeout = 8000
            connection.readTimeout = 8000

            if (connection.responseCode == 200) {
                val response = connection.inputStream.bufferedReader().use { it.readText() }
                val json = JSONObject(response)

                val profile = GithubProfile(
                    name = json.optString("name", "Unknown"),
                    login = json.optString("login", username),
                    bio = json.optString("bio", "No bio available.").replace("\r\n", " "),
                    company = json.optString("company", "Independent"),
                    publicRepos = json.optInt("public_repos", 0),
                    avatarUrl = json.optString("avatar_url", ""),
                    followers = json.optInt("followers", 0),
                    following = json.optInt("following", 0),
                    location = json.optString("location", ""),
                    blog = json.optString("blog", ""),
                    publicGists = json.optInt("public_gists", 0),
                    hireable = json.optBoolean("hireable", false),
                    createdAt = json.optString("created_at", "")
                )
                saveUserInfo(profile)
                return@withContext profile
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return@withContext null
    }
}
