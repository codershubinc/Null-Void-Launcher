package com.codershubinc.nullvoidlauncher.data.prefs

import android.content.SharedPreferences
import androidx.core.content.edit
import com.codershubinc.nullvoidlauncher.data.WidgetFont
import com.codershubinc.nullvoidlauncher.data.model.DrawerStyle

class AppDrawerPrefs(prefs: SharedPreferences) : BasePrefs(prefs) {

    var drawerStyle: DrawerStyle by enumPref("drawer_style", DrawerStyle.SPOTLIGHT)
    var drawerFont: WidgetFont by enumPref("drawer_font", WidgetFont.DEFAULT)

    var favoriteApps: List<String>
        get() = prefs.getStringSet("favorite_apps", emptySet())?.toList() ?: emptyList()
        set(value) {
            prefs.edit { putStringSet("favorite_apps", value.toSet()) }
        }

    // Alias for favoriteApps
    var favorites: List<String>
        get() = favoriteApps
        set(value) {
            favoriteApps = value
        }

    var hiddenApps: Set<String> by stringSetPref("hidden_apps", emptySet())

    fun toggleHiddenApp(componentNameStr: String) {
        val current = hiddenApps.toMutableSet()
        if (current.contains(componentNameStr)) {
            current.remove(componentNameStr)
        } else {
            current.add(componentNameStr)
        }
        hiddenApps = current
    }
}
