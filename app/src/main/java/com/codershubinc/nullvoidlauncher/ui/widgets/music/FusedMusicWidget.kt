package com.codershubinc.nullvoidlauncher.ui.widgets.music

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
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

@Composable
fun FusedMusicWidget() {
    var track by remember { mutableStateOf<MusicTrack?>(null) }
    var tapCount by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
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

    track?.let {
        if (it.title != null && it.title != "" && it.title != "Unknown Title") {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .background(Color.DarkGray, shape = RoundedCornerShape(24.dp))
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) {
                        tapCount++
                    }
                    .padding(horizontal = 12.dp, vertical = 6.dp)
                    .wrapContentWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                if (it.artwork != null) {
                    Image(
                        bitmap = it.artwork.asImageBitmap(),
                        contentDescription = null,
                        modifier = Modifier
                            .size(24.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }

                Text(
                    text = "${it.title.lowercase()} • ${it.artist?.lowercase() ?: "unknown"}",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.widthIn(max = 200.dp)
                )
            }
        }
    }
}
