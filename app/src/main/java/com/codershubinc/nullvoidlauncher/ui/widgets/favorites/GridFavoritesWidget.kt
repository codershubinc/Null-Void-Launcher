package com.codershubinc.nullvoidlauncher.ui.widgets.favorites

import android.content.Context
import android.content.pm.LauncherApps
import androidx.compose.animation.*
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.codershubinc.nullvoidlauncher.data.UserManager
import com.codershubinc.nullvoidlauncher.data.repository.AppInfo
import com.codershubinc.nullvoidlauncher.data.repository.LazyAppIcon
import com.codershubinc.nullvoidlauncher.data.repository.getInstalledApps
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

import com.codershubinc.nullvoidlauncher.data.WidgetFont

/**
 * GridFavoritesWidget — Shows favourite apps in a 2×N grid of equal-size
 * icon tiles with the shared widget corner radius and glass-style background.
 */
@Composable
fun GridFavoritesWidget(
    modifier: Modifier = Modifier,
    previewApps: List<AppInfo>? = null,
    font: WidgetFont = WidgetFont.DEFAULT
) {
    val context = LocalContext.current
    val userManager = remember { UserManager(context) }
    val widgetColor = Color(userManager.getWidgetColor())
    val cornerRadius = userManager.getWidgetCornerRadius()

    var favoriteApps by remember { mutableStateOf<List<AppInfo>>(previewApps ?: emptyList()) }
    var isVisible by remember { mutableStateOf(previewApps != null) }

    LaunchedEffect(previewApps) {
        if (previewApps != null) {
            favoriteApps = previewApps
            isVisible = true
            return@LaunchedEffect
        }
        withContext(Dispatchers.IO) {
            val all = getInstalledApps(context)
            val savedFavs = userManager.getFavorites()
            favoriteApps = if (savedFavs.isNotEmpty()) {
                savedFavs.mapNotNull { pkg -> all.find { it.componentName.flattenToString() == pkg } }
            } else all.take(4)
        }
        isVisible = true
    }

    val shape = RoundedCornerShape(cornerRadius.dp)
    val tileShape = RoundedCornerShape((cornerRadius * 0.7f).dp)
    val columns = 2
    val rows = (favoriteApps.size + 1) / columns

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        repeat(rows) { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)) {
                for (col in 0 until columns) {
                    val idx = row * columns + col
                    val app = favoriteApps.getOrNull(idx)
                    if (app != null) {
                        AnimatedVisibility(
                            visible = isVisible,
                            enter = fadeIn(tween(400, delayMillis = idx * 60)) +
                                    scaleIn(
                                        initialScale = 0.6f,
                                        animationSpec = spring(
                                            dampingRatio = Spring.DampingRatioMediumBouncy,
                                            stiffness = Spring.StiffnessLow
                                        )
                                    )
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(tileShape)
                                    .background(widgetColor.copy(alpha = widgetColor.alpha.coerceAtMost(0.18f)))
                                    .border(1.dp, Color.White.copy(alpha = 0.12f), tileShape)
                                    .clickable {
                                        val la = context.getSystemService(Context.LAUNCHER_APPS_SERVICE) as LauncherApps
                                        la.startMainActivity(app.componentName, app.userHandle, null, null)
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                LazyAppIcon(
                                    app = app,
                                    context = context,
                                    size = 28,
                                    iconStyle = userManager.getIconStyle()
                                )
                            }
                        }
                    } else {
                        Spacer(modifier = Modifier.size(52.dp))
                    }
                }
            }
        }
    }
}
