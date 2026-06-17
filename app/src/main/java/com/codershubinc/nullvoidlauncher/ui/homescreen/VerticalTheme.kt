package com.codershubinc.nullvoidlauncher.ui.homescreen

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.codershubinc.nullvoidlauncher.data.ClockStyle
import com.codershubinc.nullvoidlauncher.ui.widgets.ClockWidget
import com.codershubinc.nullvoidlauncher.ui.widgets.MusicWidget

@Composable
fun VerticalTheme() {
    val style = ClockStyle.VERTICAL
    Box(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        Box(modifier = Modifier
            .padding(top = 120.dp, start = 0.dp)
            .align(Alignment.TopStart)
        ) {
            ClockWidget(style)
        }

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            MusicWidget(style)
        }
    }
}
