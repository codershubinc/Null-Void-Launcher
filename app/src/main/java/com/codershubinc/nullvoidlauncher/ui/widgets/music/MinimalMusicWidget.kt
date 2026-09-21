package com.codershubinc.nullvoidlauncher.ui.widgets.music

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
 * MinimalMusicWidget — Just a slim pill with title, artist and play/pause.
 * No artwork, no border clutter. Pure text + icon control.
 */
@Composable
fun MinimalMusicWidget(
    modifier: Modifier = Modifier,
    previewTrack: MusicTrack? = null,
    font: WidgetFont = WidgetFont.DEFAULT
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

    val shape = RoundedCornerShape(cornerRadius.dp)

    Row(
        modifier = modifier
            .widthIn(max = 500.dp)
            .fillMaxWidth()
            .background(Color.White.copy(alpha = 0.06f), shape)
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
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
            val displayTitle = when {
                !hasPermission -> "Music Sync Disabled"
                track != null -> track?.title ?: "Playing"
                else -> "No Music Playing"
            }
            val displayArtist = when {
                !hasPermission -> "Tap to grant permission"
                track != null -> track?.artist
                else -> "Tap to open music"
            }
            Text(
                text = displayTitle,
                color = Color.White,
                fontSize = 14.sp,
                fontFamily = font.toFontFamily(),
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (!displayArtist.isNullOrBlank() && !displayArtist.equals("Unknown Artist", ignoreCase = true)) {
                Text(
                    text = displayArtist,
                    color = Color.White.copy(alpha = 0.45f),
                    fontSize = 11.sp,
                    fontFamily = font.toFontFamily(),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        Icon(
            imageVector = if (track?.isPlaying == true) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
            contentDescription = "Play/Pause",
            tint = Color.White,
            modifier = Modifier
                .size(28.dp)
                .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) {
                    if (!hasPermission) {
                        context.startActivity(android.content.Intent(android.provider.Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS).apply {
                            flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK
                        })
                    } else {
                        MediaService.instance?.togglePlayPause()
                    }
                }
        )
    }
}
