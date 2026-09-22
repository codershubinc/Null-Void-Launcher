package com.codershubinc.nullvoidlauncher.ui.homescreen

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import com.codershubinc.nullvoidlauncher.data.DoubleTapAction
import com.codershubinc.nullvoidlauncher.data.LauncherTheme
import com.codershubinc.nullvoidlauncher.data.UserManager
import com.codershubinc.nullvoidlauncher.data.toConfig
import com.codershubinc.nullvoidlauncher.data.wallpaper.CloudWallpaperEngine

@Composable
fun WidgetScreen(
    isDrawerOpen: Boolean,
    theme: LauncherTheme,
    onOpenDrawer: () -> Unit,
    onWallpaperChanged: (() -> Unit)? = null,
    onOpenNetworkUsage: () -> Unit = {},
    onOpenBluetoothSettings: () -> Unit = {},
    onOpenWidgetSettings: () -> Unit = {}
) {
    val config = theme.toConfig()
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val userManager = remember { UserManager(context) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = {
                        when (userManager.getDoubleTapAction()) {
                            DoubleTapAction.CYCLE_WALLPAPER -> {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                CloudWallpaperEngine.cycleNextWallpaper(context, userManager)
                                onWallpaperChanged?.invoke()
                            }
                            DoubleTapAction.NONE -> {}
                        }
                    }
                )
            }
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
        ElegantTheme(
            config = config,
            onOpenDrawer = onOpenDrawer,
            onOpenNetworkUsage = onOpenNetworkUsage,
            onOpenBluetoothSettings = onOpenBluetoothSettings,
            onOpenWidgetSettings = onOpenWidgetSettings
        )
    }
}
