package com.codershubinc.nullvoidlauncher.data.repository

import android.content.ComponentName
import android.content.Context
import android.content.pm.LauncherApps
import android.graphics.Bitmap
import android.graphics.drawable.AdaptiveIconDrawable
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.UserHandle
import android.os.UserManager
import androidx.collection.LruCache
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Chat
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

object AppIconCache {
    val cache = LruCache<ComponentName, ImageBitmap>(200)
}

object AppRepository {
    private val _appsVersion = MutableStateFlow(0)
    val appsVersion: StateFlow<Int> = _appsVersion.asStateFlow()

    fun notifyAppsChanged() {
        AppIconCache.cache.evictAll()
        _appsVersion.value++
    }
}

/**
 * Checks if a stored favorite key (which may be a ComponentName or packageName) matches an app.
 * Resilient against app updates where the launcher activity class name changes.
 */
fun isAppKeyMatching(key: String, app: AppInfo): Boolean {
    val compStr = app.componentName.flattenToString()
    val pkg = app.packageName
    return key == compStr || key == pkg || key.substringBefore('/') == pkg
}

/**
 * Checks if an app matches any key in a collection of favorite keys.
 */
fun isAppInFavorites(app: AppInfo, favorites: Collection<String>): Boolean {
    return favorites.any { isAppKeyMatching(it, app) }
}

/**
 * Resolves a stored favorite key to an installed AppInfo.
 * Resilient to app updates where the launcher activity class name changes.
 */
fun findAppForFavoriteKey(key: String, installedApps: List<AppInfo>): AppInfo? {
    // 1. Exact ComponentName match first
    val exact = installedApps.find { it.componentName.flattenToString() == key }
    if (exact != null) return exact

    // 2. Fallback to package name match (handling app updates where the main activity was renamed)
    val targetPkg = key.substringBefore('/')
    return installedApps.find { it.packageName == targetPkg }
}

/**
 * Resolves the full list of favorite AppInfo objects from SharedPreferences.
 * Automatically heals outdated component names when apps update, and updates
 * SharedPreferences to ensure persistent favorites consistency.
 */
fun resolveFavoriteApps(
    userManager: com.codershubinc.nullvoidlauncher.data.UserManager,
    installedApps: List<AppInfo>
): List<AppInfo> {
    val savedFavs = userManager.drawer.favorites
    if (savedFavs.isEmpty()) {
        return installedApps.take(5)
    }

    val resolvedList = mutableListOf<AppInfo>()
    val updatedKeys = mutableListOf<String>()
    var needsMigration = false

    for (favKey in savedFavs) {
        val app = findAppForFavoriteKey(favKey, installedApps)
        if (app != null) {
            resolvedList.add(app)
            val currentCanonical = app.componentName.flattenToString()
            updatedKeys.add(currentCanonical)
            if (favKey != currentCanonical) {
                needsMigration = true
            }
        }
    }

    if (needsMigration) {
        userManager.drawer.favorites = updatedKeys
    }

    return if (resolvedList.isNotEmpty()) resolvedList else installedApps.take(5)
}

private fun drawableToBitmap(drawable: Drawable): Bitmap {
    if (drawable is BitmapDrawable && drawable.bitmap != null) {
        return drawable.bitmap
    }
    val w = if (drawable.intrinsicWidth > 0) drawable.intrinsicWidth else 128
    val h = if (drawable.intrinsicHeight > 0) drawable.intrinsicHeight else 128
    val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
    val canvas = android.graphics.Canvas(bitmap)
    drawable.setBounds(0, 0, canvas.width, canvas.height)
    drawable.draw(canvas)
    return bitmap
}

enum class AppCategory(val title: String) {
    ALL("All"),
    COMMUNICATION("Chat"),
    MEDIA("Media"),
    PRODUCTIVITY("Work"),
    TOOLS("Tools"),
    GAMES("Games"),
    OTHER("More")
}

// 1. Data Class (Lighter: No Icon stored here)
data class AppInfo(
    val label: String,
    val packageName: String,
    val componentName: ComponentName,
    val userHandle: UserHandle,
    val category: AppCategory = AppCategory.OTHER
)

private fun resolveCategory(packageName: String, label: String): AppCategory {
    val pkg = packageName.lowercase()
    val lbl = label.lowercase()
    return when {
        pkg.contains("whatsapp") || pkg.contains("telegram") || pkg.contains("signal") ||
        pkg.contains("message") || pkg.contains("dialer") || pkg.contains("phone") ||
        pkg.contains("contact") || pkg.contains("discord") || pkg.contains("messenger") ||
        pkg.contains("twitter") || pkg.contains(" x ") || pkg.contains("instagram") ||
        pkg.contains("facebook") || pkg.contains("reddit") || pkg.contains("linkedin") -> AppCategory.COMMUNICATION

        pkg.contains("youtube") || pkg.contains("spotify") || pkg.contains("music") ||
        pkg.contains("video") || pkg.contains("netflix") || pkg.contains("prime") ||
        pkg.contains("audio") || pkg.contains("podcast") || pkg.contains("camera") ||
        pkg.contains("gallery") || pkg.contains("photos") -> AppCategory.MEDIA

        pkg.contains("mail") || pkg.contains("gmail") || pkg.contains("outlook") ||
        pkg.contains("docs") || pkg.contains("sheet") || pkg.contains("slide") ||
        pkg.contains("office") || pkg.contains("keep") || pkg.contains("note") ||
        pkg.contains("notion") || pkg.contains("calendar") || pkg.contains("drive") ||
        pkg.contains("dropbox") || pkg.contains("pdf") -> AppCategory.PRODUCTIVITY

        pkg.contains("game") || pkg.contains("play") && pkg.contains("games") ||
        pkg.contains("unity") || pkg.contains("supercell") || pkg.contains("roblox") ||
        pkg.contains("minecraft") || lbl.contains("game") -> AppCategory.GAMES

        pkg.contains("calc") || pkg.contains("clock") || pkg.contains("time") ||
        pkg.contains("setting") || pkg.contains("file") || pkg.contains("browser") ||
        pkg.contains("chrome") || pkg.contains("firefox") || pkg.contains("tool") ||
        pkg.contains("terminal") || pkg.contains("store") || pkg.contains("vending") -> AppCategory.TOOLS

        else -> AppCategory.OTHER
    }
}

// 2. Function to fetch installed apps (Now super fast!)
fun safeLaunchApp(context: Context, app: AppInfo): Boolean {
    return try {
        val launcherApps = context.getSystemService(Context.LAUNCHER_APPS_SERVICE) as? LauncherApps
        if (launcherApps != null) {
            launcherApps.startMainActivity(app.componentName, app.userHandle, null, null)
            true
        } else {
            val intent = context.packageManager.getLaunchIntentForPackage(app.packageName)
            if (intent != null) {
                intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
                true
            } else {
                android.widget.Toast.makeText(context, "App cannot be launched", android.widget.Toast.LENGTH_SHORT).show()
                false
            }
        }
    } catch (e: Exception) {
        android.widget.Toast.makeText(context, "Failed to launch ${app.label}", android.widget.Toast.LENGTH_SHORT).show()
        false
    }
}

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
                val label = activity.label.toString()
                allApps.add(
                    AppInfo(
                        label = label,
                        packageName = pkg,
                        componentName = activity.componentName,
                        userHandle = profile,
                        category = resolveCategory(pkg, label)
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
        packageName.contains("whatsapp") -> Icons.AutoMirrored.Rounded.Chat
        packageName.contains("telegram") -> Icons.AutoMirrored.Rounded.Send
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
    grayscale: Boolean = false,
    iconStyle: com.codershubinc.nullvoidlauncher.data.IconStyle = com.codershubinc.nullvoidlauncher.data.IconStyle.DEFAULT
) {
    var imageBitmap by remember(app.componentName) {
        mutableStateOf(AppIconCache.cache[app.componentName])
    }

    LaunchedEffect(app.componentName) {
        if (imageBitmap == null) {
            withContext(Dispatchers.IO) {
                val bmp = try {
                    val launcherApps = context.getSystemService(Context.LAUNCHER_APPS_SERVICE) as? LauncherApps
                    val activityList = launcherApps?.getActivityList(app.packageName, app.userHandle)
                    val activityInfo = activityList?.firstOrNull { it.componentName == app.componentName }
                    val drawable = activityInfo?.getBadgedIcon(context.resources.displayMetrics.densityDpi)
                        ?: context.packageManager.getActivityIcon(app.componentName)
                    drawableToBitmap(drawable).asImageBitmap()
                } catch (e: Exception) {
                    try {
                        val drawable = context.packageManager.getApplicationIcon(app.packageName)
                        drawableToBitmap(drawable).asImageBitmap()
                    } catch (e2: Exception) {
                        null
                    }
                }
                if (bmp != null) {
                    AppIconCache.cache.put(app.componentName, bmp)
                    imageBitmap = bmp
                }
            }
        }
    }

    Box(modifier = Modifier.size(size.dp), contentAlignment = Alignment.Center) {
        val customIcon = getCustomIcon(app.packageName.lowercase())

        if (customIcon != null && (tint != null || iconStyle == com.codershubinc.nullvoidlauncher.data.IconStyle.MINIMAL_OUTLINE)) {
            Icon(
                imageVector = customIcon,
                contentDescription = app.label,
                modifier = Modifier.fillMaxSize(),
                tint = tint ?: Color(0xFFC5A35E)
            )
        } else if (imageBitmap != null) {
            val colorFilter = when {
                tint != null -> ColorFilter.tint(tint)
                iconStyle == com.codershubinc.nullvoidlauncher.data.IconStyle.MONOCHROME || grayscale ->
                    ColorFilter.colorMatrix(ColorMatrix().apply { setToSaturation(0f) })
                else -> null
            }

            Image(
                bitmap = imageBitmap!!,
                contentDescription = app.label,
                modifier = Modifier.fillMaxSize(),
                colorFilter = colorFilter
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White.copy(alpha = 0.12f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = app.label.take(1).uppercase(),
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = (size * 0.45f).sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
