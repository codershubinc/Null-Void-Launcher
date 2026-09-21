package com.codershubinc.nullvoidlauncher.ui.widgets.network

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codershubinc.nullvoidlauncher.data.UserManager
import com.codershubinc.nullvoidlauncher.data.WidgetFont
import com.codershubinc.nullvoidlauncher.ui.network.NetworkHelper
import com.codershubinc.nullvoidlauncher.ui.network.NetworkInfoState

/**
 * ElegantNetworkWidget — Modern glassmorphism network telemetry widget.
 * Mirrors the structure, titles, and layout of the Terminal widget,
 * refined with customizable glassmorphism, accent colors, and elegant typography.
 */
@Composable
fun ElegantNetworkWidget(
    networkInfo: NetworkInfoState,
    modifier: Modifier = Modifier,
    font: WidgetFont = WidgetFont.DEFAULT,
    showUsage: Boolean = true,
    onTap: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null,
    onClick: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val userManager = remember { UserManager(context) }
    val widgetColor = Color(userManager.getWidgetColor())
    val cornerRadius = userManager.getWidgetCornerRadius()
    val glassEffect = userManager.getWidgetGlassEffect()
    val shape = RoundedCornerShape(cornerRadius.dp)

    val onlineAccent = Color(0xFF8CA6FF)
    val offlineAccent = Color(0xFFFF5252).copy(alpha = 0.85f)

    Column(
        modifier = modifier
            .width(230.dp) // Fixed width container eliminates jitter
            .clip(shape)
            .background(
                if (glassEffect) {
                    Brush.verticalGradient(
                        listOf(
                            widgetColor.copy(alpha = widgetColor.alpha.coerceAtMost(0.35f)),
                            widgetColor.copy(alpha = widgetColor.alpha.coerceAtMost(0.12f))
                        )
                    )
                } else {
                    Brush.verticalGradient(listOf(widgetColor, widgetColor))
                }
            )
            .border(
                1.dp,
                Brush.verticalGradient(
                    listOf(
                        Color.White.copy(alpha = 0.25f),
                        Color.White.copy(alpha = 0.06f)
                    )
                ),
                shape
            )
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        if (onTap != null) onTap()
                        else if (onClick != null) onClick()
                        else NetworkHelper.openWifiSettings(context)
                    },
                    onLongPress = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onLongClick?.invoke()
                    }
                )
            }
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // Row 1: Online status & optional data usage
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (networkInfo.isConnected) "net0: <ONLINE>" else "net0: <OFFLINE>",
                color = if (networkInfo.isConnected) onlineAccent else offlineAccent,
                fontSize = 10.5.sp,
                fontFamily = font.toFontFamily(),
                fontWeight = FontWeight.Bold
            )

            if (showUsage && networkInfo.todayUsageText != "0 B") {
                Text(
                    text = "[${networkInfo.todayUsageText}]",
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 9.5.sp,
                    fontFamily = font.toFontFamily()
                )
            }
        }

        // Row 2: Title — Speed
        Text(
            text = "speed: ${networkInfo.displaySpeed}",
            color = Color.White.copy(alpha = 0.95f),
            fontSize = 11.5.sp,
            fontFamily = font.toFontFamily(),
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        // Row 3: Subtitle — SSID & IP
        Text(
            text = "inet:  ${networkInfo.subtitleText}",
            color = onlineAccent.copy(alpha = 0.85f),
            fontSize = 10.5.sp,
            fontFamily = font.toFontFamily(),
            fontWeight = FontWeight.Normal,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
