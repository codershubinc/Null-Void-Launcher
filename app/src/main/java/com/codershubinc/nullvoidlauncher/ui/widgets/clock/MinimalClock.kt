package com.codershubinc.nullvoidlauncher.ui.widgets.clock

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
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
 * MinimalClock — Ultra-clean typography and sleek minimalist layout.
 * Features large airy time numerals, dynamic day widget, date, and slim battery pill.
 */
@Composable
fun MinimalClock(
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
    val isCharging = batteryStatus.equals("CHARGING", ignoreCase = true)

    Column(
        modifier = modifier.padding(start = 6.dp, top = 20.dp),
        horizontalAlignment = Alignment.Start
    ) {
        // Large minimalist time
        Text(
            text = timeText,
            color = Color.White,
            fontSize = 58.sp,
            fontFamily = font.toFontFamily(),
            fontWeight = FontWeight.Light,
            letterSpacing = (-1).sp
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Day of week widget
        DayWidget(dayText = dayText, style = dayStyle)

        Spacer(modifier = Modifier.height(14.dp))

        // Date and Battery pill row
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "$monthName $dayOfMonth".uppercase(),
                color = Color.White.copy(alpha = 0.55f),
                fontSize = 13.sp,
                fontFamily = font.toFontFamily(),
                fontWeight = FontWeight.Medium,
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

        if (showStorage) {
            Spacer(modifier = Modifier.height(12.dp))
            StorageWidget(
                modifier = Modifier.padding(start = 2.dp),
                style = storageStyle,
                font = storageFont,
                onLongClick = onLongClick
            )
        }
    }
}
