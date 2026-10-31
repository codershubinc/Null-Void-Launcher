package com.codershubinc.nullvoidlauncher.ui.widgets.music

import androidx.compose.animation.core.*
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
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codershubinc.nullvoidlauncher.data.UserManager
import com.codershubinc.nullvoidlauncher.data.WidgetFont
import com.codershubinc.nullvoidlauncher.ui.music.MediaService
import com.codershubinc.nullvoidlauncher.ui.music.MusicTrack
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlin.time.Duration.Companion.milliseconds

/**
 * RetroMusicWidget — A cassette-tape inspired vintage audio deck widget.
 * Restructured with a retro status header, dual-spool cassette/album window,
 * animated amber VU audio meters, and dedicated tactile transport keys.
 */
@Composable
fun RetroMusicWidget(
    modifier: Modifier = Modifier,
    previewTrack: MusicTrack? = null,
    font: WidgetFont = WidgetFont.MONOSPACE
) {
    val context = LocalContext.current
    val userManager = remember { UserManager(context) }
    val cornerRadius = userManager.widgets.cornerRadius

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
            track = withContext(Dispatchers.IO) { MediaService.instance?.getMediaSessionInfo() }
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

    val isPlaying = track?.isPlaying == true
    val amber = Color(0xFFC5A35E)
    val bg = Color(0xFF161008)
    val shape = RoundedCornerShape(cornerRadius.dp)

    // Animated retro VU meter bars
    val infinite = rememberInfiniteTransition(label = "retro_vu")
    val bar1 by infinite.animateFloat(initialValue = 2f, targetValue = 9f, animationSpec = infiniteRepeatable(tween(320, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "rvu1")
    val bar2 by infinite.animateFloat(initialValue = 8f, targetValue = 3f, animationSpec = infiniteRepeatable(tween(400, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "rvu2")
    val bar3 by infinite.animateFloat(initialValue = 3f, targetValue = 10f, animationSpec = infiniteRepeatable(tween(270, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "rvu3")
    val bar4 by infinite.animateFloat(initialValue = 9f, targetValue = 2f, animationSpec = infiniteRepeatable(tween(460, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "rvu4")

    Column(
        modifier = modifier
            .widthIn(max = 500.dp)
            .fillMaxWidth()
            .clip(shape)
            .background(bg)
            .border(1.2.dp, amber.copy(alpha = 0.45f), shape)
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
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        // 1. TOP RETRO STATUS HEADER
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(if (isPlaying) Color(0xFFFFB300) else amber.copy(alpha = 0.3f))
                )
                Text(
                    text = "CASSETTE // TAPE-A",
                    color = amber.copy(alpha = 0.85f),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = font.toFontFamily(),
                    letterSpacing = 1.sp
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(3.dp))
                    .background(amber.copy(alpha = 0.15f))
                    .border(0.8.dp, amber.copy(alpha = 0.4f), RoundedCornerShape(3.dp))
                    .padding(horizontal = 6.dp, vertical = 1.5.dp)
            ) {
                Text(
                    text = when {
                        !hasPermission -> "NO SYNC"
                        isPlaying -> "PLAY ▶"
                        track != null -> "PAUSE ❚❚"
                        else -> "STANDBY"
                    },
                    color = amber,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = font.toFontFamily(),
                    letterSpacing = 0.5.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 2. MAIN DECK ROW (Art + Title/Artist + Tactile Controls)
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Cassette Window / Album Art
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFF26190A))
                    .border(1.dp, amber.copy(alpha = 0.4f), RoundedCornerShape(6.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (track?.artwork != null) {
                    Image(
                        bitmap = track!!.artwork!!.asImageBitmap(),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(13.dp)
                                .clip(CircleShape)
                                .background(amber.copy(alpha = 0.2f))
                                .border(1.dp, amber.copy(alpha = 0.6f), CircleShape)
                        )
                        Box(
                            modifier = Modifier
                                .width(7.dp)
                                .height(2.dp)
                                .background(amber.copy(alpha = 0.5f))
                        )
                        Box(
                            modifier = Modifier
                                .size(13.dp)
                                .clip(CircleShape)
                                .background(amber.copy(alpha = 0.2f))
                                .border(1.dp, amber.copy(alpha = 0.6f), CircleShape)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Track Details & Simulated VU Level Meter
            Column(modifier = Modifier.weight(1f)) {
                val displayTitle = when {
                    !hasPermission -> "SYNC DISABLED"
                    track != null -> track?.title ?: "PLAYING"
                    else -> "NO AUDIO"
                }
                val displayArtist = when {
                    !hasPermission -> "TAP // PERMISSION"
                    track != null -> track?.artist
                    else -> "TAP // OPEN PLAYER"
                }

                Text(
                    text = displayTitle.uppercase(),
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = font.toFontFamily(),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(2.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if (!displayArtist.isNullOrBlank() && !displayArtist.equals("Unknown Artist", ignoreCase = true)) {
                        Text(
                            text = displayArtist.uppercase(),
                            color = amber.copy(alpha = 0.75f),
                            fontSize = 10.sp,
                            fontFamily = font.toFontFamily(),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, fill = false)
                        )
                    }

                    // Mini retro VU audio bars
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(2.dp),
                        verticalAlignment = Alignment.Bottom,
                        modifier = Modifier.height(10.dp)
                    ) {
                        val heights = if (isPlaying) listOf(bar1, bar2, bar3, bar4) else listOf(3f, 5f, 3f, 4f)
                        heights.forEach { h ->
                            Box(
                                modifier = Modifier
                                    .width(2.5.dp)
                                    .height(h.dp)
                                    .clip(RoundedCornerShape(1.dp))
                                    .background(amber.copy(alpha = 0.85f))
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            // 3. RETRO TACTILE TRANSPORT KEYS
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Previous Button
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFF26190A))
                        .border(1.dp, amber.copy(alpha = 0.35f), RoundedCornerShape(6.dp))
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) {
                            MediaService.instance?.previous()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.SkipPrevious,
                        contentDescription = "Previous",
                        tint = amber,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Play / Pause Key
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(amber.copy(alpha = 0.2f))
                        .border(1.2.dp, amber.copy(alpha = 0.7f), RoundedCornerShape(6.dp))
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
                        imageVector = if (isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                        contentDescription = "Play/Pause",
                        tint = amber,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Next Button
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFF26190A))
                        .border(1.dp, amber.copy(alpha = 0.35f), RoundedCornerShape(6.dp))
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) {
                            MediaService.instance?.next()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.SkipNext,
                        contentDescription = "Next",
                        tint = amber,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
