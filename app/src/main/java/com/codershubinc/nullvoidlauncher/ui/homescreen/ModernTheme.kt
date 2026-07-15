package com.codershubinc.nullvoidlauncher.ui.homescreen

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import com.codershubinc.nullvoidlauncher.data.LauncherThemeConfig
import com.codershubinc.nullvoidlauncher.ui.widgets.ClockWidget
import com.codershubinc.nullvoidlauncher.ui.widgets.FavoritesWidget
import com.codershubinc.nullvoidlauncher.ui.widgets.MusicWidget

@Composable
fun ModernTheme(config: LauncherThemeConfig) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        Box(modifier = Modifier
            .padding(top = 140.dp, start = 0.dp)
        ) {
            ClockWidget(
                style = config.clockStyle,
                modifier = Modifier.graphicsLayer {
                    rotationZ = -90f
                    transformOrigin = TransformOrigin(0f, 0f)
                    translationY = size.width
                }
            )
        }
        
        FavoritesWidget(
            style = config.favoritesStyle,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 32.dp, top = 60.dp, end = 24.dp)
        )
        
        Spacer(modifier = Modifier.weight(1f))
        
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            MusicWidget(config.musicStyle)
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}
