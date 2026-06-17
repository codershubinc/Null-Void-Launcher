package com.codershubinc.nullvoidlauncher.ui.homescreen

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.codershubinc.nullvoidlauncher.data.LauncherTheme
import com.codershubinc.nullvoidlauncher.data.UserManager
import com.codershubinc.nullvoidlauncher.ui.drawer.AppDrawerScreen
import com.codershubinc.nullvoidlauncher.ui.github.GithubProfileScreen
import com.codershubinc.nullvoidlauncher.ui.settings.SettingsScreen
import com.codershubinc.nullvoidlauncher.ui.widgets.WidgetScreen

@Composable
fun HomeScreen() {
    val context = LocalContext.current

    // Initialize our native data manager
    val userManager = remember { UserManager(context) }
    val scope = rememberCoroutineScope()
    var githubUsername by remember { mutableStateOf(userManager.getUsername()) }
    var currentTheme by remember { mutableStateOf(userManager.getLauncherTheme()) }
    var showWallpaper by remember { mutableStateOf(userManager.getShowWallpaper()) }

    var isDrawerOpen by remember { mutableStateOf(false) }
    var isSettingsOpen by remember { mutableStateOf(false) }
    val pagerState = rememberPagerState(initialPage = 1) { 2 }

    BackHandler(enabled = isDrawerOpen || isSettingsOpen || pagerState.currentPage == 0) {
        if (isSettingsOpen) isSettingsOpen = false
        else if (isDrawerOpen) isDrawerOpen = false
    }

    Box(modifier = Modifier
        .fillMaxSize()
        .background(if (showWallpaper) Color.Transparent else Color.Black)
    ) {

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
                onClose = { isSettingsOpen = false }
            )
        }
    }
}
