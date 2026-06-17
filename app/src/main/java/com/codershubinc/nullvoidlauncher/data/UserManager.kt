package com.codershubinc.nullvoidlauncher.data

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import androidx.core.content.edit

// The data structure to hold the API response
data class GithubProfile(
    val name: String,
    val login: String,
    val bio: String,
    val company: String,
    val publicRepos: Int,
    val avatarUrl : String
)

enum class ClockStyle {
    MINIMAL,
    TERMINAL,
    BOLD,
    VERTICAL,
    VOID,
    MODERN
}

class UserManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("null_void_prefs", Context.MODE_PRIVATE)

    fun saveUsername(username: String) {
        prefs.edit { putString("github_username", username) }
    }

    fun getUsername(): String {
        return prefs.getString("github_username", "CodersHubInc") ?: "CodersHubInc"
    }

    fun saveClockStyle(style: ClockStyle) {
        prefs.edit { putString("clock_style", style.name) }
    }

    fun getClockStyle(): ClockStyle {
        val styleName = prefs.getString("clock_style", ClockStyle.MINIMAL.name)
        return try { ClockStyle.valueOf(styleName!!) } catch (e: Exception) { ClockStyle.MINIMAL }
    }

    fun saveFavorites(favorites: List<String>) {
        prefs.edit { putStringSet("favorite_apps", favorites.toSet()) }
    }

    fun getFavorites(): List<String> {
        return prefs.getStringSet("favorite_apps", emptySet())?.toList() ?: emptyList()
    }

    fun saveShowWallpaper(show: Boolean) {
        prefs.edit { putBoolean("show_wallpaper", show) }
    }

    fun getShowWallpaper(): Boolean {
        return prefs.getBoolean("show_wallpaper", false)
    }

    fun saveUserInfo(info: GithubProfile) {
        prefs.edit {
            putString("gh_name", info.name)
            putString("gh_login", info.login)
            putString("gh_bio", info.bio)
            putString("gh_company", info.company)
            putInt("gh_public_repos", info.publicRepos)
            putString("gh_avatar_url", info.avatarUrl)
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
            avatarUrl = prefs.getString("gh_avatar_url", "") ?: ""
        )
    }
    suspend fun fetchGithubProfile(username: String, force: Boolean = false): GithubProfile? = withContext(Dispatchers.IO) {
        try {
            val cachedUserInfo = getUserInfo()
            if (cachedUserInfo != null && !force && (cachedUserInfo.login.equals(username, ignoreCase = true))) {
                return@withContext cachedUserInfo
            }

            val url = URL("https://api.github.com/users/$username")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.setRequestProperty("User-Agent", "NullVoidLauncher")

            if (connection.responseCode == 200) {
                val response = connection.inputStream.bufferedReader().use { it.readText() }
                val json = JSONObject(response)

                val profile = GithubProfile(
                    name = json.optString("name", "Unknown"),
                    login = json.optString("login", username),
                    bio = json.optString("bio", "No bio available.").replace("\r\n", " "),
                    company = json.optString("company", "Independent"),
                    publicRepos = json.optInt("public_repos", 0),
                    avatarUrl = json.optString("avatar_url", "")
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
