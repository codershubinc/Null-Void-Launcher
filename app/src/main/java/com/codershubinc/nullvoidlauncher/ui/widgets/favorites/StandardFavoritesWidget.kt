package com.codershubinc.nullvoidlauncher.ui.widgets.favorites

import android.content.Context
import android.content.pm.LauncherApps
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.codershubinc.nullvoidlauncher.data.UserManager
import com.codershubinc.nullvoidlauncher.data.repository.AppInfo
import com.codershubinc.nullvoidlauncher.data.repository.LazyAppIcon
import com.codershubinc.nullvoidlauncher.data.repository.getInstalledApps
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun StandardFavoritesWidget() {
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
                all.take(5)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 32.dp, top = 60.dp),
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        favoriteApps.forEach { app ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clickable {
                        val launcherApps = context.getSystemService(Context.LAUNCHER_APPS_SERVICE) as LauncherApps
                        launcherApps.startMainActivity(app.componentName, app.userHandle, null, null)
                    }
            ) {
                LazyAppIcon(app, context, size = 28)
                Spacer(modifier = Modifier.width(16.dp))
            }
        }
    }
}
