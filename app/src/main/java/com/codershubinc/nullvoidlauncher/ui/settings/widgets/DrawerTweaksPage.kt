/*
 * Copyright (C) 2026- Swapnil Ingle
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.codershubinc.nullvoidlauncher.ui.settings.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codershubinc.nullvoidlauncher.data.DrawerStyle
import com.codershubinc.nullvoidlauncher.data.UserManager
import com.codershubinc.nullvoidlauncher.data.WidgetFont
import com.codershubinc.nullvoidlauncher.ui.components.ModernCard

@Composable
fun DrawerTweaksPage(
    userManager: UserManager,
    onBack: () -> Unit
) {
    var drawerStyle by remember { mutableStateOf(userManager.getDrawerStyle()) }
    var drawerFont by remember { mutableStateOf(userManager.getDrawerFont()) }

    WidgetPageScaffold(
        title = "App Drawer Theme",
        subtitle = "Choose your app search interface layout and style",
        onBack = onBack
    ) {
        ModernCard {
            Text(
                text = "Live Preview",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(14.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        when (drawerStyle) {
                            DrawerStyle.SPOTLIGHT -> Color(0xFF16161D)
                            DrawerStyle.TERMINAL -> Color(0xFF071107)
                            DrawerStyle.MINIMAL -> Color(0xFF0A0A0A)
                            DrawerStyle.GRID -> Color(0xFF0F0F14)
                            DrawerStyle.ELEGANT -> Color(0xFF0C0C0F)
                        }
                    )
                    .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Column {
                    // Preview Search Bar
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(
                                when (drawerStyle) {
                                    DrawerStyle.SPOTLIGHT -> RoundedCornerShape(24.dp)
                                    DrawerStyle.TERMINAL -> RoundedCornerShape(6.dp)
                                    DrawerStyle.MINIMAL -> RoundedCornerShape(0.dp)
                                    else -> RoundedCornerShape(16.dp)
                                }
                            )
                            .background(
                                when (drawerStyle) {
                                    DrawerStyle.SPOTLIGHT -> Color(0xFF22222D)
                                    DrawerStyle.TERMINAL -> Color(0xFF0D200E)
                                    DrawerStyle.MINIMAL -> Color.Transparent
                                    else -> Color.White.copy(alpha = 0.06f)
                                }
                            )
                            .border(
                                width = 1.dp,
                                color = when (drawerStyle) {
                                    DrawerStyle.SPOTLIGHT -> Color(0xFF2979FF).copy(alpha = 0.35f)
                                    DrawerStyle.TERMINAL -> Color(0xFF00FF66).copy(alpha = 0.4f)
                                    DrawerStyle.MINIMAL -> Color.Transparent
                                    else -> Color.White.copy(alpha = 0.1f)
                                },
                                shape = when (drawerStyle) {
                                    DrawerStyle.SPOTLIGHT -> RoundedCornerShape(24.dp)
                                    DrawerStyle.TERMINAL -> RoundedCornerShape(6.dp)
                                    DrawerStyle.MINIMAL -> RoundedCornerShape(0.dp)
                                    else -> RoundedCornerShape(16.dp)
                                }
                            )
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Search,
                            contentDescription = null,
                            tint = when (drawerStyle) {
                                DrawerStyle.SPOTLIGHT -> Color(0xFF64B5F6)
                                DrawerStyle.TERMINAL -> Color(0xFF00FF66)
                                DrawerStyle.ELEGANT -> Color(0xFFC5A35E)
                                else -> Color.White.copy(alpha = 0.6f)
                            },
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = when (drawerStyle) {
                                DrawerStyle.SPOTLIGHT -> "Spotlight Search..."
                                DrawerStyle.TERMINAL -> "user@nullvoid:~$ grep"
                                DrawerStyle.MINIMAL -> "type to search..."
                                DrawerStyle.GRID -> "Search apps..."
                                DrawerStyle.ELEGANT -> "Search or calculate..."
                            },
                            color = Color.White.copy(alpha = 0.6f),
                            fontSize = 14.sp,
                            fontFamily = drawerFont.toFontFamily()
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Preview row sample
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.White.copy(alpha = 0.08f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("A", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Sample Application",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                fontFamily = drawerFont.toFontFamily()
                            )
                            Text(
                                text = when (drawerStyle) {
                                    DrawerStyle.SPOTLIGHT -> "Top Hit • Frequent"
                                    DrawerStyle.TERMINAL -> "SYS.EXEC.OK"
                                    DrawerStyle.ELEGANT -> "SYSTEM.PROTOCOL.LAUNCH"
                                    else -> "Application"
                                },
                                color = when (drawerStyle) {
                                    DrawerStyle.SPOTLIGHT -> Color(0xFF64B5F6).copy(alpha = 0.7f)
                                    DrawerStyle.TERMINAL -> Color(0xFF00FF66).copy(alpha = 0.7f)
                                    DrawerStyle.ELEGANT -> Color(0xFFC5A35E).copy(alpha = 0.7f)
                                    else -> Color.White.copy(alpha = 0.4f)
                                },
                                fontSize = 10.sp,
                                fontFamily = drawerFont.toFontFamily()
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Drawer Style Selector
            WidgetStyleSelector(
                title = "App Drawer Theme",
                styles = DrawerStyle.values().toList(),
                selectedStyle = drawerStyle,
                onStyleSelected = {
                    drawerStyle = it
                    userManager.saveDrawerStyle(it)
                },
                getLabel = {
                    when (it) {
                        DrawerStyle.SPOTLIGHT -> "Spotlight (Search & Hits)"
                        DrawerStyle.ELEGANT -> "Elegant (Serif & Gold)"
                        DrawerStyle.GRID -> "Grid (4-Column Icons)"
                        DrawerStyle.TERMINAL -> "Terminal (CLI Prompt)"
                        DrawerStyle.MINIMAL -> "Minimal (Clean Text)"
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Font Selector
            WidgetFontSelector(
                selectedFont = drawerFont,
                onFontSelected = {
                    drawerFont = it
                    userManager.saveDrawerFont(it)
                }
            )
        }
    }
}
