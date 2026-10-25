package com.codershubinc.nullvoidlauncher.data.model

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
