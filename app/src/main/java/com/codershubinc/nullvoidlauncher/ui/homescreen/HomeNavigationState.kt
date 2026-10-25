package com.codershubinc.nullvoidlauncher.ui.homescreen

import com.codershubinc.nullvoidlauncher.ui.settings.WidgetSubPage
import com.codershubinc.nullvoidlauncher.utils.UpdateInfo
import java.io.File

/**
 * Unified sealed hierarchy representing all modal and full-screen overlays on HomeScreen.
 * Eliminates multi-boolean flag combinations and invalid overlapping states.
 */
sealed interface HomeOverlay {
    data object None : HomeOverlay
    data object Drawer : HomeOverlay
    data object Settings : HomeOverlay
    data object FocusMode : HomeOverlay
    data object About : HomeOverlay
    data object NetworkUsage : HomeOverlay
    data object BluetoothSettings : HomeOverlay
    data object WidgetSettings : HomeOverlay
    data class WidgetTweaks(
        val subPage: WidgetSubPage,
        val returnToWidgetSettings: Boolean = false
    ) : HomeOverlay
}

/**
 * Holds in-app release download, verification, and installation state.
 */
data class UpdateDownloadState(
    val isDownloading: Boolean = false,
    val progress: Float = 0f,
    val downloadedBytes: Long = 0L,
    val totalBytes: Long = 0L,
    val error: String? = null,
    val pendingUpdate: UpdateInfo? = null,
    val downloadedApk: File? = null,
    val showInstallPermissionDialog: Boolean = false
)
