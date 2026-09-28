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
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codershubinc.nullvoidlauncher.data.UserManager
import com.codershubinc.nullvoidlauncher.data.repository.AppInfo
import com.codershubinc.nullvoidlauncher.data.repository.LazyAppIcon

@Composable
fun AppMenuDialog(
    app: AppInfo,
    context: Context,
    userManager: UserManager,
    favoritesList: List<String>,
    hiddenApps: Set<String>,
    onFavoritesChanged: (List<String>) -> Unit,
    onHiddenAppsChanged: (Set<String>) -> Unit,
    onDismiss: () -> Unit
) {
    val appKey = app.componentName.flattenToString()
    val isFav = favoritesList.contains(appKey)
    val isHid = hiddenApps.contains(appKey)

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF141418),
        shape = RoundedCornerShape(24.dp),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(10.dp))
                        .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    LazyAppIcon(app, context)
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(
                        text = app.label,
                        color = Color.White,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = app.packageName,
                        color = Color.White.copy(alpha = 0.4f),
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // App Info
                AppMenuActionItem(
                    icon = Icons.Rounded.Info,
                    title = "App Info",
                    subtitle = "System permissions, storage & battery"
                ) {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.parse("package:${app.packageName}")
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    context.startActivity(intent)
                    onDismiss()
                }

                // Toggle Favorites
                AppMenuActionItem(
                    icon = if (isFav) Icons.Rounded.StarBorder else Icons.Rounded.Star,
                    title = if (isFav) "Remove from Favorites" else "Add to Favorites",
                    subtitle = if (isFav) "Remove from quick launcher dock" else "Pin to quick launcher dock"
                ) {
                    val current = favoritesList.toMutableList()
                    if (isFav) {
                        current.remove(appKey)
                    } else {
                        if (!current.contains(appKey)) current.add(appKey)
                    }
                    userManager.saveFavorites(current)
                    onFavoritesChanged(current)
                    onDismiss()
                }

                // Toggle Hide App
                AppMenuActionItem(
                    icon = if (isHid) Icons.Rounded.Visibility else Icons.Rounded.VisibilityOff,
                    title = if (isHid) "Unhide Application" else "Hide Application",
                    subtitle = if (isHid) "Restore app to main drawer" else "Hide app from drawer search"
                ) {
                    userManager.toggleHiddenApp(appKey)
                    onHiddenAppsChanged(userManager.getHiddenApps())
                    onDismiss()
                }

                // Uninstall App
                AppMenuActionItem(
                    icon = Icons.Rounded.DeleteOutline,
                    title = "Uninstall",
                    subtitle = "Remove app from device",
                    tint = Color(0xFFFF5252)
                ) {
                    val uninstallIntent = Intent(Intent.ACTION_DELETE).apply {
                        data = Uri.parse("package:${app.packageName}")
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    context.startActivity(uninstallIntent)
                    onDismiss()
                }
            }
        },
        confirmButton = {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onDismiss() }
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "Cancel",
                    color = Color.White.copy(alpha = 0.6f),
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                )
            }
        }
    )
}

@Composable
fun AppMenuActionItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    tint: Color = Color.White,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(tint.copy(alpha = 0.08f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = title,
                color = tint,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = subtitle,
                color = Color.White.copy(alpha = 0.4f),
                fontSize = 11.sp
            )
        }
    }
}
