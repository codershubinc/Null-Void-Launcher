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
