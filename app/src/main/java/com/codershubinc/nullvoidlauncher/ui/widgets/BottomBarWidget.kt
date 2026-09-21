package com.codershubinc.nullvoidlauncher.ui.widgets

import androidx.compose.runtime.Composable
import com.codershubinc.nullvoidlauncher.data.BottomBarStyle
import com.codershubinc.nullvoidlauncher.ui.widgets.bottombar.PixelBottomBar

import androidx.compose.ui.Modifier

@Composable
fun BottomBarWidget(style: BottomBarStyle = BottomBarStyle.PIXEL, modifier: Modifier = Modifier, onOpenDrawer: () -> Unit) {
    when (style) {
        BottomBarStyle.PIXEL -> PixelBottomBar(modifier = modifier, onOpenDrawer = onOpenDrawer)
        BottomBarStyle.NONE -> { /* Do nothing */ }
    }
}
