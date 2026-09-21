package com.codershubinc.nullvoidlauncher.ui.widgets

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import com.codershubinc.nullvoidlauncher.data.ClockStyle
import com.codershubinc.nullvoidlauncher.ui.widgets.clock.*
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*
import kotlin.time.Duration.Companion.milliseconds


import com.codershubinc.nullvoidlauncher.data.UserManager
import com.codershubinc.nullvoidlauncher.data.WidgetFont

@Composable
fun ClockWidget(
    style: ClockStyle = ClockStyle.ELEGANT,
    modifier: Modifier = Modifier,
    font: WidgetFont? = null,
    previewTimeText: String? = null,
    previewDayText: String? = null,
    previewMonthName: String? = null,
    previewDayOfMonth: String? = null,
    previewBatteryLevel: Int? = null,
    previewBatteryStatus: String? = null,
    onTap: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val haptic = androidx.compose.ui.platform.LocalHapticFeedback.current
    val userManager = remember { UserManager(context) }
    val effectiveFont = font ?: userManager.getClockFont()
    var timeText by remember { mutableStateOf(previewTimeText ?: "") }
    var amPmTimeText by remember { mutableStateOf("") }
    var dayText by remember { mutableStateOf(previewDayText ?: "") }
    var dateText by remember { mutableStateOf("") }
    var dayOfMonth by remember { mutableStateOf(previewDayOfMonth ?: "") }
    var monthName by remember { mutableStateOf(previewMonthName ?: "") }
    var batteryLevel by remember { mutableIntStateOf(previewBatteryLevel ?: 85) }
    var batteryStatus by remember { mutableStateOf(previewBatteryStatus ?: "DISCHARGING") }
    var currentYear by remember { mutableStateOf("") }

    LaunchedEffect(previewTimeText) {
        if (previewTimeText != null) {
            timeText = previewTimeText
            dayText = previewDayText ?: "FRIDAY"
            monthName = previewMonthName ?: "september"
            dayOfMonth = previewDayOfMonth ?: "20"
            batteryLevel = previewBatteryLevel ?: 85
            batteryStatus = previewBatteryStatus ?: "DISCHARGING"
            return@LaunchedEffect
        }
        val timeFormatter = SimpleDateFormat("HH:mm", Locale.getDefault())
        val amPmTimeFormatter = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val dayFormatter = SimpleDateFormat("EEEE", Locale.getDefault())
        val dateFormatter = SimpleDateFormat("dd MMMM, yyyy", Locale.getDefault())
        val dayOfMonthFormatter = SimpleDateFormat("dd", Locale.getDefault())
        val monthNameFormatter = SimpleDateFormat("MMMM", Locale.getDefault())
        
        while (true) {
            val now = Date()
            timeText = timeFormatter.format(now)
            amPmTimeText = amPmTimeFormatter.format(now).replace(" ", "")
            dayText = dayFormatter.format(now).uppercase()
            dateText = dateFormatter.format(now).uppercase()
            dayOfMonth = dayOfMonthFormatter.format(now)
            monthName = monthNameFormatter.format(now).lowercase()
            currentYear = SimpleDateFormat("yyyy", Locale.getDefault()).format(now)
            delay(1000.milliseconds)
        }
    }

    DisposableEffect(context, previewTimeText) {
        if (previewTimeText != null) {
            return@DisposableEffect onDispose {}
        }
        val batteryReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val level = intent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
                val scale = intent?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
                if ((level != -1) && (scale != -1)) {
                    batteryLevel = (level * 100 / scale.toFloat()).toInt()
                }

                val status = intent?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
                batteryStatus = when (status) {
                    BatteryManager.BATTERY_STATUS_CHARGING -> "Charging"
                    BatteryManager.BATTERY_STATUS_DISCHARGING -> "On Battery"
                    BatteryManager.BATTERY_STATUS_FULL -> "Full"
                    BatteryManager.BATTERY_STATUS_NOT_CHARGING -> "Discharging"
                    else -> "On Battery"
                }
            }
        }
        context.registerReceiver(batteryReceiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        onDispose { context.unregisterReceiver(batteryReceiver) }
    }

    fun openDefaultClockApp() {
        val intent = Intent(android.provider.AlarmClock.ACTION_SHOW_ALARMS).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        try {
            context.startActivity(intent)
        } catch (_: Exception) {
            val clockIntent = context.packageManager.getLaunchIntentForPackage("com.google.android.deskclock")
                ?: context.packageManager.getLaunchIntentForPackage("com.sec.android.app.clockpackage")
                ?: Intent(android.provider.Settings.ACTION_DATE_SETTINGS).apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK }
            try { context.startActivity(clockIntent) } catch (_: Exception) {}
        }
    }

    Box(
        modifier = modifier.pointerInput(Unit) {
            detectTapGestures(
                onTap = {
                    if (onTap != null) onTap() else openDefaultClockApp()
                },
                onLongPress = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onLongClick?.invoke()
                }
            )
        }
    ) {
        when (style) {
            ClockStyle.ELEGANT  -> ElegantClock(timeText, dayText, monthName, dayOfMonth, batteryLevel, batteryStatus, font = effectiveFont, onLongClick = onLongClick)
            ClockStyle.MINIMAL  -> MinimalClock(timeText, dayText, monthName, dayOfMonth, batteryLevel, batteryStatus, font = effectiveFont, onLongClick = onLongClick)
            ClockStyle.MODERN   -> ModernClock(timeText, dayText, monthName, dayOfMonth, batteryLevel, batteryStatus, font = effectiveFont, onLongClick = onLongClick)
            ClockStyle.RETRO    -> RetroClock(timeText, dayText, monthName, dayOfMonth, batteryLevel, batteryStatus, font = effectiveFont, onLongClick = onLongClick)
            ClockStyle.TERMINAL -> TerminalClock(timeText, dayText, monthName, dayOfMonth, batteryLevel, batteryStatus, font = effectiveFont, onLongClick = onLongClick)
        }
    }
}
