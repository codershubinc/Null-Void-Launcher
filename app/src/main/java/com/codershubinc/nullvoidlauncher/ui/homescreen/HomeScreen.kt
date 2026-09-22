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
import androidx.compose.ui.unit.dp
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
import com.codershubinc.nullvoidlauncher.ui.widgets.globleSearch.ElegantSearchScreen
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
    var isNetworkUsageOpen by remember { mutableStateOf(false) }
    var isBluetoothSettingsOpen by remember { mutableStateOf(false) }
    var isEditMode by remember { mutableStateOf(false) }
    val pagerState = rememberPagerState(initialPage = 1) { 2 }

    val blurRadius by remember {
        derivedStateOf {
            val pageOffset = pagerState.currentPage + pagerState.currentPageOffsetFraction
            val transitionBlur = (1f - pageOffset.coerceIn(0f, 1f)) * 25f
            val userBlur = if (wallpaperBlur) wallpaperBlurIntensity else 0f
            transitionBlur + userBlur
        }
    }

    BackHandler(enabled = isEditMode || isDrawerOpen || isWidgetSettingsOpen || isSettingsOpen || isFocusModeOpen || isAboutOpen || isNetworkUsageOpen || isBluetoothSettingsOpen || pagerState.currentPage == 0) {
        if (isEditMode) isEditMode = false
        else if (isBluetoothSettingsOpen) isBluetoothSettingsOpen = false
        else if (isNetworkUsageOpen) isNetworkUsageOpen = false
        else if (isWidgetSettingsOpen) isWidgetSettingsOpen = false
        else if (isSettingsOpen) isSettingsOpen = false
        else if (isFocusModeOpen) isFocusModeOpen = false
        else if (isAboutOpen) isAboutOpen = false
        else if (isDrawerOpen) isDrawerOpen = false
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
            userScrollEnabled = !isEditMode,
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
                    onWallpaperChanged = {
                        wallpaperUri = userManager.getWallpaperUri()
                        showWallpaper = userManager.getShowWallpaper()
                    },
                    onOpenNetworkUsage = { isNetworkUsageOpen = true },
                    onOpenBluetoothSettings = { isBluetoothSettingsOpen = true },
                    onOpenWidgetSettings = { isWidgetSettingsOpen = true },
                    isEditMode = isEditMode,
                    onEnterEditMode = { isEditMode = true },
                    onExitEditMode = { isEditMode = false }
                )
            }
        }

        AnimatedVisibility(
            visible = isDrawerOpen,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            modifier = Modifier.fillMaxSize()
        ) {
            ElegantSearchScreen(
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
            visible = isWidgetSettingsOpen,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            modifier = Modifier.fillMaxSize()
        ) {
            com.codershubinc.nullvoidlauncher.ui.settings.WidgetSettingsScreen(
                userManager = userManager,
                onClose = { isWidgetSettingsOpen = false }
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
    }
}
