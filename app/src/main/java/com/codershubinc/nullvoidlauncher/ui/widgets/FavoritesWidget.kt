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
import com.codershubinc.nullvoidlauncher.data.FavoritesStyle
import com.codershubinc.nullvoidlauncher.ui.widgets.favorites.StandardFavoritesWidget
import com.codershubinc.nullvoidlauncher.ui.widgets.favorites.ElegantFavoritesWidget

@Composable
fun FavoritesWidget(style: FavoritesStyle = FavoritesStyle.STANDARD) {
    when (style) {
        FavoritesStyle.STANDARD -> StandardFavoritesWidget()
        FavoritesStyle.ELEGANT -> ElegantFavoritesWidget()
        FavoritesStyle.NONE -> { /* Do nothing */ }
    }
}
