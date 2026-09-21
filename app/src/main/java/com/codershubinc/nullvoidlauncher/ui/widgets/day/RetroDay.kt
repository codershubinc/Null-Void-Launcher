package com.codershubinc.nullvoidlauncher.ui.widgets.day

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import com.codershubinc.nullvoidlauncher.data.WidgetFont
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * RetroDay — Vintage cassette / amber terminal day indicator.
 * Displays a styled retro tag pill with REC indicator, day name in monospace,
 * and warm amber borders.
 */
@Composable
fun RetroDay(
    dayText: String,
    modifier: Modifier = Modifier,
    font: WidgetFont = WidgetFont.MONOSPACE
) {
    val amber = Color(0xFFC5A35E)
    val bg = Color(0xFF1A1208)
    val shape = RoundedCornerShape(8.dp)

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .clip(shape)
                .background(bg)
                .border(1.dp, amber.copy(alpha = 0.5f), shape)
                .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(amber)
            )
            Text(
                text = "DAY // TRACK",
                color = amber.copy(alpha = 0.85f),
                fontSize = 10.sp,
                fontFamily = font.toFontFamily(),
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }

        Text(
            text = dayText.uppercase(),
            color = Color.White,
            fontSize = 44.sp,
            fontWeight = FontWeight.Black,
            fontFamily = font.toFontFamily(),
            letterSpacing = 2.sp
        )
    }
}
