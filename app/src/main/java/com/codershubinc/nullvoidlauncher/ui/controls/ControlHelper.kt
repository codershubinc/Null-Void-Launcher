package com.codershubinc.nullvoidlauncher.ui.controls

import android.app.NotificationManager
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.ContentObserver
import android.hardware.camera2.CameraManager
import android.media.AudioManager
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.widget.Toast
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class ControlDeckState(
    val isTorchOn: Boolean = false,
    val ringerState: ControlHelper.RingerState = ControlHelper.RingerState.NORMAL,
    val isAutoRotate: Boolean = false,
    val isDndActive: Boolean = false,
    val isBluetoothEnabled: Boolean = false,
    val isWifiEnabled: Boolean = false
)

object ControlHelper {

    private val _deckState = MutableStateFlow(ControlDeckState())
    val deckState: StateFlow<ControlDeckState> = _deckState.asStateFlow()

    private var registered = false
    private var appContext: Context? = null
    private var torchCallback: CameraManager.TorchCallback? = null
    private var rotationObserver: ContentObserver? = null

    enum class RingerState(val label: String) {
        NORMAL("Ring"),
        VIBRATE("Vibrate"),
        SILENT("Silent")
    }

    fun isTorchActive(): Boolean = _deckState.value.isTorchOn

    fun register(context: Context) {
        if (registered) return
        val app = context.applicationContext
        appContext = app

        // 1. Initial State Sync
        refreshState(app)

        // 2. Camera Torch Callback (Direct hardware state tracking from OS)
        try {
            val cameraManager = app.getSystemService(Context.CAMERA_SERVICE) as? CameraManager
            if (cameraManager != null) {
                val cb = object : CameraManager.TorchCallback() {
                    override fun onTorchModeChanged(cameraId: String, enabled: Boolean) {
                        _deckState.value = _deckState.value.copy(isTorchOn = enabled)
                    }

                    override fun onTorchModeUnavailable(cameraId: String) {
                        _deckState.value = _deckState.value.copy(isTorchOn = false)
                    }
                }
                torchCallback = cb
                cameraManager.registerTorchCallback(cb, Handler(Looper.getMainLooper()))
            }
        } catch (_: Exception) {}

        // 3. Broadcast Receiver for Ringer, DND, Bluetooth, WiFi
        try {
            val filter = IntentFilter().apply {
                addAction(AudioManager.RINGER_MODE_CHANGED_ACTION)
                addAction(NotificationManager.ACTION_INTERRUPTION_FILTER_CHANGED)
                addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
                addAction(WifiManager.WIFI_STATE_CHANGED_ACTION)
            }
            val receiver = object : BroadcastReceiver() {
                override fun onReceive(ctx: Context?, intent: Intent?) {
                    val targetCtx = ctx ?: appContext ?: return
                    refreshState(targetCtx)
                }
            }
            app.registerReceiver(receiver, filter)
        } catch (_: Exception) {}

        // 4. ContentObserver for Auto-Rotate
        try {
            val uri = Settings.System.getUriFor(Settings.System.ACCELEROMETER_ROTATION)
            val observer = object : ContentObserver(Handler(Looper.getMainLooper())) {
                override fun onChange(selfChange: Boolean) {
                    val appRef = appContext ?: return
                    _deckState.value = _deckState.value.copy(
                        isAutoRotate = isAutoRotateEnabled(appRef)
                    )
                }
            }
            rotationObserver = observer
            app.contentResolver.registerContentObserver(uri, false, observer)
        } catch (_: Exception) {}

        registered = true
    }

    fun refreshState(context: Context) {
        val ringer = getRingerState(context)
        val autoRotate = isAutoRotateEnabled(context)
        val dnd = isDndActive(context)
        val bt = isBluetoothActive(context)
        val wifi = isWifiActive(context)

        _deckState.value = _deckState.value.copy(
            ringerState = ringer,
            isAutoRotate = autoRotate,
            isDndActive = dnd,
            isBluetoothEnabled = bt,
            isWifiEnabled = wifi
        )
    }

    // ── Flashlight ─────────────────────────────────────────────────────────────
    fun toggleTorch(context: Context) {
        val app = context.applicationContext
        try {
            val cameraManager = app.getSystemService(Context.CAMERA_SERVICE) as? CameraManager
            val cameraId = cameraManager?.cameraIdList?.firstOrNull()
            if (cameraManager != null && cameraId != null) {
                val newState = !_deckState.value.isTorchOn
                cameraManager.setTorchMode(cameraId, newState)
                _deckState.value = _deckState.value.copy(isTorchOn = newState)
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Flashlight unavailable", Toast.LENGTH_SHORT).show()
        }
    }

    // ── Ringer ─────────────────────────────────────────────────────────────────
    fun getRingerState(context: Context): RingerState {
        val am = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager ?: return RingerState.NORMAL
        return when (am.ringerMode) {
            AudioManager.RINGER_MODE_SILENT -> RingerState.SILENT
            AudioManager.RINGER_MODE_VIBRATE -> RingerState.VIBRATE
            else -> RingerState.NORMAL
        }
    }

    fun cycleRingerMode(context: Context): RingerState {
        val am = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager ?: return RingerState.NORMAL
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
        val hasDndAccess = nm?.isNotificationPolicyAccessGranted == true

        try {
            when (am.ringerMode) {
                AudioManager.RINGER_MODE_NORMAL -> {
                    am.ringerMode = AudioManager.RINGER_MODE_VIBRATE
                }
                AudioManager.RINGER_MODE_VIBRATE -> {
                    if (hasDndAccess || Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                        am.ringerMode = AudioManager.RINGER_MODE_SILENT
                    } else {
                        // Without DND policy permission, cannot switch to Silent without crashing/failing.
                        // Loop back to Normal or prompt sound settings
                        am.ringerMode = AudioManager.RINGER_MODE_NORMAL
                        Toast.makeText(context, "Grant DND permission for Silent mode", Toast.LENGTH_SHORT).show()
                    }
                }
                else -> {
                    am.ringerMode = AudioManager.RINGER_MODE_NORMAL
                }
            }
        } catch (e: Exception) {
            openSoundSettings(context)
        }
        val newState = getRingerState(context)
        _deckState.value = _deckState.value.copy(ringerState = newState)
        return newState
    }

    // ── Auto Rotate ────────────────────────────────────────────────────────────
    fun isAutoRotateEnabled(context: Context): Boolean {
        return try {
            Settings.System.getInt(context.contentResolver, Settings.System.ACCELEROMETER_ROTATION, 0) == 1
        } catch (_: Exception) {
            false
        }
    }

    fun toggleAutoRotate(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Settings.System.canWrite(context)) {
            try {
                val current = isAutoRotateEnabled(context)
                val target = if (current) 0 else 1
                Settings.System.putInt(context.contentResolver, Settings.System.ACCELEROMETER_ROTATION, target)
                _deckState.value = _deckState.value.copy(isAutoRotate = target == 1)
                return
            } catch (_: Exception) {}
        }
        // Fallback: Open Display Settings
        openDisplaySettings(context)
    }

    // ── Do Not Disturb (DND) ───────────────────────────────────────────────────
    fun isDndActive(context: Context): Boolean {
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager ?: return false
        return nm.currentInterruptionFilter != NotificationManager.INTERRUPTION_FILTER_ALL
    }

    fun toggleDnd(context: Context) {
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager ?: return
        if (nm.isNotificationPolicyAccessGranted) {
            try {
                if (isDndActive(context)) {
                    nm.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL)
                    _deckState.value = _deckState.value.copy(isDndActive = false)
                } else {
                    nm.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_PRIORITY)
                    _deckState.value = _deckState.value.copy(isDndActive = true)
                }
                return
            } catch (_: Exception) {}
        }
        // Open DND policy access settings if permission is needed
        try {
            val intent = Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
            Toast.makeText(context, "Enable access for NullVoid to toggle DND", Toast.LENGTH_SHORT).show()
        } catch (_: Exception) {
            openSoundSettings(context)
        }
    }

    // ── Bluetooth & Wi-Fi Shortcuts / State ────────────────────────────────────
    fun isBluetoothActive(context: Context): Boolean {
        return try {
            val bm = context.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager
            bm?.adapter?.isEnabled == true
        } catch (_: Exception) {
            false
        }
    }

    fun isWifiActive(context: Context): Boolean {
        return try {
            val wm = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as? WifiManager
            wm?.isWifiEnabled == true
        } catch (_: Exception) {
            false
        }
    }

    // ── System Setting Launchers ───────────────────────────────────────────────
    fun openDisplaySettings(context: Context) {
        val intent = Intent(Settings.ACTION_DISPLAY_SETTINGS).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        try { context.startActivity(intent) } catch (_: Exception) {}
    }

    fun openHotspotSettings(context: Context) {
        // Try direct Tethering / Hotspot settings intents
        val tetherIntents = listOf(
            Intent().setClassName("com.android.settings", "com.android.settings.TetherSettings"),
            Intent("android.settings.TETHER_SETTINGS"),
            Intent("android.settings.WIRELESS_SETTINGS"),
            Intent(Settings.ACTION_WIRELESS_SETTINGS),
            Intent(Settings.ACTION_SETTINGS)
        )
        for (intent in tetherIntents) {
            try {
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(intent)
                return
            } catch (_: Exception) {}
        }
    }

    fun openSoundSettings(context: Context) {
        val intent = Intent(Settings.ACTION_SOUND_SETTINGS).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        try { context.startActivity(intent) } catch (_: Exception) {}
    }

    fun openBluetoothSettings(context: Context) {
        val intent = Intent(Settings.ACTION_BLUETOOTH_SETTINGS).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        try { context.startActivity(intent) } catch (_: Exception) {}
    }

    fun openWifiSettings(context: Context) {
        val intent = Intent(Settings.ACTION_WIFI_SETTINGS).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        try { context.startActivity(intent) } catch (_: Exception) {}
    }
}
