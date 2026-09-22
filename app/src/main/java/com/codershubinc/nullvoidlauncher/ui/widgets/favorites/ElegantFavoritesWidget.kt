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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.codershubinc.nullvoidlauncher.data.UserManager
import com.codershubinc.nullvoidlauncher.data.repository.AppInfo
import com.codershubinc.nullvoidlauncher.data.repository.LazyAppIcon
import com.codershubinc.nullvoidlauncher.data.repository.getInstalledApps
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


import com.codershubinc.nullvoidlauncher.data.WidgetFont
import androidx.compose.ui.draw.blur

@Composable
fun ElegantFavoritesWidget(
    modifier: Modifier = Modifier,
    previewApps: List<AppInfo>? = null,
    font: WidgetFont = WidgetFont.DEFAULT
) {
    val context = LocalContext.current
    val userManager = remember { UserManager(context) }
    
    val widgetColor = Color(userManager.getWidgetColor())
    val cornerRadius = userManager.getWidgetCornerRadius()
    val blurIntensity = userManager.getWidgetBlurIntensity()
    val glassEffect = userManager.getWidgetGlassEffect()

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
                savedFavs.mapNotNull { pkg ->
                    all.find { it.componentName.flattenToString() == pkg }
                }
            } else {
                all.take(4) // Assume first 4 for demo
            }
        }
        isVisible = true
    }

    val shape = RoundedCornerShape(cornerRadius.dp)

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        favoriteApps.forEachIndexed { index, app ->
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(animationSpec = tween(500, delayMillis = index * 100)) +
                        scaleIn(
                            initialScale = 0.5f,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessLow
                            )
                        ),
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(shape)
                        .background(
                            if (glassEffect) {
                                Brush.verticalGradient(
                                    colors = listOf(
                                        widgetColor.copy(alpha = widgetColor.alpha.coerceAtMost(0.4f)),
                                        widgetColor.copy(alpha = widgetColor.alpha.coerceAtMost(0.1f))
                                    )
                                )
                            } else {
                                Brush.verticalGradient(colors = listOf(widgetColor, widgetColor))
                            }
                        )
                        .border(
                            width = 1.dp,
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.3f),
                                    Color.White.copy(alpha = 0.05f)
                                )
                            ),
                            shape = shape
                        )
                        .clickable {
                            val launcherApps = context.getSystemService(Context.LAUNCHER_APPS_SERVICE) as LauncherApps
                            launcherApps.startMainActivity(app.componentName, app.userHandle, null, null)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    LazyAppIcon(
                        app = app,
                        context = context,
                        size = 32,
                        iconStyle = userManager.getIconStyle()
                    )
                }
            }
        }
    }
}
