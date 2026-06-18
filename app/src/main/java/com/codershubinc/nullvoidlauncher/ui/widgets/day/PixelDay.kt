package com.codershubinc.nullvoidlauncher.ui.widgets.day

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Composable
fun PixelDay(dayText: String) {
    Text(
        text = dayText,
        color = Color(0xFF7E7E7E),
        fontSize = 32.sp,
        fontWeight = FontWeight.Light,
        fontFamily = FontFamily.SansSerif
    )
}
