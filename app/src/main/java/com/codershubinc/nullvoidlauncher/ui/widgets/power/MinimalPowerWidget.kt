package com.codershubinc.nullvoidlauncher.ui.widgets.power

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BatteryStd
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
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
 * MinimalPowerWidget — Ultra-compact horizontal pill for battery telemetry
 * with smooth animated charging transitions and pulse indicator.
 */
@Composable
fun MinimalPowerWidget(
    powerInfo: PowerInfoState,
    modifier: Modifier = Modifier,
    font: WidgetFont = WidgetFont.DEFAULT,
    onTap: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null,
    onClick: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val shape = RoundedCornerShape(12.dp)
    val isCharging = powerInfo.isCharging
    val level = powerInfo.level

    val targetLevelColor = when {
        isCharging -> Color(0xFF00E676)
        level <= 15 -> Color(0xFFFF5252)
        level <= 30 -> Color(0xFFFFB300)
        else -> Color(0xFF3D5AFE)
    }

    val animatedLevelColor by animateColorAsState(
        targetValue = targetLevelColor,
        animationSpec = tween(500),
        label = "minimalBatteryLevelColor"
    )

    val targetBorderColor = if (isCharging) Color(0xFF00E676).copy(alpha = 0.35f) else Color.White.copy(alpha = 0.11f)
    val animatedBorderColor by animateColorAsState(targetValue = targetBorderColor, animationSpec = tween(500), label = "minBorder")

    Row(
        modifier = modifier
            .wrapContentWidth()
            .clip(shape)
            .background(if (isCharging) Color(0xFF00E676).copy(alpha = 0.08f) else Color.White.copy(alpha = 0.06f))
            .border(1.dp, animatedBorderColor, shape)
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
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        AnimatedContent(
            targetState = isCharging,
            transitionSpec = {
                (scaleIn(tween(400)) + fadeIn()) togetherWith (scaleOut(tween(400)) + fadeOut())
            },
            label = "minimalIconTransition"
        ) { charging ->
            Icon(
                imageVector = if (charging) Icons.Rounded.Bolt else Icons.Rounded.BatteryStd,
                contentDescription = null,
                tint = animatedLevelColor,
                modifier = Modifier.size(12.dp)
            )
        }

        Text(
            text = "${powerInfo.level}%",
            color = Color.White,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = font.toFontFamily()
        )

        AnimatedVisibility(
            visible = isCharging,
            enter = fadeIn(tween(400)) + expandHorizontally(tween(400)),
            exit = fadeOut(tween(300)) + shrinkHorizontally(tween(300))
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "•",
                    color = Color.White.copy(alpha = 0.35f),
                    fontSize = 9.sp
                )
                Text(
                    text = "CHARGING",
                    color = Color(0xFF00E676),
                    fontSize = 9.5.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = font.toFontFamily(),
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}
