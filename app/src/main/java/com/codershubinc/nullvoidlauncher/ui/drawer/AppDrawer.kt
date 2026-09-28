/*
 * Copyright (C) 2026- Swapnil Ingle
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.codershubinc.nullvoidlauncher.ui.drawer

import androidx.compose.runtime.Composable
import com.codershubinc.nullvoidlauncher.data.DrawerStyle
import com.codershubinc.nullvoidlauncher.data.UserManager
import com.codershubinc.nullvoidlauncher.data.repository.AppInfo
import com.codershubinc.nullvoidlauncher.ui.widgets.globleSearch.ElegantSearchScreen

/**
 * AppDrawer: Dispatches to the user's selected drawer style theme:
 * - SPOTLIGHT: macOS / iOS Spotlight style floating glass pill search, top hits, instant inline calculations & category tags
 * - ELEGANT: Void luxury serif gold theme with fast alphabet scroller
 * - GRID: 4-column modern icon application grid
 * - TERMINAL: Hacker / CLI command prompt styled search
 * - MINIMAL: Distraction-free typography list
 */
@Composable
fun AppDrawer(
    allApps: List<AppInfo>,
    userManager: UserManager,
    onClose: () -> Unit = {}
) {
    val drawerStyle = userManager.getDrawerStyle()
    val drawerFont = userManager.getDrawerFont()

    when (drawerStyle) {
        DrawerStyle.SPOTLIGHT -> {
            SpotlightSearchScreen(
                allApps = allApps,
                userManager = userManager,
                font = drawerFont,
                onClose = onClose
            )
        }
        DrawerStyle.ELEGANT -> {
            ElegantSearchScreen(
                allApps = allApps,
                userManager = userManager,
                font = drawerFont,
                onClose = onClose
            )
        }
        DrawerStyle.GRID -> {
            GridSearchScreen(
                allApps = allApps,
                userManager = userManager,
                font = drawerFont,
                onClose = onClose
            )
        }
        DrawerStyle.TERMINAL -> {
            TerminalSearchScreen(
                allApps = allApps,
                userManager = userManager,
                font = drawerFont,
                onClose = onClose
            )
        }
        DrawerStyle.MINIMAL -> {
            MinimalSearchScreen(
                allApps = allApps,
                userManager = userManager,
                font = drawerFont,
                onClose = onClose
            )
        }
    }
}
