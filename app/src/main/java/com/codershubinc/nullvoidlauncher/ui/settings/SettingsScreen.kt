/*
 * Copyright (C) 2026- Swapnil Ingle
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.codershubinc.nullvoidlauncher.ui.settings

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.codershubinc.nullvoidlauncher.data.UserManager
import com.codershubinc.nullvoidlauncher.data.fonts.FontManager
import com.codershubinc.nullvoidlauncher.data.model.*
import com.codershubinc.nullvoidlauncher.data.prefs.rememberPrefVersion
import com.codershubinc.nullvoidlauncher.data.repository.*
import com.codershubinc.nullvoidlauncher.ui.components.ModernCard
import com.codershubinc.nullvoidlauncher.ui.network.NetworkUsageScreen
import com.codershubinc.nullvoidlauncher.ui.settings.fonts.GoogleFontsDialog
import com.codershubinc.nullvoidlauncher.utils.Constants

private val AccentColor = Color(0xFF3D5AFE)

enum class SettingsSubPage {
    MAIN,
    WALLPAPER,
    FAVORITES
}

@Composable
fun SettingsScreen(
    userManager: UserManager,
    allApps: List<AppInfo>,
    onUsernameUpdated: (String) -> Unit,
    onThemeUpdated: (LauncherTheme) -> Unit,
    onWallpaperToggleUpdated: (Boolean) -> Unit,
    onWallpaperUriUpdated: (String?) -> Unit,
    onWallpaperBlurUpdated: (Boolean) -> Unit,
    onWallpaperBlurIntensityUpdated: (Float) -> Unit,
    onWallpaperBlurColorUpdated: (Int) -> Unit,
    onWallpaperBlurColorAlphaUpdated: (Float) -> Unit,
    onOpenWidgetSettings: () -> Unit,
    onOpenDrawerTweaks: () -> Unit = {},
    onOpenAbout: () -> Unit,
    onClose: () -> Unit
) {
    val context = LocalContext.current
    val prefVersion = rememberPrefVersion(userManager)
    val configuration = LocalConfiguration.current
    val isTablet = configuration.screenWidthDp >= 600

    var activeSubPage by remember { mutableStateOf(SettingsSubPage.MAIN) }
    var inputUsername by remember { mutableStateOf(userManager.github.username) }
    var usernameSavedFeedback by remember { mutableStateOf(false) }

    var selectedIconStyle by remember(prefVersion) { mutableStateOf(userManager.theme.iconStyle) }
    var doubleTapAction by remember(prefVersion) { mutableStateOf(userManager.theme.doubleTapAction) }
    var hideStatusBar by remember(prefVersion) { mutableStateOf(userManager.theme.hideStatusBar) }
    var autoCheckUpdates by remember(prefVersion) { mutableStateOf(userManager.updates.autoCheckUpdates) }
    var selectedFavorites by remember(prefVersion) { mutableStateOf(userManager.drawer.favorites.toSet()) }
    val selectedFavoritesCount = remember(selectedFavorites, allApps) {
        if (allApps.isNotEmpty()) {
            allApps.count { isAppInFavorites(it, selectedFavorites) }
        } else {
            selectedFavorites.size
        }
    }

    var isNetworkUsageOpen by remember { mutableStateOf(false) }
    var showGoogleFontsDialog by remember { mutableStateOf(false) }

    // Intercept hardware and gesture back
    BackHandler(enabled = activeSubPage != SettingsSubPage.MAIN) {
        activeSubPage = SettingsSubPage.MAIN
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF080808))
    ) {
        // Decorative radial glow
        Box(
            modifier = Modifier
                .size(320.dp)
                .align(Alignment.TopStart)
                .offset(x = (-80).dp, y = (-60).dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(AccentColor.copy(alpha = 0.14f), Color.Transparent)
                    )
                )
        )

        when (activeSubPage) {
            SettingsSubPage.WALLPAPER -> {
                WallpaperSettingsPage(
                    userManager = userManager,
                    onBack = { activeSubPage = SettingsSubPage.MAIN },
                    onWallpaperChanged = {
                        onWallpaperToggleUpdated(userManager.wallpaper.showWallpaper)
                        onWallpaperUriUpdated(userManager.wallpaper.wallpaperUri)
                        onWallpaperBlurUpdated(userManager.wallpaper.blur)
                        onWallpaperBlurIntensityUpdated(userManager.wallpaper.blurIntensity)
                        onWallpaperBlurColorUpdated(userManager.wallpaper.blurColor)
                        onWallpaperBlurColorAlphaUpdated(userManager.wallpaper.blurColorAlpha)
                    }
                )
            }

            SettingsSubPage.FAVORITES -> {
                FavoriteAppsPickerPage(
                    allApps = allApps,
                    selectedFavorites = selectedFavorites,
                    onFavoritesChanged = { updated ->
                        selectedFavorites = updated
                        userManager.drawer.favorites = updated.toList()
                    },
                    onBack = { activeSubPage = SettingsSubPage.MAIN }
                )
            }

            SettingsSubPage.MAIN -> {
                val scrollState = rememberScrollState()

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                        .navigationBarsPadding()
                        .padding(horizontal = if (isTablet) 48.dp else 20.dp)
                        .then(
                            if (isTablet) Modifier.widthIn(max = 900.dp).align(Alignment.TopCenter)
                            else Modifier
                        )
                ) {
                    Spacer(modifier = Modifier.height(16.dp))

                    // ── Header Bar ──────────────────────────────────────────────
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.06f))
                                .border(1.dp, Color.White.copy(alpha = 0.1f), CircleShape)
                                .clickable { onClose() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                contentDescription = "Close Settings",
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = "Settings",
                                color = Color.White,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.ExtraBold,
                                fontFamily = FontFamily.SansSerif
                            )
                            Text(
                                text = "Personalize your NullVoid experience",
                                color = Color.White.copy(alpha = 0.5f),
                                fontSize = 13.sp,
                                fontFamily = FontFamily.SansSerif
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // ── Main Content Area ─────────────────────────────────────────
                    if (isTablet) {
                        // Tablet Dual Column Layout
                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .verticalScroll(scrollState),
                            horizontalArrangement = Arrangement.spacedBy(20.dp)
                        ) {
                            // Left Column
                            Column(modifier = Modifier.weight(1f)) {
                                ProfileCard(
                                    username = inputUsername,
                                    savedFeedback = usernameSavedFeedback,
                                    onUsernameChange = {
                                        inputUsername = it
                                        usernameSavedFeedback = false
                                    },
                                    onSave = {
                                        userManager.github.username = inputUsername
                                        onUsernameUpdated(inputUsername)
                                        usernameSavedFeedback = true
                                    }
                                )

                                Spacer(modifier = Modifier.height(20.dp))

                                NavigationAndGesturesCard(
                                    selectedFavoritesCount = selectedFavoritesCount,
                                    doubleTapAction = doubleTapAction,
                                    onDoubleTapActionChange = {
                                        doubleTapAction = it
                                        userManager.theme.doubleTapAction = it
                                    },
                                    onOpenFavorites = { activeSubPage = SettingsSubPage.FAVORITES },
                                    onOpenMusicSync = {
                                        context.startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS).apply {
                                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                        })
                                    }
                                )
                            }

                            // Right Column
                            Column(modifier = Modifier.weight(1f)) {
                                AppearanceCard(
                                    userManager = userManager,
                                    selectedIconStyle = selectedIconStyle,
                                    hideStatusBar = hideStatusBar,
                                    onOpenWallpaper = { activeSubPage = SettingsSubPage.WALLPAPER },
                                    onOpenWidgets = onOpenWidgetSettings,
                                    onOpenDrawerTweaks = onOpenDrawerTweaks,
                                    onIconStyleSelect = { style ->
                                        selectedIconStyle = style
                                        userManager.theme.iconStyle = style
                                    },
                                    onOpenFonts = { showGoogleFontsDialog = true },
                                    onHideStatusBarToggle = { isChecked ->
                                        hideStatusBar = isChecked
                                        userManager.theme.hideStatusBar = isChecked
                                        updateSystemStatusBar(context, isChecked)
                                    }
                                )

                                Spacer(modifier = Modifier.height(20.dp))

                                SystemCard(
                                    autoCheckUpdates = autoCheckUpdates,
                                    onAutoCheckUpdatesToggle = { isChecked ->
                                        autoCheckUpdates = isChecked
                                        userManager.updates.autoCheckUpdates = isChecked
                                    },
                                    onOpenNetworkUsage = { isNetworkUsageOpen = true },
                                    onOpenAbout = onOpenAbout
                                )
                            }
                        }
                    } else {
                        // Mobile Single Column Layout
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .verticalScroll(scrollState)
                        ) {
                            ProfileCard(
                                username = inputUsername,
                                savedFeedback = usernameSavedFeedback,
                                onUsernameChange = {
                                    inputUsername = it
                                    usernameSavedFeedback = false
                                },
                                onSave = {
                                    userManager.github.username = inputUsername
                                    onUsernameUpdated(inputUsername)
                                    usernameSavedFeedback = true
                                }
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            AppearanceCard(
                                userManager = userManager,
                                selectedIconStyle = selectedIconStyle,
                                hideStatusBar = hideStatusBar,
                                onOpenWallpaper = { activeSubPage = SettingsSubPage.WALLPAPER },
                                onOpenWidgets = onOpenWidgetSettings,
                                onOpenDrawerTweaks = onOpenDrawerTweaks,
                                onIconStyleSelect = { style ->
                                    selectedIconStyle = style
                                    userManager.theme.iconStyle = style
                                },
                                onOpenFonts = { showGoogleFontsDialog = true },
                                onHideStatusBarToggle = { isChecked ->
                                    hideStatusBar = isChecked
                                    userManager.theme.hideStatusBar = isChecked
                                    updateSystemStatusBar(context, isChecked)
                                }
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            NavigationAndGesturesCard(
                                selectedFavoritesCount = selectedFavoritesCount,
                                doubleTapAction = doubleTapAction,
                                onDoubleTapActionChange = {
                                    doubleTapAction = it
                                    userManager.theme.doubleTapAction = it
                                },
                                onOpenFavorites = { activeSubPage = SettingsSubPage.FAVORITES },
                                onOpenMusicSync = {
                                    context.startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS).apply {
                                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                    })
                                }
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            SystemCard(
                                autoCheckUpdates = autoCheckUpdates,
                                onAutoCheckUpdatesToggle = { isChecked ->
                                    autoCheckUpdates = isChecked
                                    userManager.updates.autoCheckUpdates = isChecked
                                },
                                onOpenNetworkUsage = { isNetworkUsageOpen = true },
                                onOpenAbout = onOpenAbout
                            )

                            Spacer(modifier = Modifier.height(32.dp))
                        }
                    }
                }
            }
        }

        // Sub-dialogs
        if (isNetworkUsageOpen) {
            NetworkUsageScreen(onClose = { isNetworkUsageOpen = false })
        }

        if (showGoogleFontsDialog) {
            GoogleFontsDialog(onDismiss = { showGoogleFontsDialog = false })
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// 1. Profile Hero Card
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun ProfileCard(
    username: String,
    savedFeedback: Boolean,
    onUsernameChange: (String) -> Unit,
    onSave: () -> Unit
) {
    ModernCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF24292E))
                    .border(1.dp, Color.White.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.AccountCircle,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "GitHub Developer Profile",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Syncs bio, repos, and status telemetry",
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            BasicTextField(
                value = username,
                onValueChange = onUsernameChange,
                textStyle = TextStyle(
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                ),
                cursorBrush = SolidColor(AccentColor),
                modifier = Modifier.weight(1f),
                singleLine = true,
                decorationBox = { innerTextField ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color.White.copy(alpha = 0.05f))
                            .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(14.dp))
                            .padding(horizontal = 14.dp, vertical = 12.dp)
                    ) {
                        if (username.isEmpty()) {
                            Text("GitHub handle", color = Color.White.copy(alpha = 0.3f), fontSize = 14.sp)
                        }
                        innerTextField()
                    }
                }
            )

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (savedFeedback) Color(0xFF00E676) else AccentColor)
                    .clickable { onSave() }
                    .padding(horizontal = 18.dp, vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if (savedFeedback) {
                        Icon(Icons.Rounded.Check, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                        Text("Saved", color = Color.Black, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    } else {
                        Text("Save", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// 2. Appearance & Personalization Card
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun AppearanceCard(
    userManager: UserManager,
    selectedIconStyle: IconStyle,
    hideStatusBar: Boolean,
    onOpenWallpaper: () -> Unit,
    onOpenWidgets: () -> Unit,
    onOpenDrawerTweaks: () -> Unit,
    onIconStyleSelect: (IconStyle) -> Unit,
    onOpenFonts: () -> Unit,
    onHideStatusBarToggle: (Boolean) -> Unit
) {
    val wallpaperSummary = remember(
        userManager.wallpaper.showWallpaper,
        userManager.wallpaper.wallpaperUri,
        userManager.wallpaper.blur
    ) {
        if (!userManager.wallpaper.showWallpaper) {
            "Disabled • Pure Void Black"
        } else if (userManager.wallpaper.wallpaperUri != null) {
            if (userManager.wallpaper.blur) "Custom Image • Frosted Blur" else "Custom Image Active"
        } else {
            "Active Void Backdrop"
        }
    }

    val drawerTitle = remember(userManager.drawer.drawerStyle) {
        when (userManager.drawer.drawerStyle) {
            DrawerStyle.SPOTLIGHT -> "Spotlight Search"
            DrawerStyle.ELEGANT -> "Elegant (Serif & Gold)"
            DrawerStyle.GRID -> "App Grid"
            DrawerStyle.TERMINAL -> "Terminal (CLI)"
            DrawerStyle.MINIMAL -> "Minimal Text"
        }
    }

    ModernCard {
        Text(
            text = "APPEARANCE & PERSONALIZATION",
            color = Color.White.copy(alpha = 0.45f),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.2.sp
        )
        Spacer(modifier = Modifier.height(14.dp))

        // Wallpaper & Backdrop Subpage
        SettingsActionRow(
            icon = Icons.Rounded.Wallpaper,
            iconBg = Color(0xFF3F51B5),
            title = "Wallpaper & Backdrop",
            subtitle = wallpaperSummary,
            onClick = onOpenWallpaper
        )

        SettingsDivider()

        // Widget Studio Hub
        SettingsActionRow(
            icon = Icons.Rounded.Widgets,
            iconBg = Color(0xFF00897B),
            title = "Widget Studio",
            subtitle = "Clock, Telemetry, Power, Steps & more",
            onClick = onOpenWidgets
        )

        SettingsDivider()

        // App Drawer Theme
        SettingsActionRow(
            icon = Icons.Rounded.Search,
            iconBg = Color(0xFF8E24AA),
            title = "App Drawer Theme",
            subtitle = drawerTitle,
            onClick = onOpenDrawerTweaks
        )

        SettingsDivider()

        // App Icon Style Segmented Pill Selector
        Column(modifier = Modifier.padding(vertical = 4.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFFB8C00)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Palette,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(
                        text = "App Icon Style",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Visual rendering of home & drawer icons",
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 11.5.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 3-way Segmented Button Pill
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color.White.copy(alpha = 0.05f))
                    .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(14.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                listOf(
                    IconStyle.DEFAULT to "Default",
                    IconStyle.MONOCHROME to "Monochrome",
                    IconStyle.MINIMAL_OUTLINE to "Outline"
                ).forEach { (style, label) ->
                    val isSelected = selectedIconStyle == style
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) AccentColor else Color.Transparent)
                            .clickable { onIconStyleSelect(style) }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            color = if (isSelected) Color.White else Color.White.copy(alpha = 0.6f),
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }
            }
        }

        SettingsDivider()

        // Google Fonts & Typography
        SettingsActionRow(
            icon = Icons.Rounded.FontDownload,
            iconBg = Color(0xFF43A047),
            title = "Fonts & Typography",
            subtitle = "${FontManager.getDownloadedFonts().size} Google fonts installed",
            onClick = onOpenFonts
        )

        SettingsDivider()

        // Hide Status Bar Switch
        SettingsSwitchRow(
            icon = if (hideStatusBar) Icons.Rounded.VisibilityOff else Icons.Rounded.Visibility,
            iconBg = Color(0xFF546E7A),
            title = "Hide Status Bar",
            subtitle = if (hideStatusBar) "Immersive full-screen edge-to-edge" else "System status icons visible",
            checked = hideStatusBar,
            onCheckedChange = onHideStatusBarToggle
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// 3. Navigation & Gestures Card
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun NavigationAndGesturesCard(
    selectedFavoritesCount: Int,
    doubleTapAction: DoubleTapAction,
    onDoubleTapActionChange: (DoubleTapAction) -> Unit,
    onOpenFavorites: () -> Unit,
    onOpenMusicSync: () -> Unit
) {
    ModernCard {
        Text(
            text = "NAVIGATION & GESTURES",
            color = Color.White.copy(alpha = 0.45f),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.2.sp
        )
        Spacer(modifier = Modifier.height(14.dp))

        // Favorite Apps
        SettingsActionRow(
            icon = Icons.Rounded.Star,
            iconBg = Color(0xFFF9A825),
            title = "Favorite Apps",
            subtitle = "$selectedFavoritesCount of 5 apps pinned to home dock",
            onClick = onOpenFavorites
        )

        SettingsDivider()

        // Home Double-Tap Gesture Segmented Pill
        Column(modifier = Modifier.padding(vertical = 4.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF00ACC1)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.TouchApp,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(
                        text = "Double-Tap Gesture",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Home screen double tap action",
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 11.5.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color.White.copy(alpha = 0.05f))
                    .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(14.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                listOf(
                    DoubleTapAction.CYCLE_WALLPAPER to "Cycle Wallpaper",
                    DoubleTapAction.NONE to "Disabled"
                ).forEach { (action, label) ->
                    val isSelected = doubleTapAction == action
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) AccentColor else Color.Transparent)
                            .clickable { onDoubleTapActionChange(action) }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            color = if (isSelected) Color.White else Color.White.copy(alpha = 0.6f),
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }
            }
        }

        SettingsDivider()

        // Music Telemetry
        SettingsActionRow(
            icon = Icons.Rounded.MusicNote,
            iconBg = Color(0xFFE64A19),
            title = "Music Telemetry",
            subtitle = "Notification listener permissions for media",
            onClick = onOpenMusicSync
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// 4. System & Diagnostics Card
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun SystemCard(
    autoCheckUpdates: Boolean,
    onAutoCheckUpdatesToggle: (Boolean) -> Unit,
    onOpenNetworkUsage: () -> Unit,
    onOpenAbout: () -> Unit
) {
    ModernCard {
        Text(
            text = "SYSTEM & DIAGNOSTICS",
            color = Color.White.copy(alpha = 0.45f),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.2.sp
        )
        Spacer(modifier = Modifier.height(14.dp))

        // Auto Check Updates
        SettingsSwitchRow(
            icon = Icons.Rounded.Update,
            iconBg = Color(0xFF1E88E5),
            title = "Check for Updates",
            subtitle = if (autoCheckUpdates) "Background release monitoring enabled" else "Manual checks only",
            checked = autoCheckUpdates,
            onCheckedChange = onAutoCheckUpdatesToggle
        )

        SettingsDivider()

        // Network Diagnostics & Logs
        SettingsActionRow(
            icon = Icons.Rounded.DataUsage,
            iconBg = Color(0xFF00838F),
            title = "Network Diagnostics",
            subtitle = "Speed samples & telemetry history",
            onClick = onOpenNetworkUsage
        )

        SettingsDivider()

        // About NullVoid
        SettingsActionRow(
            icon = Icons.Rounded.Info,
            iconBg = Color(0xFF6D4C41),
            title = "About NullVoid Launcher",
            subtitle = "v${Constants.App.VERSION} • ${Constants.System.CODENAME}",
            onClick = onOpenAbout
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// 5. Dedicated Favorite Apps Picker View
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun FavoriteAppsPickerPage(
    allApps: List<AppInfo>,
    selectedFavorites: Set<String>,
    onFavoritesChanged: (Set<String>) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var query by remember { mutableStateOf("") }
    var currentSelection by remember(selectedFavorites, allApps) {
        val canonicalKeys = if (allApps.isNotEmpty()) {
            selectedFavorites.mapNotNull { key ->
                findAppForFavoriteKey(key, allApps)?.componentName?.flattenToString()
            }.toSet()
        } else {
            selectedFavorites
        }
        mutableStateOf(if (canonicalKeys.isNotEmpty() || selectedFavorites.isEmpty()) canonicalKeys else selectedFavorites)
    }

    val selectedCount = remember(currentSelection, allApps) {
        if (allApps.isNotEmpty()) {
            allApps.count { isAppInFavorites(it, currentSelection) }
        } else {
            currentSelection.size
        }
    }

    val filteredApps = remember(allApps, query) {
        if (query.isBlank()) allApps
        else allApps.filter {
            it.label.contains(query, ignoreCase = true) ||
            it.packageName.contains(query, ignoreCase = true)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF080808))
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Top Navigation Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.06f))
                    .border(1.dp, Color.White.copy(alpha = 0.1f), CircleShape)
                    .clickable { onBack() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Favorite Apps",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Select up to 5 apps for quick home dock access",
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 12.sp
                )
            }
            // Counter Badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (selectedCount == 5) Color(0xFF00E676).copy(alpha = 0.15f) else AccentColor.copy(alpha = 0.15f))
                    .border(
                        1.dp,
                        if (selectedCount == 5) Color(0xFF00E676).copy(alpha = 0.4f) else AccentColor.copy(alpha = 0.4f),
                        RoundedCornerShape(12.dp)
                    )
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "$selectedCount/5",
                    color = if (selectedCount == 5) Color(0xFF00E676) else Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Real-Time Search Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White.copy(alpha = 0.06f))
                .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Rounded.Search,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.4f),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            BasicTextField(
                value = query,
                onValueChange = { query = it },
                textStyle = TextStyle(color = Color.White, fontSize = 14.sp),
                cursorBrush = SolidColor(AccentColor),
                modifier = Modifier.weight(1f),
                singleLine = true,
                decorationBox = { inner ->
                    if (query.isEmpty()) {
                        Text("Search installed apps...", color = Color.White.copy(alpha = 0.35f), fontSize = 14.sp)
                    }
                    inner()
                }
            )
            if (query.isNotEmpty()) {
                Icon(
                    imageVector = Icons.Rounded.Close,
                    contentDescription = "Clear search",
                    tint = Color.White.copy(alpha = 0.5f),
                    modifier = Modifier
                        .size(18.dp)
                        .clip(CircleShape)
                        .clickable { query = "" }
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Apps List
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(
                items = filteredApps,
                key = { it.componentName.flattenToString() }
            ) { app ->
                val pkg = app.componentName.flattenToString()
                val isSelected = isAppInFavorites(app, currentSelection)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(if (isSelected) AccentColor.copy(alpha = 0.12f) else Color.White.copy(alpha = 0.03f))
                        .border(
                            width = 1.dp,
                            color = if (isSelected) AccentColor.copy(alpha = 0.5f) else Color.White.copy(alpha = 0.06f),
                            shape = RoundedCornerShape(18.dp)
                        )
                        .clickable {
                            if (isSelected) {
                                currentSelection = currentSelection.filterNot { isAppKeyMatching(it, app) }.toSet()
                            } else {
                                if (selectedCount < 5) {
                                    currentSelection = (currentSelection.filterNot { isAppKeyMatching(it, app) } + pkg).toSet()
                                }
                            }
                        }
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.White.copy(alpha = 0.05f)),
                        contentAlignment = Alignment.Center
                    ) {
                        LazyAppIcon(app = app, context = context, size = 26)
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = app.label,
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                        Text(
                            text = app.category.title,
                            color = Color.White.copy(alpha = 0.4f),
                            fontSize = 11.sp
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) AccentColor else Color.White.copy(alpha = 0.08f))
                            .border(
                                1.dp,
                                if (isSelected) AccentColor else Color.White.copy(alpha = 0.2f),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Rounded.Check,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(15.dp)
                            )
                        }
                    }
                }
            }
        }

        // Bottom Confirmation Button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(AccentColor)
                .clickable {
                    val finalKeys = currentSelection.mapNotNull { key ->
                        findAppForFavoriteKey(key, allApps)?.componentName?.flattenToString() ?: key
                    }.toSet()
                    onFavoritesChanged(finalKeys)
                    onBack()
                }
                .padding(vertical = 15.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Apply Selection ($selectedCount/5)",
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Reusable UI Rows
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun SettingsActionRow(
    icon: ImageVector,
    iconBg: Color,
    title: String,
    subtitle: String? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(iconBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )
            if (!subtitle.isNullOrBlank()) {
                Text(
                    text = subtitle,
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 11.5.sp
                )
            }
        }

        Icon(
            imageVector = Icons.Rounded.ChevronRight,
            contentDescription = null,
            tint = Color.White.copy(alpha = 0.35f),
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun SettingsSwitchRow(
    icon: ImageVector,
    iconBg: Color,
    title: String,
    subtitle: String? = null,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f).padding(end = 12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column {
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
                if (!subtitle.isNullOrBlank()) {
                    Text(
                        text = subtitle,
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 11.5.sp
                    )
                }
            }
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = AccentColor
            )
        )
    }
}

@Composable
private fun SettingsDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(vertical = 10.dp),
        color = Color.White.copy(alpha = 0.05f)
    )
}

private fun updateSystemStatusBar(context: Context, hide: Boolean) {
    val activity = context as? Activity ?: return
    val controller = WindowCompat.getInsetsController(activity.window, activity.window.decorView)
    controller.systemBarsBehavior =
        WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    if (hide) {
        controller.hide(WindowInsetsCompat.Type.statusBars())
    } else {
        controller.show(WindowInsetsCompat.Type.statusBars())
    }
}
