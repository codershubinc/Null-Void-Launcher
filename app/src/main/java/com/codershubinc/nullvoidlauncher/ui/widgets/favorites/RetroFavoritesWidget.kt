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
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codershubinc.nullvoidlauncher.data.UserManager
import com.codershubinc.nullvoidlauncher.data.repository.AppInfo
import com.codershubinc.nullvoidlauncher.data.repository.LazyAppIcon
import com.codershubinc.nullvoidlauncher.data.repository.getInstalledApps
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

import com.codershubinc.nullvoidlauncher.data.WidgetFont

/**
 * RetroFavoritesWidget — Vertical list of favourite apps with amber labels
 * and a dark retro card background. Each app name is shown below its icon.
 */
@Composable
fun RetroFavoritesWidget(
    modifier: Modifier = Modifier,
    previewApps: List<AppInfo>? = null,
    font: WidgetFont = WidgetFont.MONOSPACE
) {
    val context = LocalContext.current
    val userManager = remember { UserManager(context) }
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

    val amber = Color(0xFFC5A35E)
    val shape = RoundedCornerShape(cornerRadius.dp)

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        favoriteApps.forEachIndexed { index, app ->
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(tween(400, delayMillis = index * 80)) +
                        scaleIn(
                            initialScale = 0.7f,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessLow
                            )
                        )
            ) {
                Row(
                    modifier = Modifier
                        .clip(shape)
                        .background(Color(0xFF1A1208))
                        .border(1.dp, amber.copy(alpha = 0.3f), shape)
                        .clickable {
                            val launcherApps = context.getSystemService(Context.LAUNCHER_APPS_SERVICE) as LauncherApps
                            launcherApps.startMainActivity(app.componentName, app.userHandle, null, null)
                        }
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    LazyAppIcon(app = app, context = context, size = 22, tint = amber)
                    Text(
                        text = app.label,
                        color = amber,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = font.toFontFamily(),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.widthIn(max = 100.dp)
                    )
                }
            }
        }
    }
}
