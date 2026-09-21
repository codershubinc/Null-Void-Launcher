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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
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
 * DockFavoritesWidget — Floating horizontal dock capsule.
 * Renders favorite apps in an elegant horizontal row with frosted glass background.
 */
@Composable
fun DockFavoritesWidget(
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
            } else all.take(5)
        }
        isVisible = true
    }

    val dockShape = RoundedCornerShape(32.dp)
    val iconShape = RoundedCornerShape((cornerRadius * 0.8f).dp)

    Row(
        modifier = modifier
            .clip(dockShape)
            .background(
                Brush.verticalGradient(
                    listOf(
                        widgetColor.copy(alpha = widgetColor.alpha.coerceAtMost(0.35f)),
                        widgetColor.copy(alpha = widgetColor.alpha.coerceAtMost(0.12f))
                    )
                )
            )
            .border(
                1.dp,
                Brush.verticalGradient(
                    listOf(
                        Color.White.copy(alpha = 0.35f),
                        Color.White.copy(alpha = 0.08f)
                    )
                ),
                dockShape
            )
            .padding(horizontal = 14.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        favoriteApps.forEachIndexed { index, app ->
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(tween(350, delayMillis = index * 50)) +
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
                        .size(46.dp)
                        .clip(iconShape)
                        .background(Color.White.copy(alpha = 0.08f))
                        .border(1.dp, Color.White.copy(alpha = 0.12f), iconShape)
                        .clickable {
                            val la = context.getSystemService(Context.LAUNCHER_APPS_SERVICE) as LauncherApps
                            la.startMainActivity(app.componentName, app.userHandle, null, null)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    LazyAppIcon(app = app, context = context, size = 26)
                }
            }
        }
    }
}
