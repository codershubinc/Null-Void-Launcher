package com.codershubinc.nullvoidlauncher.ui.bluetooth

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothClass
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.content.ContextCompat
import com.codershubinc.nullvoidlauncher.data.UserManager
import java.util.concurrent.ConcurrentHashMap

enum class BluetoothDeviceType {
    HEADPHONES,
    HEADSET,
    SPEAKER,
    WATCH,
    PHONE,
    AUDIO,
    GENERIC
}

data class BluetoothDeviceInfo(
    val name: String,
    val address: String,
    val isConnected: Boolean,
    val batteryLevel: Int = -1, // 0..100, or -1 if unknown
    val deviceType: BluetoothDeviceType = BluetoothDeviceType.AUDIO,
    val isPreferred: Boolean = false
) {
    val displayBattery: String
        get() = if (batteryLevel in 0..100) "$batteryLevel%" else ""

    val hasBattery: Boolean
        get() = batteryLevel in 0..100
}

data class BluetoothInfoState(
    val isBluetoothEnabled: Boolean = false,
    val hasPermission: Boolean = false,
    val connectedDevices: List<BluetoothDeviceInfo> = emptyList(),
    val pairedDevices: List<BluetoothDeviceInfo> = emptyList(),
    val activeDevice: BluetoothDeviceInfo? = null
) {
    val isConnected: Boolean
        get() = activeDevice != null || connectedDevices.isNotEmpty()

    val displayDeviceName: String
        get() = activeDevice?.name ?: connectedDevices.firstOrNull()?.name ?: "Bluetooth"

    val displayBattery: String
        get() = activeDevice?.displayBattery ?: connectedDevices.firstOrNull()?.displayBattery ?: ""

    val hasBattery: Boolean
        get() = activeDevice?.hasBattery ?: connectedDevices.firstOrNull()?.hasBattery ?: false
}

object BluetoothHelper {

    // Cache battery levels received from broadcasts or reflection
    private val batteryCache = ConcurrentHashMap<String, Int>()

    fun updateCachedBattery(address: String, level: Int) {
        if (level in 0..100) {
            batteryCache[address] = level
        }
    }

    fun hasBluetoothPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_CONNECT
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    fun openBluetoothSettings(context: Context) {
        val intent = Intent(Settings.ACTION_BLUETOOTH_SETTINGS).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        try {
            context.startActivity(intent)
        } catch (_: Exception) {
            val fallback = Intent(Settings.ACTION_SETTINGS).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            try { context.startActivity(fallback) } catch (_: Exception) {}
        }
    }

    fun openAppSettings(context: Context) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", context.packageName, null)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        try {
            context.startActivity(intent)
        } catch (_: Exception) {}
    }

    @SuppressLint("MissingPermission")
    fun getBluetoothInfo(context: Context): BluetoothInfoState {
        val hasPerm = hasBluetoothPermission(context)
        if (!hasPerm) {
            return BluetoothInfoState(
                isBluetoothEnabled = false,
                hasPermission = false
            )
        }

        val bm = context.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager
        val adapter = bm?.adapter ?: BluetoothAdapter.getDefaultAdapter()

        if (adapter == null || !adapter.isEnabled) {
            return BluetoothInfoState(
                isBluetoothEnabled = false,
                hasPermission = true
            )
        }

        val userManager = UserManager(context)
        val preferredAddress = userManager.getPreferredBluetoothDevice()

        val bonded = try {
            adapter.bondedDevices?.toList() ?: emptyList()
        } catch (_: SecurityException) {
            emptyList<BluetoothDevice>()
        }

        val pairedList = mutableListOf<BluetoothDeviceInfo>()
        val connectedList = mutableListOf<BluetoothDeviceInfo>()

        for (device in bonded) {
            val address = device.address ?: ""
            val name = try { device.name ?: address } catch (_: SecurityException) { address }
            val connected = isDeviceConnected(device)
            val battery = resolveBattery(device, address)
            val type = resolveDeviceType(device)
            val isPref = address.isNotEmpty() && address.equals(preferredAddress, ignoreCase = true)

            val info = BluetoothDeviceInfo(
                name = name,
                address = address,
                isConnected = connected,
                batteryLevel = battery,
                deviceType = type,
                isPreferred = isPref
            )

            pairedList.add(info)
            if (connected) {
                connectedList.add(info)
            }
        }

        // Determine active device to display on the widget
        val activeDevice = when {
            // If preferred device is set and connected, use it
            preferredAddress.isNotEmpty() -> connectedList.find { it.address.equals(preferredAddress, ignoreCase = true) }
                ?: connectedList.firstOrNull()
            // Otherwise use the first connected device
            else -> connectedList.firstOrNull()
        }

        return BluetoothInfoState(
            isBluetoothEnabled = true,
            hasPermission = true,
            connectedDevices = connectedList,
            pairedDevices = pairedList,
            activeDevice = activeDevice
        )
    }

    private fun isDeviceConnected(device: BluetoothDevice): Boolean {
        return try {
            val method = device.javaClass.getMethod("isConnected")
            method.invoke(device) as? Boolean ?: false
        } catch (_: Exception) {
            false
        }
    }

    private fun resolveBattery(device: BluetoothDevice, address: String): Int {
        // First check in-memory cache updated via broadcasts
        val cached = batteryCache[address]
        if (cached != null && cached in 0..100) {
            return cached
        }

        // Try reflection method getBatteryLevel() available on Android 9+
        return try {
            val method = device.javaClass.getMethod("getBatteryLevel")
            val level = (method.invoke(device) as? Int) ?: -1
            if (level in 0..100) {
                batteryCache[address] = level
                level
            } else -1
        } catch (_: Exception) {
            -1
        }
    }

    private fun resolveDeviceType(device: BluetoothDevice): BluetoothDeviceType {
        return try {
            val bluetoothClass = device.bluetoothClass ?: return BluetoothDeviceType.GENERIC
            val devClass = bluetoothClass.deviceClass
            when (devClass) {
                BluetoothClass.Device.AUDIO_VIDEO_HEADPHONES,
                BluetoothClass.Device.AUDIO_VIDEO_WEARABLE_HEADSET -> BluetoothDeviceType.HEADPHONES
                BluetoothClass.Device.AUDIO_VIDEO_LOUDSPEAKER,
                BluetoothClass.Device.AUDIO_VIDEO_PORTABLE_AUDIO -> BluetoothDeviceType.SPEAKER
                BluetoothClass.Device.AUDIO_VIDEO_HANDSFREE,
                BluetoothClass.Device.AUDIO_VIDEO_CAR_AUDIO -> BluetoothDeviceType.HEADSET
                BluetoothClass.Device.WEARABLE_WRIST_WATCH -> BluetoothDeviceType.WATCH
                BluetoothClass.Device.PHONE_SMART -> BluetoothDeviceType.PHONE
                else -> {
                    if (bluetoothClass.hasService(BluetoothClass.Service.AUDIO)) {
                        BluetoothDeviceType.AUDIO
                    } else {
                        BluetoothDeviceType.GENERIC
                    }
                }
            }
        } catch (_: Exception) {
            BluetoothDeviceType.GENERIC
        }
    }
}
