package com.codershubinc.nullvoidlauncher.ui.widgets.day

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.layout
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import com.codershubinc.nullvoidlauncher.data.WidgetFont
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp

/**
 * ElegantDay — Signature vertical rotated serif day name.
 * Two-tone: first half gold filled, second half white outline stroke.
 */
@Composable
fun ElegantDay(
    dayText: String,
    modifier: Modifier = Modifier,
    font: WidgetFont = WidgetFont.SANS_SERIF
) {
    val monPart = if (dayText.length > 3) dayText.dropLast(3).uppercase() else dayText.take(1).uppercase()
    val dayPart = if (dayText.length > 3) dayText.takeLast(3).uppercase() else dayText.drop(1).uppercase()

    Box(
        modifier = modifier
            .layout { measurable, constraints ->
                val placeable = measurable.measure(constraints)
                layout(placeable.height, placeable.width) {
                    placeable.placeWithLayer(
                        x = (placeable.height - placeable.width) / 2,
                        y = (placeable.width - placeable.height) / 2
                    ) {
                        rotationZ = -90f
                    }
                }
            }
    ) {
        // Outline part (DAY)
        Text(
            text = buildAnnotatedString {
                withStyle(SpanStyle(color = Color.Transparent)) {
                    append(monPart)
                }
                append(dayPart)
            },
            color = Color.White,
            fontSize = 62.sp,
            fontWeight = FontWeight.Black,
            fontFamily = font.toFontFamily(),
            letterSpacing = 1.sp,
            style = TextStyle(
                drawStyle = Stroke(miter = 10f, width = 1.8f)
            )
        )
        // Filled part (MON)
        Text(
            text = buildAnnotatedString {
                append(monPart)
                withStyle(SpanStyle(color = Color.Transparent)) {
                    append(dayPart)
                }
            },
            color = Color(0xFFC5A35E),
            fontSize = 62.sp,
            fontWeight = FontWeight.Black,
            fontFamily = font.toFontFamily(),
            letterSpacing = 1.sp
        )
    }
}
