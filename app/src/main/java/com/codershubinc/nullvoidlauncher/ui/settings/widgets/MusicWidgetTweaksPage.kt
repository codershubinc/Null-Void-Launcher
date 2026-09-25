package com.codershubinc.nullvoidlauncher.ui.settings.widgets

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.NotificationsActive
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codershubinc.nullvoidlauncher.data.MusicStyle
import com.codershubinc.nullvoidlauncher.data.UserManager
import com.codershubinc.nullvoidlauncher.ui.components.ModernCard
import com.codershubinc.nullvoidlauncher.ui.music.MusicTrack
import com.codershubinc.nullvoidlauncher.ui.widgets.MusicWidget

@Composable
fun MusicWidgetTweaksPage(
    userManager: UserManager,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var showMusic by remember { mutableStateOf(userManager.getShowMusicWidget()) }
    var musicStyle by remember { mutableStateOf(userManager.getMusicStyle()) }
    var musicFont by remember { mutableStateOf(userManager.getMusicFont()) }

    val previewTrack = remember {
        MusicTrack(
            title = "Midnight City",
            artist = "M83 • Synthwave",
            isPlaying = true
        )
    }

    WidgetPageScaffold(
        title = "Music Widget",
        subtitle = "Customize playback bar, vinyl and neon aesthetics",
        onBack = onBack
    ) {
        WidgetVisibilityCard(
            title = "Show Music Widget",
            description = "Display active media controls and playback on home screen",
            visible = showMusic,
            onVisibleChange = {
                showMusic = it
                userManager.saveShowMusicWidget(it)
            }
        )

        Spacer(modifier = Modifier.height(20.dp))

        ModernCard {
            Text(
                text = "Live Preview",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(14.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color.White.copy(alpha = 0.03f))
                    .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(14.dp))
                    .padding(14.dp),
                contentAlignment = Alignment.Center
            ) {
                MusicWidget(
                    style = musicStyle,
                    font = musicFont,
                    previewTrack = previewTrack
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            WidgetStyleSelector(
                title = "Music Style",
                styles = MusicStyle.entries,
                selectedStyle = musicStyle,
                onStyleSelected = {
                    musicStyle = it
                    userManager.saveMusicStyle(it)
                },
                getLabel = { it.name }
            )

            WidgetFontSelector(
                selectedFont = musicFont,
                onFontSelected = {
                    musicFont = it
                    userManager.saveMusicFont(it)
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(WidgetSettingsAccent.copy(alpha = 0.12f))
                    .border(1.dp, WidgetSettingsAccent.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                    .clickable {
                        context.startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
                    }
                    .padding(horizontal = 14.dp, vertical = 11.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.NotificationsActive,
                        contentDescription = null,
                        tint = WidgetSettingsAccent,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Notification Access (Media Sync)",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Icon(
                    imageVector = Icons.Rounded.ChevronRight,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.5f)
                )
            }
        }
    }
}
