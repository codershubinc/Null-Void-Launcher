package com.codershubinc.nullvoidlauncher.ui.homescreen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.codershubinc.nullvoidlauncher.data.DoubleTapAction
import com.codershubinc.nullvoidlauncher.data.LauncherTheme
import com.codershubinc.nullvoidlauncher.data.UserManager
import com.codershubinc.nullvoidlauncher.data.toConfig
import com.codershubinc.nullvoidlauncher.data.wallpaper.CloudWallpaperEngine
import com.codershubinc.nullvoidlauncher.services.NullVoidAccessibilityService

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
    var showAccessibilityDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = {
                        when (userManager.getDoubleTapAction()) {
                            DoubleTapAction.LOCK_SCREEN -> {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                if (NullVoidAccessibilityService.isServiceEnabled(context)) {
                                    NullVoidAccessibilityService.lockScreen()
                                } else {
                                    showAccessibilityDialog = true
                                }
                            }
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

        if (showAccessibilityDialog) {
            AccessibilityPermissionDialog(
                onDismiss = { showAccessibilityDialog = false },
                onOpenSettings = {
                    NullVoidAccessibilityService.openAccessibilitySettings(context)
                }
            )
        }
    }
}

@Composable
private fun AccessibilityPermissionDialog(
    onDismiss: () -> Unit,
    onOpenSettings: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .background(Color(0xFF0F0F12), shape = RoundedCornerShape(20.dp))
                .border(1.dp, Color.White.copy(alpha = 0.15f), shape = RoundedCornerShape(20.dp))
                .padding(24.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .background(Color(0xFF3D5AFE).copy(alpha = 0.15f), shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Lock,
                        contentDescription = null,
                        tint = Color(0xFF3D5AFE),
                        modifier = Modifier.size(26.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "> ACCESSIBILITY REQUIRED",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Double-Tap to Lock uses the Null-Void Accessibility Service to instantly turn off and lock the screen without disabling biometric (fingerprint or face) unlock.\n\nNo personal data is collected or monitored.",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 13.sp,
                    fontFamily = FontFamily.SansSerif,
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White.copy(alpha = 0.08f))
                            .clickable { onDismiss() }
                            .padding(vertical = 14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "CANCEL",
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF3D5AFE))
                            .clickable {
                                onOpenSettings()
                                onDismiss()
                            }
                            .padding(vertical = 14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "ENABLE",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
