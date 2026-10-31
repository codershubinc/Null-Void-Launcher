/*
 * Copyright (C) 2026- Swapnil Ingle
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.codershubinc.nullvoidlauncher.ui.settings

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.codershubinc.nullvoidlauncher.data.UserManager
import com.codershubinc.nullvoidlauncher.data.model.DoubleTapAction
import com.codershubinc.nullvoidlauncher.data.wallpaper.CloudWallpaperEngine
import com.codershubinc.nullvoidlauncher.ui.components.ModernCard

private val AccentIndigo = Color(0xFF3D5AFE)

@Composable
fun WallpaperSettingsPage(
    userManager: UserManager,
    onBack: () -> Unit,
    onWallpaperChanged: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var showWallpaper by remember { mutableStateOf(userManager.wallpaper.showWallpaper) }
    var wallpaperUri by remember { mutableStateOf(userManager.wallpaper.wallpaperUri) }
    var blur by remember { mutableStateOf(userManager.wallpaper.blur) }
    var blurIntensity by remember { mutableFloatStateOf(userManager.wallpaper.blurIntensity) }
    var blurColor by remember { mutableStateOf(Color(userManager.wallpaper.blurColor)) }
    var blurColorAlpha by remember { mutableFloatStateOf(userManager.wallpaper.blurColorAlpha) }
    var doubleTapAction by remember { mutableStateOf(userManager.theme.doubleTapAction) }

    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (_: Exception) {}
            val uriStr = uri.toString()
            wallpaperUri = uriStr
            showWallpaper = true
            userManager.wallpaper.wallpaperUri = uriStr
            userManager.wallpaper.showWallpaper = true
            onWallpaperChanged?.invoke()
        }
    }

    fun applyWallpaper(uri: String?) {
        wallpaperUri = uri
        showWallpaper = true
        userManager.wallpaper.wallpaperUri = uri
        userManager.wallpaper.showWallpaper = true
        onWallpaperChanged?.invoke()
    }

    fun clearWallpaper() {
        wallpaperUri = null
        userManager.wallpaper.clearWallpaperUri()
        onWallpaperChanged?.invoke()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF080808))
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp)
            .verticalScroll(scrollState)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Top Navigation Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.06f))
                    .border(1.dp, Color.White.copy(alpha = 0.1f), CircleShape)
                    .clickable { onBack() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = "Wallpaper & Style",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Backdrop blur, curated voids & device gallery",
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ── 1. Live Interactive Backdrop Preview ───────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFF101014))
                .border(
                    width = 1.2.dp,
                    brush = Brush.verticalGradient(
                        listOf(Color.White.copy(alpha = 0.2f), Color.White.copy(alpha = 0.05f))
                    ),
                    shape = RoundedCornerShape(24.dp)
                )
        ) {
            if (showWallpaper && wallpaperUri != null) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(wallpaperUri)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Wallpaper Preview",
                    modifier = Modifier
                        .fillMaxSize()
                        .blur(if (blur) blurIntensity.dp else 0.dp),
                    contentScale = ContentScale.Crop
                )
                if (blur && blurColor != Color.Transparent) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(blurColor.copy(alpha = blurColorAlpha))
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.radialGradient(
                                listOf(AccentIndigo.copy(alpha = 0.15f), Color(0xFF070709))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Rounded.Wallpaper,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.3f),
                            modifier = Modifier.size(42.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (!showWallpaper) "Wallpaper Disabled (Pure Void)" else "No Custom Image Selected",
                            color = Color.White.copy(alpha = 0.5f),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Top Status Badge
            Row(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(14.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.Black.copy(alpha = 0.65f))
                    .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                    .padding(horizontal = 10.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(7.dp)
                        .clip(CircleShape)
                        .background(if (showWallpaper) Color(0xFF00E676) else Color.White.copy(alpha = 0.3f))
                )
                Text(
                    text = if (showWallpaper) "Active" else "Hidden",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ── 2. Master Toggle & Source Actions ─────────────────────────────────────────
        ModernCard {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f).padding(end = 16.dp)) {
                    Text(
                        text = "Enable Wallpaper",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Show custom or curated cloud wallpaper backdrop",
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 12.sp
                    )
                }
                Switch(
                    checked = showWallpaper,
                    onCheckedChange = { isChecked ->
                        showWallpaper = isChecked
                        userManager.wallpaper.showWallpaper = isChecked
                        onWallpaperChanged?.invoke()
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = AccentIndigo
                    )
                )
            }

            Spacer(modifier = Modifier.height(18.dp))
            HorizontalDivider(color = Color.White.copy(alpha = 0.06f))
            Spacer(modifier = Modifier.height(18.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Pick from Gallery
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(AccentIndigo)
                        .clickable {
                            photoPicker.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        }
                        .padding(vertical = 12.dp, horizontal = 14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.AddPhotoAlternate,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "Choose Photo",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Cycle Next
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White.copy(alpha = 0.08f))
                        .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(16.dp))
                        .clickable {
                            val next = CloudWallpaperEngine.cycleNextWallpaper(context, userManager)
                            applyWallpaper(next.url)
                        }
                        .padding(vertical = 12.dp, horizontal = 14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Shuffle,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "Cycle Cloud",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Clear
                if (wallpaperUri != null) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFFE53935).copy(alpha = 0.15f))
                            .border(1.dp, Color(0xFFE53935).copy(alpha = 0.35f), RoundedCornerShape(16.dp))
                        .clickable { clearWallpaper() }
                        .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.DeleteOutline,
                            contentDescription = "Clear",
                            tint = Color(0xFFFF5252),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ── 3. Curated Cloud Void Wallpapers ──────────────────────────────────────────
        ModernCard {
            Text(
                text = "Curated Void Presets",
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Hand-picked high contrast dark wallpapers from Cloudflare CDN",
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 12.sp
            )
            Spacer(modifier = Modifier.height(14.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(CloudWallpaperEngine.curatedWallpapers) { cloudWp ->
                    val isSelected = wallpaperUri == cloudWp.url
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .width(105.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { applyWallpaper(cloudWp.url) }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(width = 105.dp, height = 135.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .border(
                                    width = if (isSelected) 2.5.dp else 1.dp,
                                    color = if (isSelected) AccentIndigo else Color.White.copy(alpha = 0.12f),
                                    shape = RoundedCornerShape(16.dp)
                                )
                        ) {
                            AsyncImage(
                                model = ImageRequest.Builder(context)
                                    .data(cloudWp.url)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = cloudWp.name,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                            if (isSelected) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .align(Alignment.TopEnd)
                                        .padding(4.dp)
                                        .clip(CircleShape)
                                        .background(AccentIndigo),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Check,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(12.dp)
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = cloudWp.name,
                            color = if (isSelected) Color.White else Color.White.copy(alpha = 0.6f),
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            maxLines = 1
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ── 4. Frosted Blur & Visual Effects ──────────────────────────────────────────
        ModernCard {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f).padding(end = 16.dp)) {
                    Text(
                        text = "Frosted Backdrop Blur",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Diffuses background for maximum widget legibility",
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 12.sp
                    )
                }
                Switch(
                    checked = blur,
                    onCheckedChange = { isChecked ->
                        blur = isChecked
                        userManager.wallpaper.blur = isChecked
                        onWallpaperChanged?.invoke()
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = AccentIndigo
                    )
                )
            }

            AnimatedVisibility(visible = blur) {
                Column(modifier = Modifier.padding(top = 18.dp)) {
                    HorizontalDivider(color = Color.White.copy(alpha = 0.06f))
                    Spacer(modifier = Modifier.height(18.dp))

                    // Blur Intensity Slider
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Blur Radius",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(AccentIndigo.copy(alpha = 0.15f))
                                .border(1.dp, AccentIndigo.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "${blurIntensity.toInt()} px",
                                color = Color(0xFF82B1FF),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Slider(
                        value = blurIntensity,
                        onValueChange = {
                            blurIntensity = it
                            userManager.wallpaper.blurIntensity = it
                            onWallpaperChanged?.invoke()
                        },
                        valueRange = 0f..25f,
                        colors = SliderDefaults.colors(
                            thumbColor = Color.White,
                            activeTrackColor = AccentIndigo,
                            inactiveTrackColor = Color.White.copy(alpha = 0.1f)
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Tint Color Palette
                    Text(
                        text = "Color Tint Overlay",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    val tintColors = listOf(
                        Color.Transparent,
                        Color.Black,
                        Color.White,
                        Color(0xFF3D5AFE),
                        Color(0xFFFF4081),
                        Color(0xFF00E676),
                        Color(0xFF00E5FF),
                        Color(0xFFFFB300)
                    )

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(tintColors) { color ->
                            val isColorSelected = blurColor == color
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(if (color == Color.Transparent) Color(0xFF1E1E24) else color)
                                    .border(
                                        width = if (isColorSelected) 2.5.dp else 1.dp,
                                        color = if (isColorSelected) Color.White else Color.White.copy(alpha = 0.18f),
                                        shape = CircleShape
                                    )
                                    .clickable {
                                        blurColor = color
                                        userManager.wallpaper.blurColor = color.toArgb()
                                        onWallpaperChanged?.invoke()
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                if (color == Color.Transparent) {
                                    Icon(
                                        imageVector = Icons.Rounded.Block,
                                        contentDescription = "None",
                                        tint = Color.White.copy(alpha = 0.6f),
                                        modifier = Modifier.size(16.dp)
                                    )
                                } else if (isColorSelected) {
                                    Icon(
                                        imageVector = Icons.Rounded.Check,
                                        contentDescription = null,
                                        tint = if (color == Color.White) Color.Black else Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Tint Alpha Slider
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Tint Opacity",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(AccentIndigo.copy(alpha = 0.15f))
                                .border(1.dp, AccentIndigo.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "${(blurColorAlpha * 100).toInt()}%",
                                color = Color(0xFF82B1FF),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Slider(
                        value = blurColorAlpha,
                        onValueChange = {
                            blurColorAlpha = it
                            userManager.wallpaper.blurColorAlpha = it
                            onWallpaperChanged?.invoke()
                        },
                        valueRange = 0f..1f,
                        colors = SliderDefaults.colors(
                            thumbColor = Color.White,
                            activeTrackColor = AccentIndigo,
                            inactiveTrackColor = Color.White.copy(alpha = 0.1f)
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ── 5. Gestural Shortcut Card ────────────────────────────────────────────────
        ModernCard {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f).padding(end = 16.dp)) {
                    Text(
                        text = "Double-Tap to Cycle",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Double-tap empty home screen area to switch wallpaper instantly",
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 12.sp
                    )
                }
                Switch(
                    checked = doubleTapAction == DoubleTapAction.CYCLE_WALLPAPER,
                    onCheckedChange = { isChecked ->
                        val next = if (isChecked) DoubleTapAction.CYCLE_WALLPAPER else DoubleTapAction.NONE
                        doubleTapAction = next
                        userManager.theme.doubleTapAction = next
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = AccentIndigo
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}
