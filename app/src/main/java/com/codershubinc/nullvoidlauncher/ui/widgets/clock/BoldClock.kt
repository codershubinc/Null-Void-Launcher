package com.codershubinc.nullvoidlauncher.ui.widgets.clock

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Composable
fun BoldClock(timeText: String, batteryLevel: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = timeText,
            color = Color.White,
            fontSize = 110.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.SansSerif
        )
        if (batteryLevel != -1) {
            Text(
                text = "$batteryLevel%",
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 24.sp,
                fontWeight = FontWeight.Light
            )
        }
    }
}
