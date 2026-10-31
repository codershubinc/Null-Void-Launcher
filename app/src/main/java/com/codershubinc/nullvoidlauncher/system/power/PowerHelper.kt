package com.codershubinc.nullvoidlauncher.system.power

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.PowerManager
import android.provider.Settings
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

data class PowerInfoState(
    val level: Int = 85,
    val isCharging: Boolean = false,
    val status: String = "Discharging",
    val temperatureC: Float = 31.0f,
    val voltageV: Float = 4.1f,
    val health: String = "Good",
    val isPowerSaveMode: Boolean = false
) {
    val displayPercentage: String
        get() = "$level%"

    val displayTemperature: String
        get() = String.format(java.util.Locale.US, "%.1f°C", temperatureC)

    val displayVoltage: String
        get() = String.format(java.util.Locale.US, "%.2fV", voltageV)
}

object PowerHelper {

    fun openBatterySettings(context: Context) {
        val intent = Intent(Intent.ACTION_POWER_USAGE_SUMMARY).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        try {
            context.startActivity(intent)
        } catch (_: Exception) {
            val fallback = Intent(Settings.ACTION_BATTERY_SAVER_SETTINGS).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            try {
                context.startActivity(fallback)
            } catch (_: Exception) {
                val sysSettings = Intent(Settings.ACTION_SETTINGS).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                try { context.startActivity(sysSettings) } catch (_: Exception) {}
            }
        }
    }

    fun getPowerInfo(context: Context): PowerInfoState {
        val appContext = context.applicationContext
        val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        val batteryIntent = appContext.registerReceiver(null, filter)

        val rawLevel = batteryIntent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: 85
        val scale = batteryIntent?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: 100
        val level = if (rawLevel >= 0 && scale > 0) {
            ((rawLevel / scale.toFloat()) * 100).toInt().coerceIn(0, 100)
        } else 85

        val statusInt = batteryIntent?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
        val isCharging = statusInt == BatteryManager.BATTERY_STATUS_CHARGING ||
                statusInt == BatteryManager.BATTERY_STATUS_FULL

        val status = when (statusInt) {
            BatteryManager.BATTERY_STATUS_CHARGING -> "Charging"
            BatteryManager.BATTERY_STATUS_DISCHARGING -> "Discharging"
            BatteryManager.BATTERY_STATUS_FULL -> "Full"
            BatteryManager.BATTERY_STATUS_NOT_CHARGING -> "Not Charging"
            else -> if (isCharging) "Charging" else "On Battery"
        }

        val rawTemp = batteryIntent?.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 310) ?: 310
        val temperatureC = rawTemp / 10.0f

        val rawVoltage = batteryIntent?.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 4100) ?: 4100
        val voltageV = if (rawVoltage > 100) rawVoltage / 1000.0f else rawVoltage.toFloat()

        val healthInt = batteryIntent?.getIntExtra(BatteryManager.EXTRA_HEALTH, BatteryManager.BATTERY_HEALTH_GOOD)
            ?: BatteryManager.BATTERY_HEALTH_GOOD
        val health = when (healthInt) {
            BatteryManager.BATTERY_HEALTH_GOOD -> "Good"
            BatteryManager.BATTERY_HEALTH_OVERHEAT -> "Overheat"
            BatteryManager.BATTERY_HEALTH_DEAD -> "Dead"
            BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> "Over Voltage"
            BatteryManager.BATTERY_HEALTH_COLD -> "Cold"
            else -> "Normal"
        }

        val pm = appContext.getSystemService(Context.POWER_SERVICE) as? PowerManager
        val isPowerSaveMode = pm?.isPowerSaveMode ?: false

        return PowerInfoState(
            level = level,
            isCharging = isCharging,
            status = status,
            temperatureC = temperatureC,
            voltageV = voltageV,
            health = health,
            isPowerSaveMode = isPowerSaveMode
        )
    }

    fun observePowerInfo(context: Context): Flow<PowerInfoState> = callbackFlow {
        val appContext = context.applicationContext
        trySend(getPowerInfo(appContext))

        val receiver = object : BroadcastReceiver() {
            override fun onReceive(c: Context?, intent: Intent?) {
                trySend(getPowerInfo(appContext))
            }
        }
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_BATTERY_CHANGED)
            addAction(PowerManager.ACTION_POWER_SAVE_MODE_CHANGED)
        }
        try {
            appContext.registerReceiver(receiver, filter)
        } catch (_: Exception) {}

        awaitClose {
            try {
                appContext.unregisterReceiver(receiver)
            } catch (_: Exception) {}
        }
    }
}
