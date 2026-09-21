package com.codershubinc.nullvoidlauncher.ui.widgets.day

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.codershubinc.nullvoidlauncher.data.WidgetFont
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * MinimalDay — Ultra-clean horizontal typography with tracked lowercase letters
 * and a sleek accent indicator.
 */
@Composable
fun MinimalDay(
    dayText: String,
    modifier: Modifier = Modifier,
    font: WidgetFont = WidgetFont.SANS_SERIF
) {
    Column(modifier = modifier) {
        Text(
            text = dayText.lowercase(),
            color = Color.White,
            fontSize = 44.sp,
            fontWeight = FontWeight.Light,
            fontFamily = font.toFontFamily(),
            letterSpacing = 1.5.sp
        )
        Box(
            modifier = Modifier
                .padding(top = 4.dp)
                .width(28.dp)
                .height(2.dp)
                .background(Color(0xFF3D5AFE))
        )
    }
}
