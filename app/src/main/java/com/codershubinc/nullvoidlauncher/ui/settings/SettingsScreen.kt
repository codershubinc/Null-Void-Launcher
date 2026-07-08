package com.codershubinc.nullvoidlauncher.ui.settings

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codershubinc.nullvoidlauncher.R
import com.codershubinc.nullvoidlauncher.data.*
import com.codershubinc.nullvoidlauncher.data.repository.AppInfo

@Composable
fun SettingsScreen(
    userManager: UserManager,
    allApps: List<AppInfo>,
    onUsernameUpdated: (String) -> Unit,
    onThemeUpdated: (LauncherTheme) -> Unit,
    onWallpaperToggleUpdated: (Boolean) -> Unit,
    onWallpaperSelected: (Int) -> Unit,
    onClose: () -> Unit
) {
    val context = LocalContext.current
    var inputUsername by remember { mutableStateOf(userManager.getUsername()) }
    var selectedTheme by remember { mutableStateOf(userManager.getLauncherTheme()) }
    var showWallpaper by remember { mutableStateOf(userManager.getShowWallpaper()) }
    var wallpaperResId by remember { mutableIntStateOf(userManager.getWallpaperRes()) }
    var selectedFavorites by remember { mutableStateOf(userManager.getFavorites().toSet()) }
    
    var isSelectingApps by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

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
                .padding(horizontal = 24.dp)
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
                        Text(
                            text = "GitHub Username",
                            color = Color.White.copy(alpha = 0.4f),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        BasicTextField(
                            value = inputUsername,
                            onValueChange = { inputUsername = it },
                            textStyle = TextStyle(
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold
                            ),
                            cursorBrush = SolidColor(Color(0xFF3D5AFE)),
                            modifier = Modifier.fillMaxWidth(),
                            decorationBox = { innerTextField ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(12.dp))
                                        .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                                        .padding(16.dp)
                                ) {
                                    if (inputUsername.isEmpty()) {
                                        Text("Enter username", color = Color.White.copy(alpha = 0.2f))
                                    }
                                    innerTextField()
                                }
                            }
                        )
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
                        SettingsRow(
                            icon = Icons.Rounded.Palette,
                            label = "Theme",
                            value = selectedTheme.name,
                            onClick = {
                                val themes = LauncherTheme.entries
                                val currentIndex = themes.indexOf(selectedTheme)
                                selectedTheme = themes[(currentIndex + 1) % themes.size]
                            }
                        )
                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color.White.copy(alpha = 0.05f))
                        SettingsRow(
                            icon = Icons.Rounded.Wallpaper,
                            label = "Wallpaper",
                            value = if (showWallpaper) "Visible" else "Hidden",
                            onClick = { showWallpaper = !showWallpaper }
                        )
                        
                        if (showWallpaper) {
                            Spacer(modifier = Modifier.height(16.dp))
                            LazyRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(wallpapers) { resId ->
                                    Box(
                                        modifier = Modifier
                                            .size(60.dp, 100.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .border(
                                                width = if (wallpaperResId == resId) 2.dp else 1.dp,
                                                color = if (wallpaperResId == resId) Color(0xFF3D5AFE) else Color.White.copy(alpha = 0.1f),
                                                shape = RoundedCornerShape(12.dp)
                                            )
                                            .clickable { wallpaperResId = resId }
                                    ) {
                                        androidx.compose.foundation.Image(
                                            painter = painterResource(id = resId),
                                            contentDescription = null,
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Crop
                                        )
                                    }
                                }
                            }
                        }
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
                        SettingsRow(
                            icon = Icons.Rounded.Star,
                            label = "Favorites",
                            value = "${selectedFavorites.size} Apps selected",
                            onClick = { isSelectingApps = true }
                        )
                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color.White.copy(alpha = 0.05f))
                        SettingsRow(
                            icon = Icons.Rounded.Sync,
                            label = "Music Sync",
                            value = "Permissions",
                            onClick = {
                                context.startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(40.dp))
                    
                    // Save Button
                    Box(
                        modifier = Modifier
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
                    
                    Spacer(modifier = Modifier.height(40.dp))
                }
            } else {
                // App Selection View
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
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

@Composable
fun ModernCard(content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(Color.White.copy(alpha = 0.03f))
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    colors = listOf(Color.White.copy(alpha = 0.08f), Color.White.copy(alpha = 0.02f))
                ),
                shape = RoundedCornerShape(28.dp)
            )
            .padding(24.dp)
    ) {
        content()
    }
}

@Composable
fun SettingsRow(icon: ImageVector, label: String, value: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.05f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.7f),
                modifier = Modifier.size(18.dp)
            )
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                color = Color.White.copy(alpha = 0.4f),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
            Text(
                text = value,
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
        
        Icon(
            imageVector = Icons.Rounded.ChevronRight,
            contentDescription = null,
            tint = Color.White.copy(alpha = 0.2f)
        )
    }
}
