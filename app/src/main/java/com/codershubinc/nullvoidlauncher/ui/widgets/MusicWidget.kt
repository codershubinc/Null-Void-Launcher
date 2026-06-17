package com.codershubinc.nullvoidlauncher.ui.widgets

import androidx.compose.runtime.Composable
import com.codershubinc.nullvoidlauncher.data.ClockStyle
import com.codershubinc.nullvoidlauncher.ui.widgets.music.StandardMusicWidget

@Composable
fun MusicWidget(style: ClockStyle = ClockStyle.MINIMAL) {
    StandardMusicWidget(style)
}
