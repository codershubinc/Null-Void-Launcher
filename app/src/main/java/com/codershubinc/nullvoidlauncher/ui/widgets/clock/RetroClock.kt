package com.codershubinc.nullvoidlauncher.ui.widgets.clock

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
 * RetroClock — Cassette & vintage digital LED aesthetic.
 * Amber hues, segmented borders, and monospace font styling.
 */
@Composable
fun RetroClock(
    timeText: String,
    dayText: String,
    monthName: String,
    dayOfMonth: String,
    batteryLevel: Int,
    batteryStatus: String,
    modifier: Modifier = Modifier,
    font: WidgetFont = WidgetFont.MONOSPACE,
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
    val isCharging = batteryStatus.equals("CHARGING", ignoreCase = true) || batteryStatus.equals("Charging", ignoreCase = true)
    val amber = Color(0xFFC5A35E)
    val darkAmber = Color(0xFF261D12)

    Column(
        modifier = modifier.padding(start = 6.dp, top = 20.dp),
        horizontalAlignment = Alignment.Start
    ) {
        // Digital display card
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(darkAmber)
                .border(1.5.dp, amber.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                .padding(horizontal = 14.dp, vertical = 8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = timeText,
                    color = amber,
                    fontSize = 44.sp,
                    fontFamily = font.toFontFamily(),
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )

                Column(horizontalAlignment = Alignment.End) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(amber.copy(alpha = 0.2f))
                            .padding(horizontal = 5.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "SEC // REC",
                            color = amber,
                            fontSize = 8.sp,
                            fontFamily = font.toFontFamily(),
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "BAT $batteryLevel%",
                        color = amber.copy(alpha = 0.7f),
                        fontSize = 10.sp,
                        fontFamily = font.toFontFamily(),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        if (showDay) {
            Spacer(modifier = Modifier.height(10.dp))
            DayWidget(
                dayText = dayText,
                style = dayStyle,
                onLongClick = {
                    if (onOpenWidgetTweaks != null) onOpenWidgetTweaks(com.codershubinc.nullvoidlauncher.ui.settings.WidgetSubPage.DAY)
                    else onLongClick?.invoke()
                }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "$dayOfMonth ${monthName.take(3).uppercase()} // SYSTEM READY",
            color = amber.copy(alpha = 0.6f),
            fontSize = 11.sp,
            fontFamily = font.toFontFamily(),
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.sp
        )

        if (showPower) {
            Spacer(modifier = Modifier.height(8.dp))
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
