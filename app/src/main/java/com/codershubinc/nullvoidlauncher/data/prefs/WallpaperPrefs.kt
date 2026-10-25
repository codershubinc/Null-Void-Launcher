package com.codershubinc.nullvoidlauncher.data.prefs

import android.content.SharedPreferences
import androidx.core.content.edit

class WallpaperPrefs(prefs: SharedPreferences) : BasePrefs(prefs) {
    var showWallpaper: Boolean by booleanPref("show_wallpaper", false)
    var wallpaperRes: Int by intPref("wallpaper_res_id", -1)
    var wallpaperResId: Int get() = wallpaperRes; set(v) { wallpaperRes = v }
    var wallpaperUri: String? by nullableStringPref("wallpaper_uri", null)

    fun clearWallpaperUri() {
        prefs.edit { remove("wallpaper_uri") }
    }

    var blur: Boolean by booleanPref("wallpaper_blur", false)
    var wallpaperBlur: Boolean get() = blur; set(v) { blur = v }

    var blurIntensity: Float by floatPref("wallpaper_blur_intensity", 10f)
    var wallpaperBlurIntensity: Float get() = blurIntensity; set(v) { blurIntensity = v }

    var blurColor: Int by intPref("wallpaper_blur_color", 0x00000000)
    var wallpaperBlurColor: Int get() = blurColor; set(v) { blurColor = v }

    var blurColorAlpha: Float by floatPref("wallpaper_blur_color_alpha", 0.3f)
    var wallpaperBlurColorAlpha: Float get() = blurColorAlpha; set(v) { blurColorAlpha = v }

    var autoWallpaperEnabled: Boolean by booleanPref("auto_wallpaper_enabled", false)
    var autoWallpaperInterval: Int by intPref("auto_wallpaper_interval", 60)
    var autoWallpaperList: List<Int> by intListPref("auto_wallpaper_list", emptyList())
}
