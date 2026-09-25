package com.codershubinc.nullvoidlauncher.ui.widgets.bluetooth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codershubinc.nullvoidlauncher.data.UserManager
import com.codershubinc.nullvoidlauncher.data.WidgetFont
import com.codershubinc.nullvoidlauncher.ui.bluetooth.BluetoothDeviceType
import com.codershubinc.nullvoidlauncher.ui.bluetooth.BluetoothHelper
import com.codershubinc.nullvoidlauncher.ui.bluetooth.BluetoothInfoState

/**
 * Resolves the appropriate context-aware icon based on device name & device type.
 */
fun resolveBluetoothDeviceIcon(type: BluetoothDeviceType?, isConnected: Boolean): ImageVector {
    return when (type) {
        BluetoothDeviceType.BUDS -> Icons.Rounded.Earbuds
        BluetoothDeviceType.HEADPHONES -> Icons.Rounded.Headphones
        BluetoothDeviceType.SOUNDBAR, BluetoothDeviceType.SPEAKER -> Icons.Rounded.Speaker
        BluetoothDeviceType.HEADSET -> Icons.Rounded.HeadsetMic
        BluetoothDeviceType.WATCH -> Icons.Rounded.Watch
        BluetoothDeviceType.PHONE -> Icons.Rounded.Smartphone
        BluetoothDeviceType.TABLET -> Icons.Rounded.Tablet
        BluetoothDeviceType.LAPTOP -> Icons.Rounded.Laptop
        BluetoothDeviceType.TV -> Icons.Rounded.Tv
        BluetoothDeviceType.CAR -> Icons.Rounded.DirectionsCar
        BluetoothDeviceType.CONTROLLER -> Icons.Rounded.SportsEsports
        BluetoothDeviceType.MOUSE -> Icons.Rounded.Mouse
        BluetoothDeviceType.KEYBOARD -> Icons.Rounded.Keyboard
        BluetoothDeviceType.AUDIO -> Icons.Rounded.Audiotrack
        else -> if (isConnected) Icons.Rounded.BluetoothConnected else Icons.Rounded.Bluetooth
    }
}

/**
 * CompactGlassBluetoothWidget — Minimalist Apple/Glass inspired pill displaying only:
 * [Icon (Earbuds / Soundbar / Headphones / Speaker)] + [Battery %]
 * Styled with frosted glass gradient, subtle specular border, and tactile haptic response.
 */
@Composable
fun CompactGlassBluetoothWidget(
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
    val shape = RoundedCornerShape(cornerRadius.dp.coerceAtLeast(16.dp))

    val activeDevice = bluetoothInfo.activeDevice ?: bluetoothInfo.connectedDevices.firstOrNull()
    val isConnected = bluetoothInfo.isConnected
    val hasBattery = activeDevice?.hasBattery == true
    val batteryText = activeDevice?.displayBattery ?: ""

    val deviceIcon = resolveBluetoothDeviceIcon(activeDevice?.deviceType, isConnected)
    val accentColor = if (isConnected) Color(0xFF00E5FF) else Color.White.copy(alpha = 0.5f)

    Row(
        modifier = modifier
            .clip(shape)
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color.White.copy(alpha = 0.16f),
                        Color.White.copy(alpha = 0.05f)
                    )
                )
            )
            .border(
                1.dp,
                Brush.verticalGradient(
                    listOf(
                        Color.White.copy(alpha = 0.35f),
                        Color.White.copy(alpha = 0.08f)
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
            .padding(horizontal = 12.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(7.dp)
    ) {
        Icon(
            imageVector = deviceIcon,
            contentDescription = null,
            tint = accentColor,
            modifier = Modifier.size(17.dp)
        )

        if (hasBattery) {
            Text(
                text = batteryText,
                color = Color.White,
                fontSize = 12.sp,
                fontFamily = font.toFontFamily(),
                fontWeight = FontWeight.SemiBold
            )
        } else if (!bluetoothInfo.hasPermission) {
            Icon(
                imageVector = Icons.Rounded.Lock,
                contentDescription = null,
                tint = Color(0xFFFFB300),
                modifier = Modifier.size(12.dp)
            )
        } else if (isConnected) {
            Text(
                text = "ON",
                color = Color(0xFF00E676),
                fontSize = 10.sp,
                fontFamily = font.toFontFamily(),
                fontWeight = FontWeight.Bold
            )
        }
    }
}
