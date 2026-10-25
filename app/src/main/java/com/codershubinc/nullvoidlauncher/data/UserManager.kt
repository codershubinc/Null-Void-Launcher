package com.codershubinc.nullvoidlauncher.data

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.Composable
import com.codershubinc.nullvoidlauncher.data.model.toConfig as modelToConfig
import com.codershubinc.nullvoidlauncher.data.prefs.AppDrawerPrefs
import com.codershubinc.nullvoidlauncher.data.prefs.ControlDeckPrefs
import com.codershubinc.nullvoidlauncher.data.prefs.ThemePrefs
import com.codershubinc.nullvoidlauncher.data.prefs.UpdatePrefs
import com.codershubinc.nullvoidlauncher.data.prefs.WallpaperPrefs
import com.codershubinc.nullvoidlauncher.data.prefs.WidgetPrefs
import com.codershubinc.nullvoidlauncher.data.prefs.rememberPrefVersion as internalRememberPrefVersion
import com.codershubinc.nullvoidlauncher.data.repository.GithubRepository

// ── Backward compatibility Typealiases ───────────────────────────────────────
typealias GithubProfile = com.codershubinc.nullvoidlauncher.data.model.GithubProfile
typealias ClockStyle = com.codershubinc.nullvoidlauncher.data.model.ClockStyle
typealias DayStyle = com.codershubinc.nullvoidlauncher.data.model.DayStyle
typealias StorageStyle = com.codershubinc.nullvoidlauncher.data.model.StorageStyle
typealias NetworkStyle = com.codershubinc.nullvoidlauncher.data.model.NetworkStyle
typealias PowerStyle = com.codershubinc.nullvoidlauncher.data.model.PowerStyle
typealias BluetoothStyle = com.codershubinc.nullvoidlauncher.data.model.BluetoothStyle
typealias WeatherStyle = com.codershubinc.nullvoidlauncher.data.model.WeatherStyle
typealias StepsStyle = com.codershubinc.nullvoidlauncher.data.model.StepsStyle
typealias StepSyncMode = com.codershubinc.nullvoidlauncher.data.model.StepSyncMode
typealias LauncherTheme = com.codershubinc.nullvoidlauncher.data.model.LauncherTheme
typealias MusicStyle = com.codershubinc.nullvoidlauncher.data.model.MusicStyle
typealias FavoritesStyle = com.codershubinc.nullvoidlauncher.data.model.FavoritesStyle
typealias BottomBarStyle = com.codershubinc.nullvoidlauncher.data.model.BottomBarStyle
typealias DoubleTapAction = com.codershubinc.nullvoidlauncher.data.model.DoubleTapAction
typealias IconStyle = com.codershubinc.nullvoidlauncher.data.model.IconStyle
typealias DrawerStyle = com.codershubinc.nullvoidlauncher.data.model.DrawerStyle
typealias ControlDeckStyle = com.codershubinc.nullvoidlauncher.data.model.ControlDeckStyle
typealias ControlDeckIconStyle = com.codershubinc.nullvoidlauncher.data.model.ControlDeckIconStyle
typealias ControlDeckAction = com.codershubinc.nullvoidlauncher.data.model.ControlDeckAction
typealias LauncherThemeConfig = com.codershubinc.nullvoidlauncher.data.model.LauncherThemeConfig

fun LauncherTheme.toConfig(): LauncherThemeConfig = this.modelToConfig()

/**
 * Encapsulates the SharedPreferences.OnSharedPreferenceChangeListener lifecycle
 * in a single Composable helper to trigger UI recomposition when preferences change.
 */
@Composable
fun rememberPrefVersion(userManager: UserManager): Int = internalRememberPrefVersion(userManager)

class UserManager(context: Context) {
    val prefs: SharedPreferences = context.getSharedPreferences("null_void_prefs", Context.MODE_PRIVATE)

    val theme = ThemePrefs(prefs)
    val widgets = WidgetPrefs(prefs)
    val controlDeck = ControlDeckPrefs(prefs)
    val wallpaper = WallpaperPrefs(prefs)
    val drawer = AppDrawerPrefs(prefs)
    val updates = UpdatePrefs(prefs)
    val github = GithubRepository(prefs)

    // ── Backward-Compatible Delegating Methods ────────────────────────────────

    // Theme & Navigation
    fun getLauncherTheme(): LauncherTheme = theme.launcherTheme
    fun saveLauncherTheme(value: LauncherTheme) { theme.launcherTheme = value }
    fun getBottomBarStyle(): BottomBarStyle = theme.bottomBarStyle
    fun saveBottomBarStyle(value: BottomBarStyle) { theme.bottomBarStyle = value }
    fun getDoubleTapAction(): DoubleTapAction = theme.doubleTapAction
    fun saveDoubleTapAction(value: DoubleTapAction) { theme.doubleTapAction = value }
    fun getIconStyle(): IconStyle = theme.iconStyle
    fun saveIconStyle(value: IconStyle) { theme.iconStyle = value }
    fun getHideStatusBar(): Boolean = theme.hideStatusBar
    fun saveHideStatusBar(value: Boolean) { theme.hideStatusBar = value }

    // Widget Styles
    fun getClockStyle(): ClockStyle = widgets.clockStyle
    fun saveClockStyle(value: ClockStyle) { widgets.clockStyle = value }
    fun getDayStyle(): DayStyle = widgets.dayStyle
    fun saveDayStyle(value: DayStyle) { widgets.dayStyle = value }
    fun getStorageStyle(): StorageStyle = widgets.storageStyle
    fun saveStorageStyle(value: StorageStyle) { widgets.storageStyle = value }
    fun getNetworkStyle(): NetworkStyle = widgets.networkStyle
    fun saveNetworkStyle(value: NetworkStyle) { widgets.networkStyle = value }
    fun getPowerStyle(): PowerStyle = widgets.powerStyle
    fun savePowerStyle(value: PowerStyle) { widgets.powerStyle = value }
    fun getBluetoothStyle(): BluetoothStyle = widgets.bluetoothStyle
    fun saveBluetoothStyle(value: BluetoothStyle) { widgets.bluetoothStyle = value }
    fun getWeatherStyle(): WeatherStyle = widgets.weatherStyle
    fun saveWeatherStyle(value: WeatherStyle) { widgets.weatherStyle = value }
    fun getStepsStyle(): StepsStyle = widgets.stepsStyle
    fun saveStepsStyle(value: StepsStyle) { widgets.stepsStyle = value }
    fun getMusicStyle(): MusicStyle = widgets.musicStyle
    fun saveMusicStyle(value: MusicStyle) { widgets.musicStyle = value }
    fun getFavoritesStyle(): FavoritesStyle = widgets.favoritesStyle
    fun saveFavoritesStyle(value: FavoritesStyle) { widgets.favoritesStyle = value }

    // Widget Visibility Toggles
    fun getShowClockWidget(): Boolean = widgets.showClock
    fun saveShowClockWidget(value: Boolean) { widgets.showClock = value }
    fun getShowDayWidget(): Boolean = widgets.showDay
    fun saveShowDayWidget(value: Boolean) { widgets.showDay = value }
    fun getShowMusicWidget(): Boolean = widgets.showMusic
    fun saveShowMusicWidget(value: Boolean) { widgets.showMusic = value }
    fun getShowFavoritesWidget(): Boolean = widgets.showFavorites
    fun saveShowFavoritesWidget(value: Boolean) { widgets.showFavorites = value }
    fun getShowStorageWidget(): Boolean = widgets.showStorage
    fun saveShowStorageWidget(value: Boolean) { widgets.showStorage = value }
    fun getShowNetworkWidget(): Boolean = widgets.showNetwork
    fun saveShowNetworkWidget(value: Boolean) { widgets.showNetwork = value }
    fun getShowPowerWidget(): Boolean = widgets.showPower
    fun saveShowPowerWidget(value: Boolean) { widgets.showPower = value }
    fun getShowBluetoothWidget(): Boolean = widgets.showBluetooth
    fun saveShowBluetoothWidget(value: Boolean) { widgets.showBluetooth = value }
    fun getShowWeatherWidget(): Boolean = widgets.showWeather
    fun saveShowWeatherWidget(value: Boolean) { widgets.showWeather = value }
    fun getShowStepsWidget(): Boolean = widgets.showSteps
    fun saveShowStepsWidget(value: Boolean) { widgets.showSteps = value }

    // Widget Fonts & Appearance
    fun getClockFont(): WidgetFont = widgets.clockFont
    fun saveClockFont(value: WidgetFont) { widgets.clockFont = value }
    fun getDayFont(): WidgetFont = widgets.dayFont
    fun saveDayFont(value: WidgetFont) { widgets.dayFont = value }
    fun getStorageFont(): WidgetFont = widgets.storageFont
    fun saveStorageFont(value: WidgetFont) { widgets.storageFont = value }
    fun getNetworkFont(): WidgetFont = widgets.networkFont
    fun saveNetworkFont(value: WidgetFont) { widgets.networkFont = value }
    fun getPowerFont(): WidgetFont = widgets.powerFont
    fun savePowerFont(value: WidgetFont) { widgets.powerFont = value }
    fun getBluetoothFont(): WidgetFont = widgets.bluetoothFont
    fun saveBluetoothFont(value: WidgetFont) { widgets.bluetoothFont = value }
    fun getWeatherFont(): WidgetFont = widgets.weatherFont
    fun saveWeatherFont(value: WidgetFont) { widgets.weatherFont = value }
    fun getStepsFont(): WidgetFont = widgets.stepsFont
    fun saveStepsFont(value: WidgetFont) { widgets.stepsFont = value }
    fun getMusicFont(): WidgetFont = widgets.musicFont
    fun saveMusicFont(value: WidgetFont) { widgets.musicFont = value }
    fun getFavoritesFont(): WidgetFont = widgets.favoritesFont
    fun saveFavoritesFont(value: WidgetFont) { widgets.favoritesFont = value }
    fun getWidgetCornerRadius(): Float = widgets.cornerRadius
    fun saveWidgetCornerRadius(value: Float) { widgets.cornerRadius = value }
    fun getWidgetBlurIntensity(): Float = widgets.blurIntensity
    fun saveWidgetBlurIntensity(value: Float) { widgets.blurIntensity = value }
    fun getWidgetColor(): Int = widgets.color
    fun saveWidgetColor(value: Int) { widgets.color = value }
    fun getWidgetGlassEffect(): Boolean = widgets.glassEffect
    fun saveWidgetGlassEffect(value: Boolean) { widgets.glassEffect = value }
    fun getWidgetPreset(): String = widgets.preset
    fun saveWidgetPreset(value: String) { widgets.preset = value }
    fun getNetworkUsageEnabled(): Boolean = widgets.networkUsageEnabled
    fun saveNetworkUsageEnabled(value: Boolean) { widgets.networkUsageEnabled = value }
    fun getBluetoothOnlyConnected(): Boolean = widgets.bluetoothOnlyConnected
    fun saveBluetoothOnlyConnected(value: Boolean) { widgets.bluetoothOnlyConnected = value }
    fun getStepSyncMode(): StepSyncMode = widgets.stepSyncMode
    fun saveStepSyncMode(value: StepSyncMode) { widgets.stepSyncMode = value }

    // Control Deck
    fun getShowControlDeck(): Boolean = controlDeck.showControlDeck
    fun saveShowControlDeck(value: Boolean) { controlDeck.showControlDeck = value }
    fun getControlDeckStyle(): ControlDeckStyle = controlDeck.style
    fun saveControlDeckStyle(value: ControlDeckStyle) { controlDeck.style = value }
    fun getControlDeckIconStyle(): ControlDeckIconStyle = controlDeck.iconStyle
    fun saveControlDeckIconStyle(value: ControlDeckIconStyle) { controlDeck.iconStyle = value }
    fun getControlDeckCompact(): Boolean = controlDeck.compact
    fun saveControlDeckCompact(value: Boolean) { controlDeck.compact = value }
    fun getControlDeckActions(): Set<ControlDeckAction> = controlDeck.actions
    fun saveControlDeckActions(value: Set<ControlDeckAction>) { controlDeck.actions = value }

    // Wallpaper
    fun getShowWallpaper(): Boolean = wallpaper.showWallpaper
    fun saveShowWallpaper(value: Boolean) { wallpaper.showWallpaper = value }
    fun getWallpaperResId(): Int = wallpaper.wallpaperResId
    fun saveWallpaperResId(value: Int) { wallpaper.wallpaperResId = value }
    fun getWallpaperUri(): String? = wallpaper.wallpaperUri
    fun saveWallpaperUri(value: String?) { wallpaper.wallpaperUri = value }
    fun clearWallpaperUri() = wallpaper.clearWallpaperUri()
    fun getWallpaperBlur(): Boolean = wallpaper.wallpaperBlur
    fun saveWallpaperBlur(value: Boolean) { wallpaper.wallpaperBlur = value }
    fun getWallpaperBlurIntensity(): Float = wallpaper.wallpaperBlurIntensity
    fun saveWallpaperBlurIntensity(value: Float) { wallpaper.wallpaperBlurIntensity = value }
    fun getWallpaperBlurColor(): Int = wallpaper.wallpaperBlurColor
    fun saveWallpaperBlurColor(value: Int) { wallpaper.wallpaperBlurColor = value }
    fun getWallpaperBlurColorAlpha(): Float = wallpaper.wallpaperBlurColorAlpha
    fun saveWallpaperBlurColorAlpha(value: Float) { wallpaper.wallpaperBlurColorAlpha = value }
    fun getAutoWallpaperEnabled(): Boolean = wallpaper.autoWallpaperEnabled
    fun saveAutoWallpaperEnabled(value: Boolean) { wallpaper.autoWallpaperEnabled = value }
    fun getAutoWallpaperInterval(): Int = wallpaper.autoWallpaperInterval
    fun saveAutoWallpaperInterval(value: Int) { wallpaper.autoWallpaperInterval = value }
    fun getAutoWallpaperList(): List<Int> = wallpaper.autoWallpaperList
    fun saveAutoWallpaperList(value: List<Int>) { wallpaper.autoWallpaperList = value }

    // App Drawer
    fun getDrawerStyle(): DrawerStyle = drawer.drawerStyle
    fun saveDrawerStyle(value: DrawerStyle) { drawer.drawerStyle = value }
    fun getDrawerFont(): WidgetFont = drawer.drawerFont
    fun saveDrawerFont(value: WidgetFont) { drawer.drawerFont = value }
    fun getFavoriteApps(): List<String> = drawer.favoriteApps
    fun saveFavoriteApps(value: List<String>) { drawer.favoriteApps = value }
    fun getHiddenApps(): Set<String> = drawer.hiddenApps
    fun saveHiddenApps(value: Set<String>) { drawer.hiddenApps = value }
    fun toggleHiddenApp(packageName: String) = drawer.toggleHiddenApp(packageName)

    // Updates
    fun getAutoCheckUpdates(): Boolean = updates.autoCheckUpdates
    fun saveAutoCheckUpdates(value: Boolean) { updates.autoCheckUpdates = value }
    fun getLastUpdateCheckTime(): Long = updates.lastUpdateCheckTime
    fun saveLastUpdateCheckTime(value: Long) { updates.lastUpdateCheckTime = value }
    fun getUpdatePostponedUntil(): Long = updates.updatePostponedUntil
    fun saveUpdatePostponedUntil(value: Long) { updates.updatePostponedUntil = value }
    fun getSkippedUpdateVersion(): String = updates.skippedUpdateVersion
    fun saveSkippedUpdateVersion(value: String) { updates.skippedUpdateVersion = value }
    fun getNetworkTriggerUrl(): String = updates.networkTriggerUrl
    fun saveNetworkTriggerUrl(value: String) { updates.networkTriggerUrl = value }
    fun getNetworkTriggerPayload(): String = updates.networkTriggerPayload
    fun saveNetworkTriggerPayload(value: String) { updates.networkTriggerPayload = value }

    // Github
    fun getGithubUsername(): String = github.username
    fun saveGithubUsername(value: String) { github.username = value }
    fun getUserInfo(): GithubProfile? = github.getUserInfo()
    fun saveUserInfo(profile: GithubProfile) = github.saveUserInfo(profile)
    suspend fun fetchGithubProfile(username: String): GithubProfile? = github.fetchGithubProfile(username)
}
