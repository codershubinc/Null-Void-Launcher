package com.codershubinc.nullvoidlauncher.ui.widgets.day

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.codershubinc.nullvoidlauncher.data.WidgetFont
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Calendar

/**
 * BrutalistDay — Neo-brutalist stark contrast widget.
 * Features heavy borders, raw monospace typography, and hard offset block aesthetics.
 */
@Composable
fun BrutalistDay(
    dayText: String,
    modifier: Modifier = Modifier,
    font: WidgetFont = WidgetFont.MONOSPACE
) {
    val dayIndex = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)

    Box(modifier = modifier) {
        // Offset background block shadow
        Box(
            modifier = Modifier
                .offset(x = 4.dp, y = 4.dp)
                .matchParentSize()
                .background(Color(0xFFE5FF00))
        )

        // Front Card
        Row(
            modifier = Modifier
                .background(Color.Black)
                .border(2.dp, Color.White)
                .padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .background(Color.White)
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "D-0$dayIndex",
                    color = Color.Black,
                    fontSize = 11.sp,
                    fontFamily = font.toFontFamily(),
                    fontWeight = FontWeight.Black
                )
            }

            Text(
                text = dayText.uppercase(),
                color = Color.White,
                fontSize = 26.sp,
                fontFamily = font.toFontFamily(),
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp
            )
        }
    }
}
