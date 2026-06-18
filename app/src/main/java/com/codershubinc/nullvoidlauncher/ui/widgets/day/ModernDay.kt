package com.codershubinc.nullvoidlauncher.ui.widgets.day

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Composable
fun ModernDay(dayText: String) {
    Text(
        text = "IT'S $dayText",
        color = Color.White,
        fontSize = 54.sp,
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Black,
        letterSpacing = (-2).sp
    )
}
