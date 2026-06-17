package com.codershubinc.nullvoidlauncher.ui.homescreen

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.codershubinc.nullvoidlauncher.data.ClockStyle
import com.codershubinc.nullvoidlauncher.ui.widgets.ClockWidget
import com.codershubinc.nullvoidlauncher.ui.widgets.bottombar.PixelBottomBar

@Composable
fun PixelTheme(onOpenDrawer: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        Box(modifier = Modifier
            .padding(top = 160.dp)
            .align(Alignment.TopStart)
        ) {
            ClockWidget(ClockStyle.PIXEL)
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            PixelBottomBar(onOpenDrawer = onOpenDrawer)
        }
    }
}
