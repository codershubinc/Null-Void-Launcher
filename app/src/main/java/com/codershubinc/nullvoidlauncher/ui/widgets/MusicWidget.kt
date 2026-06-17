package com.codershubinc.nullvoidlauncher.ui.widgets

import androidx.compose.runtime.Composable
import com.codershubinc.nullvoidlauncher.data.MusicStyle
import com.codershubinc.nullvoidlauncher.ui.widgets.music.FusedMusicWidget
import com.codershubinc.nullvoidlauncher.ui.widgets.music.ModernMusicWidget
import com.codershubinc.nullvoidlauncher.ui.widgets.music.StandardMusicWidget
import com.codershubinc.nullvoidlauncher.ui.widgets.music.ElegantMusicWidget

@Composable
fun MusicWidget(style: MusicStyle = MusicStyle.STANDARD) {
    when (style) {
        MusicStyle.STANDARD -> StandardMusicWidget()
        MusicStyle.MODERN -> ModernMusicWidget()
        MusicStyle.FUSED -> FusedMusicWidget()
        MusicStyle.ELEGANT -> ElegantMusicWidget()
    }
}
