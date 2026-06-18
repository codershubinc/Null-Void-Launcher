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


@Composable
fun ElegantFavoritesWidget() {
    val context = LocalContext.current
    val userManager = remember { UserManager(context) }
    var favoriteApps by remember { mutableStateOf<List<AppInfo>>(emptyList()) }
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
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

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(end = 24.dp, bottom = 120.dp),
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
                        .clip(CircleShape)
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.12f),
                                    Color.White.copy(alpha = 0.04f)
                                )
                            )
                        )
                        .border(
                            width = 1.dp,
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.3f),
                                    Color.White.copy(alpha = 0.05f)
                                )
                            ),
                            shape = CircleShape
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
                        size = 28,
                        tint = Color.White.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}
