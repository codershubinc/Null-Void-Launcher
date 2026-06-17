package com.codershubinc.nullvoidlauncher.ui.widgets

import androidx.compose.runtime.Composable
import com.codershubinc.nullvoidlauncher.data.BottomBarStyle
import com.codershubinc.nullvoidlauncher.ui.widgets.bottombar.PixelBottomBar
import com.codershubinc.nullvoidlauncher.ui.widgets.bottombar.StandardBottomBar

@Composable
fun BottomBarWidget(style: BottomBarStyle = BottomBarStyle.STANDARD, onOpenDrawer: () -> Unit) {
    when (style) {
        BottomBarStyle.PIXEL -> PixelBottomBar(onOpenDrawer = onOpenDrawer)
        BottomBarStyle.STANDARD -> StandardBottomBar()
        BottomBarStyle.NONE -> { /* Do nothing */ }
    }
}
