package com.codershubinc.nullvoidlauncher.data.prefs

import android.content.SharedPreferences
import androidx.core.content.edit
import com.codershubinc.nullvoidlauncher.data.model.ControlDeckAction
import com.codershubinc.nullvoidlauncher.data.model.ControlDeckIconStyle
import com.codershubinc.nullvoidlauncher.data.model.ControlDeckStyle

class ControlDeckPrefs(prefs: SharedPreferences) : BasePrefs(prefs) {

    var showControlDeck: Boolean by booleanPref("show_control_deck", true)
    var style: ControlDeckStyle by enumPref("control_deck_style", ControlDeckStyle.GLASS)
    var controlDeckStyle: ControlDeckStyle get() = style; set(v) { style = v }

    var iconStyle: ControlDeckIconStyle by enumPref("control_deck_icon_style", ControlDeckIconStyle.ROUNDED)
    var controlDeckIconStyle: ControlDeckIconStyle get() = iconStyle; set(v) { iconStyle = v }

    var compact: Boolean by booleanPref("control_deck_compact", false)
    var controlDeckCompact: Boolean get() = compact; set(v) { compact = v }

    var actions: Set<ControlDeckAction>
        get() {
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
        set(value) {
            val strSet = value.map { it.name }.toSet()
            prefs.edit { putStringSet("control_deck_enabled_actions", strSet) }
        }

    fun isActionEnabled(action: ControlDeckAction): Boolean {
        return actions.contains(action)
    }

    fun setActionEnabled(action: ControlDeckAction, enabled: Boolean) {
        val current = actions.toMutableSet()
        if (enabled) current.add(action) else current.remove(action)
        actions = current
    }
}
