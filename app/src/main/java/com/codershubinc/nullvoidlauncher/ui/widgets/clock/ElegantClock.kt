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
import androidx.compose.runtime.*
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
    onLongClick: (() -> Unit)? = null,
    onOpenWidgetTweaks: ((com.codershubinc.nullvoidlauncher.ui.settings.WidgetSubPage) -> Unit)? = null
) {
    val context = LocalContext.current
    val userManager = remember { UserManager(context) }
    var prefVersion by remember { mutableIntStateOf(0) }

    DisposableEffect(userManager) {
        val listener = android.content.SharedPreferences.OnSharedPreferenceChangeListener { _, _ ->
            prefVersion++
        }
        userManager.prefs.registerOnSharedPreferenceChangeListener(listener)
        onDispose {
            userManager.prefs.unregisterOnSharedPreferenceChangeListener(listener)
        }
    }

    val showDay = remember(prefVersion) { userManager.getShowDayWidget() }
    val dayStyle = remember(prefVersion) { userManager.getDayStyle() }
    val showPower = remember(prefVersion) { userManager.getShowPowerWidget() }
    val powerStyle = remember(prefVersion) { userManager.getPowerStyle() }
    val powerFont = remember(prefVersion) { userManager.getPowerFont() }
    val showStorage = remember(prefVersion) { userManager.getShowStorageWidget() }
    val storageStyle = remember(prefVersion) { userManager.getStorageStyle() }
    val storageFont = remember(prefVersion) { userManager.getStorageFont() }
    val showWeather = remember(prefVersion) { userManager.getShowWeatherWidget() }
    val weatherStyle = remember(prefVersion) { userManager.getWeatherStyle() }
    val weatherFont = remember(prefVersion) { userManager.getWeatherFont() }
    val showSteps = remember(prefVersion) { userManager.getShowStepsWidget() }
    val stepsStyle = remember(prefVersion) { userManager.getStepsStyle() }
    val stepsFont = remember(prefVersion) { userManager.getStepsFont() }
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

        if (showDay) {
            Spacer(modifier = Modifier.height(14.dp))
            // Day Widget
            DayWidget(
                dayText = dayText,
                style = dayStyle,
                onLongClick = {
                    if (onOpenWidgetTweaks != null) onOpenWidgetTweaks(com.codershubinc.nullvoidlauncher.ui.settings.WidgetSubPage.DAY)
                    else onLongClick?.invoke()
                }
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Clean Modern Environmental & Status Badges
        Column(
            modifier = Modifier.padding(start = 0.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (showWeather) {
                com.codershubinc.nullvoidlauncher.ui.widgets.WeatherWidget(
                    style = weatherStyle,
                    font = weatherFont,
                    onLongClick = {
                        if (onOpenWidgetTweaks != null) onOpenWidgetTweaks(com.codershubinc.nullvoidlauncher.ui.settings.WidgetSubPage.WEATHER)
                        else onLongClick?.invoke()
                    }
                )
            }

            // Steps / Google Fit Widget
            if (showSteps) {
                com.codershubinc.nullvoidlauncher.ui.widgets.StepsWidget(
                    style = stepsStyle,
                    font = stepsFont,
                    onLongClick = {
                        if (onOpenWidgetTweaks != null) onOpenWidgetTweaks(com.codershubinc.nullvoidlauncher.ui.settings.WidgetSubPage.STEPS)
                        else onLongClick?.invoke()
                    }
                )
            }

            // Power / Battery Integrated Telemetry Widget (Configurable Variants)
            if (showPower) {
                PowerWidget(
                    style = powerStyle,
                    font = powerFont,
                    previewInfo = PowerInfoState(level = batteryLevel, status = batteryStatus, isCharging = isCharging),
                    onTap = { PowerHelper.openBatterySettings(context) },
                    onLongClick = {
                        if (onOpenWidgetTweaks != null) onOpenWidgetTweaks(com.codershubinc.nullvoidlauncher.ui.settings.WidgetSubPage.POWER)
                        else onLongClick?.invoke()
                    }
                )
            }
        }

        if (showStorage) {
            Spacer(modifier = Modifier.height(14.dp))
            StorageWidget(
                modifier = Modifier.padding(start = 2.dp),
                style = storageStyle,
                font = storageFont,
                onLongClick = {
                    if (onOpenWidgetTweaks != null) onOpenWidgetTweaks(com.codershubinc.nullvoidlauncher.ui.settings.WidgetSubPage.STORAGE)
                    else onLongClick?.invoke()
                }
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
