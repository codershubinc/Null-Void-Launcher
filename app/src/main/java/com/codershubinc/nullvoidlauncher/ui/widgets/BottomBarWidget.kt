package com.codershubinc.nullvoidlauncher.ui.widgets

import androidx.compose.runtime.Composable
import com.codershubinc.nullvoidlauncher.data.BottomBarStyle
import com.codershubinc.nullvoidlauncher.ui.widgets.bottombar.PixelBottomBar
import com.codershubinc.nullvoidlauncher.ui.widgets.bottombar.StandardBottomBar

import androidx.compose.ui.Modifier

@Composable
fun BottomBarWidget(style: BottomBarStyle = BottomBarStyle.STANDARD, modifier: Modifier = Modifier, onOpenDrawer: () -> Unit) {
    when (style) {
        BottomBarStyle.PIXEL -> PixelBottomBar(modifier = modifier, onOpenDrawer = onOpenDrawer)
        BottomBarStyle.STANDARD -> StandardBottomBar(modifier = modifier)
        BottomBarStyle.NONE -> { /* Do nothing */ }
    }
}
