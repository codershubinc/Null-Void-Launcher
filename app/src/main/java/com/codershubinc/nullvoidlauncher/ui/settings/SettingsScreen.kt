package com.codershubinc.nullvoidlauncher.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.content.Intent
import android.provider.Settings
import androidx.compose.ui.platform.LocalContext
import com.codershubinc.nullvoidlauncher.R
import com.codershubinc.nullvoidlauncher.data.*
import com.codershubinc.nullvoidlauncher.data.repository.AppInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

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
    
    var isSelectingApps by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().background(Color.Black).statusBarsPadding().padding(24.dp)) {
        if (!isSelectingApps) {
            LazyColumn(modifier = Modifier.weight(1f)) {
                item {
                    Text(text = "SETTINGS", color = Color.White, fontSize = 24.sp, fontFamily = FontFamily.Monospace, letterSpacing = 4.sp, modifier = Modifier.padding(bottom = 32.dp))

                    Text(text = "GITHUB USERNAME", color = Color.Gray, fontSize = 14.sp, fontFamily = FontFamily.Monospace, modifier = Modifier.padding(bottom = 8.dp))

                    BasicTextField(
                        value = inputUsername,
                        onValueChange = { inputUsername = it },
                        textStyle = TextStyle(color = Color.White, fontSize = 18.sp, fontFamily = FontFamily.Monospace),
                        cursorBrush = SolidColor(Color.White),
                        modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                        decorationBox = { innerTextField ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "> ", color = Color.DarkGray, fontFamily = FontFamily.Monospace, fontSize = 18.sp)
                                innerTextField()
                            }
                        }
                    )

                    Text(text = "LAUNCHER THEME", color = Color.Gray, fontSize = 14.sp, fontFamily = FontFamily.Monospace, modifier = Modifier.padding(bottom = 12.dp))

                    LauncherTheme.entries.forEach { theme ->
                        val isSelected = selectedTheme == theme
                        Text(
                            text = if (isSelected) "[ ${theme.name} ]" else "  ${theme.name}",
                            color = if (isSelected) Color.White else Color.DarkGray,
                            fontSize = 18.sp,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedTheme = theme }
                                .padding(vertical = 8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(text = "WALLPAPER", color = Color.Gray, fontSize = 14.sp, fontFamily = FontFamily.Monospace, modifier = Modifier.padding(bottom = 12.dp))
                    Text(
                        text = if (showWallpaper) "[ SHOW WALLPAPER: ON ]" else "  SHOW WALLPAPER: OFF",
                        color = if (showWallpaper) Color.White else Color.DarkGray,
                        fontSize = 18.sp,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showWallpaper = !showWallpaper }
                            .padding(vertical = 8.dp)
                    )

                    if (showWallpaper) {
                        Spacer(modifier = Modifier.height(12.dp))
                        LazyRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(wallpapers) { resId ->
                                Box(
                                    modifier = Modifier
                                        .size(70.dp, 120.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (wallpaperResId == resId) Color.White else Color.Transparent)
                                        .padding(if (wallpaperResId == resId) 2.dp else 0.dp)
                                        .clickable { wallpaperResId = resId }
                                ) {
                                    androidx.compose.foundation.Image(
                                        painter = painterResource(id = resId),
                                        contentDescription = null,
                                        modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(6.dp)),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(text = "FAVORITE APPS", color = Color.Gray, fontSize = 14.sp, fontFamily = FontFamily.Monospace, modifier = Modifier.padding(bottom = 12.dp))
                    Text(
                        text = "  SELECT FAVORITES (${selectedFavorites.size})",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isSelectingApps = true }
                            .padding(vertical = 8.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "SYSTEM PERMISSIONS",
                        color = Color.Gray,
                        fontSize = 14.sp,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    Text(
                        text = "  MUSIC SYNC ACCESS",
                        color = Color.DarkGray,
                        fontSize = 18.sp,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                context.startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
                            }
                            .padding(vertical = 8.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(40.dp))
                }
            }

            Text(
                text = "[ SAVE & CLOSE ]",
                color = Color.Gray,
                fontSize = 18.sp,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
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
                    .padding(16.dp)
            )
        } else {
            // App Selection View
            Text(text = "SELECT FAVORITES", color = Color.White, fontSize = 24.sp, fontFamily = FontFamily.Monospace, letterSpacing = 4.sp, modifier = Modifier.padding(bottom = 32.dp))
            
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(allApps) { app ->
                    val pkg = app.componentName.flattenToString()
                    val isSelected = selectedFavorites.contains(pkg)
                    Text(
                        text = if (isSelected) "[ ${app.label.uppercase()} ]" else "  ${app.label.uppercase()}",
                        color = if (isSelected) Color.White else Color.DarkGray,
                        fontSize = 18.sp,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                if (isSelected) {
                                    selectedFavorites = selectedFavorites - pkg
                                } else {
                                    if (selectedFavorites.size < 5) {
                                        selectedFavorites = selectedFavorites + pkg
                                    }
                                }
                            }
                            .padding(vertical = 8.dp)
                    )
                }
            }

            Text(
                text = "[DONE]",
                color = Color.Gray,
                fontSize = 18.sp,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .clickable { isSelectingApps = false }
                    .padding(16.dp)
            )
        }
    }
}
