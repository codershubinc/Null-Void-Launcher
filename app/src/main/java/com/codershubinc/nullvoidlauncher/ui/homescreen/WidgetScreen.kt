package com.codershubinc.nullvoidlauncher.ui.homescreen

import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import com.codershubinc.nullvoidlauncher.data.ClockStyle
import com.codershubinc.nullvoidlauncher.data.LauncherTheme

@Composable
fun WidgetScreen(isDrawerOpen: Boolean, theme: LauncherTheme, onOpenDrawer: () -> Unit) {
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
            LauncherTheme.VERTICAL -> VerticalTheme()
            LauncherTheme.MODERN -> ModernTheme()
            LauncherTheme.PIXEL -> PixelTheme(onOpenDrawer = onOpenDrawer)
            LauncherTheme.MINIMAL -> StandardTheme(ClockStyle.MINIMAL)
            LauncherTheme.TERMINAL -> StandardTheme(ClockStyle.TERMINAL)
            LauncherTheme.BOLD -> StandardTheme(ClockStyle.BOLD)
            LauncherTheme.VOID -> StandardTheme(ClockStyle.VOID)
        }
    }
}
