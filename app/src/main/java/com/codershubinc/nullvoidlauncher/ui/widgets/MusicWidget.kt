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
import com.codershubinc.nullvoidlauncher.data.MusicStyle
import com.codershubinc.nullvoidlauncher.ui.widgets.music.FusedMusicWidget
import com.codershubinc.nullvoidlauncher.ui.widgets.music.ModernMusicWidget
import com.codershubinc.nullvoidlauncher.ui.widgets.music.StandardMusicWidget
import com.codershubinc.nullvoidlauncher.ui.widgets.music.ElegantMusicWidget

@Composable
fun MusicWidget(style: MusicStyle = MusicStyle.STANDARD) {
    when (style) {
        MusicStyle.STANDARD -> StandardMusicWidget()
        MusicStyle.MODERN -> ModernMusicWidget()
        MusicStyle.FUSED -> FusedMusicWidget()
        MusicStyle.ELEGANT -> ElegantMusicWidget()
    }
}
