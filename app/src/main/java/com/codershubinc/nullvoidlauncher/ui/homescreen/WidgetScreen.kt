package com.codershubinc.nullvoidlauncher.ui.homescreen

import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import com.codershubinc.nullvoidlauncher.data.LauncherTheme
import com.codershubinc.nullvoidlauncher.data.toConfig

@Composable
fun WidgetScreen(isDrawerOpen: Boolean, theme: LauncherTheme, onOpenDrawer: () -> Unit) {
    val config = theme.toConfig()
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(isDrawerOpen) {
                if (!isDrawerOpen) {
                    detectVerticalDragGestures { change, dragAmount ->
                        change.consume()
                        if (dragAmount < -20) {
                            onOpenDrawer()
                        }
                    }
                }
            }
    ) {
        when (theme) {
            LauncherTheme.VERTICAL -> VerticalTheme(config)
            LauncherTheme.MODERN -> ModernTheme(config)
            LauncherTheme.PIXEL -> PixelTheme(config, onOpenDrawer = onOpenDrawer)
            LauncherTheme.ELEGANT -> ElegantTheme(config)
            else -> StandardTheme(config)
        }
    }
}
