package com.codershubinc.nullvoidlauncher.data.repository

import android.content.ComponentName
import android.content.Context
import android.content.pm.LauncherApps
import android.graphics.drawable.AdaptiveIconDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.UserHandle
import android.os.UserManager
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// 1. Data Class (Lighter: No Icon stored here)
data class AppInfo(
    val label: String,
    val packageName: String,
    val componentName: ComponentName,
    val userHandle: UserHandle
)

// 2. Function to fetch installed apps (Now super fast!)
fun getInstalledApps(context: Context): List<AppInfo> {
    val launcherApps = context.getSystemService(Context.LAUNCHER_APPS_SERVICE) as LauncherApps
    val userManager = context.getSystemService(Context.USER_SERVICE) as UserManager

    val allApps = mutableListOf<AppInfo>()
    val profiles = userManager.userProfiles

    for (profile in profiles) {
        val activities = launcherApps.getActivityList(null, profile)
        for (activity in activities) {
            val pkg = activity.applicationInfo.packageName
            if (pkg != context.packageName) {
                allApps.add(
                    AppInfo(
                        label = activity.label.toString(),
                        packageName = pkg,
                        componentName = activity.componentName,
                        userHandle = profile
                    )
                )
            }
        }
    }

    return allApps.distinctBy { it.componentName }.sortedBy { it.label.lowercase() }
}

@Composable
fun LazyAppIcon(app: AppInfo, context: Context, size: Int = 28) {
    var icon by remember(app) { mutableStateOf<Drawable?>(null) }

    LaunchedEffect(app) {
        withContext(Dispatchers.IO) {
            val launcherApps = context.getSystemService(Context.LAUNCHER_APPS_SERVICE) as LauncherApps
            val activities = launcherApps.getActivityList(app.packageName, app.userHandle)
            val activityInfo = activities.find { it.componentName == app.componentName }
            
            val density = context.resources.displayMetrics.densityDpi
            
            // 1. Get the base icon (this respects system icon themes better than badged icons)
            var drawable = activityInfo?.getIcon(density)
            
            // 2. For Android 13+, check for the monochrome (Material You themed) version
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (drawable is AdaptiveIconDrawable) {
                    val monochrome = drawable.monochrome
                    if (monochrome != null) {
                        drawable = monochrome
                    }
                }
            }

            // 3. Re-apply the user badge (like the work profile briefcase) manually
            icon = if (drawable != null) {
                context.packageManager.getUserBadgedIcon(drawable, app.userHandle)
            } else {
                null
            }
        }
    }

    Box(modifier = Modifier.size(size.dp)) {
        if (icon != null) {
            Image(
                painter = rememberAsyncImagePainter(icon),
                contentDescription = null,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Box(modifier = Modifier.fillMaxSize().background(Color.DarkGray.copy(alpha = 0.3f)))
        }
    }
}
