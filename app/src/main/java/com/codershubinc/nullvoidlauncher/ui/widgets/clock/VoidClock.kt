package com.codershubinc.nullvoidlauncher.ui.widgets.clock

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun VoidClock(timeText: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = timeText,
            color = Color.White,
            fontSize = 120.sp,
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.ExtraLight,
            letterSpacing = (-8).sp,
            modifier = Modifier.graphicsLayer { alpha = 0.8f }
        )
        Box(
            modifier = Modifier
                .width(200.dp)
                .height(1.dp)
                .background(Color.White.copy(alpha = 0.2f))
        )
        Text(
            text = "SYSTEM_VOID_ACTIVE",
            color = Color.White.copy(alpha = 0.4f),
            fontSize = 10.sp,
            fontFamily = FontFamily.Monospace,
            letterSpacing = 4.sp,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}
