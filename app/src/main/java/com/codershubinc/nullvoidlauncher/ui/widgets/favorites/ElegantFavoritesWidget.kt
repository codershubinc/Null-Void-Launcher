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
import com.codershubinc.nullvoidlauncher.data.prefs.rememberPrefVersion
import com.codershubinc.nullvoidlauncher.data.repository.*
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
    val prefVersion = rememberPrefVersion(userManager)
    val appsVersion by AppRepository.appsVersion.collectAsState()
    
    val widgetColor = Color(userManager.widgets.color)
    val cornerRadius = userManager.widgets.cornerRadius
    val blurIntensity = userManager.widgets.blurIntensity
    val glassEffect = userManager.widgets.glassEffect

    var favoriteApps by remember { mutableStateOf<List<AppInfo>>(previewApps ?: emptyList()) }
    var isVisible by remember { mutableStateOf(previewApps != null) }

    LaunchedEffect(previewApps, prefVersion, appsVersion) {
        if (previewApps != null) {
            favoriteApps = previewApps
            isVisible = true
            return@LaunchedEffect
        }
        withContext(Dispatchers.IO) {
            val all = getInstalledApps(context)
            favoriteApps = resolveFavoriteApps(userManager, all)
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
                            com.codershubinc.nullvoidlauncher.data.repository.safeLaunchApp(context, app)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    LazyAppIcon(
                        app = app,
                        context = context,
                        size = 32,
                        iconStyle = userManager.theme.iconStyle
                    )
                }
            }
        }
    }
}
