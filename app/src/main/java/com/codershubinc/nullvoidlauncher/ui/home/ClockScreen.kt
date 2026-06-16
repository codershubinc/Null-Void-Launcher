package com.codershubinc.nullvoidlauncher.ui.home

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.text.Layout
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.codershubinc.nullvoidlauncher.data.ClockStyle

@Composable
fun ClockScreen(isDrawerOpen: Boolean, clockStyle: ClockStyle, onOpenDrawer: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(isDrawerOpen) {
                if (!isDrawerOpen) {
                    detectVerticalDragGestures { change, dragAmount ->
                        change.consume()
                        if (dragAmount < -20) { onOpenDrawer() }
                    }
                }
            },
        contentAlignment = Alignment.Center
    ) {
        ClockWidget(clockStyle)
    }
}

@Composable
fun ClockWidget(style: ClockStyle) {
    val context = LocalContext.current
    var timeText by remember { mutableStateOf("") }
    var batteryLevel by remember { mutableStateOf(-1) }
    var batteryStatus by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        val formatter = SimpleDateFormat("HH:mm", Locale.getDefault())
        while (true) {
            timeText = formatter.format(Date())
            delay(1000)
        }
    }

    DisposableEffect(context) {
        val batteryReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val level = intent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
                val scale = intent?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
                if (level != -1 && scale != -1) {
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

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        when (style) {
            ClockStyle.MINIMAL -> {
                Text(
                    text = timeText,
                    color = Color.White,
                    fontSize = 72.sp,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 4.sp
                )
                if (batteryLevel != -1) {
                    Spacer(modifier = Modifier.height(8.dp))
                    val statusDisplay = if (batteryStatus.isNotEmpty() && batteryStatus != "UNKNOWN") " [$batteryStatus]" else ""
                    Text(text = "BAT $batteryLevel%$statusDisplay", color = Color.DarkGray, fontSize = 16.sp, fontFamily = FontFamily.Monospace, letterSpacing = 2.sp)
                }
            }
            ClockStyle.TERMINAL -> {
                Text(
                    text = "> TIME: $timeText",
                    color = Color.Green,
                    fontSize = 48.sp,
                    fontFamily = FontFamily.Monospace
                )
                if (batteryLevel != -1) {
                    val statusDisplay = if (batteryStatus.isNotEmpty() && batteryStatus != "UNKNOWN") " ($batteryStatus)" else ""
                    Text(
                        text = "> BATT: $batteryLevel%$statusDisplay",
                        color = Color.Green,
                        fontSize = 18.sp,
                        fontFamily = FontFamily.Monospace,

                    )
                }
                Text(
                    text = "> STATUS: ONLINE",
                    color = Color.Green,
                    fontSize = 18.sp,
                    fontFamily = FontFamily.Monospace,
                    textAlign = TextAlign.Start
                )
            }
            ClockStyle.BOLD -> {
                Text(text = timeText, color = Color.White, fontSize = 110.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.SansSerif)
                if (batteryLevel != -1) {
                    Text(text = "$batteryLevel%", color = Color.White.copy(alpha = 0.5f), fontSize = 24.sp, fontWeight = FontWeight.Light)
                }
            }
        }
    }
}
