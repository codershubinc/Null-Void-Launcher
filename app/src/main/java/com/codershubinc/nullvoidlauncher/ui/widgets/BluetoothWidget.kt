package com.codershubinc.nullvoidlauncher.ui.widgets

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.codershubinc.nullvoidlauncher.data.BluetoothStyle
import com.codershubinc.nullvoidlauncher.data.UserManager
import com.codershubinc.nullvoidlauncher.data.WidgetFont
import com.codershubinc.nullvoidlauncher.ui.bluetooth.BluetoothHelper
import com.codershubinc.nullvoidlauncher.ui.bluetooth.BluetoothInfoState
import com.codershubinc.nullvoidlauncher.ui.widgets.bluetooth.ElegantBluetoothWidget
import com.codershubinc.nullvoidlauncher.ui.widgets.bluetooth.MinimalBluetoothWidget
import com.codershubinc.nullvoidlauncher.ui.widgets.bluetooth.RetroBluetoothWidget
import com.codershubinc.nullvoidlauncher.ui.widgets.bluetooth.TerminalBluetoothWidget

/**
 * BluetoothWidget — Master routing composable for connected Bluetooth device & battery telemetry.
 * Automatically hides when no device is connected if configured to show only when connected.
 * Supports:
 * - Tap: opens default app (System Bluetooth settings) or requests Bluetooth permission
 * - Long Press: opens launcher custom configuration page to select device
 */
@Composable
fun BluetoothWidget(
    style: BluetoothStyle = BluetoothStyle.ELEGANT,
    modifier: Modifier = Modifier,
    font: WidgetFont? = null,
    showOnlyIfConnected: Boolean? = null,
    previewInfo: BluetoothInfoState? = null,
    onTap: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val userManager = remember { UserManager(context) }
    val effectiveFont = font ?: userManager.getBluetoothFont()
    val onlyConnected = showOnlyIfConnected ?: userManager.getBluetoothShowOnlyIfConnected()

    var bluetoothInfo by remember {
        mutableStateOf(previewInfo ?: BluetoothHelper.getBluetoothInfo(context))
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { _ ->
        bluetoothInfo = BluetoothHelper.getBluetoothInfo(context)
    }

    val effectiveTap: () -> Unit = {
        if (!bluetoothInfo.hasPermission && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissionLauncher.launch(Manifest.permission.BLUETOOTH_CONNECT)
        } else {
            onTap?.invoke() ?: BluetoothHelper.openBluetoothSettings(context)
        }
    }

    DisposableEffect(previewInfo) {
        if (previewInfo != null) {
            bluetoothInfo = previewInfo
            return@DisposableEffect onDispose {}
        }

        bluetoothInfo = BluetoothHelper.getBluetoothInfo(context)

        val receiver = object : BroadcastReceiver() {
            override fun onReceive(c: Context?, intent: Intent?) {
                if (intent == null) return

                val action = intent.action
                if (action == "android.bluetooth.device.action.BATTERY_LEVEL_CHANGED") {
                    val device = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                    val level = intent.getIntExtra("android.bluetooth.device.extra.BATTERY_LEVEL", -1)
                    if (device != null && level in 0..100) {
                        BluetoothHelper.updateCachedBattery(device.address, level)
                    }
                }

                bluetoothInfo = BluetoothHelper.getBluetoothInfo(context)
            }
        }

        val filter = IntentFilter().apply {
            addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
            addAction(BluetoothDevice.ACTION_ACL_CONNECTED)
            addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED)
            addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED)
            addAction("android.bluetooth.device.action.BATTERY_LEVEL_CHANGED")
            addAction("android.bluetooth.headset.profile.action.CONNECTION_STATE_CHANGED")
            addAction("android.bluetooth.a2dp.profile.action.CONNECTION_STATE_CHANGED")
        }

        context.registerReceiver(receiver, filter)

        onDispose {
            try {
                context.unregisterReceiver(receiver)
            } catch (_: Exception) {}
        }
    }

    // Hide while no device is connected if onlyConnected is true (except when previewing in settings or if permission is missing)
    if (previewInfo == null && onlyConnected && bluetoothInfo.hasPermission && !bluetoothInfo.isConnected) {
        return
    }

    when (style) {
        BluetoothStyle.ELEGANT  -> ElegantBluetoothWidget(
            bluetoothInfo = bluetoothInfo,
            modifier = modifier,
            font = effectiveFont,
            onTap = effectiveTap,
            onLongClick = onLongClick
        )
        BluetoothStyle.MINIMAL  -> MinimalBluetoothWidget(
            bluetoothInfo = bluetoothInfo,
            modifier = modifier,
            font = effectiveFont,
            onTap = effectiveTap,
            onLongClick = onLongClick
        )
        BluetoothStyle.TERMINAL -> TerminalBluetoothWidget(
            bluetoothInfo = bluetoothInfo,
            modifier = modifier,
            font = effectiveFont,
            onTap = effectiveTap,
            onLongClick = onLongClick
        )
        BluetoothStyle.RETRO    -> RetroBluetoothWidget(
            bluetoothInfo = bluetoothInfo,
            modifier = modifier,
            font = effectiveFont,
            onTap = effectiveTap,
            onLongClick = onLongClick
        )
    }
}
