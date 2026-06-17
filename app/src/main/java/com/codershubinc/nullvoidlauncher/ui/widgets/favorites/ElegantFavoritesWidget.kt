package com.codershubinc.nullvoidlauncher.ui.widgets.favorites

import android.content.Context
import android.content.pm.LauncherApps
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
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(end = 24.dp, bottom = 120.dp),
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        favoriteApps.forEach { app ->
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White.copy(alpha = 0.05f))
                    .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                    .clickable {
                        val launcherApps = context.getSystemService(Context.LAUNCHER_APPS_SERVICE) as LauncherApps
                        launcherApps.startMainActivity(app.componentName, app.userHandle, null, null)
                    },
                contentAlignment = Alignment.Center
            ) {
                LazyAppIcon(app, context, size = 24)
            }
        }
    }
}
