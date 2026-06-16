package com.codershubinc.nullvoidlauncher.ui.home

import android.content.ComponentName
import android.content.Context
import android.content.pm.LauncherApps
import android.os.UserHandle
import android.os.UserManager

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
