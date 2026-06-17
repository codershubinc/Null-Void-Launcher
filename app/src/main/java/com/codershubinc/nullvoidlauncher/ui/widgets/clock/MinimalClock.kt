package com.codershubinc.nullvoidlauncher.ui.widgets.clock

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MinimalClock(timeText: String, batteryLevel: Int, batteryStatus: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = timeText,
            color = Color.White,
            fontSize = 72.sp,
            fontFamily = FontFamily.Monospace,
            letterSpacing = 4.sp,
        )
        if (batteryLevel != -1) {
            Spacer(modifier = Modifier.height(8.dp))
            val statusDisplay =
                if (batteryStatus.isNotEmpty() && batteryStatus != "UNKNOWN") " [$batteryStatus]" else ""
            Text(
                text = "BAT $batteryLevel%$statusDisplay",
                color = Color.DarkGray,
                fontSize = 16.sp,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 2.sp
            )
        }
    }
}
