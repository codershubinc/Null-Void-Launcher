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
import kotlinx.coroutines.withContext

object AppIconCache {
    val cache = LruCache<ComponentName, ImageBitmap>(200)
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
    var imageBitmap by remember(app.componentName) {
        mutableStateOf(AppIconCache.cache[app.componentName])
    }

    LaunchedEffect(app.componentName) {
        if (imageBitmap == null) {
            withContext(Dispatchers.IO) {
                val bmp = try {
                    val drawable = context.packageManager.getActivityIcon(app.componentName)
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

        if (customIcon != null && tint != null) {
            Icon(
                imageVector = customIcon,
                contentDescription = app.label,
                modifier = Modifier.fillMaxSize(),
                tint = tint
            )
        } else if (imageBitmap != null) {
            val colorFilter = when {
                tint != null -> ColorFilter.tint(tint)
                grayscale -> ColorFilter.colorMatrix(ColorMatrix().apply { setToSaturation(0f) })
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
