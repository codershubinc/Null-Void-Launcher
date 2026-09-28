/*
 * Copyright (C) 2026- Swapnil Ingle
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.codershubinc.nullvoidlauncher.ui.drawer

import android.content.Context
import android.content.pm.LauncherApps
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codershubinc.nullvoidlauncher.data.UserManager
import com.codershubinc.nullvoidlauncher.data.WidgetFont
import com.codershubinc.nullvoidlauncher.data.repository.AppInfo
import com.codershubinc.nullvoidlauncher.utils.MathEvaluator
import kotlinx.coroutines.delay

/**
 * TerminalSearchScreen: Hacker / CLI command prompt styled app drawer.
 * Monospace typography, green / cyan prompt, ASCII indicators, minimal footprint.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TerminalSearchScreen(
    allApps: List<AppInfo>,
    userManager: UserManager,
    font: WidgetFont = WidgetFont.MONOSPACE,
    onClose: () -> Unit = {}
) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    var selectedAppForMenu by remember { mutableStateOf<AppInfo?>(null) }
    var hiddenApps by remember { mutableStateOf(userManager.getHiddenApps()) }
    var favoritesList by remember { mutableStateOf(userManager.getFavorites()) }
    var showHiddenAppsOnly by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(120)
        focusRequester.requestFocus()
        keyboardController?.show()
    }

    val mathResult = remember(searchQuery) {
        MathEvaluator.evaluate(searchQuery)
    }

    val visibleApps = remember(allApps, hiddenApps, showHiddenAppsOnly) {
        allApps.filter { app ->
            val key = app.componentName.flattenToString()
            if (showHiddenAppsOnly) hiddenApps.contains(key) else !hiddenApps.contains(key)
        }.sortedBy { it.label.lowercase() }
    }

    val filteredApps = remember(searchQuery, visibleApps) {
        if (searchQuery.trim().isEmpty()) {
            visibleApps
        } else {
            val query = searchQuery.trim().lowercase()
            visibleApps.filter { it.label.lowercase().contains(query) }
        }
    }

    val termColor = Color(0xFF00FF66)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF050B05))
            .statusBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp)
        ) {
            Spacer(modifier = Modifier.height(14.dp))

            // CLI PROMPT BAR
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0A180A), RoundedCornerShape(8.dp))
                    .border(1.dp, termColor.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 14.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "user@nullvoid:~$ ",
                    color = termColor,
                    fontFamily = font.toFontFamily(),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )

                BasicTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    textStyle = TextStyle(
                        color = Color.White,
                        fontSize = 15.sp,
                        fontFamily = font.toFontFamily(),
                        fontWeight = FontWeight.Medium
                    ),
                    cursorBrush = SolidColor(termColor),
                    modifier = Modifier
                        .weight(1f)
                        .focusRequester(focusRequester),
                    decorationBox = { innerTextField ->
                        if (searchQuery.isEmpty()) {
                            Text(
                                text = "exec grep...",
                                color = termColor.copy(alpha = 0.35f),
                                fontFamily = font.toFontFamily(),
                                fontSize = 15.sp
                            )
                        }
                        innerTextField()
                    }
                )

                if (searchQuery.isNotEmpty()) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = "Clear",
                        tint = termColor.copy(alpha = 0.7f),
                        modifier = Modifier
                            .size(18.dp)
                            .clip(CircleShape)
                            .clickable { searchQuery = "" }
                    )
                } else {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = "Close",
                        tint = termColor.copy(alpha = 0.4f),
                        modifier = Modifier
                            .size(18.dp)
                            .clip(CircleShape)
                            .clickable {
                                keyboardController?.hide()
                                onClose()
                            }
                    )
                }
            }

            // Math result in Terminal
            if (mathResult != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF0F2613), RoundedCornerShape(6.dp))
                        .border(1.dp, termColor.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = ">> STDOUT: $mathResult",
                        color = termColor,
                        fontFamily = font.toFontFamily(),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "[COPY]",
                        color = termColor.copy(alpha = 0.8f),
                        fontFamily = font.toFontFamily(),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                            val clip = android.content.ClipData.newPlainText("Result", mathResult)
                            clipboard.setPrimaryClip(clip)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Status Row
            Text(
                text = ">>> FOUND ${filteredApps.size} BINARIES MATCHING QUERY",
                color = termColor.copy(alpha = 0.55f),
                fontFamily = font.toFontFamily(),
                fontSize = 11.sp,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Terminal List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                contentPadding = PaddingValues(bottom = 32.dp)
            ) {
                itemsIndexed(
                    items = filteredApps,
                    key = { _, app -> app.componentName.flattenToString() + app.userHandle.hashCode() }
                ) { index, app ->
                    val isFav = favoritesList.contains(app.componentName.flattenToString())
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color.White.copy(alpha = 0.02f))
                            .combinedClickable(
                                onClick = {
                                    val launcherApps = context.getSystemService(Context.LAUNCHER_APPS_SERVICE) as LauncherApps
                                    launcherApps.startMainActivity(app.componentName, app.userHandle, null, null)
                                    keyboardController?.hide()
                                    onClose()
                                },
                                onLongClick = { selectedAppForMenu = app }
                            )
                            .padding(horizontal = 10.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = String.format("%03d ", index),
                            color = termColor.copy(alpha = 0.4f),
                            fontFamily = font.toFontFamily(),
                            fontSize = 12.sp
                        )

                        Text(
                            text = "./${app.label.lowercase().replace(" ", "_")}",
                            color = Color.White,
                            fontFamily = font.toFontFamily(),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.weight(1f)
                        )

                        if (isFav) {
                            Text(
                                text = "[*]",
                                color = termColor,
                                fontFamily = font.toFontFamily(),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // App Long-Press Action Modal
        if (selectedAppForMenu != null) {
            AppMenuDialog(
                app = selectedAppForMenu!!,
                context = context,
                userManager = userManager,
                favoritesList = favoritesList,
                hiddenApps = hiddenApps,
                onFavoritesChanged = { favoritesList = it },
                onHiddenAppsChanged = { hiddenApps = it },
                onDismiss = { selectedAppForMenu = null }
            )
        }
    }
}
