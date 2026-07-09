package com.codershubinc.nullvoidlauncher.ui.settings.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.Sync
import androidx.compose.material.icons.rounded.Wallpaper
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codershubinc.nullvoidlauncher.data.LauncherTheme
import com.codershubinc.nullvoidlauncher.ui.components.InfoRow

@Composable
fun ProfileSection(inputUsername: String, onUsernameChange: (String) -> Unit) {
    Column {
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
            onValueChange = onUsernameChange,
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
}

@Composable
fun AppearanceSection(
    selectedTheme: LauncherTheme,
    showWallpaper: Boolean,
    wallpaperResId: Int,
    wallpapers: List<Int>,
    onThemeClick: () -> Unit,
    onWallpaperToggle: () -> Unit,
    onWallpaperSelected: (Int) -> Unit
) {
    Column {
        InfoRow(
            icon = Icons.Rounded.Palette,
            label = "Theme",
            value = selectedTheme.name,
            onClick = onThemeClick,
            showChevron = true
        )
        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color.White.copy(alpha = 0.05f))
        InfoRow(
            icon = Icons.Rounded.Wallpaper,
            label = "Wallpaper",
            value = if (showWallpaper) "Visible" else "Hidden",
            onClick = onWallpaperToggle,
            showChevron = true
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
                            .clickable { onWallpaperSelected(resId) }
                    ) {
                        Image(
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
}

@Composable
fun NavigationSection(
    selectedFavoritesSize: Int,
    onFavoritesClick: () -> Unit,
    onMusicSyncClick: () -> Unit
) {
    Column {
        InfoRow(
            icon = Icons.Rounded.Star,
            label = "Favorites",
            value = "$selectedFavoritesSize Apps selected",
            onClick = onFavoritesClick,
            showChevron = true
        )
        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color.White.copy(alpha = 0.05f))
        InfoRow(
            icon = Icons.Rounded.Sync,
            label = "Music Sync",
            value = "Permissions",
            onClick = onMusicSyncClick,
            showChevron = true
        )
    }
}
