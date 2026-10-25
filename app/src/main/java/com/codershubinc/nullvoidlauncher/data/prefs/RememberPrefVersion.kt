package com.codershubinc.nullvoidlauncher.data.prefs

import android.content.SharedPreferences
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.codershubinc.nullvoidlauncher.data.UserManager

/**
 * Encapsulates the SharedPreferences.OnSharedPreferenceChangeListener lifecycle
 * in a single Composable helper to trigger UI recomposition when preferences change.
 */
@Composable
fun rememberPrefVersion(userManager: UserManager): Int {
    var prefVersion by remember { mutableIntStateOf(0) }

    DisposableEffect(userManager) {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, _ ->
            prefVersion++
        }
        userManager.prefs.registerOnSharedPreferenceChangeListener(listener)
        onDispose {
            userManager.prefs.unregisterOnSharedPreferenceChangeListener(listener)
        }
    }

    return prefVersion
}
