package com.codershubinc.nullvoidlauncher.ui.home

import android.content.ComponentName
import android.content.Context
import android.content.pm.LauncherApps
import android.graphics.drawable.Drawable
import android.os.UserHandle
import android.os.UserManager

// 1. Data Class with Icon, ComponentName, and UserHandle
data class AppInfo(
    val label: String,
    val packageName: String,
    val componentName: ComponentName,
    val userHandle: UserHandle,
    val icon: Drawable? = null
)

// 2. Function to fetch installed apps
fun getInstalledApps(context: Context): List<AppInfo> {
    val launcherApps = context.getSystemService(Context.LAUNCHER_APPS_SERVICE) as LauncherApps
    val userManager = context.getSystemService(Context.USER_SERVICE) as UserManager

    val allApps = mutableListOf<AppInfo>()

    // Get all user profiles (including work profile)
    val profiles = userManager.userProfiles

    for (profile in profiles) {
        val activities = launcherApps.getActivityList(null, profile)
        for (activity in activities) {
            val pkg = activity.applicationInfo.packageName
            // Exclude the launcher itself
            if (pkg != context.packageName) {
                allApps.add(
                    AppInfo(
                        label = activity.label.toString(),
                        packageName = pkg,
                        componentName = activity.componentName, // Needed to launch
                        userHandle = profile,                   // Needed to launch
                        icon = activity.getBadgedIcon(0)        // Adds the work profile briefcase badge!
                    )
                )
            }
        }
    }

    // Use componentName for distinctBy so Work and Personal apps both show up
    return allApps.distinctBy { it.componentName }.sortedBy { it.label.lowercase() }
}