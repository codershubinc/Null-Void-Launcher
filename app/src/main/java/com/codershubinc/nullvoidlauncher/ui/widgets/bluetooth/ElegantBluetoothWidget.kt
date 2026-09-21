package com.codershubinc.nullvoidlauncher.ui.widgets.bluetooth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codershubinc.nullvoidlauncher.data.UserManager
import com.codershubinc.nullvoidlauncher.data.WidgetFont
import com.codershubinc.nullvoidlauncher.ui.bluetooth.BluetoothDeviceInfo
import com.codershubinc.nullvoidlauncher.ui.bluetooth.BluetoothDeviceType
import com.codershubinc.nullvoidlauncher.ui.bluetooth.BluetoothHelper
import com.codershubinc.nullvoidlauncher.ui.bluetooth.BluetoothInfoState

/**
 * ElegantBluetoothWidget — Frosted glassmorphism card for connected Bluetooth device & battery.
 * Exactly 230dp width matches Network and Power widgets for an immaculate telemetry stack.
 * Gestures:
 * - Tap: Opens system Bluetooth settings (or default app)
 * - Long Press: Opens launcher Bluetooth device configuration page
 */
@Composable
fun ElegantBluetoothWidget(
    bluetoothInfo: BluetoothInfoState,
    modifier: Modifier = Modifier,
    font: WidgetFont = WidgetFont.DEFAULT,
    onTap: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val userManager = remember { UserManager(context) }
    val widgetColor = Color(userManager.getWidgetColor())
    val cornerRadius = userManager.getWidgetCornerRadius()
    val glassEffect = userManager.getWidgetGlassEffect()
    val shape = RoundedCornerShape(cornerRadius.dp)

    val activeDevice = bluetoothInfo.activeDevice ?: bluetoothInfo.connectedDevices.firstOrNull()
    val isConnected = bluetoothInfo.isConnected
    val accent = Color(0xFF00E5FF) // Cyan / Bluetooth electric blue

    val deviceName = activeDevice?.name ?: "Bluetooth"
    val hasBattery = activeDevice?.hasBattery == true
    val batteryText = activeDevice?.displayBattery ?: ""

    Row(
        modifier = modifier
            .width(230.dp) // Exact width matching Network and Power widgets
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
                        if (onTap != null) onTap() else BluetoothHelper.openBluetoothSettings(context)
                    },
                    onLongPress = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onLongClick?.invoke()
                    }
                )
            }
            .padding(horizontal = 14.dp, vertical = 11.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Device Type Icon Badge
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(if (isConnected) accent.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.08f))
                .border(1.dp, if (isConnected) accent.copy(alpha = 0.45f) else Color.White.copy(alpha = 0.12f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            val icon = when (activeDevice?.deviceType) {
                BluetoothDeviceType.HEADPHONES -> Icons.Rounded.Headphones
                BluetoothDeviceType.SPEAKER -> Icons.Rounded.Speaker
                BluetoothDeviceType.WATCH -> Icons.Rounded.Watch
                BluetoothDeviceType.HEADSET -> Icons.Rounded.HeadsetMic
                else -> if (isConnected) Icons.Rounded.BluetoothConnected else Icons.Rounded.Bluetooth
            }
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isConnected) accent else Color.White.copy(alpha = 0.4f),
                modifier = Modifier.size(19.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Center Content: Device Name Title & Battery Subtitle
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            // Device Name
            Text(
                text = if (!bluetoothInfo.hasPermission) "Bluetooth" else deviceName,
                color = Color.White,
                fontSize = 12.sp,
                fontFamily = font.toFontFamily(),
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            // Subtitle: Battery level or connection status
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (!bluetoothInfo.hasPermission) {
                    Icon(
                        imageVector = Icons.Rounded.Lock,
                        contentDescription = null,
                        tint = Color(0xFFFFB300),
                        modifier = Modifier.size(11.dp)
                    )
                    Text(
                        text = "Permission Needed • Tap",
                        color = Color(0xFFFFB300),
                        fontSize = 10.5.sp,
                        fontFamily = font.toFontFamily(),
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1
                    )
                } else if (hasBattery) {
                    Icon(
                        imageVector = Icons.Rounded.BatteryChargingFull,
                        contentDescription = null,
                        tint = Color(0xFF00E676),
                        modifier = Modifier.size(13.dp)
                    )
                    Text(
                        text = "$batteryText Battery",
                        color = Color.White.copy(alpha = 0.75f),
                        fontSize = 10.5.sp,
                        fontFamily = font.toFontFamily(),
                        fontWeight = FontWeight.Medium,
                        maxLines = 1
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(5.dp)
                            .clip(CircleShape)
                            .background(if (isConnected) Color(0xFF00E676) else Color.White.copy(alpha = 0.4f))
                    )
                    Text(
                        text = if (isConnected) "Connected" else "Disconnected",
                        color = Color.White.copy(alpha = 0.55f),
                        fontSize = 10.5.sp,
                        fontFamily = font.toFontFamily(),
                        maxLines = 1
                    )
                }
            }
        }

        // Mini corner battery tag if available
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
