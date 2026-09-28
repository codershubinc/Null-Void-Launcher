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
import androidx.compose.animation.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
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
 * SpotlightSearchScreen: Inspired by macOS Spotlight / iOS Spotlight.
 * Floating frosted-glass pill bar with instant top hit suggestions,
 * math quick-calculate badge, categories, and slick glassmorphic result cards.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SpotlightSearchScreen(
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

    var selectedCategory by remember { mutableStateOf(com.codershubinc.nullvoidlauncher.data.repository.AppCategory.ALL) }

    val visibleApps = remember(allApps, hiddenApps, showHiddenAppsOnly, selectedCategory) {
        allApps.filter { app ->
            val key = app.componentName.flattenToString()
            val matchesHidden = if (showHiddenAppsOnly) hiddenApps.contains(key) else !hiddenApps.contains(key)
            val matchesCategory = selectedCategory == com.codershubinc.nullvoidlauncher.data.repository.AppCategory.ALL || app.category == selectedCategory
            matchesHidden && matchesCategory
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

    val topHits = remember(searchQuery, filteredApps, favoritesList) {
        if (searchQuery.trim().isEmpty()) {
            // Show favorite apps as top hits when search bar is idle
            filteredApps.filter { favoritesList.contains(it.componentName.flattenToString()) }.take(5)
        } else {
            // Best matching hits
            filteredApps.take(3)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.82f))
            .statusBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // SPOTLIGHT CAPSULE SEARCH BAR
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(28.dp))
                    .background(Color(0xFF1E1E26).copy(alpha = 0.85f))
                    .border(
                        width = 1.2.dp,
                        brush = Brush.linearGradient(
                            listOf(
                                Color.White.copy(alpha = 0.22f),
                                Color.White.copy(alpha = 0.05f)
                            )
                        ),
                        shape = RoundedCornerShape(28.dp)
                    )
                    .padding(horizontal = 18.dp, vertical = 14.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Rounded.Search,
                        contentDescription = "Search",
                        tint = Color(0xFF64B5F6),
                        modifier = Modifier.size(24.dp)
                    )

                    Spacer(modifier = Modifier.width(14.dp))

                    BasicTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        textStyle = TextStyle(
                            color = Color.White,
                            fontSize = 18.sp,
                            fontFamily = font.toFontFamily(),
                            fontWeight = FontWeight.Medium
                        ),
                        cursorBrush = SolidColor(Color(0xFF64B5F6)),
                        modifier = Modifier
                            .weight(1f)
                            .focusRequester(focusRequester),
                        decorationBox = { innerTextField ->
                            if (searchQuery.isEmpty()) {
                                Text(
                                    text = if (showHiddenAppsOnly) "Spotlight: Hidden items..." else "Spotlight Search or Calculate...",
                                    color = Color.White.copy(alpha = 0.38f),
                                    fontFamily = font.toFontFamily(),
                                    fontSize = 17.sp
                                )
                            }
                            innerTextField()
                        }
                    )

                    if (searchQuery.isNotEmpty()) {
                        Icon(
                            imageVector = Icons.Rounded.Close,
                            contentDescription = "Clear",
                            tint = Color.White.copy(alpha = 0.6f),
                            modifier = Modifier
                                .size(22.dp)
                                .clip(CircleShape)
                                .clickable { searchQuery = "" }
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Rounded.Close,
                            contentDescription = "Close",
                            tint = Color.White.copy(alpha = 0.4f),
                            modifier = Modifier
                                .size(22.dp)
                                .clip(CircleShape)
                                .clickable {
                                    keyboardController?.hide()
                                    onClose()
                                }
                        )
                    }
                }
            }

            // Math Quick Calculator Card (Spotlight style inline answer)
            if (mathResult != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(Color(0xFF2979FF).copy(alpha = 0.18f))
                        .border(1.dp, Color(0xFF2979FF).copy(alpha = 0.4f), RoundedCornerShape(18.dp))
                        .padding(horizontal = 18.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF2979FF).copy(alpha = 0.3f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Calculate,
                                contentDescription = "Math",
                                tint = Color(0xFF82B1FF),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "= $mathResult",
                            color = Color.White,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = font.toFontFamily()
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.White.copy(alpha = 0.1f))
                            .clickable {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                                val clip = android.content.ClipData.newPlainText("Result", mathResult)
                                clipboard.setPrimaryClip(clip)
                            }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "COPY",
                            color = Color(0xFF82B1FF),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // TOP HITS / QUICK APPS ROW
            if (topHits.isNotEmpty()) {
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = if (searchQuery.isEmpty()) "FREQUENT & FAVORITES" else "TOP HITS",
                    color = Color(0xFF64B5F6).copy(alpha = 0.85f),
                    fontSize = 11.sp,
                    letterSpacing = 1.5.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(topHits, key = { it.componentName.flattenToString() + "_top" }) { app ->
                        SpotlightTopHitChip(
                            app = app,
                            context = context,
                            font = font,
                            onClick = {
                                val launcherApps = context.getSystemService(Context.LAUNCHER_APPS_SERVICE) as LauncherApps
                                launcherApps.startMainActivity(app.componentName, app.userHandle, null, null)
                                keyboardController?.hide()
                                onClose()
                            },
                            onLongClick = { selectedAppForMenu = app }
                        )
                    }
                }
            }

            // CATEGORY CHIP BAR (When no active query)
            if (searchQuery.isEmpty()) {
                Spacer(modifier = Modifier.height(14.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(com.codershubinc.nullvoidlauncher.data.repository.AppCategory.values().size) { idx ->
                        val cat = com.codershubinc.nullvoidlauncher.data.repository.AppCategory.values()[idx]
                        val isSelected = selectedCategory == cat
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (isSelected) Color(0xFF2979FF) else Color.White.copy(alpha = 0.05f))
                                .border(
                                    1.dp,
                                    if (isSelected) Color(0xFF2979FF) else Color.White.copy(alpha = 0.08f),
                                    RoundedCornerShape(14.dp)
                                )
                                .clickable { selectedCategory = cat }
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = cat.title,
                                color = if (isSelected) Color.White else Color.White.copy(alpha = 0.6f),
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // SECTION HEADER
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = when {
                        searchQuery.isNotEmpty() -> "MATCHING RESULTS (${filteredApps.size})"
                        showHiddenAppsOnly -> "HIDDEN (${filteredApps.size})"
                        selectedCategory != com.codershubinc.nullvoidlauncher.data.repository.AppCategory.ALL -> "${selectedCategory.title.uppercase()} (${filteredApps.size})"
                        else -> "APPLICATIONS (${filteredApps.size})"
                    },
                    color = Color.White.copy(alpha = 0.45f),
                    fontSize = 11.sp,
                    letterSpacing = 1.sp,
                    fontWeight = FontWeight.Bold
                )

                if (hiddenApps.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (showHiddenAppsOnly) Color(0xFF2979FF).copy(alpha = 0.25f) else Color.White.copy(alpha = 0.05f))
                            .border(1.dp, if (showHiddenAppsOnly) Color(0xFF2979FF) else Color.White.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                            .clickable { showHiddenAppsOnly = !showHiddenAppsOnly }
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (showHiddenAppsOnly) Icons.Rounded.VisibilityOff else Icons.Rounded.Visibility,
                                contentDescription = null,
                                tint = if (showHiddenAppsOnly) Color(0xFF82B1FF) else Color.White.copy(alpha = 0.6f),
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (showHiddenAppsOnly) "Show All" else "Hidden (${hiddenApps.size})",
                                color = if (showHiddenAppsOnly) Color.White else Color.White.copy(alpha = 0.6f),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // RESULTS LIST
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                contentPadding = PaddingValues(bottom = 32.dp)
            ) {
                itemsIndexed(
                    items = filteredApps,
                    key = { _, app -> app.componentName.flattenToString() + app.userHandle.hashCode() }
                ) { _, app ->
                    SpotlightAppItem(
                        app = app,
                        context = context,
                        font = font,
                        iconStyle = userManager.getIconStyle(),
                        isFavorite = favoritesList.contains(app.componentName.flattenToString()),
                        isHidden = hiddenApps.contains(app.componentName.flattenToString()),
                        onClick = {
                            val launcherApps = context.getSystemService(Context.LAUNCHER_APPS_SERVICE) as LauncherApps
                            launcherApps.startMainActivity(app.componentName, app.userHandle, null, null)
                            keyboardController?.hide()
                            onClose()
                        },
                        onLongClick = { selectedAppForMenu = app }
                    )
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun SpotlightTopHitChip(
    app: AppInfo,
    context: Context,
    font: WidgetFont,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(18.dp))
            .background(Color(0xFF1B1B22))
            .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(18.dp))
            .combinedClickable(onClick = onClick, onLongClick = onLongClick)
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            LazyAppIcon(app = app, context = context)
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = app.label,
            color = Color.White,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = font.toFontFamily(),
            maxLines = 1
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun SpotlightAppItem(
    app: AppInfo,
    context: Context,
    font: WidgetFont,
    iconStyle: com.codershubinc.nullvoidlauncher.data.IconStyle,
    isFavorite: Boolean,
    isHidden: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF131318).copy(alpha = 0.75f))
            .border(1.dp, Color.White.copy(alpha = 0.04f), RoundedCornerShape(16.dp))
            .combinedClickable(onClick = onClick, onLongClick = onLongClick)
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .background(Color.White.copy(alpha = 0.04f), RoundedCornerShape(12.dp))
                .border(1.dp, Color.White.copy(alpha = 0.07f), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            LazyAppIcon(app = app, context = context, iconStyle = iconStyle)
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = app.label,
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = font.toFontFamily()
            )
            Text(
                text = app.category.title,
                color = Color.White.copy(alpha = 0.35f),
                fontSize = 11.sp,
                fontFamily = font.toFontFamily()
            )
        }

        if (isFavorite) {
            Icon(
                imageVector = Icons.Rounded.Star,
                contentDescription = "Favorite",
                tint = Color(0xFF64B5F6),
                modifier = Modifier
                    .size(16.dp)
                    .padding(end = 4.dp)
            )
        }

        if (isHidden) {
            Icon(
                imageVector = Icons.Rounded.VisibilityOff,
                contentDescription = "Hidden",
                tint = Color.White.copy(alpha = 0.35f),
                modifier = Modifier
                    .size(16.dp)
                    .padding(end = 4.dp)
            )
        }
    }
}
