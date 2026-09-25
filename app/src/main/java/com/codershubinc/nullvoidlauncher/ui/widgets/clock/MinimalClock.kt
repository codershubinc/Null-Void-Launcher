package com.codershubinc.nullvoidlauncher.ui.widgets.clock

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
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
    val showStorage = remember(prefVersion) { userManager.getShowStorageWidget() }
    val storageStyle = remember(prefVersion) { userManager.getStorageStyle() }
    val storageFont = remember(prefVersion) { userManager.getStorageFont() }
    val showPower = remember(prefVersion) { userManager.getShowPowerWidget() }
    val powerStyle = remember(prefVersion) { userManager.getPowerStyle() }
    val powerFont = remember(prefVersion) { userManager.getPowerFont() }
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

        if (showDay) {
            Spacer(modifier = Modifier.height(6.dp))
            // Day of week widget
            DayWidget(
                dayText = dayText,
                style = dayStyle,
                onLongClick = {
                    if (onOpenWidgetTweaks != null) onOpenWidgetTweaks(com.codershubinc.nullvoidlauncher.ui.settings.WidgetSubPage.DAY)
                    else onLongClick?.invoke()
                }
            )
        }

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
                    onLongClick = {
                        if (onOpenWidgetTweaks != null) onOpenWidgetTweaks(com.codershubinc.nullvoidlauncher.ui.settings.WidgetSubPage.POWER)
                        else onLongClick?.invoke()
                    }
                )
            }
        }

        if (showStorage) {
            Spacer(modifier = Modifier.height(12.dp))
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
