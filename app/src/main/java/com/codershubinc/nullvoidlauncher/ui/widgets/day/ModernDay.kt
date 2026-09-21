package com.codershubinc.nullvoidlauncher.ui.widgets.day

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.codershubinc.nullvoidlauncher.data.WidgetFont
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * ModernDay — Glassmorphic cyberpunk badge with bold, futuristic typography.
 */
@Composable
fun ModernDay(
    dayText: String,
    modifier: Modifier = Modifier,
    font: WidgetFont = WidgetFont.SANS_SERIF
) {
    val shape = RoundedCornerShape(12.dp)

    Box(
        modifier = modifier
            .clip(shape)
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xFF3D5AFE).copy(alpha = 0.22f),
                        Color.White.copy(alpha = 0.06f)
                    )
                )
            )
            .border(
                1.dp,
                Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xFF3D5AFE).copy(alpha = 0.5f),
                        Color.White.copy(alpha = 0.12f)
                    )
                ),
                shape
            )
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Column {
            Text(
                text = "TODAY",
                color = Color(0xFF3D5AFE),
                fontSize = 10.sp,
                fontFamily = font.toFontFamily(),
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )
            Text(
                text = dayText.uppercase(),
                color = Color.White,
                fontSize = 32.sp,
                fontFamily = font.toFontFamily(),
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp
            )
        }
    }
}
