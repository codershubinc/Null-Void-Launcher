/*
 * Copyright (C) 2026- Swapnil Ingle
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.codershubinc.nullvoidlauncher.ui.widgets

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import com.codershubinc.nullvoidlauncher.data.FavoritesStyle
import com.codershubinc.nullvoidlauncher.data.UserManager
import com.codershubinc.nullvoidlauncher.data.WidgetFont
import com.codershubinc.nullvoidlauncher.data.repository.AppInfo
import com.codershubinc.nullvoidlauncher.ui.widgets.favorites.DockFavoritesWidget
import com.codershubinc.nullvoidlauncher.ui.widgets.favorites.ElegantFavoritesWidget
import com.codershubinc.nullvoidlauncher.ui.widgets.favorites.GridFavoritesWidget
import com.codershubinc.nullvoidlauncher.ui.widgets.favorites.RetroFavoritesWidget

@Composable
fun FavoritesWidget(
    style: FavoritesStyle = FavoritesStyle.ELEGANT,
    modifier: Modifier = Modifier,
    font: WidgetFont? = null,
    previewApps: List<AppInfo>? = null,
    onLongClick: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val userManager = remember { UserManager(context) }
    var prefVersion by remember { mutableIntStateOf(0) }

    DisposableEffect(userManager) {
        val listener = android.content.SharedPreferences.OnSharedPreferenceChangeListener { _, _ ->
            prefVersion++
        }
        userManager.prefs.registerOnSharedPreferenceChangeListener(listener)
        onDispose {
            userManager.prefs.unregisterOnSharedPreferenceChangeListener(listener)
        }
    }

    val effectiveFont = font ?: remember(prefVersion) { userManager.getFavoritesFont() }

    val gestureModifier = if (onLongClick != null) {
        Modifier.pointerInput(Unit) {
            detectTapGestures(
                onLongPress = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onLongClick.invoke()
                }
            )
        }
    } else Modifier

    val finalModifier = modifier.then(gestureModifier)

    when (style) {
        FavoritesStyle.ELEGANT -> ElegantFavoritesWidget(finalModifier, previewApps, font = effectiveFont)
        FavoritesStyle.RETRO   -> RetroFavoritesWidget(finalModifier, previewApps, font = effectiveFont)
        FavoritesStyle.GRID    -> GridFavoritesWidget(finalModifier, previewApps, font = effectiveFont)
        FavoritesStyle.DOCK    -> DockFavoritesWidget(finalModifier, previewApps, font = effectiveFont)
    }
}
