package com.codershubinc.nullvoidlauncher.ui.homescreen

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalConfiguration
import com.codershubinc.nullvoidlauncher.data.LauncherThemeConfig
import com.codershubinc.nullvoidlauncher.ui.widgets.ClockWidget
import com.codershubinc.nullvoidlauncher.ui.widgets.FavoritesWidget
import com.codershubinc.nullvoidlauncher.ui.widgets.MusicWidget

@Composable
fun ElegantTheme(config: LauncherThemeConfig  ) {
    val configuration = LocalConfiguration.current
    val isTablet = configuration.screenWidthDp > 600

    // Theme Content
    Box(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = if (isTablet) 40.dp else 0.dp)
    ) {
        // Clock on the left
        ClockWidget(config.clockStyle)

        // Favorites on the right (bottom aligned relative to their group)
        Box(modifier = Modifier.align(Alignment.BottomEnd).padding(bottom = if (isTablet) 80.dp else 40.dp)) {
            FavoritesWidget(config.favoritesStyle)
        }

        // Music at the very bottom
        Box(modifier = Modifier.align(Alignment.BottomCenter)) {
            MusicWidget(config.musicStyle)
        }
    }
}
