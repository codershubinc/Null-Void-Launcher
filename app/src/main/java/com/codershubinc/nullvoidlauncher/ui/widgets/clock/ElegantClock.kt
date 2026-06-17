package com.codershubinc.nullvoidlauncher.ui.widgets.clock

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.TextStyle

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
            fontSize = 12.sp,
            fontFamily = FontFamily.SansSerif,
            letterSpacing = 2.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Large day
        Column(
            modifier = Modifier.padding(start = 0.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = dayText.dropLast(3).uppercase(),
                    color = Color(0xFFC5A35E),
                    fontSize = 64.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Serif
                )
                
                // Approximating outline with low alpha and a different color
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

        Spacer(modifier = Modifier.height(20.dp))

        // Details text
        Column(modifier = Modifier.padding(start = 10.dp)) {
            Text(
                text = "Humidity is 50% with wind speed 32km/h in your locality.",
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 10.sp,
                lineHeight = 14.sp
            )
            Text(
                text = "Battery Level is $batteryLevel% status $batteryStatus.",
                color = Color.White.copy(alpha = 0.5f),
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
            Box(
               modifier = Modifier
                   .size(16.dp)
                   .background(Color.White, shape = androidx.compose.foundation.shape.CircleShape),
               contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.size(10.dp)) {
                    drawLine(Color.Black, center, Offset(center.x, 2.dp.toPx()), strokeWidth = 1.dp.toPx())
                    drawLine(Color.Black, center, Offset(size.width - 2.dp.toPx(), center.y), strokeWidth = 1.dp.toPx())
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = timeText,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
