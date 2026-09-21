package com.codershubinc.nullvoidlauncher.ui.widgets.bluetooth

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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codershubinc.nullvoidlauncher.data.WidgetFont
import com.codershubinc.nullvoidlauncher.ui.bluetooth.BluetoothHelper
import com.codershubinc.nullvoidlauncher.ui.bluetooth.BluetoothInfoState

/**
 * TerminalBluetoothWidget — Hacker / CLI terminal prompt aesthetic for Bluetooth.
 * Fixed 230dp width eliminates layout jitter.
 * Gestures:
 * - Tap: Opens system Bluetooth settings
 * - Long Press: Opens launcher Bluetooth configuration page
 */
@Composable
fun TerminalBluetoothWidget(
    bluetoothInfo: BluetoothInfoState,
    modifier: Modifier = Modifier,
    font: WidgetFont = WidgetFont.MONOSPACE,
    onTap: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val terminalCyan = Color(0xFF00E5FF)
    val terminalDim = Color(0xFF00838F)
    val shape = RoundedCornerShape(8.dp)

    val activeDevice = bluetoothInfo.activeDevice ?: bluetoothInfo.connectedDevices.firstOrNull()
    val isConnected = bluetoothInfo.isConnected
    val deviceName = activeDevice?.name ?: "Bluetooth"
    val hasBattery = activeDevice?.hasBattery == true
    val batteryText = activeDevice?.displayBattery ?: ""

    Column(
        modifier = modifier
            .width(230.dp)
            .clip(shape)
            .background(Color(0xFF040A0C))
            .border(1.dp, terminalCyan.copy(alpha = 0.35f), shape)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        if (onTap != null) onTap() else BluetoothHelper.openBluetoothSettings(context)
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
                text = if (!bluetoothInfo.hasPermission) "bt0: <PERM_REQUIRED>" else if (isConnected) "bt0: <PAIRED,ACTIVE>" else "bt0: <IDLE>",
                color = if (!bluetoothInfo.hasPermission) Color(0xFFFFB300) else if (isConnected) terminalCyan else Color(0xFFFF5252),
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )

            if (hasBattery) {
                Text(
                    text = "[$batteryText]",
                    color = Color(0xFF00E676),
                    fontSize = 9.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Title: Device name
        Text(
            text = if (!bluetoothInfo.hasPermission) "dev:   [PERMISSION NEEDED]" else "dev:   $deviceName",
            color = Color.White.copy(alpha = 0.95f),
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        // Subtitle: Battery or link status
        Text(
            text = if (!bluetoothInfo.hasPermission) "perm:  TAP TO GRANT ACCESS" else if (hasBattery) "power: BAT_$batteryText" else "link:  CONNECTED",
            color = if (!bluetoothInfo.hasPermission) Color(0xFFFFB300) else if (hasBattery) Color(0xFF00E676) else terminalDim,
            fontSize = 10.sp,
            fontFamily = FontFamily.Monospace,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
