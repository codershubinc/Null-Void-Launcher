package com.codershubinc.nullvoidlauncher.ui.widgets.clock

import androidx.compose.animation.core.*
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
import com.codershubinc.nullvoidlauncher.data.model.StorageStyle
import com.codershubinc.nullvoidlauncher.data.UserManager
import com.codershubinc.nullvoidlauncher.data.prefs.rememberPrefVersion
import com.codershubinc.nullvoidlauncher.ui.power.PowerHelper
import com.codershubinc.nullvoidlauncher.ui.power.PowerInfoState
import com.codershubinc.nullvoidlauncher.ui.widgets.DayWidget
import com.codershubinc.nullvoidlauncher.ui.widgets.PowerWidget
import com.codershubinc.nullvoidlauncher.ui.widgets.StorageWidget

import com.codershubinc.nullvoidlauncher.data.WidgetFont

/**
 * TerminalClock — Hacker / CLI terminal prompt aesthetic.
 * Features monospace prompt syntax, animated blinking cursor, and green accent highlights.
 */
@Composable
fun TerminalClock(
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
    val prefVersion = rememberPrefVersion(userManager)

    val showDay = remember(prefVersion) { userManager.widgets.showDay }
    val dayStyle = remember(prefVersion) { userManager.widgets.dayStyle }
    val showStorage = remember(prefVersion) { userManager.widgets.showStorage }
    val storageStyle = remember(prefVersion) { userManager.widgets.storageStyle }
    val storageFont = remember(prefVersion) { userManager.widgets.storageFont }
    val showPower = remember(prefVersion) { userManager.widgets.showPower }
    val powerStyle = remember(prefVersion) { userManager.widgets.powerStyle }
    val powerFont = remember(prefVersion) { userManager.widgets.powerFont }
    val showSteps = remember(prefVersion) { userManager.widgets.showSteps }
    val stepsStyle = remember(prefVersion) { userManager.widgets.stepsStyle }
    val stepsFont = remember(prefVersion) { userManager.widgets.stepsFont }
    val isCharging = batteryStatus.equals("CHARGING", ignoreCase = true) || batteryStatus.equals("Charging", ignoreCase = true)

    val terminalGreen = Color(0xFF00FF66)
    val terminalDim = Color(0xFF00B344)

    val infiniteTransition = rememberInfiniteTransition(label = "cursor_blink")
    val cursorAlpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "cursor"
    )

    Column(
        modifier = modifier.padding(start = 6.dp, top = 20.dp),
        horizontalAlignment = Alignment.Start
    ) {
        // Terminal Window Card
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFF060B08))
                .border(1.dp, terminalGreen.copy(alpha = 0.35f), RoundedCornerShape(10.dp))
                .padding(14.dp)
        ) {
            // Prompt header
            Text(
                text = "null@launcher:~$ uptime",
                color = terminalDim,
                fontSize = 11.sp,
                fontFamily = font.toFontFamily(),
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Time with blinking cursor
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "> $timeText",
                    color = terminalGreen,
                    fontSize = 38.sp,
                    fontFamily = font.toFontFamily(),
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "█",
                    color = terminalGreen.copy(alpha = cursorAlpha),
                    fontSize = 32.sp,
                    fontFamily = font.toFontFamily(),
                    modifier = Modifier.padding(start = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Date & battery system line
            Text(
                text = "DATE: ${dayText.take(3)} $dayOfMonth ${monthName.take(3).uppercase()}",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 11.sp,
                fontFamily = font.toFontFamily(),
                fontWeight = FontWeight.SemiBold
            )

            if (showPower) {
                Spacer(modifier = Modifier.height(6.dp))
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

        if (showSteps) {
            Spacer(modifier = Modifier.height(10.dp))
            com.codershubinc.nullvoidlauncher.ui.widgets.StepsWidget(
                modifier = Modifier.padding(start = 2.dp),
                style = stepsStyle,
                font = stepsFont,
                onLongClick = {
                    if (onOpenWidgetTweaks != null) onOpenWidgetTweaks(com.codershubinc.nullvoidlauncher.ui.settings.WidgetSubPage.STEPS)
                    else onLongClick?.invoke()
                }
            )
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
