package com.codershubinc.nullvoidlauncher.ui.widgets.clock

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun VerticalClock(timeText: String, batteryLevel: Int, batteryStatus: String) {
    Column(horizontalAlignment = Alignment.Start) {
        Text(
            text = timeText,
            color = Color.White,
            fontSize = 64.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp
        )
        if (batteryLevel != -1) {
            val statusDisplay = if (batteryStatus.isNotEmpty() && batteryStatus != "UNKNOWN") " $batteryStatus" else ""
            Text(
                text = "LVL $batteryLevel%$statusDisplay",
                color = Color.DarkGray,
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.sp,
            )
        }
    }
}
