package com.codershubinc.nullvoidlauncher.ui.controls

import android.content.Context
import android.content.Intent
import android.hardware.camera2.CameraManager
import android.media.AudioManager
import android.provider.Settings

object ControlHelper {

    private var isTorchOn = false

    fun isTorchActive(): Boolean = isTorchOn

    fun toggleTorch(context: Context, onStateChanged: (Boolean) -> Unit) {
        try {
            val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as? CameraManager
            val cameraId = cameraManager?.cameraIdList?.firstOrNull()
            if (cameraManager != null && cameraId != null) {
                isTorchOn = !isTorchOn
                cameraManager.setTorchMode(cameraId, isTorchOn)
                onStateChanged(isTorchOn)
            }
        } catch (_: Exception) {
            isTorchOn = false
            onStateChanged(false)
        }
    }

    enum class RingerState(val label: String) {
        NORMAL("Ring"),
        VIBRATE("Vibrate"),
        SILENT("Silent")
    }

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
        try {
            when (am.ringerMode) {
                AudioManager.RINGER_MODE_NORMAL -> am.ringerMode = AudioManager.RINGER_MODE_VIBRATE
                AudioManager.RINGER_MODE_VIBRATE -> am.ringerMode = AudioManager.RINGER_MODE_SILENT
                else -> am.ringerMode = AudioManager.RINGER_MODE_NORMAL
            }
        } catch (_: Exception) {
            // Some newer Android versions require Do Not Disturb access for SILENT
            val intent = Intent(Settings.ACTION_SOUND_SETTINGS).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            try { context.startActivity(intent) } catch (_: Exception) {}
        }
        return getRingerState(context)
    }

    fun isAutoRotateEnabled(context: Context): Boolean {
        return try {
            Settings.System.getInt(context.contentResolver, Settings.System.ACCELEROMETER_ROTATION, 0) == 1
        } catch (_: Exception) {
            false
        }
    }

    fun openDisplaySettings(context: Context) {
        val intent = Intent(Settings.ACTION_DISPLAY_SETTINGS).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        try { context.startActivity(intent) } catch (_: Exception) {}
    }

    fun openHotspotSettings(context: Context) {
        val intent = Intent(Settings.ACTION_WIRELESS_SETTINGS).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        try {
            context.startActivity(intent)
        } catch (_: Exception) {
            try {
                context.startActivity(Intent(Settings.ACTION_SETTINGS).apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK })
            } catch (_: Exception) {}
        }
    }
}
