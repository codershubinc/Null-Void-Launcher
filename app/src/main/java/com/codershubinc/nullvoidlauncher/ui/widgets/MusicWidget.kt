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

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
    previewTrack: MusicTrack? = null
) {
    val context = LocalContext.current
    val effectiveFont = font ?: UserManager(context).getMusicFont()

    when (style) {
        MusicStyle.ELEGANT -> ElegantMusicWidget(modifier, previewTrack, font = effectiveFont)
        MusicStyle.RETRO   -> RetroMusicWidget(modifier, previewTrack, font = effectiveFont)
        MusicStyle.MINIMAL -> MinimalMusicWidget(modifier, previewTrack, font = effectiveFont)
        MusicStyle.VINYL   -> VinylMusicWidget(modifier, previewTrack, font = effectiveFont)
        MusicStyle.NEON    -> NeonMusicWidget(modifier, previewTrack, font = effectiveFont)
    }
}
