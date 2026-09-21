package com.codershubinc.nullvoidlauncher.ui.widgets.music

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
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
import com.codershubinc.nullvoidlauncher.ui.music.MediaService
import com.codershubinc.nullvoidlauncher.ui.music.MusicTrack
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlin.time.Duration.Companion.milliseconds

import com.codershubinc.nullvoidlauncher.data.WidgetFont

/**
 * RetroMusicWidget — A cassette-tape inspired horizontal music widget.
 * Shows album art on the left, track info and full transport controls in an
 * amber-on-dark retro style. Corner radius and other properties come from
 * the shared WidgetSettings in UserManager.
 */
@Composable
fun RetroMusicWidget(
    modifier: Modifier = Modifier,
    previewTrack: MusicTrack? = null,
    font: WidgetFont = WidgetFont.MONOSPACE
) {
    val context = LocalContext.current
    val userManager = remember { UserManager(context) }
    val cornerRadius = userManager.getWidgetCornerRadius()

    var track by remember { mutableStateOf<MusicTrack?>(previewTrack) }
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

    val amber = Color(0xFFC5A35E)
    val bg = Color(0xFF1A1208)
    val shape = RoundedCornerShape(cornerRadius.dp)

    Row(
        modifier = modifier
            .widthIn(max = 500.dp)
            .fillMaxWidth()
            .clip(shape)
            .background(bg)
            .border(1.dp, amber.copy(alpha = 0.4f), shape)
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
                    try {
                        context.startActivity(musicIntent)
                    } catch (_: Exception) {}
                }
            }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Album art
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF2A1E08))
                .border(1.dp, amber.copy(alpha = 0.3f), RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            if (track?.artwork != null) {
                track!!.artwork?.let {
                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            } else {
                Icon(Icons.Rounded.MusicNote, contentDescription = null, tint = amber, modifier = Modifier.size(28.dp))
            }
        }

        // Track info + controls
        Column(modifier = Modifier.weight(1f)) {
            val displayTitle = when {
                !hasPermission -> "SYNC DISABLED"
                track != null -> track?.title ?: "PLAYING"
                else -> "NO TRACK"
            }
            val displayArtist = when {
                !hasPermission -> "TAP TO ENABLE"
                track != null -> track?.artist
                else -> "TAP TO LAUNCH"
            }
            Text(
                text = displayTitle,
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = font.toFontFamily(),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (!displayArtist.isNullOrBlank() && !displayArtist.equals("Unknown Artist", ignoreCase = true)) {
                Text(
                    text = displayArtist,
                    color = amber.copy(alpha = 0.8f),
                    fontSize = 10.sp,
                    fontFamily = font.toFontFamily(),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            // Transport controls
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Rounded.SkipPrevious, null, tint = amber, modifier = Modifier
                    .size(24.dp)
                    .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) {
                        MediaService.instance?.previous()
                    })
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(amber.copy(alpha = 0.15f))
                        .border(1.dp, amber.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                        .clickable { MediaService.instance?.togglePlayPause() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (track?.isPlaying == true) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                        contentDescription = null,
                        tint = amber,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Icon(Icons.Rounded.SkipNext, null, tint = amber, modifier = Modifier
                    .size(24.dp)
                    .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) {
                        MediaService.instance?.next()
                    })
            }
        }
    }
}
