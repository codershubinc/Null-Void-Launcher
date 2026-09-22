package com.codershubinc.nullvoidlauncher.data

import android.content.Context
import android.content.SharedPreferences
import com.codershubinc.nullvoidlauncher.utils.Constants
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
    ELEGANT,
    MINIMAL,
    MODERN,
    RETRO,
    TERMINAL
}

enum class DayStyle {
    ELEGANT,
    RETRO,
    MINIMAL,
    MODERN,
    BRUTALIST
}

enum class StorageStyle {
    ELEGANT,
    MINIMAL,
    GAUGE_BAR,
    RING,
    TERMINAL,
    RETRO
}

enum class NetworkStyle {
    ELEGANT,
    MINIMAL,
    TERMINAL,
    RETRO
}

enum class PowerStyle {
    ELEGANT,
    MINIMAL,
    GAUGE_BAR,
    RING,
    TERMINAL,
    RETRO
}

enum class BluetoothStyle {
    ELEGANT,
    MINIMAL,
    TERMINAL,
    RETRO
}

enum class WeatherStyle {
    ELEGANT,
    MINIMAL,
    TERMINAL,
    RETRO
}

enum class LauncherTheme {
    ELEGANT
}

enum class MusicStyle {
    ELEGANT,
    RETRO,
    MINIMAL,
    VINYL,
    NEON
}

enum class FavoritesStyle {
    ELEGANT,
    RETRO,
    GRID,
    DOCK
}

enum class BottomBarStyle {
    PIXEL,
    NONE
}

enum class DoubleTapAction {
    CYCLE_WALLPAPER,
    NONE
}

enum class IconStyle {
    DEFAULT,
    MONOCHROME,
    MINIMAL_OUTLINE
}

data class LauncherThemeConfig(
    val clockStyle: ClockStyle,
    val musicStyle: MusicStyle,
    val favoritesStyle: FavoritesStyle,
    val bottomBarStyle: BottomBarStyle
)

fun LauncherTheme.toConfig(): LauncherThemeConfig {
    return LauncherThemeConfig(
        ClockStyle.ELEGANT,
        MusicStyle.ELEGANT,
        FavoritesStyle.ELEGANT,
        BottomBarStyle.PIXEL
    )
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
        val styleName = prefs.getString("clock_style", ClockStyle.ELEGANT.name)
        return try { ClockStyle.valueOf(styleName!!) } catch (e: Exception) { ClockStyle.ELEGANT }
    }

    fun saveLauncherTheme(theme: LauncherTheme) {
        prefs.edit { putString("launcher_theme", theme.name) }
    }

    fun getLauncherTheme(): LauncherTheme {
        val themeName = prefs.getString("launcher_theme", LauncherTheme.ELEGANT.name)
        return try { LauncherTheme.valueOf(themeName!!) } catch (e: Exception) { LauncherTheme.ELEGANT }
    }

    fun saveMusicStyle(style: MusicStyle) {
        prefs.edit { putString("music_style", style.name) }
    }

    fun getMusicStyle(): MusicStyle {
        val name = prefs.getString("music_style", MusicStyle.ELEGANT.name)
        return try { MusicStyle.valueOf(name!!) } catch (e: Exception) { MusicStyle.ELEGANT }
    }

    fun saveFavoritesStyle(style: FavoritesStyle) {
        prefs.edit { putString("favorites_style", style.name) }
    }

    fun getFavoritesStyle(): FavoritesStyle {
        val name = prefs.getString("favorites_style", FavoritesStyle.ELEGANT.name)
        return try { FavoritesStyle.valueOf(name!!) } catch (e: Exception) { FavoritesStyle.ELEGANT }
    }

    fun saveDayStyle(style: DayStyle) {
        prefs.edit { putString("day_style", style.name) }
    }

    fun getDayStyle(): DayStyle {
        val name = prefs.getString("day_style", DayStyle.ELEGANT.name)
        return try { DayStyle.valueOf(name!!) } catch (e: Exception) { DayStyle.ELEGANT }
    }

    fun saveNetworkStyle(style: NetworkStyle) {
        prefs.edit { putString("network_style", style.name) }
    }

    fun getNetworkStyle(): NetworkStyle {
        val name = prefs.getString("network_style", NetworkStyle.ELEGANT.name)
        return try { NetworkStyle.valueOf(name!!) } catch (e: Exception) { NetworkStyle.ELEGANT }
    }

    fun saveBottomBarStyle(style: BottomBarStyle) {
        prefs.edit { putString("bottom_bar_style", style.name) }
    }

    fun getBottomBarStyle(): BottomBarStyle {
        val name = prefs.getString("bottom_bar_style", BottomBarStyle.PIXEL.name)
        return try { BottomBarStyle.valueOf(name!!) } catch (e: Exception) { BottomBarStyle.PIXEL }
    }

    fun saveFavorites(favorites: List<String>) {
        prefs.edit { putStringSet("favorite_apps", favorites.toSet()) }
    }

    fun getFavorites(): List<String> {
        return prefs.getStringSet("favorite_apps", emptySet())?.toList() ?: emptyList()
    }

    fun saveHiddenApps(hidden: Set<String>) {
        prefs.edit { putStringSet("hidden_apps", hidden) }
    }

    fun getHiddenApps(): Set<String> {
        return prefs.getStringSet("hidden_apps", emptySet()) ?: emptySet()
    }

    fun toggleHiddenApp(componentNameStr: String) {
        val current = getHiddenApps().toMutableSet()
        if (current.contains(componentNameStr)) {
            current.remove(componentNameStr)
        } else {
            current.add(componentNameStr)
        }
        saveHiddenApps(current)
    }

    fun saveShowWallpaper(show: Boolean) {
        prefs.edit { putBoolean("show_wallpaper", show) }
    }

    fun getShowWallpaper(): Boolean {
        return prefs.getBoolean("show_wallpaper", false)
    }

    fun saveHideStatusBar(hide: Boolean) {
        prefs.edit { putBoolean("hide_status_bar", hide) }
    }

    fun getHideStatusBar(): Boolean {
        return prefs.getBoolean("hide_status_bar", true)
    }

    fun saveShowNetworkUsageOnWidget(show: Boolean) {
        prefs.edit { putBoolean("show_network_usage_on_widget", show) }
    }

    fun getShowNetworkUsageOnWidget(): Boolean {
        return prefs.getBoolean("show_network_usage_on_widget", true)
    }

    fun saveWallpaperRes(resId: Int) {
        prefs.edit { putInt("wallpaper_res_id", resId) }
    }

    fun getWallpaperRes(): Int {
        return prefs.getInt("wallpaper_res_id", -1)
    }

    // URI-based wallpaper (user-selected from device)
    fun saveWallpaperUri(uri: String) {
        prefs.edit { putString("wallpaper_uri", uri) }
    }

    fun getWallpaperUri(): String? {
        return prefs.getString("wallpaper_uri", null)
    }

    fun clearWallpaperUri() {
        prefs.edit { remove("wallpaper_uri") }
    }

    fun saveWallpaperBlur(blur: Boolean) {
        prefs.edit { putBoolean("wallpaper_blur", blur) }
    }

    fun getWallpaperBlur(): Boolean {
        return prefs.getBoolean("wallpaper_blur", false)
    }

    fun saveWallpaperBlurIntensity(intensity: Float) {
        prefs.edit { putFloat("wallpaper_blur_intensity", intensity) }
    }

    fun getWallpaperBlurIntensity(): Float {
        return prefs.getFloat("wallpaper_blur_intensity", 10f)
    }

    fun saveWallpaperBlurColor(color: Int) {
        prefs.edit { putInt("wallpaper_blur_color", color) }
    }

    fun getWallpaperBlurColor(): Int {
        return prefs.getInt("wallpaper_blur_color", 0x00000000)
    }

    fun saveWallpaperBlurColorAlpha(alpha: Float) {
        prefs.edit { putFloat("wallpaper_blur_color_alpha", alpha) }
    }

    fun getWallpaperBlurColorAlpha(): Float {
        return prefs.getFloat("wallpaper_blur_color_alpha", 0.3f)
    }

    fun saveAutoWallpaperEnabled(enabled: Boolean) {
        prefs.edit { putBoolean("auto_wallpaper_enabled", enabled) }
    }

    fun getAutoWallpaperEnabled(): Boolean {
        return prefs.getBoolean("auto_wallpaper_enabled", false)
    }

    fun saveAutoWallpaperInterval(seconds: Int) {
        prefs.edit { putInt("auto_wallpaper_interval", seconds) }
    }

    fun getAutoWallpaperInterval(): Int {
        return prefs.getInt("auto_wallpaper_interval", 60)
    }

    fun saveAutoWallpaperList(resIds: List<Int>) {
        prefs.edit { putStringSet("auto_wallpaper_list", resIds.map { it.toString() }.toSet()) }
    }

    fun getAutoWallpaperList(): List<Int> {
        return prefs.getStringSet("auto_wallpaper_list", emptySet())?.mapNotNull { it.toIntOrNull() } ?: emptyList()
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
            connection.setRequestProperty("User-Agent", Constants.Github.USER_AGENT)

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

    // Widget Customization Settings
    fun saveWidgetCornerRadius(radius: Float) = prefs.edit { putFloat("widget_corner_radius", radius) }
    fun getWidgetCornerRadius(): Float = prefs.getFloat("widget_corner_radius", 24f)

    fun saveWidgetBlurIntensity(intensity: Float) = prefs.edit { putFloat("widget_blur_intensity", intensity) }
    fun getWidgetBlurIntensity(): Float = prefs.getFloat("widget_blur_intensity", 50f)

    fun saveWidgetColor(color: Int) = prefs.edit { putInt("widget_color", color) }
    fun getWidgetColor(): Int = prefs.getInt("widget_color", 0x1AFFFFFF) // Transparent white

    fun saveWidgetGlassEffect(enabled: Boolean) = prefs.edit { putBoolean("widget_glass_effect", enabled) }
    fun getWidgetGlassEffect(): Boolean = prefs.getBoolean("widget_glass_effect", true)

    fun saveWidgetPreset(presetName: String) = prefs.edit { putString("widget_preset", presetName) }
    fun getWidgetPreset(): String = prefs.getString("widget_preset", "GLASS") ?: "GLASS"

    // Widget Toggles
    fun saveShowClockWidget(show: Boolean) = prefs.edit { putBoolean("show_clock_widget", show) }
    fun getShowClockWidget(): Boolean = prefs.getBoolean("show_clock_widget", true)

    fun saveShowDayWidget(show: Boolean) = prefs.edit { putBoolean("show_day_widget", show) }
    fun getShowDayWidget(): Boolean = prefs.getBoolean("show_day_widget", true)

    fun saveShowMusicWidget(show: Boolean) = prefs.edit { putBoolean("show_music_widget", show) }
    fun getShowMusicWidget(): Boolean = prefs.getBoolean("show_music_widget", true)

    fun saveShowFavoritesWidget(show: Boolean) = prefs.edit { putBoolean("show_favorites_widget", show) }
    fun getShowFavoritesWidget(): Boolean = prefs.getBoolean("show_favorites_widget", true)

    fun saveShowStorageWidget(show: Boolean) = prefs.edit { putBoolean("show_storage_widget", show) }
    fun getShowStorageWidget(): Boolean = prefs.getBoolean("show_storage_widget", true)

    fun saveStorageStyle(style: StorageStyle) = prefs.edit { putString("storage_style", style.name) }
    fun getStorageStyle(): StorageStyle {
        val name = prefs.getString("storage_style", StorageStyle.ELEGANT.name)
        return try { StorageStyle.valueOf(name!!) } catch (e: Exception) { StorageStyle.ELEGANT }
    }

    fun saveShowNetworkWidget(show: Boolean) = prefs.edit { putBoolean("show_network_widget", show) }
    fun getShowNetworkWidget(): Boolean = prefs.getBoolean("show_network_widget", true)

    fun saveShowPowerWidget(show: Boolean) = prefs.edit { putBoolean("show_power_widget", show) }
    fun getShowPowerWidget(): Boolean = prefs.getBoolean("show_power_widget", true)

    fun savePowerStyle(style: PowerStyle) = prefs.edit { putString("power_style", style.name) }
    fun getPowerStyle(): PowerStyle {
        val name = prefs.getString("power_style", PowerStyle.ELEGANT.name)
        return try { PowerStyle.valueOf(name!!) } catch (e: Exception) { PowerStyle.ELEGANT }
    }

    fun saveShowBluetoothWidget(show: Boolean) = prefs.edit { putBoolean("show_bluetooth_widget", show) }
    fun getShowBluetoothWidget(): Boolean = prefs.getBoolean("show_bluetooth_widget", true)

    fun saveBluetoothShowOnlyIfConnected(onlyConnected: Boolean) = prefs.edit { putBoolean("bluetooth_only_connected", onlyConnected) }
    fun getBluetoothShowOnlyIfConnected(): Boolean = prefs.getBoolean("bluetooth_only_connected", true)

    fun savePreferredBluetoothDevice(address: String) = prefs.edit { putString("bluetooth_preferred_device", address) }
    fun getPreferredBluetoothDevice(): String = prefs.getString("bluetooth_preferred_device", "") ?: ""

    fun saveBluetoothStyle(style: BluetoothStyle) = prefs.edit { putString("bluetooth_style", style.name) }
    fun getBluetoothStyle(): BluetoothStyle {
        val name = prefs.getString("bluetooth_style", BluetoothStyle.ELEGANT.name)
        return try { BluetoothStyle.valueOf(name!!) } catch (e: Exception) { BluetoothStyle.ELEGANT }
    }

    // Widget Font Settings
    fun saveClockFont(font: WidgetFont) = prefs.edit { putString("clock_font", font.name) }
    fun getClockFont(): WidgetFont {
        val name = prefs.getString("clock_font", WidgetFont.DEFAULT.name)
        return try { WidgetFont.valueOf(name!!) } catch (e: Exception) { WidgetFont.DEFAULT }
    }

    fun saveDayFont(font: WidgetFont) = prefs.edit { putString("day_font", font.name) }
    fun getDayFont(): WidgetFont {
        val name = prefs.getString("day_font", WidgetFont.SANS_SERIF.name)
        return try { WidgetFont.valueOf(name!!) } catch (e: Exception) { WidgetFont.SANS_SERIF }
    }

    fun saveMusicFont(font: WidgetFont) = prefs.edit { putString("music_font", font.name) }
    fun getMusicFont(): WidgetFont {
        val name = prefs.getString("music_font", WidgetFont.DEFAULT.name)
        return try { WidgetFont.valueOf(name!!) } catch (e: Exception) { WidgetFont.DEFAULT }
    }

    fun saveFavoritesFont(font: WidgetFont) = prefs.edit { putString("favorites_font", font.name) }
    fun getFavoritesFont(): WidgetFont {
        val name = prefs.getString("favorites_font", WidgetFont.DEFAULT.name)
        return try { WidgetFont.valueOf(name!!) } catch (e: Exception) { WidgetFont.DEFAULT }
    }

    fun saveStorageFont(font: WidgetFont) = prefs.edit { putString("storage_font", font.name) }
    fun getStorageFont(): WidgetFont {
        val name = prefs.getString("storage_font", WidgetFont.SANS_SERIF.name)
        return try { WidgetFont.valueOf(name!!) } catch (e: Exception) { WidgetFont.SANS_SERIF }
    }

    fun saveNetworkFont(font: WidgetFont) = prefs.edit { putString("network_font", font.name) }
    fun getNetworkFont(): WidgetFont {
        val name = prefs.getString("network_font", WidgetFont.MONOSPACE.name)
        return try { WidgetFont.valueOf(name!!) } catch (e: Exception) { WidgetFont.MONOSPACE }
    }

    fun savePowerFont(font: WidgetFont) = prefs.edit { putString("power_font", font.name) }
    fun getPowerFont(): WidgetFont {
        val name = prefs.getString("power_font", WidgetFont.MONOSPACE.name)
        return try { WidgetFont.valueOf(name!!) } catch (e: Exception) { WidgetFont.MONOSPACE }
    }

    fun saveBluetoothFont(font: WidgetFont) = prefs.edit { putString("bluetooth_font", font.name) }
    fun getBluetoothFont(): WidgetFont {
        val name = prefs.getString("bluetooth_font", WidgetFont.DEFAULT.name)
        return try { WidgetFont.valueOf(name!!) } catch (e: Exception) { WidgetFont.DEFAULT }
    }

    // Gestures
    fun saveDoubleTapAction(action: DoubleTapAction) = prefs.edit { putString("double_tap_action", action.name) }
    fun getDoubleTapAction(): DoubleTapAction {
        val name = prefs.getString("double_tap_action", DoubleTapAction.CYCLE_WALLPAPER.name)
        return try { DoubleTapAction.valueOf(name!!) } catch (e: Exception) { DoubleTapAction.CYCLE_WALLPAPER }
    }

    // Icon Styles
    fun saveIconStyle(style: IconStyle) = prefs.edit { putString("icon_style", style.name) }
    fun getIconStyle(): IconStyle {
        val name = prefs.getString("icon_style", IconStyle.DEFAULT.name)
        return try { IconStyle.valueOf(name!!) } catch (e: Exception) { IconStyle.DEFAULT }
    }

    // Weather Widget Settings
    fun saveShowWeatherWidget(show: Boolean) = prefs.edit { putBoolean("show_weather_widget", show) }
    fun getShowWeatherWidget(): Boolean = prefs.getBoolean("show_weather_widget", true)

    fun saveWeatherStyle(style: WeatherStyle) = prefs.edit { putString("weather_style", style.name) }
    fun getWeatherStyle(): WeatherStyle {
        val name = prefs.getString("weather_style", WeatherStyle.ELEGANT.name)
        return try { WeatherStyle.valueOf(name!!) } catch (e: Exception) { WeatherStyle.ELEGANT }
    }

    fun saveWeatherFont(font: WidgetFont) = prefs.edit { putString("weather_font", font.name) }
    fun getWeatherFont(): WidgetFont {
        val name = prefs.getString("weather_font", WidgetFont.DEFAULT.name)
        return try { WidgetFont.valueOf(name!!) } catch (e: Exception) { WidgetFont.DEFAULT }
    }

    // Quick Toggles / Control Deck
    fun saveShowControlDeck(show: Boolean) = prefs.edit { putBoolean("show_control_deck", show) }
    fun getShowControlDeck(): Boolean = prefs.getBoolean("show_control_deck", true)

    // Free-form widget offsets (saved in Dp)
    fun saveWidgetOffset(widgetKey: String, offsetX: Float, offsetY: Float) {
        prefs.edit {
            putFloat("widget_offset_x_$widgetKey", offsetX)
            putFloat("widget_offset_y_$widgetKey", offsetY)
        }
    }

    fun getWidgetOffsetX(widgetKey: String): Float {
        return prefs.getFloat("widget_offset_x_$widgetKey", 0f)
    }

    fun getWidgetOffsetY(widgetKey: String): Float {
        return prefs.getFloat("widget_offset_y_$widgetKey", 0f)
    }

    fun resetWidgetOffsets() {
        val keys = listOf("telemetry", "clock", "favorites", "music")
        prefs.edit {
            for (k in keys) {
                remove("widget_offset_x_$k")
                remove("widget_offset_y_$k")
            }
        }
    }
}
