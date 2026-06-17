package com.codershubinc.nullvoidlauncher.ui.widgets

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codershubinc.nullvoidlauncher.ui.music.MediaService
import com.codershubinc.nullvoidlauncher.ui.music.MusicTrack
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

@Composable
fun MusicWidget() {
    var track by remember { mutableStateOf<MusicTrack?>(null) }
    var tapCount by remember { mutableIntStateOf(0) }


    LaunchedEffect(Unit) {
        while (true) {
            val fetchTrack = withContext(Dispatchers.IO) {
                MediaService.instance?.getMediaSessionInfo()
            }
            track = fetchTrack
            delay(1000)
        }
    }
    LaunchedEffect(tapCount) {
        if (tapCount > 0) {
            delay(300) // Wait 300ms for consecutive taps
            when (tapCount) {
                1 -> MediaService.instance?.togglePlayPause() // Single Tap
                2 -> MediaService.instance?.next()            // Double Tap
                3 -> MediaService.instance?.previous()        // Triple Tap
            }
            tapCount = 0 // Reset the counter
        }
    }

    track?.let {
        if (track!!.title != "" || track!!.title!="Unknown Title") {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .clickable(
                        indication = null, // Removes ripple for that clean terminal look
                        interactionSource = remember { MutableInteractionSource() }
                    ) {
                        tapCount++
                    }
            ) {
                if (it.artwork != null) {
                    Image(
                        bitmap = it.artwork.asImageBitmap(),
                        contentDescription = null,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                }

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = it.title?.uppercase() ?: "UNKNOWN",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 14.sp,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 2.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .fillMaxWidth(1f)

                    )
                    Text(
                        text = it.artist?.uppercase() ?: "UNKNOWN ARTIST",
                        color = Color.DarkGray,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.sp
                    )
                }
            }
        }
    }
}
