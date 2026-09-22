package com.codershubinc.nullvoidlauncher.ui.widgets.power

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BatteryChargingFull
import androidx.compose.material.icons.rounded.BatteryStd
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codershubinc.nullvoidlauncher.data.WidgetFont
import com.codershubinc.nullvoidlauncher.ui.power.PowerHelper
import com.codershubinc.nullvoidlauncher.ui.power.PowerInfoState

/**
 * GaugeBarPowerWidget — Modern linear progress micro-gauge battery telemetry.
 * Designed to integrate seamlessly alongside or beneath clock/date/day widgets.
 */
@Composable
fun GaugeBarPowerWidget(
    powerInfo: PowerInfoState,
    modifier: Modifier = Modifier,
    font: WidgetFont = WidgetFont.DEFAULT,
    onTap: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null,
    onClick: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val isCharging = powerInfo.isCharging
    val level = powerInfo.level
    val levelColor = when {
        isCharging -> Color(0xFF00E676)
        level <= 15 -> Color(0xFFFF5252)
        level <= 30 -> Color(0xFFFFB300)
        else -> Color(0xFF00E5FF)
    }

    val shape = RoundedCornerShape(12.dp)

    Row(
        modifier = modifier
            .wrapContentWidth()
            .clip(shape)
            .background(Color.White.copy(alpha = 0.06f))
            .border(1.dp, Color.White.copy(alpha = 0.11f), shape)
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
            .padding(horizontal = 9.dp, vertical = 4.5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(
            imageVector = if (isCharging) Icons.Rounded.BatteryChargingFull else Icons.Rounded.BatteryStd,
            contentDescription = null,
            tint = levelColor,
            modifier = Modifier.size(13.dp)
        )

        Text(
            text = "${powerInfo.level}%",
            color = Color.White,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = font.toFontFamily()
        )

        // Micro linear gauge bar
        Box(
            modifier = Modifier
                .width(44.dp)
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(Color.White.copy(alpha = 0.15f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(fraction = (level / 100f).coerceIn(0.06f, 1f))
                    .clip(RoundedCornerShape(2.dp))
                    .background(levelColor)
            )
        }

        if (isCharging) {
            Text(
                text = "CHARGING",
                color = Color(0xFF00E676),
                fontSize = 9.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = font.toFontFamily(),
                letterSpacing = 0.5.sp
            )
        }
    }
}
