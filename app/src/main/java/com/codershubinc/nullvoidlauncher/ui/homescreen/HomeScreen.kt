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
import com.codershubinc.nullvoidlauncher.data.repository.AppInfo
import com.codershubinc.nullvoidlauncher.data.repository.getInstalledApps

import com.codershubinc.nullvoidlauncher.ui.focus.FocusModeScreen
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
    var githubUsername by remember { mutableStateOf(userManager.getUsername()) }
    var currentTheme by remember { mutableStateOf(userManager.getLauncherTheme()) }
    var showWallpaper by remember { mutableStateOf(userManager.getShowWallpaper()) }
    var wallpaperUri by remember { mutableStateOf(userManager.getWallpaperUri()) }
    var wallpaperBlur by remember { mutableStateOf(userManager.getWallpaperBlur()) }
    var wallpaperBlurIntensity by remember { mutableFloatStateOf(userManager.getWallpaperBlurIntensity()) }
    var wallpaperBlurColor by remember { mutableStateOf(Color(userManager.getWallpaperBlurColor())) }
    var wallpaperBlurColorAlpha by remember { mutableFloatStateOf(userManager.getWallpaperBlurColorAlpha()) }

    var allApps by remember { mutableStateOf(emptyList<AppInfo>()) }

    // ── Update Checker & Dialog State ─────────────────────────────────────────
    var pendingUpdateInfo by remember { mutableStateOf<com.codershubinc.nullvoidlauncher.utils.UpdateInfo?>(null) }
    var isUpdateDownloading by remember { mutableStateOf(false) }
    var updateDownloadProgress by remember { mutableFloatStateOf(0f) }
    var updateDownloadedBytes by remember { mutableLongStateOf(0L) }
    var updateDownloadTotalBytes by remember { mutableLongStateOf(0L) }
    var updateDownloadError by remember { mutableStateOf<String?>(null) }
    var showInstallPermissionDialog by remember { mutableStateOf(false) }
    var downloadedApkToInstall by remember { mutableStateOf<java.io.File?>(null) }

    fun triggerInstall(file: java.io.File) {
        if (com.codershubinc.nullvoidlauncher.utils.AppUpdater.canInstallPackages(context)) {
            val installed = com.codershubinc.nullvoidlauncher.utils.AppUpdater.installApk(context, file)
            if (!installed) {
                updateDownloadError = "Failed to launch package installer"
            }
        } else {
            downloadedApkToInstall = file
            showInstallPermissionDialog = true
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
            isUpdateDownloading = true
            updateDownloadProgress = 0f
            updateDownloadError = null
            val result = com.codershubinc.nullvoidlauncher.utils.AppUpdater.downloadApk(
                context = context,
                downloadUrl = downloadUrl,
                onProgress = { progress, downloaded, total ->
                    updateDownloadProgress = progress
                    updateDownloadedBytes = downloaded
                    updateDownloadTotalBytes = total
                }
            )
            isUpdateDownloading = false
            result.onSuccess { apkFile ->
                triggerInstall(apkFile)
            }.onFailure { error ->
                updateDownloadError = error.localizedMessage ?: "Download failed"
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
            userManager.saveLastUpdateCheckTime(System.currentTimeMillis())
            result.onSuccess { info ->
                if (info.isUpdateAvailable && info.latestVersion != userManager.getSkippedVersion()) {
                    pendingUpdateInfo = info
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

    var isDrawerOpen by remember { mutableStateOf(false) }
    var isSettingsOpen by remember { mutableStateOf(false) }
    var isFocusModeOpen by remember { mutableStateOf(false) }
    var isAboutOpen by remember { mutableStateOf(false) }
    var isWidgetSettingsOpen by remember { mutableStateOf(false) }
    var targetWidgetSubPage by remember { mutableStateOf<com.codershubinc.nullvoidlauncher.ui.settings.WidgetSubPage?>(null) }
    var isNetworkUsageOpen by remember { mutableStateOf(false) }
    var isBluetoothSettingsOpen by remember { mutableStateOf(false) }
    val pagerState = rememberPagerState(initialPage = 1) { 2 }

    val blurRadius by remember {
        derivedStateOf {
            val pageOffset = pagerState.currentPage + pagerState.currentPageOffsetFraction
            val transitionBlur = (1f - pageOffset.coerceIn(0f, 1f)) * 25f
            val userBlur = if (wallpaperBlur) wallpaperBlurIntensity else 0f
            transitionBlur + userBlur
        }
    }

    BackHandler(enabled = isDrawerOpen || isWidgetSettingsOpen || targetWidgetSubPage != null || isSettingsOpen || isFocusModeOpen || isAboutOpen || isNetworkUsageOpen || isBluetoothSettingsOpen || pagerState.currentPage == 0) {
        if (targetWidgetSubPage != null) targetWidgetSubPage = null
        else if (isBluetoothSettingsOpen) isBluetoothSettingsOpen = false
        else if (isNetworkUsageOpen) isNetworkUsageOpen = false
        else if (isWidgetSettingsOpen) isWidgetSettingsOpen = false
        else if (isSettingsOpen) isSettingsOpen = false
        else if (isFocusModeOpen) isFocusModeOpen = false
        else if (isAboutOpen) isAboutOpen = false
        else if (isDrawerOpen) isDrawerOpen = false
        else if (pagerState.currentPage == 0) {
            scope.launch {
                pagerState.animateScrollToPage(1)
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
                    onOpenSettings = { isSettingsOpen = true },
                    onOpenFocusMode = { isFocusModeOpen = true },
                    onOpenAbout = { isAboutOpen = true },
                    onClose = {
                        scope.launch {
                            pagerState.animateScrollToPage(1)
                        }
                    }
                )
                1 -> WidgetScreen(
                    isDrawerOpen = isDrawerOpen,
                    theme = currentTheme,
                    onOpenDrawer = { isDrawerOpen = true },
                    onOpenGithubProfile = {
                        scope.launch {
                            pagerState.animateScrollToPage(0)
                        }
                    },
                    onWallpaperChanged = {
                        wallpaperUri = userManager.getWallpaperUri()
                        showWallpaper = userManager.getShowWallpaper()
                    },
                    onOpenNetworkUsage = { isNetworkUsageOpen = true },
                    onOpenBluetoothSettings = { isBluetoothSettingsOpen = true },
                    onOpenWidgetSettings = { isWidgetSettingsOpen = true },
                    onOpenWidgetTweaks = { subPage -> targetWidgetSubPage = subPage }
                )
            }
        }

        AnimatedVisibility(
            visible = isDrawerOpen,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            modifier = Modifier.fillMaxSize()
        ) {
            AppDrawer(
                allApps = allApps,
                userManager = userManager,
                onClose = { isDrawerOpen = false }
            )
        }

        AnimatedVisibility(
            visible = isSettingsOpen,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.fillMaxSize()
        ) {
            SettingsScreen(
                userManager = userManager,
                allApps = allApps,
                onUsernameUpdated = { newName -> 
                    githubUsername = newName
                    scope.launch { userManager.fetchGithubProfile(newName, true) }
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
                onOpenWidgetSettings = { isWidgetSettingsOpen = true },
                onOpenDrawerTweaks = {
                    targetWidgetSubPage = com.codershubinc.nullvoidlauncher.ui.settings.WidgetSubPage.DRAWER
                },
                onOpenAbout = { isAboutOpen = true },
                onClose = { isSettingsOpen = false }
            )
        }

        AnimatedVisibility(
            visible = isFocusModeOpen,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.fillMaxSize()
        ) {
            FocusModeScreen(onClose = { isFocusModeOpen = false })
        }

        AnimatedVisibility(
            visible = isAboutOpen,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.fillMaxSize()
        ) {
            AboutScreen(
                userManager = userManager,
                onClose = { isAboutOpen = false }
            )
        }

        AnimatedVisibility(
            visible = isWidgetSettingsOpen || targetWidgetSubPage != null,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            modifier = Modifier.fillMaxSize()
        ) {
            com.codershubinc.nullvoidlauncher.ui.settings.WidgetSettingsScreen(
                userManager = userManager,
                initialPage = targetWidgetSubPage ?: com.codershubinc.nullvoidlauncher.ui.settings.WidgetSubPage.NONE,
                onClose = {
                    isWidgetSettingsOpen = false
                    targetWidgetSubPage = null
                }
            )
        }

        AnimatedVisibility(
            visible = isNetworkUsageOpen,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            modifier = Modifier.fillMaxSize()
        ) {
            NetworkUsageScreen(
                onClose = { isNetworkUsageOpen = false }
            )
        }

        AnimatedVisibility(
            visible = isBluetoothSettingsOpen,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            modifier = Modifier.fillMaxSize()
        ) {
            com.codershubinc.nullvoidlauncher.ui.bluetooth.BluetoothSettingsScreen(
                userManager = userManager,
                onClose = { isBluetoothSettingsOpen = false }
            )
        }

        // Periodic Update Popup Dialog
        val currentPendingUpdate = pendingUpdateInfo
        if (currentPendingUpdate != null && currentPendingUpdate.isUpdateAvailable) {
            com.codershubinc.nullvoidlauncher.ui.components.UpdatePromptDialog(
                updateInfo = currentPendingUpdate,
                isDownloading = isUpdateDownloading,
                downloadProgress = updateDownloadProgress,
                downloadedBytes = updateDownloadedBytes,
                totalBytes = updateDownloadTotalBytes,
                downloadError = updateDownloadError,
                onUpdateNow = {
                    startInAppDownload(currentPendingUpdate)
                },
                onRemindTomorrow = {
                    com.codershubinc.nullvoidlauncher.utils.AppUpdater.postponeUpdateTomorrow(userManager)
                    pendingUpdateInfo = null
                },
                onDismiss = {
                    pendingUpdateInfo = null
                },
                onSkipVersion = {
                    com.codershubinc.nullvoidlauncher.utils.AppUpdater.skipVersion(userManager, currentPendingUpdate.latestVersion)
                    pendingUpdateInfo = null
                }
            )
        }

        // Install Permission Dialog fallback
        if (showInstallPermissionDialog) {
            androidx.compose.material3.AlertDialog(
                onDismissRequest = { showInstallPermissionDialog = false },
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
                                showInstallPermissionDialog = false
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
                            .clickable { showInstallPermissionDialog = false }
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
