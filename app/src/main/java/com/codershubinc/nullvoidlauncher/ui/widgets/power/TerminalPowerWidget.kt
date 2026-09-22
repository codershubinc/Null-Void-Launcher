package com.codershubinc.nullvoidlauncher.ui.widgets.power

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codershubinc.nullvoidlauncher.data.WidgetFont
import com.codershubinc.nullvoidlauncher.ui.power.PowerHelper
import com.codershubinc.nullvoidlauncher.ui.power.PowerInfoState

/**
 * TerminalPowerWidget — Hacker / CLI terminal prompt aesthetic for battery telemetry.
 * Integrates seamlessly alongside or beneath clock/date/day widgets.
 */
@Composable
fun TerminalPowerWidget(
    powerInfo: PowerInfoState,
    modifier: Modifier = Modifier,
    font: WidgetFont = WidgetFont.MONOSPACE,
    onTap: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null,
    onClick: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val terminalGreen = Color(0xFF00FF66)
    val terminalDim = Color(0xFF00B344)
    val shape = RoundedCornerShape(6.dp)

    val isCharging = powerInfo.isCharging
    val level = powerInfo.level
    val statusColor = when {
        isCharging -> terminalGreen
        level <= 15 -> Color(0xFFFF5252)
        level <= 30 -> Color(0xFFFFB300)
        else -> terminalGreen
    }

    Row(
        modifier = modifier
            .wrapContentWidth()
            .clip(shape)
            .background(Color(0xFF060B08))
            .border(1.dp, terminalGreen.copy(alpha = 0.35f), shape)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        if (onTap != null) onTap()
                        else if (onClick != null) onClick()
                        else PowerHelper.openBatterySettings(context)
                    },
                    onLongPress = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onLongClick?.invoke()
                    }
                )
            }
            .padding(horizontal = 8.dp, vertical = 3.5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Text(
            text = "pwr0:",
            color = terminalDim,
            fontSize = 10.sp,
            fontFamily = font.toFontFamily(),
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "${powerInfo.level}%",
            color = statusColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = font.toFontFamily()
        )

        if (isCharging) {
            Text(
                text = "[CHR]",
                color = terminalGreen,
                fontSize = 9.sp,
                fontFamily = font.toFontFamily(),
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
