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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codershubinc.nullvoidlauncher.data.UserManager
import com.codershubinc.nullvoidlauncher.data.WidgetFont
import com.codershubinc.nullvoidlauncher.data.repository.AppInfo
import com.codershubinc.nullvoidlauncher.data.repository.LazyAppIcon
import com.codershubinc.nullvoidlauncher.utils.MathEvaluator
import kotlinx.coroutines.delay

/**
 * MinimalSearchScreen: Text-first ultra-distraction-free clean typography launcher.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MinimalSearchScreen(
    allApps: List<AppInfo>,
    userManager: UserManager,
    font: WidgetFont = WidgetFont.DEFAULT,
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF040404))
            .statusBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // Minimalist pure-text search line
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
            ) {
                BasicTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    textStyle = TextStyle(
                        color = Color.White,
                        fontSize = 24.sp,
                        fontFamily = font.toFontFamily(),
                        fontWeight = FontWeight.Light,
                        letterSpacing = 1.sp
                    ),
                    cursorBrush = SolidColor(Color.White),
                    modifier = Modifier
                        .weight(1f)
                        .focusRequester(focusRequester),
                    decorationBox = { innerTextField ->
                        if (searchQuery.isEmpty()) {
                            Text(
                                text = "type to search...",
                                color = Color.White.copy(alpha = 0.25f),
                                fontFamily = font.toFontFamily(),
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Light,
                                letterSpacing = 1.sp
                            )
                        }
                        innerTextField()
                    }
                )

                if (searchQuery.isNotEmpty()) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = "Clear",
                        tint = Color.White.copy(alpha = 0.5f),
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .clickable { searchQuery = "" }
                    )
                } else {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = "Close",
                        tint = Color.White.copy(alpha = 0.35f),
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .clickable {
                                keyboardController?.hide()
                                onClose()
                            }
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color.White.copy(alpha = 0.1f))
            )

            if (mathResult != null) {
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "= $mathResult",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontFamily = font.toFontFamily(),
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier.clickable {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                        val clip = android.content.ClipData.newPlainText("Result", mathResult)
                        clipboard.setPrimaryClip(clip)
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Pure text list
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                contentPadding = PaddingValues(bottom = 32.dp)
            ) {
                itemsIndexed(
                    items = filteredApps,
                    key = { _, app -> app.componentName.flattenToString() + app.userHandle.hashCode() }
                ) { _, app ->
                    val isFav = favoritesList.contains(app.componentName.flattenToString())
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .combinedClickable(
                                onClick = {
                                    val launcherApps = context.getSystemService(Context.LAUNCHER_APPS_SERVICE) as LauncherApps
                                    launcherApps.startMainActivity(app.componentName, app.userHandle, null, null)
                                    keyboardController?.hide()
                                    onClose()
                                },
                                onLongClick = { selectedAppForMenu = app }
                            )
                            .padding(vertical = 12.dp)
                    ) {
                        Text(
                            text = app.label.lowercase(),
                            color = Color.White.copy(alpha = 0.85f),
                            fontFamily = font.toFontFamily(),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Normal,
                            modifier = Modifier.weight(1f)
                        )

                        if (isFav) {
                            Icon(
                                imageVector = Icons.Rounded.Star,
                                contentDescription = "Favorite",
                                tint = Color.White.copy(alpha = 0.4f),
                                modifier = Modifier.size(16.dp)
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
