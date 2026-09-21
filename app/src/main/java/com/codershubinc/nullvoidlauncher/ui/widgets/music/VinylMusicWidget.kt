package com.codershubinc.nullvoidlauncher.ui.widgets.music

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
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
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codershubinc.nullvoidlauncher.data.UserManager
import com.codershubinc.nullvoidlauncher.ui.music.MediaService
import com.codershubinc.nullvoidlauncher.ui.music.MusicTrack
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlin.time.Duration.Companion.milliseconds

import com.codershubinc.nullvoidlauncher.data.WidgetFont

/**
 * VinylMusicWidget — Turntable / Vinyl record aesthetic.
 * An animated spinning vinyl disc peeks from an album sleeve with groovy concentric grooves.
 */
@Composable
fun VinylMusicWidget(
    modifier: Modifier = Modifier,
    previewTrack: MusicTrack? = null,
    font: WidgetFont = WidgetFont.DEFAULT
) {
    val context = LocalContext.current
    val userManager = remember { UserManager(context) }
    val cornerRadius = userManager.getWidgetCornerRadius()
    val shape = RoundedCornerShape(cornerRadius.dp)

    var track by remember { mutableStateOf(previewTrack) }
    var hasPermission by remember { mutableStateOf(MediaService.isPermissionGranted(context)) }

    LaunchedEffect(previewTrack) {
        if (previewTrack != null) {
            track = previewTrack
            return@LaunchedEffect
        }
        while (true) {
            hasPermission = MediaService.isPermissionGranted(context)
            val live = withContext(Dispatchers.IO) { MediaService.instance?.getMediaSessionInfo() }
            track = live
            delay(1000.milliseconds)
        }
    }

    val isPlaying = track?.isPlaying == true
    val infiniteTransition = rememberInfiniteTransition(label = "vinyl_rotation")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "vinyl_angle"
    )

    Row(
        modifier = modifier
            .widthIn(max = 500.dp)
            .fillMaxWidth()
            .clip(shape)
            .background(Color(0xFF121216))
            .border(1.dp, Color.White.copy(alpha = 0.08f), shape)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                if (!hasPermission) {
                    context.startActivity(android.content.Intent(android.provider.Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS).apply {
                        flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK
                    })
                } else if (track == null) {
                    val musicIntent = android.content.Intent(android.content.Intent.ACTION_MAIN).apply {
                        addCategory(android.content.Intent.CATEGORY_APP_MUSIC)
                        flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    try { context.startActivity(musicIntent) } catch (_: Exception) {}
                }
            }
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Vinyl Record & Sleeve Stack
        Box(
            modifier = Modifier.size(68.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            // Spinning vinyl disc peeking out
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .offset(x = 10.dp)
                    .rotate(if (isPlaying) rotation else 0f)
                    .clip(CircleShape)
                    .background(Color(0xFF1E1E24))
                    .border(1.dp, Color.White.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                // Vinyl grooves
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val r = size.minDimension / 2
                    drawCircle(Color.White.copy(alpha = 0.05f), radius = r * 0.8f, style = Stroke(1.dp.toPx()))
                    drawCircle(Color.White.copy(alpha = 0.05f), radius = r * 0.6f, style = Stroke(1.dp.toPx()))
                    drawCircle(Color.White.copy(alpha = 0.05f), radius = r * 0.4f, style = Stroke(1.dp.toPx()))
                }
                // Center hole / label
                Box(
                    modifier = Modifier
                        .size(18.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFC5A35E))
                        .border(2.dp, Color(0xFF1E1E24), CircleShape)
                )
            }

            // Album art sleeve
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF24242C))
                    .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(8.dp)),
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
                    Icon(
                        Icons.Rounded.MusicNote,
                        contentDescription = null,
                        tint = Color(0xFFC5A35E),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(14.dp))

        // Track Info
        Column(modifier = Modifier.weight(1f)) {
            val title = when {
                !hasPermission -> "Music Sync Disabled"
                track != null -> track?.title ?: "Playing"
                else -> "Vinyl Idle"
            }
            val artist = when {
                !hasPermission -> "Tap to grant access"
                track != null -> track?.artist
                else -> "Tap to drop the needle"
            }
            Text(
                text = title,
                color = Color.White,
                fontSize = 13.sp,
                fontFamily = font.toFontFamily(),
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (!artist.isNullOrBlank() && !artist.equals("Unknown Artist", ignoreCase = true)) {
                Text(
                    text = artist,
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 11.sp,
                    fontFamily = font.toFontFamily(),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Transport buttons
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.08f))
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
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }

            Icon(
                imageVector = Icons.Rounded.SkipNext,
                contentDescription = "Next",
                tint = Color.White.copy(alpha = 0.7f),
                modifier = Modifier
                    .size(26.dp)
                    .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) {
                        MediaService.instance?.next()
                    }
            )
        }
    }
}
