package com.codershubinc.nullvoidlauncher.ui.homescreen

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.codershubinc.nullvoidlauncher.data.ClockStyle
import com.codershubinc.nullvoidlauncher.ui.widgets.ClockWidget
import com.codershubinc.nullvoidlauncher.ui.widgets.FavoritesWidget
import com.codershubinc.nullvoidlauncher.ui.widgets.MusicWidget

@Composable
fun ModernTheme() {
    val style = ClockStyle.MODERN
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        Box(modifier = Modifier
            .padding(top = 140.dp, start = 0.dp)
        ) {
            ClockWidget(style)
        }
        
        FavoritesWidget()
        
        Spacer(modifier = Modifier.weight(1f))
        
        MusicWidget(style)
        Spacer(modifier = Modifier.height(24.dp))
    }
}
