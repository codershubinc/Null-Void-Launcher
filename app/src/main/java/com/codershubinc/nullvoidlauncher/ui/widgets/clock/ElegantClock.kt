package com.codershubinc.nullvoidlauncher.ui.widgets.clock

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.layout.layout
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import com.codershubinc.nullvoidlauncher.data.StorageStyle
import com.codershubinc.nullvoidlauncher.ui.widgets.StorageWidget
import java.util.Vector

@Composable
fun ElegantClock(
    timeText: String,
    dayText: String,
    monthName: String,
    dayOfMonth: String,
    batteryLevel: Int,
    batteryStatus: String
) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .padding(start = 16.dp, top = 40.dp),
        horizontalAlignment = Alignment.Start
    ) {
        // Date
        Text(
            text = "$monthName $dayOfMonth".uppercase(),
            color = Color.White.copy(alpha = 0.6f),
            fontSize = 18.sp,
            fontFamily = FontFamily.SansSerif,
            letterSpacing = 2.sp,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Large day
        val day = dayText.uppercase()
        val monPart = dayText.dropLast(3).uppercase()
        val dayPart = dayText.takeLast(3).uppercase()

        Box(
            modifier = Modifier
                .padding(start = 0.dp)
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
                fontSize = 64.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Serif,
                style = TextStyle(
                    drawStyle = Stroke(miter = 10f, width = 2f)
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
                fontSize = 64.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Serif
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Details text
        Column(modifier = Modifier.padding(start = 0.dp)) {
            Text(
                text = "Humidity is 50% with wind speed 32km/h in your locality.",
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 10.sp,
                lineHeight = 14.sp
            )
            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(color = Color.White.copy(alpha = 0.5f))) {
                        append("Battery Level is ")
                    }
                    withStyle(style = SpanStyle(color = Color.White, fontSize = 20.sp)) {
                        append("$batteryLevel%")
                    }
                    withStyle(style = SpanStyle(color = Color.White.copy(alpha = 0.5f))) {
                        append(" status ")
                    }
                    withStyle(style = SpanStyle(color = Color.White, fontSize = 20.sp)) {
                        append("$batteryStatus.")
                    }
                },
                fontSize = 10.sp,
                lineHeight = 14.sp
            )
            Text(
                text = "Your upcoming event is Live, Joy!",
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 10.sp,
                lineHeight = 14.sp
            )
        }
        Spacer(modifier = Modifier.height(12.dp))

        // Storage Info
        StorageWidget(modifier = Modifier.padding(start = 2.dp) , StorageStyle.ELEGANT)
    

        Spacer(modifier = Modifier.height(16.dp))

        // Horizontal line
        Box(
            modifier = Modifier
                .width(100.dp)
                .height(1.dp)
                .background(Color.White.copy(alpha = 0.3f))
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Time with icon
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.AccessTime,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = timeText,
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
fun ElegantClockPreview() {
    ElegantClock(
        timeText = "10:30 PM",
        dayText = "MONDAY",
        monthName = "OCTOBER",
        dayOfMonth = "24",
        batteryLevel = 85,
        batteryStatus = "Discharging"
    )
}
