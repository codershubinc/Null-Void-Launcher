package com.codershubinc.nullvoidlauncher.ui.widgets.clock

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PixelClock(dayOfMonth: String, monthName: String, weekName: String, year: String) {
    Column(
        horizontalAlignment = Alignment.Start,
        modifier = Modifier.padding(start = 10.dp)
    ) {
        Text(
            text = dayOfMonth,
            color = Color.White,
            fontSize = 180.sp,
            fontWeight = FontWeight.Black,
            lineHeight = 160.sp,
            fontFamily = FontFamily.SansSerif
        )
        Row(
            modifier = Modifier
                .offset(x = 10.dp, y = (-20).dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = monthName,
                color = Color.White,
                fontSize = 56.sp,
                fontWeight = FontWeight.Light,
                fontFamily = FontFamily.SansSerif
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = year,
                color = Color.Gray,
                fontSize = 24.sp,
                fontWeight = FontWeight.Light,
                fontFamily = FontFamily.SansSerif,
                modifier = Modifier.rotate(270f)
            )

        }
        Text(
            text = weekName,
            color = Color(0xFF7E7E7E),
            fontSize = 32.sp,
            fontWeight = FontWeight.Light,
            fontFamily = FontFamily.SansSerif,
            modifier = Modifier
                .offset(x = 10.dp ),
        )
    }
}
