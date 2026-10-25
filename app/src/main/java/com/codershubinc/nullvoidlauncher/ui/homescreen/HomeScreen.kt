package com.codershubinc.nullvoidlauncher.ui.homescreen

import android.content.Context
import android.content.pm.LauncherApps
import android.os.UserHandle
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.codershubinc.nullvoidlauncher.data.UserManager
import com.codershubinc.nullvoidlauncher.data.model.LauncherTheme
import com.codershubinc.nullvoidlauncher.data.repository.AppInfo
import com.codershubinc.nullvoidlauncher.data.repository.getInstalledApps

import com.codershubinc.nullvoidlauncher.ui.github.GithubProfileScreen
import com.codershubinc.nullvoidlauncher.ui.settings.SettingsScreen
import com.codershubinc.nullvoidlauncher.ui.drawer.AppDrawer
import com.codershubinc.nullvoidlauncher.ui.about.AboutScreen
import com.codershubinc.nullvoidlauncher.ui.network.NetworkUsageScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun HomeScreen() {
    val context = LocalContext.current

    val userManager = remember { UserManager(context) }
    val scope = rememberCoroutineScope()
    var githubUsername by remember { mutableStateOf(userManager.github.username) }
    var currentTheme by remember { mutableStateOf(userManager.theme.launcherTheme) }
    var showWallpaper by remember { mutableStateOf(userManager.wallpaper.showWallpaper) }
    var wallpaperUri by remember { mutableStateOf(userManager.wallpaper.wallpaperUri) }
    var wallpaperBlur by remember { mutableStateOf(userManager.wallpaper.blur) }
    var wallpaperBlurIntensity by remember { mutableFloatStateOf(userManager.wallpaper.blurIntensity) }
    var wallpaperBlurColor by remember { mutableStateOf(Color(userManager.wallpaper.blurColor)) }
    var wallpaperBlurColorAlpha by remember { mutableFloatStateOf(userManager.wallpaper.blurColorAlpha) }

    var allApps by remember { mutableStateOf(emptyList<AppInfo>()) }

    // ── Update Checker & Dialog State ─────────────────────────────────────────
    var updateState by remember { mutableStateOf(UpdateDownloadState()) }

    fun triggerInstall(file: java.io.File) {
        if (com.codershubinc.nullvoidlauncher.utils.AppUpdater.canInstallPackages(context)) {
            val installed = com.codershubinc.nullvoidlauncher.utils.AppUpdater.installApk(context, file)
            if (!installed) {
                updateState = updateState.copy(error = "Failed to launch package installer")
            }
        } else {
            updateState = updateState.copy(
                downloadedApk = file,
                showInstallPermissionDialog = true
            )
        }
    }

    fun startInAppDownload(info: com.codershubinc.nullvoidlauncher.utils.UpdateInfo) {
        val downloadUrl = info.apkDownloadUrl
        if (downloadUrl.isNullOrEmpty()) {
            val uriHandler = android.net.Uri.parse(info.releaseUrl)
            val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, uriHandler)
            try { context.startActivity(intent) } catch (_: Exception) {}
            return
        }

        scope.launch {
            updateState = updateState.copy(isDownloading = true, progress = 0f, error = null)
            val result = com.codershubinc.nullvoidlauncher.utils.AppUpdater.downloadApk(
                context = context,
                downloadUrl = downloadUrl,
                expectedSha256 = info.expectedSha256,
                onProgress = { progress, downloaded, total ->
                    updateState = updateState.copy(
                        progress = progress,
                        downloadedBytes = downloaded,
                        totalBytes = total
                    )
                }
            )
            updateState = updateState.copy(isDownloading = false)
            result.onSuccess { apkFile ->
                triggerInstall(apkFile)
            }.onFailure { error ->
                updateState = updateState.copy(error = error.localizedMessage ?: "Download failed")
            }
        }
    }

    fun refreshApps() {
        scope.launch {
            val apps = withContext(Dispatchers.IO) {
                getInstalledApps(context)
            }
            allApps = apps
        }
    }

    LaunchedEffect(Unit) {
        refreshApps()

        // Periodic Background Check for updates
        if (com.codershubinc.nullvoidlauncher.utils.AppUpdater.shouldCheckForPeriodicUpdate(userManager)) {
            val result = com.codershubinc.nullvoidlauncher.utils.AppUpdater.checkForUpdates(context)
            userManager.updates.lastUpdateCheckTime = System.currentTimeMillis()
            result.onSuccess { info ->
                if (info.isUpdateAvailable && info.latestVersion != userManager.updates.skippedVersion) {
                    updateState = updateState.copy(pendingUpdate = info)
                }
            }
        }
    }

    DisposableEffect(context) {
        val launcherApps = context.getSystemService(Context.LAUNCHER_APPS_SERVICE) as LauncherApps
        val callback = object : LauncherApps.Callback() {
            override fun onPackageRemoved(packageName: String?, user: UserHandle?) = refreshApps()
            override fun onPackageAdded(packageName: String?, user: UserHandle?) = refreshApps()
            override fun onPackageChanged(packageName: String?, user: UserHandle?) = refreshApps()
            override fun onPackagesAvailable(packageNames: Array<out String>?, user: UserHandle?, replacing: Boolean) = refreshApps()
            override fun onPackagesUnavailable(packageNames: Array<out String>?, user: UserHandle?, replacing: Boolean) = refreshApps()
        }
        launcherApps.registerCallback(callback)
        onDispose {
            launcherApps.unregisterCallback(callback)
        }
    }

    var activeOverlay by remember { mutableStateOf<HomeOverlay>(HomeOverlay.None) }
    val pagerState = rememberPagerState(initialPage = 1) { 2 }

    val blurRadius by remember {
        derivedStateOf {
            val pageOffset = pagerState.currentPage + pagerState.currentPageOffsetFraction
            val transitionBlur = (1f - pageOffset.coerceIn(0f, 1f)) * 25f
            val userBlur = if (wallpaperBlur) wallpaperBlurIntensity else 0f
            transitionBlur + userBlur
        }
    }

    BackHandler(enabled = activeOverlay != HomeOverlay.None || pagerState.currentPage == 0) {
        when (val current = activeOverlay) {
            is HomeOverlay.WidgetTweaks -> {
                activeOverlay = if (current.returnToWidgetSettings) {
                    HomeOverlay.WidgetSettings
                } else {
                    HomeOverlay.None
                }
            }
            is HomeOverlay.Drawer,
            is HomeOverlay.Settings,
            is HomeOverlay.FocusMode,
            is HomeOverlay.About,
            is HomeOverlay.NetworkUsage,
            is HomeOverlay.BluetoothSettings,
            is HomeOverlay.WidgetSettings -> {
                activeOverlay = HomeOverlay.None
            }
            HomeOverlay.None -> {
                if (pagerState.currentPage == 0) {
                    scope.launch {
                        pagerState.animateScrollToPage(1)
                    }
                }
            }
        }
    }

    Box(modifier = Modifier
        .fillMaxSize()
        .background(if (showWallpaper && wallpaperUri == null) Color.Transparent else Color.Black)
    ) {
        if (showWallpaper && wallpaperUri != null) {
            Box(modifier = Modifier.fillMaxSize()) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(wallpaperUri)
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .blur(blurRadius.dp),
                    contentScale = ContentScale.Crop
                )
                if (wallpaperBlur && wallpaperBlurColor != Color.Transparent) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(wallpaperBlurColor.copy(alpha = wallpaperBlurColorAlpha))
                    )
                }
            }
        }

        HorizontalPager(
            state = pagerState,
            userScrollEnabled = pagerState.currentPage == 0,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            when (page) {
                0 -> GithubProfileScreen(
                    username = githubUsername,
                    userManager = userManager,
                    onUsernameChanged = { newHandle ->
                        githubUsername = newHandle
                    },
                    onOpenSettings = { activeOverlay = HomeOverlay.Settings },
                    onOpenAbout = { activeOverlay = HomeOverlay.About },
                    onClose = {
                        scope.launch {
                            pagerState.animateScrollToPage(1)
                        }
                    }
                )
                1 -> WidgetScreen(
                    isDrawerOpen = activeOverlay is HomeOverlay.Drawer,
                    theme = currentTheme,
                    onOpenDrawer = { activeOverlay = HomeOverlay.Drawer },
                    onOpenGithubProfile = {
                        scope.launch {
                            pagerState.animateScrollToPage(0)
                        }
                    },
                    onWallpaperChanged = {
                        wallpaperUri = userManager.wallpaper.wallpaperUri
                        showWallpaper = userManager.wallpaper.showWallpaper
                    },
                    onOpenNetworkUsage = { activeOverlay = HomeOverlay.NetworkUsage },
                    onOpenBluetoothSettings = { activeOverlay = HomeOverlay.BluetoothSettings },
                    onOpenWidgetSettings = { activeOverlay = HomeOverlay.WidgetSettings },
                    onOpenWidgetTweaks = { subPage -> activeOverlay = HomeOverlay.WidgetTweaks(subPage) }
                )
            }
        }

        AnimatedVisibility(
            visible = activeOverlay is HomeOverlay.Drawer,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            modifier = Modifier.fillMaxSize()
        ) {
            AppDrawer(
                allApps = allApps,
                userManager = userManager,
                onClose = { activeOverlay = HomeOverlay.None }
            )
        }

        AnimatedVisibility(
            visible = activeOverlay is HomeOverlay.Settings,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.fillMaxSize()
        ) {
            SettingsScreen(
                userManager = userManager,
                allApps = allApps,
                onUsernameUpdated = { newName -> 
                    githubUsername = newName
                    scope.launch { userManager.github.fetchGithubProfile(newName, true) }
                },
                onThemeUpdated = { newTheme ->
                    currentTheme = newTheme
                },
                onWallpaperToggleUpdated = { show ->
                    showWallpaper = show
                },
                onWallpaperUriUpdated = { uri ->
                    wallpaperUri = uri
                },
                onWallpaperBlurUpdated = { blur ->
                    wallpaperBlur = blur
                },
                onWallpaperBlurIntensityUpdated = { intensity ->
                    wallpaperBlurIntensity = intensity
                },
                onWallpaperBlurColorUpdated = { color ->
                    wallpaperBlurColor = Color(color)
                },
                onWallpaperBlurColorAlphaUpdated = { alpha ->
                    wallpaperBlurColorAlpha = alpha
                },
                onOpenWidgetSettings = { activeOverlay = HomeOverlay.WidgetSettings },
                onOpenDrawerTweaks = {
                    activeOverlay = HomeOverlay.WidgetTweaks(
                        com.codershubinc.nullvoidlauncher.ui.settings.WidgetSubPage.DRAWER,
                        returnToWidgetSettings = false
                    )
                },
                onOpenAbout = { activeOverlay = HomeOverlay.About },
                onClose = { activeOverlay = HomeOverlay.None }
            )
        }

        AnimatedVisibility(
            visible = activeOverlay is HomeOverlay.About,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.fillMaxSize()
        ) {
            AboutScreen(
                userManager = userManager,
                onClose = { activeOverlay = HomeOverlay.None }
            )
        }

        val showWidgetSettings = activeOverlay is HomeOverlay.WidgetSettings || activeOverlay is HomeOverlay.WidgetTweaks
        AnimatedVisibility(
            visible = showWidgetSettings,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            modifier = Modifier.fillMaxSize()
        ) {
            val initialPage = (activeOverlay as? HomeOverlay.WidgetTweaks)?.subPage
                ?: com.codershubinc.nullvoidlauncher.ui.settings.WidgetSubPage.NONE
            com.codershubinc.nullvoidlauncher.ui.settings.WidgetSettingsScreen(
                userManager = userManager,
                initialPage = initialPage,
                onClose = {
                    activeOverlay = HomeOverlay.None
                }
            )
        }

        AnimatedVisibility(
            visible = activeOverlay is HomeOverlay.NetworkUsage,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            modifier = Modifier.fillMaxSize()
        ) {
            NetworkUsageScreen(
                onClose = { activeOverlay = HomeOverlay.None }
            )
        }

        AnimatedVisibility(
            visible = activeOverlay is HomeOverlay.BluetoothSettings,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            modifier = Modifier.fillMaxSize()
        ) {
            com.codershubinc.nullvoidlauncher.ui.bluetooth.BluetoothSettingsScreen(
                userManager = userManager,
                onClose = { activeOverlay = HomeOverlay.None }
            )
        }

        // Periodic Update Popup Dialog
        val currentPendingUpdate = updateState.pendingUpdate
        if (currentPendingUpdate != null && currentPendingUpdate.isUpdateAvailable) {
            com.codershubinc.nullvoidlauncher.ui.components.UpdatePromptDialog(
                updateInfo = currentPendingUpdate,
                isDownloading = updateState.isDownloading,
                downloadProgress = updateState.progress,
                downloadedBytes = updateState.downloadedBytes,
                totalBytes = updateState.totalBytes,
                downloadError = updateState.error,
                onUpdateNow = {
                    startInAppDownload(currentPendingUpdate)
                },
                onRemindTomorrow = {
                    com.codershubinc.nullvoidlauncher.utils.AppUpdater.postponeUpdateTomorrow(userManager)
                    updateState = updateState.copy(pendingUpdate = null)
                },
                onDismiss = {
                    updateState = updateState.copy(pendingUpdate = null)
                },
                onSkipVersion = {
                    com.codershubinc.nullvoidlauncher.utils.AppUpdater.skipVersion(userManager, currentPendingUpdate.latestVersion)
                    updateState = updateState.copy(pendingUpdate = null)
                }
            )
        }

        // Install Permission Dialog fallback
        if (updateState.showInstallPermissionDialog) {
            androidx.compose.material3.AlertDialog(
                onDismissRequest = { updateState = updateState.copy(showInstallPermissionDialog = false) },
                title = {
                    androidx.compose.material3.Text(
                        text = "Permission Required",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    androidx.compose.material3.Text(
                        text = "NullVoid Launcher needs permission to install unknown apps to complete this update. Please enable it in system settings.",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 14.sp
                    )
                },
                confirmButton = {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF3D5AFE))
                            .clickable {
                                updateState = updateState.copy(showInstallPermissionDialog = false)
                                com.codershubinc.nullvoidlauncher.utils.AppUpdater.openInstallPermissionSettings(context)
                            }
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        androidx.compose.material3.Text(
                            text = "Settings",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                },
                dismissButton = {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { updateState = updateState.copy(showInstallPermissionDialog = false) }
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        androidx.compose.material3.Text(
                            text = "Cancel",
                            color = Color.White.copy(alpha = 0.6f),
                            fontSize = 14.sp
                        )
                    }
                },
                containerColor = Color(0xFF141416),
                shape = RoundedCornerShape(16.dp)
            )
        }
    }
}
