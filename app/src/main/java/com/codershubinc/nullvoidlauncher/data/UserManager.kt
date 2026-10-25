package com.codershubinc.nullvoidlauncher.data

import android.content.Context
import android.content.SharedPreferences
import com.codershubinc.nullvoidlauncher.data.model.toConfig as modelToConfig
import com.codershubinc.nullvoidlauncher.data.prefs.AppDrawerPrefs
import com.codershubinc.nullvoidlauncher.data.prefs.ControlDeckPrefs
import com.codershubinc.nullvoidlauncher.data.prefs.ThemePrefs
import com.codershubinc.nullvoidlauncher.data.prefs.UpdatePrefs
import com.codershubinc.nullvoidlauncher.data.prefs.WallpaperPrefs
import com.codershubinc.nullvoidlauncher.data.prefs.WidgetPrefs
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

class UserManager(context: Context) {
    val prefs: SharedPreferences = context.getSharedPreferences("null_void_prefs", Context.MODE_PRIVATE)

    val theme = ThemePrefs(prefs)
    val widgets = WidgetPrefs(prefs)
    val controlDeck = ControlDeckPrefs(prefs)
    val wallpaper = WallpaperPrefs(prefs)
    val drawer = AppDrawerPrefs(prefs)
    val updates = UpdatePrefs(prefs)
    val github = GithubRepository(prefs)
}
