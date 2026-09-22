package com.codershubinc.nullvoidlauncher.ui.widgets.clock

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BatteryChargingFull
import androidx.compose.material.icons.rounded.BatteryStd
import androidx.compose.material.icons.rounded.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codershubinc.nullvoidlauncher.data.StorageStyle
import com.codershubinc.nullvoidlauncher.data.UserManager
import com.codershubinc.nullvoidlauncher.data.WidgetFont
import com.codershubinc.nullvoidlauncher.ui.power.PowerHelper
import com.codershubinc.nullvoidlauncher.ui.power.PowerInfoState
import com.codershubinc.nullvoidlauncher.ui.widgets.DayWidget
import com.codershubinc.nullvoidlauncher.ui.widgets.PowerWidget
import com.codershubinc.nullvoidlauncher.ui.widgets.StorageWidget

@Composable
fun ElegantClock(
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
    val showPower = userManager.getShowPowerWidget()
    val powerStyle = userManager.getPowerStyle()
    val powerFont = userManager.getPowerFont()
    val storageStyle = userManager.getStorageStyle()
    val storageFont = userManager.getStorageFont()
    val isCharging = batteryStatus == "Charging"

    Column(
        modifier = modifier
            .padding(start = 5.dp, top = 20.dp),
        horizontalAlignment = Alignment.Start
    ) {
        // Date
        Text(
            text = "$monthName $dayOfMonth".uppercase(),
            color = Color.White.copy(alpha = 0.65f),
            fontSize = 17.sp,
            fontFamily = font.toFontFamily(),
            letterSpacing = 2.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Day Widget
        DayWidget(dayText = dayText, style = dayStyle)

        Spacer(modifier = Modifier.height(18.dp))

        // Clean Modern Environmental & Status Badges
        Column(
            modifier = Modifier.padding(start = 0.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val showWeather = userManager.getShowWeatherWidget()
            if (showWeather) {
                com.codershubinc.nullvoidlauncher.ui.widgets.WeatherWidget(
                    style = userManager.getWeatherStyle(),
                    font = userManager.getWeatherFont(),
                    onClick = onLongClick
                )
            }

            // Power / Battery Integrated Telemetry Widget (Configurable Variants)
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

        val showStorage = userManager.getShowStorageWidget()
        if (showStorage) {
            Spacer(modifier = Modifier.height(14.dp))
            StorageWidget(
                modifier = Modifier.padding(start = 2.dp),
                style = storageStyle,
                font = storageFont,
                onLongClick = onLongClick
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
