package com.codershubinc.nullvoidlauncher.ui.widgets.power

import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
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
 * RingPowerWidget — Smartwatch-inspired circular ring meter telemetry.
 * Integrates seamlessly alongside or beneath clock/date/day widgets.
 */
@Composable
fun RingPowerWidget(
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
        else -> Color(0xFF3D5AFE)
    }

    val shape = RoundedCornerShape(14.dp)

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
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // Canvas Circular Ring Meter
        Box(
            modifier = Modifier.size(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val strokeWidth = 2.dp.toPx()
                // Track
                drawCircle(
                    color = Color.White.copy(alpha = 0.15f),
                    style = Stroke(strokeWidth)
                )
                // Battery sweep arc
                drawArc(
                    color = levelColor,
                    startAngle = -90f,
                    sweepAngle = (level / 100f) * 360f,
                    useCenter = false,
                    style = Stroke(strokeWidth, cap = StrokeCap.Round)
                )
            }
            if (isCharging) {
                Icon(
                    imageVector = Icons.Rounded.Bolt,
                    contentDescription = null,
                    tint = Color(0xFF00E676),
                    modifier = Modifier.size(9.dp)
                )
            }
        }

        Text(
            text = "${powerInfo.level}%",
            color = Color.White,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = font.toFontFamily()
        )

        if (isCharging) {
            Text(
                text = "• Charging",
                color = Color(0xFF00E676),
                fontSize = 10.sp,
                fontFamily = font.toFontFamily()
            )
        }
    }
}
