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
    BUDS,
    HEADSET,
    SPEAKER,
    SOUNDBAR,
    WATCH,
    PHONE,
    LAPTOP,
    TABLET,
    TV,
    CAR,
    CONTROLLER,
    MOUSE,
    KEYBOARD,
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
            val type = resolveDeviceType(device, name)
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

    private fun resolveDeviceType(device: BluetoothDevice, name: String = ""): BluetoothDeviceType {
        val lower = name.lowercase().trim()

        // 1. In-ear Earbuds / TWS / Airpods
        if (lower.contains("airpod") || lower.contains("earbud") || lower.contains("buds") ||
            lower.contains("earphone") || lower.contains("tws") || lower.contains("dots") ||
            lower.contains("airdots") || lower.contains("pixel buds") || lower.contains("galaxy buds") ||
            lower.contains("freebuds") || lower.contains("linkbuds") || lower.contains("tune flex") ||
            lower.contains("tune beam") || lower.contains("c Round") || lower.contains("in-ear") ||
            lower.contains("ear-buds") || lower.contains("cords") || lower.contains("neckband") ||
            lower.contains("rockerz") || lower.contains("airdopes") || lower.contains("bullets") ||
            lower.contains("wireless stereo") || lower.contains("true wireless")
        ) {
            return BluetoothDeviceType.BUDS
        }

        // 2. Soundbars & Home Theater
        if (lower.contains("soundbar") || lower.contains("sound bar") || lower.contains("home theater") ||
            lower.contains("cinema") || lower.contains("subwoofer") || lower.contains("soundstage") ||
            lower.contains("sound tower") || lower.contains("partybox")
        ) {
            return BluetoothDeviceType.SOUNDBAR
        }

        // 3. Over-ear / On-ear Headphones
        if (lower.contains("headphone") || lower.contains("over-ear") || lower.contains("on-ear") ||
            lower.contains("wh-1000") || lower.contains("xm4") || lower.contains("xm5") || lower.contains("xm3") ||
            lower.contains("qc35") || lower.contains("qc45") || lower.contains("quietcomfort") ||
            lower.contains("momentum") || lower.contains("crusher") || lower.contains("beats studio") ||
            lower.contains("beats solo") || lower.contains("major iv") || lower.contains("monitor")
        ) {
            return BluetoothDeviceType.HEADPHONES
        }

        // 4. Smartwatches & Fitness Trackers
        if (lower.contains("watch") || lower.contains("band") || lower.contains("fitbit") ||
            lower.contains("garmin") || lower.contains("amazfit") || lower.contains("wearos") ||
            lower.contains("galaxy watch") || lower.contains("pixel watch") || lower.contains("apple watch") ||
            lower.contains("smartwatch") || lower.contains("tracker") || lower.contains("mi band") ||
            lower.contains("honor band")
        ) {
            return BluetoothDeviceType.WATCH
        }

        // 5. Portable & Home Speakers
        if (lower.contains("speaker") || lower.contains("boombox") || lower.contains("jbl") ||
            lower.contains("echo") || lower.contains("nest mini") || lower.contains("nest audio") ||
            lower.contains("homepod") || lower.contains("marshall") || lower.contains("emberton") ||
            lower.contains("stanmore") || lower.contains("acton") || lower.contains("flip") ||
            lower.contains("charge") || lower.contains("clip") || lower.contains("wonderboom") ||
            lower.contains("megaboom") || lower.contains("tribit") || lower.contains("soundcore") ||
            lower.contains("anker") || lower.contains("pill") || lower.contains("bose soundlink")
        ) {
            return BluetoothDeviceType.SPEAKER
        }

        // 6. Gaming Controllers
        if (lower.contains("controller") || lower.contains("gamepad") || lower.contains("dualshock") ||
            lower.contains("dualsense") || lower.contains("xbox") || lower.contains("joy-con") ||
            lower.contains("switch pro") || lower.contains("wireless controller")
        ) {
            return BluetoothDeviceType.CONTROLLER
        }

        // 7. Input devices (Mouse / Keyboard)
        if (lower.contains("mouse") || lower.contains("mx master") || lower.contains("magic mouse")) {
            return BluetoothDeviceType.MOUSE
        }
        if (lower.contains("keyboard") || lower.contains("keychron") || lower.contains("magic keyboard")) {
            return BluetoothDeviceType.KEYBOARD
        }

        // 8. TVs & Displays
        if (lower.contains("tv") || lower.contains("bravia") || lower.contains("fire tv") ||
            lower.contains("chromecast") || lower.contains("smart tv") || lower.contains("roku")
        ) {
            return BluetoothDeviceType.TV
        }

        // 9. Car Audio / Handsfree
        if (lower.contains("car") || lower.contains("auto") || lower.contains("audi") ||
            lower.contains("bmw") || lower.contains("mercedes") || lower.contains("honda") ||
            lower.contains("toyota") || lower.contains("hyundai") || lower.contains("ford") ||
            lower.contains("sync") || lower.contains("uconnect") || lower.contains("carplay") ||
            lower.contains("handsfree") || lower.contains("carkit")
        ) {
            return BluetoothDeviceType.CAR
        }

        // 10. PC / Laptop / Tablet
        if (lower.contains("macbook") || lower.contains("laptop") || lower.contains("thinkpad") ||
            lower.contains("notebook") || lower.contains("zenbook") || lower.contains("desktop") ||
            lower.contains("pc")
        ) {
            return BluetoothDeviceType.LAPTOP
        }
        if (lower.contains("ipad") || lower.contains("tab") || lower.contains("tablet")) {
            return BluetoothDeviceType.TABLET
        }

        // Fallback: Inspect Android BluetoothClass device & major classes
        return try {
            val bluetoothClass = device.bluetoothClass ?: return BluetoothDeviceType.GENERIC
            val devClass = bluetoothClass.deviceClass
            when (devClass) {
                BluetoothClass.Device.AUDIO_VIDEO_HEADPHONES -> BluetoothDeviceType.HEADPHONES
                BluetoothClass.Device.AUDIO_VIDEO_WEARABLE_HEADSET -> BluetoothDeviceType.BUDS
                BluetoothClass.Device.AUDIO_VIDEO_LOUDSPEAKER,
                BluetoothClass.Device.AUDIO_VIDEO_PORTABLE_AUDIO -> BluetoothDeviceType.SPEAKER
                BluetoothClass.Device.AUDIO_VIDEO_HANDSFREE,
                BluetoothClass.Device.AUDIO_VIDEO_CAR_AUDIO -> BluetoothDeviceType.CAR
                BluetoothClass.Device.AUDIO_VIDEO_SET_TOP_BOX,
                BluetoothClass.Device.AUDIO_VIDEO_VIDEO_DISPLAY_AND_LOUDSPEAKER -> BluetoothDeviceType.TV
                BluetoothClass.Device.WEARABLE_WRIST_WATCH -> BluetoothDeviceType.WATCH
                BluetoothClass.Device.PHONE_SMART,
                BluetoothClass.Device.PHONE_CELLULAR -> BluetoothDeviceType.PHONE
                BluetoothClass.Device.COMPUTER_LAPTOP -> BluetoothDeviceType.LAPTOP
                BluetoothClass.Device.COMPUTER_DESKTOP -> BluetoothDeviceType.LAPTOP
                1076 -> BluetoothDeviceType.TABLET // BluetoothClass.Device.COMPUTER_TABLET
                BluetoothClass.Device.Major.PERIPHERAL -> {
                    when (devClass) {
                        1344 -> BluetoothDeviceType.KEYBOARD // Peripheral Keyboard
                        1408 -> BluetoothDeviceType.MOUSE // Peripheral Pointer / Mouse
                        1472 -> BluetoothDeviceType.CONTROLLER // Peripheral Joystick / Gamepad
                        else -> BluetoothDeviceType.GENERIC
                    }
                }
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
