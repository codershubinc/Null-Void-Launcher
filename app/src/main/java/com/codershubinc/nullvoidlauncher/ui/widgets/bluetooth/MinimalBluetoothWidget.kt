package com.codershubinc.nullvoidlauncher.ui.widgets.bluetooth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BluetoothConnected
import androidx.compose.material.icons.rounded.Headphones
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
import com.codershubinc.nullvoidlauncher.ui.bluetooth.BluetoothDeviceType
import com.codershubinc.nullvoidlauncher.ui.bluetooth.BluetoothHelper
import com.codershubinc.nullvoidlauncher.ui.bluetooth.BluetoothInfoState

/**
 * MinimalBluetoothWidget — Ultra-compact horizontal pill for Bluetooth telemetry.
 * Fixed 230dp width eliminates layout jitter.
 * Gestures:
 * - Tap: Opens system Bluetooth settings
 * - Long Press: Opens launcher Bluetooth configuration page
 */
@Composable
fun MinimalBluetoothWidget(
    bluetoothInfo: BluetoothInfoState,
    modifier: Modifier = Modifier,
    font: WidgetFont = WidgetFont.DEFAULT,
    onTap: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val shape = RoundedCornerShape(20.dp)

    val activeDevice = bluetoothInfo.activeDevice ?: bluetoothInfo.connectedDevices.firstOrNull()
    val isConnected = bluetoothInfo.isConnected
    val deviceName = activeDevice?.name ?: "Bluetooth"
    val hasBattery = activeDevice?.hasBattery == true
    val batteryText = activeDevice?.displayBattery ?: ""

    Row(
        modifier = modifier
            .width(230.dp) // Fixed width prevents jitter
            .clip(shape)
            .background(Color.White.copy(alpha = 0.06f))
            .border(1.dp, Color.White.copy(alpha = 0.12f), shape)
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
            .padding(horizontal = 12.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = if (activeDevice?.deviceType == BluetoothDeviceType.HEADPHONES) Icons.Rounded.Headphones else Icons.Rounded.BluetoothConnected,
            contentDescription = null,
            tint = if (isConnected) Color(0xFF00E5FF) else Color.White.copy(alpha = 0.35f),
            modifier = Modifier.size(16.dp)
        )

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(1.dp)
        ) {
            Text(
                text = if (!bluetoothInfo.hasPermission) "Bluetooth" else deviceName,
                color = Color.White,
                fontSize = 11.sp,
                fontFamily = font.toFontFamily(),
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = if (!bluetoothInfo.hasPermission) {
                    "Permission required • Tap"
                } else if (hasBattery) {
                    "$batteryText Battery"
                } else {
                    if (isConnected) "Connected" else "Disconnected"
                },
                color = if (!bluetoothInfo.hasPermission) Color(0xFFFFB300) else if (hasBattery) Color(0xFF00E676) else Color.White.copy(alpha = 0.5f),
                fontSize = 9.5.sp,
                fontFamily = font.toFontFamily(),
                fontWeight = if (!bluetoothInfo.hasPermission) FontWeight.Bold else FontWeight.Normal,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        if (hasBattery) {
            Text(
                text = batteryText,
                color = Color(0xFF00E676),
                fontSize = 9.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
        }
    }
}
