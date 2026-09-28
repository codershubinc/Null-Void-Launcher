package com.codershubinc.nullvoidlauncher.ui.controls

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.VolumeOff
import androidx.compose.material.icons.automirrored.outlined.VolumeUp
import androidx.compose.material.icons.automirrored.rounded.VolumeOff
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.automirrored.sharp.VolumeOff
import androidx.compose.material.icons.automirrored.sharp.VolumeUp
import androidx.compose.material.icons.automirrored.twotone.VolumeOff
import androidx.compose.material.icons.automirrored.twotone.VolumeUp
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material.icons.sharp.*
import androidx.compose.material.icons.twotone.*
import androidx.compose.ui.graphics.vector.ImageVector
import com.codershubinc.nullvoidlauncher.data.ControlDeckIconStyle

object ControlDeckIconProvider {

    fun getTorchIcon(iconStyle: ControlDeckIconStyle, isOn: Boolean): ImageVector {
        return when (iconStyle) {
            ControlDeckIconStyle.ROUNDED -> if (isOn) Icons.Rounded.FlashlightOn else Icons.Rounded.FlashlightOff
            ControlDeckIconStyle.OUTLINED -> if (isOn) Icons.Outlined.FlashlightOn else Icons.Outlined.FlashlightOff
            ControlDeckIconStyle.SHARP -> if (isOn) Icons.Sharp.FlashlightOn else Icons.Sharp.FlashlightOff
            ControlDeckIconStyle.TWO_TONE -> if (isOn) Icons.TwoTone.FlashlightOn else Icons.TwoTone.FlashlightOff
        }
    }

    fun getRingerIcon(iconStyle: ControlDeckIconStyle, state: ControlHelper.RingerState): ImageVector {
        return when (iconStyle) {
            ControlDeckIconStyle.ROUNDED -> when (state) {
                ControlHelper.RingerState.SILENT -> Icons.AutoMirrored.Rounded.VolumeOff
                ControlHelper.RingerState.VIBRATE -> Icons.Rounded.Vibration
                ControlHelper.RingerState.NORMAL -> Icons.AutoMirrored.Rounded.VolumeUp
            }
            ControlDeckIconStyle.OUTLINED -> when (state) {
                ControlHelper.RingerState.SILENT -> Icons.AutoMirrored.Outlined.VolumeOff
                ControlHelper.RingerState.VIBRATE -> Icons.Outlined.Vibration
                ControlHelper.RingerState.NORMAL -> Icons.AutoMirrored.Outlined.VolumeUp
            }
            ControlDeckIconStyle.SHARP -> when (state) {
                ControlHelper.RingerState.SILENT -> Icons.AutoMirrored.Sharp.VolumeOff
                ControlHelper.RingerState.VIBRATE -> Icons.Sharp.Vibration
                ControlHelper.RingerState.NORMAL -> Icons.AutoMirrored.Sharp.VolumeUp
            }
            ControlDeckIconStyle.TWO_TONE -> when (state) {
                ControlHelper.RingerState.SILENT -> Icons.AutoMirrored.TwoTone.VolumeOff
                ControlHelper.RingerState.VIBRATE -> Icons.TwoTone.Vibration
                ControlHelper.RingerState.NORMAL -> Icons.AutoMirrored.TwoTone.VolumeUp
            }
        }
    }

    fun getRotationIcon(iconStyle: ControlDeckIconStyle, isAuto: Boolean): ImageVector {
        return when (iconStyle) {
            ControlDeckIconStyle.ROUNDED -> if (isAuto) Icons.Rounded.ScreenRotation else Icons.Rounded.ScreenLockRotation
            ControlDeckIconStyle.OUTLINED -> if (isAuto) Icons.Outlined.ScreenRotation else Icons.Outlined.ScreenLockRotation
            ControlDeckIconStyle.SHARP -> if (isAuto) Icons.Sharp.ScreenRotation else Icons.Sharp.ScreenLockRotation
            ControlDeckIconStyle.TWO_TONE -> if (isAuto) Icons.TwoTone.ScreenRotation else Icons.TwoTone.ScreenLockRotation
        }
    }

    fun getDndIcon(iconStyle: ControlDeckIconStyle, isDnd: Boolean): ImageVector {
        return when (iconStyle) {
            ControlDeckIconStyle.ROUNDED -> if (isDnd) Icons.Rounded.DoNotDisturbOn else Icons.Rounded.DoNotDisturbOff
            ControlDeckIconStyle.OUTLINED -> if (isDnd) Icons.Outlined.DoNotDisturbOn else Icons.Outlined.DoNotDisturbOff
            ControlDeckIconStyle.SHARP -> if (isDnd) Icons.Sharp.DoNotDisturbOn else Icons.Sharp.DoNotDisturbOff
            ControlDeckIconStyle.TWO_TONE -> if (isDnd) Icons.TwoTone.DoNotDisturbOn else Icons.TwoTone.DoNotDisturbOff
        }
    }

    fun getHotspotIcon(iconStyle: ControlDeckIconStyle): ImageVector {
        return when (iconStyle) {
            ControlDeckIconStyle.ROUNDED -> Icons.Rounded.WifiTethering
            ControlDeckIconStyle.OUTLINED -> Icons.Outlined.WifiTethering
            ControlDeckIconStyle.SHARP -> Icons.Sharp.WifiTethering
            ControlDeckIconStyle.TWO_TONE -> Icons.TwoTone.WifiTethering
        }
    }

    fun getBluetoothIcon(iconStyle: ControlDeckIconStyle, isEnabled: Boolean): ImageVector {
        return when (iconStyle) {
            ControlDeckIconStyle.ROUNDED -> if (isEnabled) Icons.Rounded.Bluetooth else Icons.Rounded.BluetoothDisabled
            ControlDeckIconStyle.OUTLINED -> if (isEnabled) Icons.Outlined.Bluetooth else Icons.Outlined.BluetoothDisabled
            ControlDeckIconStyle.SHARP -> if (isEnabled) Icons.Sharp.Bluetooth else Icons.Sharp.BluetoothDisabled
            ControlDeckIconStyle.TWO_TONE -> if (isEnabled) Icons.TwoTone.Bluetooth else Icons.TwoTone.BluetoothDisabled
        }
    }

    fun getWifiIcon(iconStyle: ControlDeckIconStyle, isEnabled: Boolean): ImageVector {
        return when (iconStyle) {
            ControlDeckIconStyle.ROUNDED -> if (isEnabled) Icons.Rounded.Wifi else Icons.Rounded.WifiOff
            ControlDeckIconStyle.OUTLINED -> if (isEnabled) Icons.Outlined.Wifi else Icons.Outlined.WifiOff
            ControlDeckIconStyle.SHARP -> if (isEnabled) Icons.Sharp.Wifi else Icons.Sharp.WifiOff
            ControlDeckIconStyle.TWO_TONE -> if (isEnabled) Icons.TwoTone.Wifi else Icons.TwoTone.WifiOff
        }
    }
}
