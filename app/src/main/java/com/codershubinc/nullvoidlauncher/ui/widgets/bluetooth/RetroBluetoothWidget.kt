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
 * RetroBluetoothWidget — Amber vintage digital LED aesthetic for Bluetooth.
 * Fixed 230dp width eliminates layout jitter.
 * Gestures:
 * - Tap: Opens system Bluetooth settings
 * - Long Press: Opens launcher Bluetooth configuration page
 */
@Composable
fun RetroBluetoothWidget(
    bluetoothInfo: BluetoothInfoState,
    modifier: Modifier = Modifier,
    font: WidgetFont = WidgetFont.DEFAULT,
    onTap: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val amber = Color(0xFFC5A35E)
    val darkAmber = Color(0xFF261D12)
    val shape = RoundedCornerShape(10.dp)

    val activeDevice = bluetoothInfo.activeDevice ?: bluetoothInfo.connectedDevices.firstOrNull()
    val isConnected = bluetoothInfo.isConnected
    val deviceName = activeDevice?.name ?: "Bluetooth"
    val hasBattery = activeDevice?.hasBattery == true
    val batteryText = activeDevice?.displayBattery ?: ""

    Row(
        modifier = modifier
            .width(230.dp)
            .clip(shape)
            .background(darkAmber)
            .border(1.2.dp, amber.copy(alpha = 0.4f), shape)
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
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(amber.copy(alpha = 0.2f))
                .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
            Text(
                text = "BT",
                color = amber,
                fontSize = 9.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (!bluetoothInfo.hasPermission) "BLUETOOTH" else deviceName.uppercase(),
                    color = amber,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )

                if (hasBattery) {
                    Text(
                        text = "[$batteryText]",
                        color = amber,
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Text(
                text = if (!bluetoothInfo.hasPermission) {
                    "PERM REQUIRED // TAP"
                } else if (hasBattery) {
                    "$batteryText BATTERY • CONNECTED"
                } else {
                    if (isConnected) "CONNECTED" else "DISCONNECTED"
                },
                color = if (!bluetoothInfo.hasPermission) Color(0xFFFFB300) else amber.copy(alpha = 0.75f),
                fontSize = 9.5.sp,
                fontFamily = font.toFontFamily(),
                fontWeight = if (!bluetoothInfo.hasPermission) FontWeight.Bold else FontWeight.Normal,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
