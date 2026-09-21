package com.codershubinc.nullvoidlauncher.ui.widgets.clock

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codershubinc.nullvoidlauncher.data.StorageStyle
import com.codershubinc.nullvoidlauncher.data.UserManager
import com.codershubinc.nullvoidlauncher.ui.power.PowerHelper
import com.codershubinc.nullvoidlauncher.ui.power.PowerInfoState
import com.codershubinc.nullvoidlauncher.ui.widgets.DayWidget
import com.codershubinc.nullvoidlauncher.ui.widgets.PowerWidget
import com.codershubinc.nullvoidlauncher.ui.widgets.StorageWidget

import com.codershubinc.nullvoidlauncher.data.WidgetFont

/**
 * ModernClock — Futuristic stacked typography clock.
 * Displays hours and minutes stacked with an accent neon bar and system stats.
 */
@Composable
fun ModernClock(
    timeText: String,
    dayText: String,
    monthName: String,
    dayOfMonth: String,
    batteryLevel: Int,
    batteryStatus: String,
    modifier: Modifier = Modifier,
    font: WidgetFont = WidgetFont.DEFAULT,
    onLongClick: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val userManager = remember { UserManager(context) }
    val dayStyle = userManager.getDayStyle()
    val showStorage = userManager.getShowStorageWidget()
    val storageStyle = userManager.getStorageStyle()
    val storageFont = userManager.getStorageFont()
    val showPower = userManager.getShowPowerWidget()
    val powerStyle = userManager.getPowerStyle()
    val powerFont = userManager.getPowerFont()
    val isCharging = batteryStatus.equals("CHARGING", ignoreCase = true) || batteryStatus.equals("Charging", ignoreCase = true)

    val parts = timeText.split(":")
    val hour = parts.getOrNull(0) ?: "12"
    val minute = parts.getOrNull(1) ?: "00"

    val accent = Color(0xFF3D5AFE)
    val cyan = Color(0xFF00E5FF)

    Column(
        modifier = modifier.padding(start = 6.dp, top = 20.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Neon accent vertical bar
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(84.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Brush.verticalGradient(listOf(accent, cyan)))
            )

            // Stacked Hours and Minutes
            Column {
                Text(
                    text = hour,
                    color = Color.White,
                    fontSize = 42.sp,
                    fontFamily = font.toFontFamily(),
                    fontWeight = FontWeight.Black,
                    lineHeight = 42.sp
                )
                Text(
                    text = minute,
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 42.sp,
                    fontFamily = font.toFontFamily(),
                    fontWeight = FontWeight.Black,
                    lineHeight = 42.sp
                )
            }

            // Date and Day details alongside
            Column(
                modifier = Modifier.padding(start = 4.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                DayWidget(dayText = dayText, style = dayStyle)

                Text(
                    text = "$monthName $dayOfMonth".uppercase(),
                    color = cyan,
                    fontSize = 11.sp,
                    fontFamily = font.toFontFamily(),
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                if (showPower) {
                    PowerWidget(
                        style = powerStyle,
                        font = powerFont,
                        previewInfo = PowerInfoState(level = batteryLevel, status = batteryStatus, isCharging = isCharging),
                        onTap = { PowerHelper.openBatterySettings(context) },
                        onLongClick = onLongClick
                    )
                }
            }
        }

        if (showStorage) {
            Spacer(modifier = Modifier.height(16.dp))
            StorageWidget(
                modifier = Modifier.padding(start = 2.dp),
                style = storageStyle,
                font = storageFont,
                onLongClick = onLongClick
            )
        }
    }
}
