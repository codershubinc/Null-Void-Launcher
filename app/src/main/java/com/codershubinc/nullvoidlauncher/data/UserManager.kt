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
    MODERN,
    PIXEL,
    ELEGANT
}

enum class DayStyle {
    MINIMAL,
    MODERN,
    PIXEL,
    ELEGANT,
    BRUTAL
}

enum class StorageStyle {
    STANDARD,
    MODERN,
    ELEGANT
}

enum class LauncherTheme {
    MINIMAL,
    TERMINAL,
    BOLD,
    VERTICAL,
    VOID,
    MODERN,
    PIXEL,
    ELEGANT
}

enum class MusicStyle {
    STANDARD,
    MODERN,
    FUSED,
    ELEGANT
}

enum class FavoritesStyle {
    STANDARD,
    NONE,
    ELEGANT
}

enum class BottomBarStyle {
    STANDARD,
    PIXEL,
    NONE
}

data class LauncherThemeConfig(
    val clockStyle: ClockStyle,
    val musicStyle: MusicStyle,
    val favoritesStyle: FavoritesStyle,
    val bottomBarStyle: BottomBarStyle
)

fun LauncherTheme.toConfig(): LauncherThemeConfig {
    return when (this) {
        LauncherTheme.MINIMAL -> LauncherThemeConfig(
            ClockStyle.MINIMAL,
            MusicStyle.STANDARD,
            FavoritesStyle.NONE,
            BottomBarStyle.NONE
        )
        LauncherTheme.TERMINAL -> LauncherThemeConfig(
            ClockStyle.TERMINAL, MusicStyle.STANDARD, FavoritesStyle.NONE, BottomBarStyle.NONE)
        LauncherTheme.BOLD -> LauncherThemeConfig(
            ClockStyle.BOLD,
            MusicStyle.STANDARD,
            FavoritesStyle.NONE,
            BottomBarStyle.NONE
        )
        LauncherTheme.VERTICAL -> LauncherThemeConfig(
            ClockStyle.VERTICAL,
            MusicStyle.STANDARD,
            FavoritesStyle.NONE,
            BottomBarStyle.NONE
        )
        LauncherTheme.VOID -> LauncherThemeConfig(
            ClockStyle.VOID,
            MusicStyle.STANDARD,
            FavoritesStyle.NONE,
            BottomBarStyle.NONE
        )
        LauncherTheme.MODERN -> LauncherThemeConfig(
            ClockStyle.MODERN,
            MusicStyle.MODERN,
            FavoritesStyle.STANDARD,
            BottomBarStyle.NONE
        )
        LauncherTheme.PIXEL -> LauncherThemeConfig(
            ClockStyle.PIXEL,
            MusicStyle.ELEGANT,
            FavoritesStyle.NONE,
            BottomBarStyle.PIXEL
        )
        LauncherTheme.ELEGANT -> LauncherThemeConfig(
            ClockStyle.ELEGANT,
            MusicStyle.ELEGANT,
            FavoritesStyle.ELEGANT,
            BottomBarStyle.PIXEL
        )
    }
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

    fun saveLauncherTheme(theme: LauncherTheme) {
        prefs.edit { putString("launcher_theme", theme.name) }
    }

    fun getLauncherTheme(): LauncherTheme {
        val themeName = prefs.getString("launcher_theme", LauncherTheme.MINIMAL.name)
        return try { LauncherTheme.valueOf(themeName!!) } catch (e: Exception) { LauncherTheme.MINIMAL }
    }

    fun saveMusicStyle(style: MusicStyle) {
        prefs.edit { putString("music_style", style.name) }
    }

    fun getMusicStyle(): MusicStyle {
        val name = prefs.getString("music_style", MusicStyle.STANDARD.name)
        return try { MusicStyle.valueOf(name!!) } catch (e: Exception) { MusicStyle.STANDARD }
    }

    fun saveFavoritesStyle(style: FavoritesStyle) {
        prefs.edit { putString("favorites_style", style.name) }
    }

    fun getFavoritesStyle(): FavoritesStyle {
        val name = prefs.getString("favorites_style", FavoritesStyle.STANDARD.name)
        return try { FavoritesStyle.valueOf(name!!) } catch (e: Exception) { FavoritesStyle.STANDARD }
    }

    fun saveBottomBarStyle(style: BottomBarStyle) {
        prefs.edit { putString("bottom_bar_style", style.name) }
    }

    fun getBottomBarStyle(): BottomBarStyle {
        val name = prefs.getString("bottom_bar_style", BottomBarStyle.STANDARD.name)
        return try { BottomBarStyle.valueOf(name!!) } catch (e: Exception) { BottomBarStyle.STANDARD }
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

    fun saveWallpaperRes(resId: Int) {
        prefs.edit { putInt("wallpaper_res_id", resId) }
    }

    fun getWallpaperRes(): Int {
        return prefs.getInt("wallpaper_res_id", -1)
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
