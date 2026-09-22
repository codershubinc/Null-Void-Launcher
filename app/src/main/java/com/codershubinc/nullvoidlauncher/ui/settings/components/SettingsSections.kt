package com.codershubinc.nullvoidlauncher.ui.settings.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
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
    showWallpaper: Boolean,
    wallpaperUri: String?,
    wallpaperBlur: Boolean,
    wallpaperBlurIntensity: Float,
    onWallpaperToggle: () -> Unit,
    onWallpaperUriSelected: (String?) -> Unit,
    onWallpaperBlurToggle: () -> Unit,
    onWallpaperBlurIntensityChange: (Float) -> Unit,
    wallpaperBlurColor: Color,
    wallpaperBlurColorAlpha: Float,
    onWallpaperBlurColorChange: (Color) -> Unit,
    onWallpaperBlurColorAlphaChange: (Float) -> Unit
) {
    val context = LocalContext.current

    // Android Photo Picker launcher
    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            // Persist read permission so we can re-read after restart
            context.contentResolver.takePersistableUriPermission(
                uri,
                android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            onWallpaperUriSelected(uri.toString())
        }
    }

    Column {
        InfoRow(
            icon = Icons.Rounded.Wallpaper,
            label = "Wallpaper",
            value = if (showWallpaper) "Visible" else "Hidden",
            onClick = onWallpaperToggle,
            showChevron = true
        )

        if (showWallpaper) {
            Spacer(modifier = Modifier.height(16.dp))

            // Current wallpaper preview
            if (wallpaperUri != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, Color(0xFF3D5AFE), RoundedCornerShape(16.dp))
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(context).data(wallpaperUri).crossfade(true).build(),
                        contentDescription = "Wallpaper preview",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Pick from device / Clear buttons
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF3D5AFE))
                        .clickable {
                            photoPicker.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        }
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                ) {
                    Text("Choose from Device", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }

                if (wallpaperUri != null) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color.White.copy(alpha = 0.08f))
                            .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(14.dp))
                            .clickable { onWallpaperUriSelected(null) }
                            .padding(horizontal = 20.dp, vertical = 10.dp)
                    ) {
                        Text("Clear", color = Color.White.copy(alpha = 0.7f), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color.White.copy(alpha = 0.05f))

            InfoRow(
                icon = Icons.Rounded.BlurOn,
                label = "Wallpaper Blur",
                value = if (wallpaperBlur) "Enabled" else "Disabled",
                onClick = onWallpaperBlurToggle,
                showChevron = true
            )

            if (wallpaperBlur) {
                Spacer(modifier = Modifier.height(16.dp))
                Column(modifier = Modifier.padding(horizontal = 8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Intensity",
                            color = Color.White.copy(alpha = 0.6f),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${wallpaperBlurIntensity.toInt()}px",
                            color = Color(0xFF3D5AFE),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Slider(
                        value = wallpaperBlurIntensity,
                        onValueChange = onWallpaperBlurIntensityChange,
                        valueRange = 0f..25f,
                        colors = SliderDefaults.colors(
                            thumbColor = Color(0xFF3D5AFE),
                            activeTrackColor = Color(0xFF3D5AFE),
                            inactiveTrackColor = Color.White.copy(alpha = 0.1f)
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Blur Color Tint",
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    val tintColors = listOf(
                        Color.Transparent, Color.Black, Color.White,
                        Color(0xFF3D5AFE), Color(0xFFFF4081), Color(0xFF4CAF50), Color(0xFFFFEB3B)
                    )
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(tintColors) { color ->
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(if (color == Color.Transparent) Color.DarkGray else color)
                                    .border(
                                        width = if (wallpaperBlurColor == color) 2.dp else 1.dp,
                                        color = if (wallpaperBlurColor == color) Color.White else Color.White.copy(alpha = 0.2f),
                                        shape = CircleShape
                                    )
                                    .clickable { onWallpaperBlurColorChange(color) },
                                contentAlignment = Alignment.Center
                            ) {
                                if (color == Color.Transparent) {
                                    Icon(Icons.Rounded.Close, null, tint = Color.White, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Tint Alpha",
                            color = Color.White.copy(alpha = 0.6f),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${(wallpaperBlurColorAlpha * 100).toInt()}%",
                            color = Color(0xFF3D5AFE),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Slider(
                        value = wallpaperBlurColorAlpha,
                        onValueChange = onWallpaperBlurColorAlphaChange,
                        valueRange = 0f..1f,
                        colors = SliderDefaults.colors(
                            thumbColor = Color(0xFF3D5AFE),
                            activeTrackColor = Color(0xFF3D5AFE),
                            inactiveTrackColor = Color.White.copy(alpha = 0.1f)
                        )
                    )
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

@Composable
fun GesturesSection(
    doubleTapAction: com.codershubinc.nullvoidlauncher.data.DoubleTapAction,
    onDoubleTapActionChange: (com.codershubinc.nullvoidlauncher.data.DoubleTapAction) -> Unit
) {
    Column {
        InfoRow(
            icon = Icons.Rounded.TouchApp,
            label = "Double-Tap Action",
            value = when (doubleTapAction) {
                com.codershubinc.nullvoidlauncher.data.DoubleTapAction.CYCLE_WALLPAPER -> "Cycle Wallpaper"
                com.codershubinc.nullvoidlauncher.data.DoubleTapAction.NONE -> "Disabled"
            },
            onClick = {
                val nextAction = when (doubleTapAction) {
                    com.codershubinc.nullvoidlauncher.data.DoubleTapAction.CYCLE_WALLPAPER -> com.codershubinc.nullvoidlauncher.data.DoubleTapAction.NONE
                    com.codershubinc.nullvoidlauncher.data.DoubleTapAction.NONE -> com.codershubinc.nullvoidlauncher.data.DoubleTapAction.CYCLE_WALLPAPER
                }
                onDoubleTapActionChange(nextAction)
            },
            showChevron = true
        )
    }
}

