package com.codershubinc.nullvoidlauncher.ui.homescreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.codershubinc.nullvoidlauncher.R
import com.codershubinc.nullvoidlauncher.data.LauncherThemeConfig
import com.codershubinc.nullvoidlauncher.ui.widgets.BottomBarWidget
import com.codershubinc.nullvoidlauncher.ui.widgets.ClockWidget
import com.codershubinc.nullvoidlauncher.ui.widgets.FavoritesWidget
import com.codershubinc.nullvoidlauncher.ui.widgets.MusicWidget

@Composable
fun ElegantTheme(config: LauncherThemeConfig  ) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Wallpaper Image
        Image(
            painter = painterResource(id = R.drawable.wallpaper_elegant),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .blur(20.dp),
            contentScale = ContentScale.Crop
        )

        // Theme Content
        Box(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Clock on the left
            ClockWidget(config.clockStyle)

            // Favorites on the right (bottom aligned relative to their group)
            Box(modifier = Modifier.align(Alignment.BottomEnd)) {
                FavoritesWidget(config.favoritesStyle)
            }

            // Music at the very bottom
            Box(modifier = Modifier.align(Alignment.BottomCenter)) {
                MusicWidget(config.musicStyle)
            }
        }
    }
}
