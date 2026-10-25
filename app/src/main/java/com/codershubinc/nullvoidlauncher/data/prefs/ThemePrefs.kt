package com.codershubinc.nullvoidlauncher.data.prefs

import android.content.SharedPreferences
import com.codershubinc.nullvoidlauncher.data.model.BottomBarStyle
import com.codershubinc.nullvoidlauncher.data.model.DoubleTapAction
import com.codershubinc.nullvoidlauncher.data.model.IconStyle
import com.codershubinc.nullvoidlauncher.data.model.LauncherTheme

class ThemePrefs(prefs: SharedPreferences) : BasePrefs(prefs) {
    var launcherTheme: LauncherTheme by enumPref("launcher_theme", LauncherTheme.ELEGANT)
    var bottomBarStyle: BottomBarStyle by enumPref("bottom_bar_style", BottomBarStyle.PIXEL)
    var doubleTapAction: DoubleTapAction by enumPref("double_tap_action", DoubleTapAction.CYCLE_WALLPAPER)
    var iconStyle: IconStyle by enumPref("icon_style", IconStyle.DEFAULT)
    var hideStatusBar: Boolean by booleanPref("hide_status_bar", true)
}
