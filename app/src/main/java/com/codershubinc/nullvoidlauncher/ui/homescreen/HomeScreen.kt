package com.codershubinc.nullvoidlauncher.ui.homescreen

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
import com.codershubinc.nullvoidlauncher.R
import androidx.compose.ui.platform.LocalContext
import com.codershubinc.nullvoidlauncher.data.UserManager
import com.codershubinc.nullvoidlauncher.ui.drawer.AppDrawerScreen
import com.codershubinc.nullvoidlauncher.ui.github.GithubProfileScreen
import com.codershubinc.nullvoidlauncher.ui.settings.SettingsScreen

@Composable
fun HomeScreen() {
    val context = LocalContext.current

    val userManager = remember { UserManager(context) }
    val scope = rememberCoroutineScope()
    var githubUsername by remember { mutableStateOf(userManager.getUsername()) }
    var currentTheme by remember { mutableStateOf(userManager.getLauncherTheme()) }
    var showWallpaper by remember { mutableStateOf(userManager.getShowWallpaper()) }
    var wallpaperResId by remember { mutableIntStateOf(userManager.getWallpaperRes()) }

    var isDrawerOpen by remember { mutableStateOf(false) }
    var isSettingsOpen by remember { mutableStateOf(false) }
    val pagerState = rememberPagerState(initialPage = 1) { 2 }

    val blurRadius by remember {
        derivedStateOf {
            val pageOffset = pagerState.currentPage + pagerState.currentPageOffsetFraction
            (1f - pageOffset.coerceIn(0f, 1f)) * 25f
        }
    }

    BackHandler(enabled = isDrawerOpen || isSettingsOpen || pagerState.currentPage == 0) {
        if (isSettingsOpen) isSettingsOpen = false
        else if (isDrawerOpen) isDrawerOpen = false
    }

    Box(modifier = Modifier
        .fillMaxSize()
        .background(if (showWallpaper && wallpaperResId == -1) Color.Transparent else Color.Black)
    ) {
        if (showWallpaper && wallpaperResId != -1) {
            Image(
                painter = painterResource(id = wallpaperResId),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .blur(blurRadius.dp),
                contentScale = ContentScale.Crop
            )
        }

        HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { page ->
            when (page) {
                0 -> GithubProfileScreen(
                    username = githubUsername,
                    userManager = userManager,
                    onOpenSettings = { isSettingsOpen = true },
                    onClose = {
                        scope.launch {
                            pagerState.animateScrollToPage(1)
                        }
                    }
                )
                1 -> WidgetScreen(
                    isDrawerOpen = isDrawerOpen,
                    theme = currentTheme,
                    onOpenDrawer = { isDrawerOpen = true }
                )
            }
        }

        AnimatedVisibility(
            visible = isDrawerOpen,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            modifier = Modifier.fillMaxSize()
        ) {
            AppDrawerScreen(onClose = { isDrawerOpen = false })
        }

        AnimatedVisibility(
            visible = isSettingsOpen,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.fillMaxSize()
        ) {
            SettingsScreen(
                userManager = userManager,
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
                onWallpaperSelected = { resId ->
                    wallpaperResId = resId
                },
                onClose = { isSettingsOpen = false }
            )
        }
    }
}
