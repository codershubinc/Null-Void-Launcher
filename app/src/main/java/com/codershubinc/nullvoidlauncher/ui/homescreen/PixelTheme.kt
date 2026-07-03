package com.codershubinc.nullvoidlauncher.ui.homescreen

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalConfiguration
import com.codershubinc.nullvoidlauncher.data.LauncherThemeConfig
import com.codershubinc.nullvoidlauncher.ui.widgets.BottomBarWidget
import com.codershubinc.nullvoidlauncher.ui.widgets.ClockWidget
import com.codershubinc.nullvoidlauncher.ui.widgets.MusicWidget

@Composable
fun PixelTheme(config: LauncherThemeConfig, onOpenDrawer: () -> Unit) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp
    val isTablet = screenWidth > 600

    Box(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        Box(modifier = Modifier
            .padding(top = if (isTablet) 100.dp else 160.dp, start = if (isTablet) 40.dp else 0.dp)
            .align(Alignment.TopStart)
        ) {
            ClockWidget(config.clockStyle)
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = if (isTablet) 48.dp else 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            MusicWidget(config.musicStyle)
            BottomBarWidget(style = config.bottomBarStyle, onOpenDrawer = onOpenDrawer)
        }
    }
}
