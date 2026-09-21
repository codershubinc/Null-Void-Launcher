package com.codershubinc.nullvoidlauncher.ui.widgets.network

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codershubinc.nullvoidlauncher.data.WidgetFont
import com.codershubinc.nullvoidlauncher.ui.network.NetworkHelper
import com.codershubinc.nullvoidlauncher.ui.network.NetworkInfoState

/**
 * TerminalNetworkWidget — Hacker / CLI terminal prompt aesthetic.
 * Fixed width 230dp container eliminates resizing jitter.
 */
@Composable
fun TerminalNetworkWidget(
    networkInfo: NetworkInfoState,
    modifier: Modifier = Modifier,
    font: WidgetFont = WidgetFont.MONOSPACE,
    showUsage: Boolean = true,
    onTap: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null,
    onClick: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val terminalGreen = Color(0xFF00FF66)
    val terminalDim = Color(0xFF00B344)
    val shape = RoundedCornerShape(8.dp)

    Column(
        modifier = modifier
            .width(230.dp) // Fixed width container eliminates jitter
            .clip(shape)
            .background(Color(0xFF060B08))
            .border(1.dp, terminalGreen.copy(alpha = 0.35f), shape)
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
            .padding(horizontal = 12.dp, vertical = 9.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (networkInfo.isConnected) "net0: <ONLINE>" else "net0: <OFFLINE>",
                color = if (networkInfo.isConnected) terminalGreen else Color(0xFFFF5252),
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )

            if (showUsage && networkInfo.todayUsageText != "0 B") {
                Text(
                    text = "[${networkInfo.todayUsageText}]",
                    color = terminalDim.copy(alpha = 0.75f),
                    fontSize = 9.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        // Title: Speed
        Text(
            text = "speed: ${networkInfo.displaySpeed}",
            color = Color.White.copy(alpha = 0.95f),
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        // Subtitle: SSID & IP
        Text(
            text = "inet:  ${networkInfo.subtitleText}",
            color = terminalGreen,
            fontSize = 10.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Normal,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
