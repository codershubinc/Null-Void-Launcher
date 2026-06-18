package com.codershubinc.nullvoidlauncher.ui.widgets.day

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ElegantDay(dayText: String) {
    Column(
        modifier = Modifier
            .rotate(-90f)
            .requiredHeight(250.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = dayText.dropLast(3).uppercase(),
                color = Color(0xFFC5A35E),
                fontSize = 64.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Serif
            )

            Text(
                text = dayText.takeLast(3).uppercase(),
                color = Color.White,
                fontSize = 64.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Serif,
                style = TextStyle(
                    drawStyle = Stroke(miter = 10f, width = 2f)
                )
            )
        }
    }
}
