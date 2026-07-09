package com.codershubinc.nullvoidlauncher.ui.settings

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codershubinc.nullvoidlauncher.R
import com.codershubinc.nullvoidlauncher.data.*
import com.codershubinc.nullvoidlauncher.data.repository.AppInfo
import com.codershubinc.nullvoidlauncher.ui.components.*
import com.codershubinc.nullvoidlauncher.ui.settings.components.*

@Composable
fun SettingsScreen(
    userManager: UserManager,
    allApps: List<AppInfo>,
    onUsernameUpdated: (String) -> Unit,
    onThemeUpdated: (LauncherTheme) -> Unit,
    onWallpaperToggleUpdated: (Boolean) -> Unit,
    onWallpaperSelected: (Int) -> Unit,
    onOpenAbout: () -> Unit,
    onClose: () -> Unit
) {
    val context = LocalContext.current
    var inputUsername by remember { mutableStateOf(userManager.getUsername()) }
    var selectedTheme by remember { mutableStateOf(userManager.getLauncherTheme()) }
    var showWallpaper by remember { mutableStateOf(userManager.getShowWallpaper()) }
    var wallpaperResId by remember { mutableIntStateOf(userManager.getWallpaperRes()) }
    var selectedFavorites by remember { mutableStateOf(userManager.getFavorites().toSet()) }
    
    var isSelectingApps by remember { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    val configuration = LocalConfiguration.current
    val isTablet = configuration.screenWidthDp >= 600

    if (showThemeDialog) {
        ThemeSelectionDialog(
            currentTheme = selectedTheme,
            onThemeSelected = {
                selectedTheme = it
                showThemeDialog = false
            },
            onDismiss = { showThemeDialog = false }
        )
    }

    val wallpapers = listOf(
        R.drawable.wallpaper_elegant,
        R.drawable.wallpaper_black_bunny,
        R.drawable.wallpaper_event_horizon,
        R.drawable.wallpaper_abstract_1,
        R.drawable.wallpaper_abstract_2,
        R.drawable.wallpaper_abstract_3,
        R.drawable.wallpaper_abstract_4,
        R.drawable.wallpaper_train_your_dragon
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF080808))
    ) {
        // Decorative background glow
        Box(
            modifier = Modifier
                .size(300.dp)
                .align(Alignment.TopStart)
                .offset(x = (-100).dp, y = (-50).dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFF3D5AFE).copy(alpha = 0.12f), Color.Transparent)
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = if (isTablet) 64.dp else 24.dp)
                .then(
                    if (isTablet) Modifier.widthIn(max = 800.dp).align(Alignment.TopCenter)
                    else Modifier
                )
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            
            // Back Button
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.05f))
                    .clickable { 
                        if (isSelectingApps) isSelectingApps = false else onClose()
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Header
            Text(
                text = if (isSelectingApps) "Favorites" else "Settings",
                color = Color.White,
                fontSize = 40.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = FontFamily.SansSerif
            )
            Text(
                text = if (isSelectingApps) "Select your top 5 apps" else "Configure your NullVoid experience",
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 16.sp,
                fontFamily = FontFamily.SansSerif
            )

            Spacer(modifier = Modifier.height(32.dp))

            if (!isSelectingApps) {
                if (isTablet) {
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(scrollState),
                        horizontalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            // Profile Section
                            Text(
                                text = "Profile",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(start = 4.dp, bottom = 12.dp)
                            )
                            ModernCard {
                                ProfileSection(inputUsername) { inputUsername = it }
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            // Navigation Section
                            Text(
                                text = "Navigation",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(start = 4.dp, bottom = 12.dp)
                            )
                            ModernCard {
                                NavigationSection(
                                    selectedFavoritesSize = selectedFavorites.size,
                                    onFavoritesClick = { isSelectingApps = true },
                                    onMusicSyncClick = {
                                        context.startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
                                    }
                                )
                            }
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            // Appearance Section
                            Text(
                                text = "Appearance",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(start = 4.dp, bottom = 12.dp)
                            )
                            ModernCard {
                                AppearanceSection(
                                    selectedTheme = selectedTheme,
                                    showWallpaper = showWallpaper,
                                    wallpaperResId = wallpaperResId,
                                    wallpapers = wallpapers,
                                    onThemeClick = { showThemeDialog = true },
                                    onWallpaperToggle = { showWallpaper = !showWallpaper },
                                    onWallpaperSelected = { wallpaperResId = it }
                                )
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            // About Section
                            Text(
                                text = "System",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(start = 4.dp, bottom = 12.dp)
                            )
                            ModernCard {
                                InfoRow(
                                    icon = Icons.Rounded.Info,
                                    label = "About",
                                    value = "NullVoid Protocol",
                                    onClick = onOpenAbout,
                                    showChevron = true
                                )
                            }
                        }
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(scrollState)
                    ) {
                        // Profile Section
                        Text(
                            text = "Profile",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(start = 4.dp, bottom = 12.dp)
                        )
                        ModernCard {
                            ProfileSection(inputUsername) { inputUsername = it }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Appearance Section
                        Text(
                            text = "Appearance",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(start = 4.dp, bottom = 12.dp)
                        )
                        ModernCard {
                            AppearanceSection(
                                selectedTheme = selectedTheme,
                                showWallpaper = showWallpaper,
                                wallpaperResId = wallpaperResId,
                                wallpapers = wallpapers,
                                onThemeClick = { showThemeDialog = true },
                                onWallpaperToggle = { showWallpaper = !showWallpaper },
                                onWallpaperSelected = { wallpaperResId = it }
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Navigation Section
                        Text(
                            text = "Navigation",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(start = 4.dp, bottom = 12.dp)
                        )
                        ModernCard {
                            NavigationSection(
                                selectedFavoritesSize = selectedFavorites.size,
                                onFavoritesClick = { isSelectingApps = true },
                                onMusicSyncClick = {
                                    context.startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
                                }
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // About Section
                        Text(
                            text = "System",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(start = 4.dp, bottom = 12.dp)
                        )
                        ModernCard {
                            InfoRow(
                                icon = Icons.Rounded.Info,
                                label = "About",
                                value = "NullVoid Protocol",
                                onClick = onOpenAbout,
                                showChevron = true
                            )
                        }

                        Spacer(modifier = Modifier.height(40.dp))
                    }
                }

                // Save Button
                Box(
                    modifier = Modifier
                        .padding(bottom = 24.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFF3D5AFE))
                        .clickable {
                            userManager.saveUsername(inputUsername)
                            userManager.saveLauncherTheme(selectedTheme)
                            userManager.saveShowWallpaper(showWallpaper)
                            userManager.saveWallpaperRes(wallpaperResId)
                            userManager.saveFavorites(selectedFavorites.toList())

                            onUsernameUpdated(inputUsername)
                            onThemeUpdated(selectedTheme)
                            onWallpaperToggleUpdated(showWallpaper)
                            onWallpaperSelected(wallpaperResId)
                            onClose()
                        }
                        .padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Save Configuration",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                // App Selection View
                LazyVerticalGrid(
                    columns = if (isTablet) GridCells.Fixed(2) else GridCells.Fixed(1),
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(allApps) { app ->
                        val pkg = app.componentName.flattenToString()
                        val isSelected = selectedFavorites.contains(pkg)
                        
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (isSelected) Color(0xFF3D5AFE).copy(alpha = 0.1f) else Color.White.copy(alpha = 0.03f))
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) Color(0xFF3D5AFE) else Color.White.copy(alpha = 0.05f),
                                    shape = RoundedCornerShape(20.dp)
                                )
                                .clickable {
                                    if (isSelected) {
                                        selectedFavorites = selectedFavorites - pkg
                                    } else {
                                        if (selectedFavorites.size < 5) {
                                            selectedFavorites = selectedFavorites + pkg
                                        }
                                    }
                                }
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) Color(0xFF3D5AFE) else Color.White.copy(alpha = 0.1f)),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSelected) {
                                    Icon(Icons.Rounded.Check, null, tint = Color.White, modifier = Modifier.size(16.dp))
                                }
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = app.label,
                                color = if (isSelected) Color.White else Color.White.copy(alpha = 0.7f),
                                fontSize = 16.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }
                
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFF3D5AFE))
                        .clickable { isSelectingApps = false }
                        .padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Confirm Selection", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
