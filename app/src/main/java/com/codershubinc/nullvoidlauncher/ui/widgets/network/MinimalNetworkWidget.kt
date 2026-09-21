package com.codershubinc.nullvoidlauncher.ui.widgets.network

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Wifi
import androidx.compose.material.icons.rounded.WifiOff
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codershubinc.nullvoidlauncher.data.WidgetFont
import com.codershubinc.nullvoidlauncher.ui.network.NetworkHelper
import com.codershubinc.nullvoidlauncher.ui.network.NetworkInfoState

/**
 * MinimalNetworkWidget — Ultra-compact horizontal pill widget.
 * Fixed width 230dp ensures zero layout shifting.
 */
@Composable
fun MinimalNetworkWidget(
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
    val shape = RoundedCornerShape(20.dp)
    val isConnected = networkInfo.isConnected

    Row(
        modifier = modifier
            .width(230.dp) // Fixed width container eliminates jitter
            .clip(shape)
            .background(Color.White.copy(alpha = 0.06f))
            .border(1.dp, Color.White.copy(alpha = 0.12f), shape)
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
            .padding(horizontal = 12.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = if (isConnected) Icons.Rounded.Wifi else Icons.Rounded.WifiOff,
            contentDescription = null,
            tint = if (isConnected) Color(0xFF6C8CFF) else Color.White.copy(alpha = 0.35f),
            modifier = Modifier.size(16.dp)
        )

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(1.dp)
        ) {
            Text(
                text = networkInfo.displaySpeed,
                color = Color.White,
                fontSize = 11.sp,
                fontFamily = font.toFontFamily(),
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = networkInfo.subtitleText,
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 9.5.sp,
                fontFamily = font.toFontFamily(),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        if (showUsage && networkInfo.todayUsageText != "0 B") {
            Text(
                text = networkInfo.todayUsageText,
                color = Color.White.copy(alpha = 0.4f),
                fontSize = 8.5.sp,
                fontFamily = FontFamily.Monospace,
                maxLines = 1
            )
        }
    }
}
