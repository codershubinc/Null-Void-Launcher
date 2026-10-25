package com.codershubinc.nullvoidlauncher.data.prefs

import android.content.SharedPreferences
import com.codershubinc.nullvoidlauncher.data.WidgetFont
import com.codershubinc.nullvoidlauncher.data.model.*

class WidgetPrefs(prefs: SharedPreferences) : BasePrefs(prefs) {

    // ── Widget Styles ────────────────────────────────────────────────────────────
    var clockStyle: ClockStyle by enumPref("clock_style", ClockStyle.ELEGANT)
    var musicStyle: MusicStyle by enumPref("music_style", MusicStyle.ELEGANT)
    var favoritesStyle: FavoritesStyle by enumPref("favorites_style", FavoritesStyle.ELEGANT)
    var dayStyle: DayStyle by enumPref("day_style", DayStyle.ELEGANT)
    var networkStyle: NetworkStyle by enumPref("network_style", NetworkStyle.ELEGANT)
    var powerStyle: PowerStyle by enumPref("power_style", PowerStyle.ELEGANT)
    var storageStyle: StorageStyle by enumPref("storage_style", StorageStyle.ELEGANT)
    var bluetoothStyle: BluetoothStyle by enumPref("bluetooth_style", BluetoothStyle.ELEGANT)
    var weatherStyle: WeatherStyle by enumPref("weather_style", WeatherStyle.ELEGANT)
    var stepsStyle: StepsStyle by enumPref("steps_style", StepsStyle.ELEGANT)

    // ── Visibility Toggles ───────────────────────────────────────────────────────
    var showClock: Boolean by booleanPref("show_clock_widget", true)
    var showDay: Boolean by booleanPref("show_day_widget", true)
    var showMusic: Boolean by booleanPref("show_music_widget", true)
    var showFavorites: Boolean by booleanPref("show_favorites_widget", true)
    var showStorage: Boolean by booleanPref("show_storage_widget", true)
    var showNetwork: Boolean by booleanPref("show_network_widget", true)
    var showPower: Boolean by booleanPref("show_power_widget", true)
    var showBluetooth: Boolean by booleanPref("show_bluetooth_widget", true)
    var showWeather: Boolean by booleanPref("show_weather_widget", true)
    var showSteps: Boolean by booleanPref("show_steps_widget", true)

    // Widget toggle aliases
    var showClockWidget: Boolean get() = showClock; set(v) { showClock = v }
    var showDayWidget: Boolean get() = showDay; set(v) { showDay = v }
    var showMusicWidget: Boolean get() = showMusic; set(v) { showMusic = v }
    var showFavoritesWidget: Boolean get() = showFavorites; set(v) { showFavorites = v }
    var showStorageWidget: Boolean get() = showStorage; set(v) { showStorage = v }
    var showNetworkWidget: Boolean get() = showNetwork; set(v) { showNetwork = v }
    var showPowerWidget: Boolean get() = showPower; set(v) { showPower = v }
    var showBluetoothWidget: Boolean get() = showBluetooth; set(v) { showBluetooth = v }
    var showWeatherWidget: Boolean get() = showWeather; set(v) { showWeather = v }
    var showStepsWidget: Boolean get() = showSteps; set(v) { showSteps = v }

    // ── Widget Fonts ─────────────────────────────────────────────────────────────
    var clockFont: WidgetFont by enumPref("clock_font", WidgetFont.DEFAULT)
    var dayFont: WidgetFont by enumPref("day_font", WidgetFont.SANS_SERIF)
    var musicFont: WidgetFont by enumPref("music_font", WidgetFont.DEFAULT)
    var favoritesFont: WidgetFont by enumPref("favorites_font", WidgetFont.DEFAULT)
    var storageFont: WidgetFont by enumPref("storage_font", WidgetFont.SANS_SERIF)
    var networkFont: WidgetFont by enumPref("network_font", WidgetFont.MONOSPACE)
    var powerFont: WidgetFont by enumPref("power_font", WidgetFont.MONOSPACE)
    var bluetoothFont: WidgetFont by enumPref("bluetooth_font", WidgetFont.DEFAULT)
    var weatherFont: WidgetFont by enumPref("weather_font", WidgetFont.DEFAULT)
    var stepsFont: WidgetFont by enumPref("steps_font", WidgetFont.DEFAULT)

    // ── Widget Appearance ────────────────────────────────────────────────────────
    var cornerRadius: Float by floatPref("widget_corner_radius", 24f)
    var blurIntensity: Float by floatPref("widget_blur_intensity", 50f)
    var color: Int by intPref("widget_color", 0x1AFFFFFF)
    var glassEffect: Boolean by booleanPref("widget_glass_effect", true)
    var preset: String by stringPref("widget_preset", "GLASS")

    // Appearance aliases
    var widgetCornerRadius: Float get() = cornerRadius; set(v) { cornerRadius = v }
    var widgetBlurIntensity: Float get() = blurIntensity; set(v) { blurIntensity = v }
    var widgetColor: Int get() = color; set(v) { color = v }
    var widgetGlassEffect: Boolean get() = glassEffect; set(v) { glassEffect = v }
    var widgetPreset: String get() = preset; set(v) { preset = v }

    // ── Network & Bluetooth Options ──────────────────────────────────────────────
    var showNetworkUsageOnWidget: Boolean by booleanPref("show_network_usage_on_widget", true)
    var networkUsageEnabled: Boolean get() = showNetworkUsageOnWidget; set(v) { showNetworkUsageOnWidget = v }
    var bluetoothOnlyConnected: Boolean by booleanPref("bluetooth_only_connected", true)
    var bluetoothShowOnlyIfConnected: Boolean get() = bluetoothOnlyConnected; set(v) { bluetoothOnlyConnected = v }
    var preferredBluetoothDevice: String by stringPref("bluetooth_preferred_device", "")

    // ── Steps & Health Connect ───────────────────────────────────────────────────
    var stepsDailyGoal: Int by intPref("steps_daily_goal", 6000)
    var stepSyncMode: StepSyncMode by enumPref("step_sync_mode", StepSyncMode.AUTO)
    var stepsManualOffset: Int by intPref("steps_manual_offset", 0)
    var healthConnectPromptDismissed: Boolean by booleanPref("health_connect_prompt_dismissed", false)
}
