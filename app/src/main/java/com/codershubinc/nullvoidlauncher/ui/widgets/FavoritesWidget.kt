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
    previewApps: List<AppInfo>? = null
) {
    val context = LocalContext.current
    val effectiveFont = font ?: UserManager(context).getFavoritesFont()

    when (style) {
        FavoritesStyle.ELEGANT -> ElegantFavoritesWidget(modifier, previewApps, font = effectiveFont)
        FavoritesStyle.RETRO   -> RetroFavoritesWidget(modifier, previewApps, font = effectiveFont)
        FavoritesStyle.GRID    -> GridFavoritesWidget(modifier, previewApps, font = effectiveFont)
        FavoritesStyle.DOCK    -> DockFavoritesWidget(modifier, previewApps, font = effectiveFont)
    }
}
