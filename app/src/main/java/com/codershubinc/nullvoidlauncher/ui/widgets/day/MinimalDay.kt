package com.codershubinc.nullvoidlauncher.ui.widgets.day

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Composable
fun MinimalDay(dayText: String) {
    Text(
        text = dayText.uppercase(),
        color = Color.White,
        fontSize = 24.sp,
        fontWeight = FontWeight.Medium,
        fontFamily = FontFamily.Monospace,
        letterSpacing = 4.sp
    )
}
