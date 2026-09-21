package com.codershubinc.nullvoidlauncher.ui.widgets.power

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bolt
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codershubinc.nullvoidlauncher.data.WidgetFont
import com.codershubinc.nullvoidlauncher.ui.power.PowerHelper
import com.codershubinc.nullvoidlauncher.ui.power.PowerInfoState

/**
 * RetroPowerWidget — Amber vintage digital LED aesthetic for battery telemetry.
 * Integrates seamlessly alongside or beneath clock/date/day widgets.
 */
@Composable
fun RetroPowerWidget(
    powerInfo: PowerInfoState,
    modifier: Modifier = Modifier,
    font: WidgetFont = WidgetFont.MONOSPACE,
    onTap: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null,
    onClick: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val amber = Color(0xFFC5A35E)
    val darkAmber = Color(0xFF261D12)
    val shape = RoundedCornerShape(6.dp)
    val isCharging = powerInfo.isCharging

    Row(
        modifier = modifier
            .wrapContentWidth()
            .clip(shape)
            .background(darkAmber)
            .border(1.dp, amber.copy(alpha = 0.45f), shape)
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
            .padding(horizontal = 7.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(3.dp))
                .background(amber.copy(alpha = 0.2f))
                .padding(horizontal = 4.dp, vertical = 1.dp)
        ) {
            Text(
                text = "PWR",
                color = amber,
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = font.toFontFamily()
            )
        }

        Text(
            text = "${powerInfo.level}%",
            color = amber,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = font.toFontFamily()
        )

        if (isCharging) {
            Icon(
                imageVector = Icons.Rounded.Bolt,
                contentDescription = null,
                tint = amber,
                modifier = Modifier.size(11.dp)
            )
        }

        Text(
            text = powerInfo.status.uppercase(),
            color = amber.copy(alpha = 0.7f),
            fontSize = 9.sp,
            fontFamily = font.toFontFamily()
        )
    }
}
