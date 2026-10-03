package com.codershubinc.nullvoidlauncher.data

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.codershubinc.nullvoidlauncher.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

// The data structure to hold the API response
data class GithubProfile(
    val name: String,
    val login: String,
    val bio: String,
    val company: String,
    val publicRepos: Int,
    val avatarUrl: String
)

enum class ClockStyle { ELEGANT, MINIMAL, MODERN, RETRO, TERMINAL }
enum class DayStyle { ELEGANT, RETRO, MINIMAL, MODERN, BRUTALIST }
enum class StorageStyle { ELEGANT, MINIMAL, GAUGE_BAR, RING, TERMINAL, RETRO }
enum class NetworkStyle { ELEGANT, MINIMAL, TERMINAL, RETRO }
enum class PowerStyle { ELEGANT, MINIMAL, GAUGE_BAR, RING, TERMINAL, RETRO }
enum class BluetoothStyle { ELEGANT, MINIMAL, COMPACT, GLASS, TERMINAL, RETRO }
enum class WeatherStyle { ELEGANT, MINIMAL, TERMINAL, RETRO }
enum class StepsStyle { ELEGANT, MINIMAL, RING, GAUGE_BAR, TERMINAL, RETRO }
enum class StepSyncMode { AUTO, HARDWARE_SENSOR, HEALTH_CONNECT }
enum class LauncherTheme { ELEGANT }
enum class MusicStyle { ELEGANT, RETRO, MINIMAL, VINYL, NEON }
enum class FavoritesStyle { ELEGANT, RETRO, GRID, DOCK }
enum class BottomBarStyle { PIXEL, NONE }
enum class DoubleTapAction { CYCLE_WALLPAPER, NONE }
enum class IconStyle { DEFAULT, MONOCHROME, MINIMAL_OUTLINE }
enum class DrawerStyle { SPOTLIGHT, ELEGANT, GRID, TERMINAL, MINIMAL }
enum class ControlDeckStyle { GLASS, MINIMAL, OUTLINE, SOLID, CHIP }
enum class ControlDeckIconStyle { ROUNDED, OUTLINED, SHARP, TWO_TONE }
enum class ControlDeckAction { TORCH, RINGER, ROTATION, HOTSPOT, DND, BLUETOOTH, WIFI }

data class LauncherThemeConfig(
    val clockStyle: ClockStyle,
    val musicStyle: MusicStyle,
    val favoritesStyle: FavoritesStyle,
    val bottomBarStyle: BottomBarStyle
)

fun LauncherTheme.toConfig(): LauncherThemeConfig = LauncherThemeConfig(
    ClockStyle.ELEGANT,
    MusicStyle.ELEGANT,
    FavoritesStyle.ELEGANT,
    BottomBarStyle.PIXEL
)

class UserManager(context: Context) {
    val prefs: SharedPreferences = context.getSharedPreferences("null_void_prefs", Context.MODE_PRIVATE)

    // ── Generic Preference Helpers (DRY) ─────────────────────────────────────────
    private inline fun <reified T : Enum<T>> getEnum(key: String, default: T): T {
        val name = prefs.getString(key, default.name) ?: return default
        return try {
            java.lang.Enum.valueOf(T::class.java, name)
        } catch (_: Exception) {
            default
        }
    }

    private fun <T : Enum<T>> setEnum(key: String, value: T) = prefs.edit { putString(key, value.name) }
    private fun getStr(key: String, default: String): String = prefs.getString(key, default) ?: default
    private fun setStr(key: String, value: String) = prefs.edit { putString(key, value) }
    private fun getBool(key: String, default: Boolean): Boolean = prefs.getBoolean(key, default)
    private fun setBool(key: String, value: Boolean) = prefs.edit { putBoolean(key, value) }
    private fun getFlt(key: String, default: Float): Float = prefs.getFloat(key, default)
    private fun setFlt(key: String, value: Float) = prefs.edit { putFloat(key, value) }
    private fun getI(key: String, default: Int): Int = prefs.getInt(key, default)
    private fun setI(key: String, value: Int) = prefs.edit { putInt(key, value) }

    // ── Profile & Theme ──────────────────────────────────────────────────────────
    fun saveUsername(v: String) = setStr("github_username", v)
    fun getUsername() = getStr("github_username", "CodersHubInc")

    fun saveLauncherTheme(v: LauncherTheme) = setEnum("launcher_theme", v)
    fun getLauncherTheme() = getEnum("launcher_theme", LauncherTheme.ELEGANT)

    fun saveBottomBarStyle(v: BottomBarStyle) = setEnum("bottom_bar_style", v)
    fun getBottomBarStyle() = getEnum("bottom_bar_style", BottomBarStyle.PIXEL)

    fun saveDoubleTapAction(v: DoubleTapAction) = setEnum("double_tap_action", v)
    fun getDoubleTapAction() = getEnum("double_tap_action", DoubleTapAction.CYCLE_WALLPAPER)

    fun saveIconStyle(v: IconStyle) = setEnum("icon_style", v)
    fun getIconStyle() = getEnum("icon_style", IconStyle.DEFAULT)

    fun saveHideStatusBar(v: Boolean) = setBool("hide_status_bar", v)
    fun getHideStatusBar() = getBool("hide_status_bar", true)

    // ── Widget Styles ────────────────────────────────────────────────────────────
    fun saveClockStyle(v: ClockStyle) = setEnum("clock_style", v)
    fun getClockStyle() = getEnum("clock_style", ClockStyle.ELEGANT)

    fun saveMusicStyle(v: MusicStyle) = setEnum("music_style", v)
    fun getMusicStyle() = getEnum("music_style", MusicStyle.ELEGANT)

    fun saveFavoritesStyle(v: FavoritesStyle) = setEnum("favorites_style", v)
    fun getFavoritesStyle() = getEnum("favorites_style", FavoritesStyle.ELEGANT)

    fun saveDayStyle(v: DayStyle) = setEnum("day_style", v)
    fun getDayStyle() = getEnum("day_style", DayStyle.ELEGANT)

    fun saveNetworkStyle(v: NetworkStyle) = setEnum("network_style", v)
    fun getNetworkStyle() = getEnum("network_style", NetworkStyle.ELEGANT)

    fun savePowerStyle(v: PowerStyle) = setEnum("power_style", v)
    fun getPowerStyle() = getEnum("power_style", PowerStyle.ELEGANT)

    fun saveStorageStyle(v: StorageStyle) = setEnum("storage_style", v)
    fun getStorageStyle() = getEnum("storage_style", StorageStyle.ELEGANT)

    fun saveBluetoothStyle(v: BluetoothStyle) = setEnum("bluetooth_style", v)
    fun getBluetoothStyle() = getEnum("bluetooth_style", BluetoothStyle.ELEGANT)

    fun saveWeatherStyle(v: WeatherStyle) = setEnum("weather_style", v)
    fun getWeatherStyle() = getEnum("weather_style", WeatherStyle.ELEGANT)

    // ── Widget Visibility Toggles ────────────────────────────────────────────────
    fun saveShowClockWidget(v: Boolean) = setBool("show_clock_widget", v)
    fun getShowClockWidget() = getBool("show_clock_widget", true)

    fun saveShowDayWidget(v: Boolean) = setBool("show_day_widget", v)
    fun getShowDayWidget() = getBool("show_day_widget", true)

    fun saveShowMusicWidget(v: Boolean) = setBool("show_music_widget", v)
    fun getShowMusicWidget() = getBool("show_music_widget", true)

    fun saveShowFavoritesWidget(v: Boolean) = setBool("show_favorites_widget", v)
    fun getShowFavoritesWidget() = getBool("show_favorites_widget", true)

    fun saveShowStorageWidget(v: Boolean) = setBool("show_storage_widget", v)
    fun getShowStorageWidget() = getBool("show_storage_widget", true)

    fun saveShowNetworkWidget(v: Boolean) = setBool("show_network_widget", v)
    fun getShowNetworkWidget() = getBool("show_network_widget", true)

    fun saveShowPowerWidget(v: Boolean) = setBool("show_power_widget", v)
    fun getShowPowerWidget() = getBool("show_power_widget", true)

    fun saveShowBluetoothWidget(v: Boolean) = setBool("show_bluetooth_widget", v)
    fun getShowBluetoothWidget() = getBool("show_bluetooth_widget", true)

    fun saveShowWeatherWidget(v: Boolean) = setBool("show_weather_widget", v)
    fun getShowWeatherWidget() = getBool("show_weather_widget", true)

    fun saveShowStepsWidget(v: Boolean) = setBool("show_steps_widget", v)
    fun getShowStepsWidget() = getBool("show_steps_widget", true)

    fun saveStepsStyle(v: StepsStyle) = setEnum("steps_style", v)
    fun getStepsStyle() = getEnum("steps_style", StepsStyle.ELEGANT)

    fun saveStepsDailyGoal(v: Int) = setI("steps_daily_goal", v)
    fun getStepsDailyGoal() = getI("steps_daily_goal", 6000)

    fun saveStepSyncMode(v: StepSyncMode) = setEnum("step_sync_mode", v)
    fun getStepSyncMode() = getEnum("step_sync_mode", StepSyncMode.AUTO)

    fun saveStepsManualOffset(v: Int) = setI("steps_manual_offset", v)
    fun getStepsManualOffset() = getI("steps_manual_offset", 0)

    fun saveHealthConnectPromptDismissed(v: Boolean) = setBool("health_connect_prompt_dismissed", v)
    fun getHealthConnectPromptDismissed() = getBool("health_connect_prompt_dismissed", false)

    fun saveDrawerStyle(v: DrawerStyle) = setEnum("drawer_style", v)
    fun getDrawerStyle() = getEnum("drawer_style", DrawerStyle.SPOTLIGHT)

    fun saveDrawerFont(v: WidgetFont) = setEnum("drawer_font", v)
    fun getDrawerFont() = getEnum("drawer_font", WidgetFont.DEFAULT)

    fun saveShowControlDeck(v: Boolean) = setBool("show_control_deck", v)
    fun getShowControlDeck() = getBool("show_control_deck", true)

    fun saveControlDeckStyle(v: ControlDeckStyle) = setEnum("control_deck_style", v)
    fun getControlDeckStyle() = getEnum("control_deck_style", ControlDeckStyle.GLASS)

    fun saveControlDeckIconStyle(v: ControlDeckIconStyle) = setEnum("control_deck_icon_style", v)
    fun getControlDeckIconStyle() = getEnum("control_deck_icon_style", ControlDeckIconStyle.ROUNDED)

    fun saveControlDeckCompact(v: Boolean) = setBool("control_deck_compact", v)
    fun getControlDeckCompact() = getBool("control_deck_compact", false)

    fun saveControlDeckActions(actions: Set<ControlDeckAction>) {
        val strSet = actions.map { it.name }.toSet()
        prefs.edit { putStringSet("control_deck_enabled_actions", strSet) }
    }

    fun getControlDeckActions(): Set<ControlDeckAction> {
        val strSet = prefs.getStringSet("control_deck_enabled_actions", null)
        return if (strSet != null) {
            strSet.mapNotNull {
                try { ControlDeckAction.valueOf(it) } catch (_: Exception) { null }
            }.toSet()
        } else {
            setOf(
                ControlDeckAction.TORCH,
                ControlDeckAction.RINGER,
                ControlDeckAction.ROTATION,
                ControlDeckAction.HOTSPOT
            )
        }
    }

    fun isControlDeckActionEnabled(action: ControlDeckAction): Boolean {
        return getControlDeckActions().contains(action)
    }

    fun setControlDeckActionEnabled(action: ControlDeckAction, enabled: Boolean) {
        val current = getControlDeckActions().toMutableSet()
        if (enabled) current.add(action) else current.remove(action)
        saveControlDeckActions(current)
    }

    fun saveShowNetworkUsageOnWidget(v: Boolean) = setBool("show_network_usage_on_widget", v)
    fun getShowNetworkUsageOnWidget() = getBool("show_network_usage_on_widget", true)

    fun saveBluetoothShowOnlyIfConnected(v: Boolean) = setBool("bluetooth_only_connected", v)
    fun getBluetoothShowOnlyIfConnected() = getBool("bluetooth_only_connected", true)

    fun savePreferredBluetoothDevice(v: String) = setStr("bluetooth_preferred_device", v)
    fun getPreferredBluetoothDevice() = getStr("bluetooth_preferred_device", "")

    // ── Widget Fonts ─────────────────────────────────────────────────────────────
    fun saveClockFont(v: WidgetFont) = setEnum("clock_font", v)
    fun getClockFont() = getEnum("clock_font", WidgetFont.DEFAULT)

    fun saveDayFont(v: WidgetFont) = setEnum("day_font", v)
    fun getDayFont() = getEnum("day_font", WidgetFont.SANS_SERIF)

    fun saveMusicFont(v: WidgetFont) = setEnum("music_font", v)
    fun getMusicFont() = getEnum("music_font", WidgetFont.DEFAULT)

    fun saveFavoritesFont(v: WidgetFont) = setEnum("favorites_font", v)
    fun getFavoritesFont() = getEnum("favorites_font", WidgetFont.DEFAULT)

    fun saveStorageFont(v: WidgetFont) = setEnum("storage_font", v)
    fun getStorageFont() = getEnum("storage_font", WidgetFont.SANS_SERIF)

    fun saveNetworkFont(v: WidgetFont) = setEnum("network_font", v)
    fun getNetworkFont() = getEnum("network_font", WidgetFont.MONOSPACE)

    fun savePowerFont(v: WidgetFont) = setEnum("power_font", v)
    fun getPowerFont() = getEnum("power_font", WidgetFont.MONOSPACE)

    fun saveBluetoothFont(v: WidgetFont) = setEnum("bluetooth_font", v)
    fun getBluetoothFont() = getEnum("bluetooth_font", WidgetFont.DEFAULT)

    fun saveWeatherFont(v: WidgetFont) = setEnum("weather_font", v)
    fun getWeatherFont() = getEnum("weather_font", WidgetFont.DEFAULT)

    fun saveStepsFont(v: WidgetFont) = setEnum("steps_font", v)
    fun getStepsFont() = getEnum("steps_font", WidgetFont.DEFAULT)

    // ── Widget Appearance ────────────────────────────────────────────────────────
    fun saveWidgetCornerRadius(v: Float) = setFlt("widget_corner_radius", v)
    fun getWidgetCornerRadius() = getFlt("widget_corner_radius", 24f)

    fun saveWidgetBlurIntensity(v: Float) = setFlt("widget_blur_intensity", v)
    fun getWidgetBlurIntensity() = getFlt("widget_blur_intensity", 50f)

    fun saveWidgetColor(v: Int) = setI("widget_color", v)
    fun getWidgetColor() = getI("widget_color", 0x1AFFFFFF)

    fun saveWidgetGlassEffect(v: Boolean) = setBool("widget_glass_effect", v)
    fun getWidgetGlassEffect() = getBool("widget_glass_effect", true)

    fun saveWidgetPreset(v: String) = setStr("widget_preset", v)
    fun getWidgetPreset() = getStr("widget_preset", "GLASS")

    // ── Wallpaper & Display ──────────────────────────────────────────────────────
    fun saveShowWallpaper(v: Boolean) = setBool("show_wallpaper", v)
    fun getShowWallpaper() = getBool("show_wallpaper", false)

    fun saveWallpaperRes(v: Int) = setI("wallpaper_res_id", v)
    fun getWallpaperRes() = getI("wallpaper_res_id", -1)

    fun saveWallpaperUri(v: String) = setStr("wallpaper_uri", v)
    fun getWallpaperUri(): String? = prefs.getString("wallpaper_uri", null)
    fun clearWallpaperUri() = prefs.edit { remove("wallpaper_uri") }

    fun saveWallpaperBlur(v: Boolean) = setBool("wallpaper_blur", v)
    fun getWallpaperBlur() = getBool("wallpaper_blur", false)

    fun saveWallpaperBlurIntensity(v: Float) = setFlt("wallpaper_blur_intensity", v)
    fun getWallpaperBlurIntensity() = getFlt("wallpaper_blur_intensity", 10f)

    fun saveWallpaperBlurColor(v: Int) = setI("wallpaper_blur_color", v)
    fun getWallpaperBlurColor() = getI("wallpaper_blur_color", 0x00000000)

    fun saveWallpaperBlurColorAlpha(v: Float) = setFlt("wallpaper_blur_color_alpha", v)
    fun getWallpaperBlurColorAlpha() = getFlt("wallpaper_blur_color_alpha", 0.3f)

    fun saveAutoWallpaperEnabled(v: Boolean) = setBool("auto_wallpaper_enabled", v)
    fun getAutoWallpaperEnabled() = getBool("auto_wallpaper_enabled", false)

    fun saveAutoWallpaperInterval(v: Int) = setI("auto_wallpaper_interval", v)
    fun getAutoWallpaperInterval() = getI("auto_wallpaper_interval", 60)

    fun saveAutoWallpaperList(resIds: List<Int>) =
        prefs.edit { putStringSet("auto_wallpaper_list", resIds.map { it.toString() }.toSet()) }

    fun getAutoWallpaperList(): List<Int> =
        prefs.getStringSet("auto_wallpaper_list", emptySet())?.mapNotNull { it.toIntOrNull() } ?: emptyList()

    // ── Apps & Favorites ─────────────────────────────────────────────────────────
    fun saveFavorites(favorites: List<String>) =
        prefs.edit { putStringSet("favorite_apps", favorites.toSet()) }

    fun getFavorites(): List<String> =
        prefs.getStringSet("favorite_apps", emptySet())?.toList() ?: emptyList()

    fun saveHiddenApps(hidden: Set<String>) =
        prefs.edit { putStringSet("hidden_apps", hidden) }

    fun getHiddenApps(): Set<String> =
        prefs.getStringSet("hidden_apps", emptySet()) ?: emptySet()

    fun toggleHiddenApp(componentNameStr: String) {
        val current = getHiddenApps().toMutableSet()
        if (current.contains(componentNameStr)) {
            current.remove(componentNameStr)
        } else {
            current.add(componentNameStr)
        }
        saveHiddenApps(current)
    }

    // ── Auto Updates & Periodic Check ───────────────────────────────────────────
    fun saveAutoCheckUpdates(v: Boolean) = setBool("auto_check_updates", v)
    fun getAutoCheckUpdates(): Boolean = getBool("auto_check_updates", true)

    fun saveLastUpdateCheckTime(timestampMillis: Long) = prefs.edit { putLong("last_update_check_time", timestampMillis) }
    fun getLastUpdateCheckTime(): Long = prefs.getLong("last_update_check_time", 0L)

    fun saveUpdatePostponedUntil(timestampMillis: Long) = prefs.edit { putLong("update_postponed_until", timestampMillis) }
    fun getUpdatePostponedUntil(): Long = prefs.getLong("update_postponed_until", 0L)

    fun saveSkippedVersion(versionTag: String) = setStr("skipped_update_version", versionTag)
    fun getSkippedVersion(): String = getStr("skipped_update_version", "")

    // ── GitHub Profile & Network ─────────────────────────────────────────────────
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
            if (cachedUserInfo != null && !force && cachedUserInfo.login.equals(username, ignoreCase = true)) {
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
}
