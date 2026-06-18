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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.Icon
import androidx.compose.ui.graphics.vector.ImageVector
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

private fun getCustomIcon(packageName: String): ImageVector? {
    return when {
        packageName.contains("chrome") || packageName.contains("browser") -> Icons.Rounded.Public
        packageName.contains("camera") -> Icons.Rounded.PhotoCamera
        packageName.contains("gallery") || packageName.contains("photos") -> Icons.Rounded.Collections
        packageName.contains("message") || packageName.contains("messaging") || packageName.contains("sms") -> Icons.Rounded.Sms
        packageName.contains("phone") || packageName.contains("dialer") -> Icons.Rounded.Call
        packageName.contains("contact") -> Icons.Rounded.Person
        packageName.contains("mail") || packageName.contains("gmail") -> Icons.Rounded.Mail
        packageName.contains("calendar") -> Icons.Rounded.CalendarToday
        packageName.contains("clock") -> Icons.Rounded.AccessTime
        packageName.contains("map") -> Icons.Rounded.Map
        packageName.contains("music") || packageName.contains("spotify") || packageName.contains("ytm") -> Icons.Rounded.MusicNote
        packageName.contains("video") || packageName.contains("youtube") || packageName.contains("player") -> Icons.Rounded.PlayCircle
        packageName.contains("setting") -> Icons.Rounded.Settings
        packageName.contains("calc") -> Icons.Rounded.Calculate
        packageName.contains("note") || packageName.contains("keep") -> Icons.Rounded.EditNote
        packageName.contains("drive") || packageName.contains("file") -> Icons.Rounded.Folder
        packageName.contains("store") || packageName.contains("vending") -> Icons.Rounded.Storefront
        packageName.contains("whatsapp") -> Icons.Rounded.Chat
        packageName.contains("telegram") -> Icons.Rounded.Send
        packageName.contains("facebook") || packageName.contains("fb") -> Icons.Rounded.People
        packageName.contains("instagram") -> Icons.Rounded.Camera
        packageName.contains("twitter") || packageName.contains(" x ") -> Icons.Rounded.Tag
        else -> null
    }
}

@Composable
fun LazyAppIcon(
    app: AppInfo,
    context: Context,
    size: Int = 28,
    tint: Color? = null,
    grayscale: Boolean = false
) {
    var icon by remember(app) { mutableStateOf<Drawable?>(null) }

    LaunchedEffect(app) {
        withContext(Dispatchers.IO) {
            val launcherApps = context.getSystemService(Context.LAUNCHER_APPS_SERVICE) as LauncherApps
            val activities = launcherApps.getActivityList(app.packageName, app.userHandle)
            val activityInfo = activities.find { it.componentName == app.componentName }
            
            val density = context.resources.displayMetrics.densityDpi
            
            // 1. Try to get foreground of adaptive icon for a cleaner look
            var drawable = activityInfo?.getIcon(density)
            
            if (drawable is AdaptiveIconDrawable) {
                // If we want a tinted look, the foreground usually looks better than the whole adaptive icon
                if (tint != null || grayscale) {
                    drawable = drawable.foreground
                }
            }

            // 2. For Android 13+, check for the monochrome version
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (drawable is AdaptiveIconDrawable) {
                    val monochrome = drawable.monochrome
                    if (monochrome != null) {
                        drawable = monochrome
                    }
                }
            }

            icon = if (drawable != null) {
                context.packageManager.getUserBadgedIcon(drawable, app.userHandle)
            } else {
                null
            }
        }
    }

    Box(modifier = Modifier.size(size.dp)) {
        val customIcon = getCustomIcon(app.packageName.lowercase())

        if (customIcon != null && tint != null) {
            Icon(
                imageVector = customIcon,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                tint = tint
            )
        } else if (icon != null) {
            val colorFilter = when {
                tint != null -> ColorFilter.tint(tint)
                grayscale -> ColorFilter.colorMatrix(ColorMatrix().apply { setToSaturation(0f) })
                else -> null
            }

            Image(
                painter = rememberAsyncImagePainter(icon),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                colorFilter = colorFilter
            )
        } else {
            Box(modifier = Modifier.fillMaxSize().background(Color.DarkGray.copy(alpha = 0.3f)))
        }
    }
}
