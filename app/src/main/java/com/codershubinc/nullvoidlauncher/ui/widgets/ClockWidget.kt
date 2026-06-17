package com.codershubinc.nullvoidlauncher.ui.widgets

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import com.codershubinc.nullvoidlauncher.data.ClockStyle
import com.codershubinc.nullvoidlauncher.ui.widgets.clock.*
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*
import kotlin.time.Duration.Companion.milliseconds


@Composable
fun ClockWidget(style: ClockStyle) {
    val context = LocalContext.current
    var timeText by remember { mutableStateOf("") }
    var amPmTimeText by remember { mutableStateOf("") }
    var dayText by remember { mutableStateOf("") }
    var dateText by remember { mutableStateOf("") }
    var dayOfMonth by remember { mutableStateOf("") }
    var monthName by remember { mutableStateOf("") }
    var batteryLevel by remember { mutableIntStateOf(-1) }
    var batteryStatus by remember { mutableStateOf("") }
    var currentYear by remember { mutableStateOf( "") }

    LaunchedEffect(Unit) {
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

    DisposableEffect(context) {
        val batteryReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val level = intent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
                val scale = intent?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
                if ((level != -1) && (scale != -1)) {
                    batteryLevel = (level * 100 / scale.toFloat()).toInt()
                }

                val status = intent?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
                batteryStatus = when (status) {
                    BatteryManager.BATTERY_STATUS_CHARGING -> "CHARGING"
                    BatteryManager.BATTERY_STATUS_DISCHARGING -> "!CHARGING"
                    BatteryManager.BATTERY_STATUS_FULL -> "FULL"
                    BatteryManager.BATTERY_STATUS_NOT_CHARGING -> "!CHARGING"
                    else -> "UNKNOWN"
                }
            }
        }
        context.registerReceiver(batteryReceiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        onDispose { context.unregisterReceiver(batteryReceiver) }
    }

    val modifier = if (style == ClockStyle.VERTICAL || style == ClockStyle.MODERN) {
        Modifier.graphicsLayer {
            rotationZ = -90f
            transformOrigin = androidx.compose.ui.graphics.TransformOrigin(0f, 0f)
            translationY = size.width
        }
    } else {
        Modifier
    }

    Box(modifier = modifier) {
        when (style) {
            ClockStyle.MINIMAL -> MinimalClock(timeText, batteryLevel, batteryStatus)
            ClockStyle.TERMINAL -> TerminalClock(timeText, batteryLevel, batteryStatus)
            ClockStyle.BOLD -> BoldClock(timeText, batteryLevel)
            ClockStyle.VERTICAL -> VerticalClock(timeText, batteryLevel, batteryStatus)
            ClockStyle.VOID -> VoidClock(timeText)
            ClockStyle.MODERN -> ModernClock(dayText, dateText, timeText)
            ClockStyle.PIXEL -> PixelClock(dayOfMonth, monthName , weekName = dayText , year = currentYear )
            ClockStyle.ELEGANT -> ElegantClock(timeText, dayText, monthName, dayOfMonth, batteryLevel, batteryStatus)
        }
    }
}
