package com.codershubinc.nullvoidlauncher.ui.widgets.clock

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp

@Composable
fun TerminalClock(timeText: String, batteryLevel: Int, batteryStatus: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "> TIME: $timeText",
            color = Color.White,
            fontSize = 48.sp,
            fontFamily = FontFamily.Monospace
        )
        if (batteryLevel != -1) {
            val statusDisplay =
                if (batteryStatus.isNotEmpty() && batteryStatus != "UNKNOWN") " ($batteryStatus)" else ""
            Text(
                text = "> BATT: $batteryLevel%$statusDisplay",
                color = Color.White,
                fontSize = 18.sp,
                fontFamily = FontFamily.Monospace,
            )
        }
        Text(
            text = "> STATUS: ONLINE",
            color = Color.White,
            fontSize = 18.sp,
            fontFamily = FontFamily.Monospace,
            textAlign = TextAlign.Start
        )
    }
}
