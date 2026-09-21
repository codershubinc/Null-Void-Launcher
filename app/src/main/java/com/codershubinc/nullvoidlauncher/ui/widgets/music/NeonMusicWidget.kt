package com.codershubinc.nullvoidlauncher.ui.widgets.music

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.GraphicEq
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
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
 * NeonMusicWidget — Cyberpunk neon visualizer widget.
 * Features glowing cyan/magenta gradient accents and animated pulsing equalizer bars.
 */
@Composable
fun NeonMusicWidget(
    modifier: Modifier = Modifier,
    previewTrack: MusicTrack? = null,
    font: WidgetFont = WidgetFont.MONOSPACE
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
    val cyan = Color(0xFF00E5FF)
    val magenta = Color(0xFFFF007F)

    val infinite = rememberInfiniteTransition(label = "eq_bars")
    val bar1 by infinite.animateFloat(initialValue = 4f, targetValue = 22f, animationSpec = infiniteRepeatable(tween(350, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "b1")
    val bar2 by infinite.animateFloat(initialValue = 18f, targetValue = 6f, animationSpec = infiniteRepeatable(tween(420, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "b2")
    val bar3 by infinite.animateFloat(initialValue = 8f, targetValue = 24f, animationSpec = infiniteRepeatable(tween(290, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "b3")
    val bar4 by infinite.animateFloat(initialValue = 20f, targetValue = 5f, animationSpec = infiniteRepeatable(tween(480, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "b4")

    Row(
        modifier = modifier
            .widthIn(max = 500.dp)
            .fillMaxWidth()
            .clip(shape)
            .background(Color(0xFF080C14))
            .border(
                1.5.dp,
                Brush.horizontalGradient(listOf(cyan.copy(alpha = 0.6f), magenta.copy(alpha = 0.6f))),
                shape
            )
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
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Neon Equalizer bars
        Row(
            modifier = Modifier
                .height(26.dp)
                .width(28.dp),
            horizontalArrangement = Arrangement.spacedBy(3.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            val heights = if (isPlaying) listOf(bar1, bar2, bar3, bar4) else listOf(6f, 10f, 6f, 8f)
            val colors = listOf(cyan, cyan, magenta, magenta)
            heights.forEachIndexed { i, h ->
                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .height(h.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(colors[i])
                )
            }
        }

        Spacer(modifier = Modifier.width(14.dp))

        // Title & Artist
        Column(modifier = Modifier.weight(1f)) {
            val title = when {
                !hasPermission -> "SYNC OFFLINE"
                track != null -> track?.title ?: "STREAMING"
                else -> "AUDIO IDLE"
            }
            val artist = when {
                !hasPermission -> "TAP // PERMISSION"
                track != null -> track?.artist
                else -> "TAP // LAUNCH"
            }
            Text(
                text = title.uppercase(),
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = font.toFontFamily(),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (!artist.isNullOrBlank() && !artist.equals("UNKNOWN", ignoreCase = true) && !artist.equals("UNKNOWN ARTIST", ignoreCase = true)) {
                Text(
                    text = artist.uppercase(),
                    color = cyan.copy(alpha = 0.8f),
                    fontSize = 10.sp,
                    fontFamily = font.toFontFamily(),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Controls
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                Icons.Rounded.SkipPrevious,
                contentDescription = null,
                tint = cyan.copy(alpha = 0.7f),
                modifier = Modifier
                    .size(22.dp)
                    .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) {
                        MediaService.instance?.previous()
                    }
            )

            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(cyan.copy(alpha = 0.15f))
                    .border(1.dp, cyan.copy(alpha = 0.7f), RoundedCornerShape(8.dp))
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
                    contentDescription = null,
                    tint = cyan,
                    modifier = Modifier.size(20.dp)
                )
            }

            Icon(
                Icons.Rounded.SkipNext,
                contentDescription = null,
                tint = magenta.copy(alpha = 0.7f),
                modifier = Modifier
                    .size(22.dp)
                    .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) {
                        MediaService.instance?.next()
                    }
            )
        }
    }
}
