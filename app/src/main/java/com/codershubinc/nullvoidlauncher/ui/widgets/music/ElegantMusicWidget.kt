package com.codershubinc.nullvoidlauncher.ui.widgets.music

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codershubinc.nullvoidlauncher.ui.music.MediaService
import com.codershubinc.nullvoidlauncher.ui.music.MusicTrack
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlin.time.Duration.Companion.milliseconds

import androidx.compose.ui.platform.LocalContext
import com.codershubinc.nullvoidlauncher.data.UserManager
import androidx.compose.ui.draw.blur

import com.codershubinc.nullvoidlauncher.data.WidgetFont

@Composable
fun ElegantMusicWidget(
    modifier: Modifier = Modifier,
    previewTrack: MusicTrack? = null,
    font: WidgetFont = WidgetFont.DEFAULT
) {
    val context = LocalContext.current
    val userManager = remember { UserManager(context) }
    
    val widgetColor = Color(userManager.getWidgetColor())
    val cornerRadius = userManager.getWidgetCornerRadius()
    val blurIntensity = userManager.getWidgetBlurIntensity()
    val glassEffect = userManager.getWidgetGlassEffect()
    val preset = userManager.getWidgetPreset()

    var track by remember { mutableStateOf<MusicTrack?>(previewTrack) }
    var tapCount by remember { mutableIntStateOf(0) }
    var hasPermission by remember { mutableStateOf(previewTrack != null || MediaService.isPermissionGranted(context)) }

    LaunchedEffect(previewTrack) {
        if (previewTrack != null) {
            track = previewTrack
            hasPermission = true
            return@LaunchedEffect
        }
        while (true) {
            hasPermission = MediaService.isPermissionGranted(context)
            val fetchTrack = withContext(Dispatchers.IO) {
                MediaService.instance?.getMediaSessionInfo()
            }
            track = fetchTrack
            delay(1000.milliseconds)
        }
    }

    LaunchedEffect(tapCount) {
        if (tapCount > 0) {
            delay(300.milliseconds)
            when (tapCount) {
                1 -> MediaService.instance?.togglePlayPause()
                2 -> MediaService.instance?.next()
                3 -> MediaService.instance?.previous()
            }
            tapCount = 0
        }
    }

    val shape = RoundedCornerShape(cornerRadius.dp)

    Row(
        modifier = modifier
            .widthIn(max = 500.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        // Main Music Pill
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .weight(1f)
                .background(
                    if (glassEffect) {
                        Brush.verticalGradient(
                            colors = listOf(
                                widgetColor.copy(alpha = widgetColor.alpha.coerceAtMost(0.4f)),
                                widgetColor.copy(alpha = widgetColor.alpha.coerceAtMost(0.1f))
                            )
                        )
                    } else {
                        Brush.verticalGradient(colors = listOf(widgetColor, widgetColor))
                    },
                    shape = shape
                )
                .border(
                    width = 1.dp,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.4f),
                            Color.White.copy(alpha = 0.1f)
                        )
                    ),
                    shape = shape
                )
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    if (!hasPermission) {
                        val intent = android.content.Intent(android.provider.Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS).apply {
                            flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK
                        }
                        context.startActivity(intent)
                    } else if (track == null) {
                        val musicIntent = android.content.Intent(android.content.Intent.ACTION_MAIN).apply {
                            addCategory(android.content.Intent.CATEGORY_APP_MUSIC)
                            flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK
                        }
                        try {
                            context.startActivity(musicIntent)
                        } catch (e: Exception) {
                            tapCount++
                        }
                    } else {
                        tapCount++
                    }
                }
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            if (preset != "MINIMAL") {
                // Music ARTWORK Icon
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    widgetColor.copy(alpha = widgetColor.alpha.coerceAtMost(0.4f)),
                                    widgetColor.copy(alpha = widgetColor.alpha.coerceAtMost(0.1f))
                                )
                            )
                        )
                        .border(
                            width = 1.dp,
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.4f),
                                    Color.White.copy(alpha = 0.1f)
                                )
                            ),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (track?.artwork == null) {
                        Icon(
                            imageVector = Icons.Rounded.MusicNote,
                            contentDescription = null,
                            tint = Color(0xFFC5A35E),
                            modifier = Modifier.size(32.dp)
                        )
                    } else {
                        track?.artwork?.let {
                            Image(
                                bitmap = it.asImageBitmap(),
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))
            }

            Column {
                val displayTitle = when {
                    !hasPermission -> "Music Sync Disabled"
                    track != null -> track?.title ?: "Playing"
                    else -> "No Music Playing"
                }
                val displayArtist = when {
                    !hasPermission -> "Tap to enable permission"
                    track != null -> track?.artist
                    else -> "Tap to open player"
                }
                Text(
                    text = displayTitle,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontFamily = font.toFontFamily(),
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (!displayArtist.isNullOrBlank() && !displayArtist.equals("Unknown Artist", ignoreCase = true)) {
                    Text(
                        text = displayArtist,
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 10.sp,
                        fontFamily = font.toFontFamily(),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Circular Play Button
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(
                    if (glassEffect) {
                        Brush.verticalGradient(
                            colors = listOf(
                                widgetColor.copy(alpha = widgetColor.alpha.coerceAtMost(0.4f)),
                                widgetColor.copy(alpha = widgetColor.alpha.coerceAtMost(0.1f))
                            )
                        )
                    } else {
                        Brush.verticalGradient(colors = listOf(widgetColor, widgetColor))
                    }
                )
                .border(
                    width = 1.dp,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.4f),
                            Color.White.copy(alpha = 0.1f)
                        )
                    ),
                    shape = CircleShape
                )
                .clickable {
                    if (!hasPermission) {
                        context.startActivity(android.content.Intent(android.provider.Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS).apply {
                            flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK
                        })
                    } else {
                        MediaService.instance?.togglePlayPause()
                    }
                },
            contentAlignment = Alignment.Center
        ) {
             Icon(
                 imageVector = if (track?.isPlaying == true) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                 contentDescription = "Play/Pause",
                 tint = Color.White,
                 modifier = Modifier.size(32.dp)
             )
        }

        Spacer(modifier = Modifier.width(12.dp))
    }
}
