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
import com.codershubinc.nullvoidlauncher.data.MusicStyle
import com.codershubinc.nullvoidlauncher.data.UserManager
import com.codershubinc.nullvoidlauncher.data.WidgetFont
import com.codershubinc.nullvoidlauncher.ui.music.MusicTrack
import com.codershubinc.nullvoidlauncher.ui.widgets.music.ElegantMusicWidget
import com.codershubinc.nullvoidlauncher.ui.widgets.music.RetroMusicWidget
import com.codershubinc.nullvoidlauncher.ui.widgets.music.MinimalMusicWidget
import com.codershubinc.nullvoidlauncher.ui.widgets.music.VinylMusicWidget
import com.codershubinc.nullvoidlauncher.ui.widgets.music.NeonMusicWidget

@Composable
fun MusicWidget(
    style: MusicStyle = MusicStyle.ELEGANT,
    modifier: Modifier = Modifier,
    font: WidgetFont? = null,
    previewTrack: MusicTrack? = null,
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

    val effectiveFont = font ?: remember(prefVersion) { userManager.getMusicFont() }

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
        MusicStyle.ELEGANT -> ElegantMusicWidget(finalModifier, previewTrack, font = effectiveFont)
        MusicStyle.RETRO   -> RetroMusicWidget(finalModifier, previewTrack, font = effectiveFont)
        MusicStyle.MINIMAL -> MinimalMusicWidget(finalModifier, previewTrack, font = effectiveFont)
        MusicStyle.VINYL   -> VinylMusicWidget(finalModifier, previewTrack, font = effectiveFont)
        MusicStyle.NEON    -> NeonMusicWidget(finalModifier, previewTrack, font = effectiveFont)
    }
}
