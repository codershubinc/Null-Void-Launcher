package com.codershubinc.nullvoidlauncher.ui.home

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
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

@Composable
fun HomeScreen() {
    var isDrawerOpen by remember { mutableStateOf(false) }

    // Intercept the system Back button to close the drawer
    BackHandler(enabled = isDrawerOpen) {
        isDrawerOpen = false
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // --- 1. Background Layer (Clock & Swipe Up Area) ---
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .pointerInput(isDrawerOpen) {
                    // Only listen for swipes if the drawer is closed
                    if (!isDrawerOpen) {
                        detectVerticalDragGestures { change, dragAmount ->
                            change.consume()
                            if (dragAmount < -20) { // Swipe UP
                                isDrawerOpen = true
                            }
                        }
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            ClockWidget()
        }

        // --- 2. The App Drawer Overlay ---
        AnimatedVisibility(
            visible = isDrawerOpen,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            modifier = Modifier.fillMaxSize()
        ) {
            AppDrawerScreen(
                onClose = { isDrawerOpen = false }
            )
        }
    }
}

@Composable
fun ClockWidget() {
    val context = LocalContext.current
    var timeText by remember { mutableStateOf("") }
    var batteryLevel by remember { mutableStateOf(-1) }
    var batteryStatus by remember { mutableStateOf("") }

    // Update the time every second
    LaunchedEffect(Unit) {
        val formatter = SimpleDateFormat("HH:mm", Locale.getDefault())
        while (true) {
            timeText = formatter.format(Date())
            delay(1000)
        }
    }

    // Efficiently listen for battery updates from the system
    DisposableEffect(context) {
        val batteryReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                // Get Level
                val level = intent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
                val scale = intent?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
                if (level != -1 && scale != -1) {
                    batteryLevel = (level * 100 / scale.toFloat()).toInt()
                }

                // Get Status
                val status = intent?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
                batteryStatus = when (status) {
                    BatteryManager.BATTERY_STATUS_CHARGING -> "CHARGING"
                    BatteryManager.BATTERY_STATUS_DISCHARGING -> "DISCHARGING"
                    BatteryManager.BATTERY_STATUS_FULL -> "FULL"
                    BatteryManager.BATTERY_STATUS_NOT_CHARGING -> "NOT CHARGING"
                    else -> "UNKNOWN"
                }
            }
        }

        // Register the receiver
        context.registerReceiver(batteryReceiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))

        // Clean up the receiver if the widget is ever removed
        onDispose {
            context.unregisterReceiver(batteryReceiver)
        }
    }

    // Wrap in a Column to stack them vertically
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = timeText,
            color = Color.White,
            fontSize = 72.sp,
            fontFamily = FontFamily.Monospace,
            letterSpacing = 4.sp
        )

        // Only render the battery text once we have a valid reading
        if (batteryLevel != -1) {
            Spacer(modifier = Modifier.height(8.dp))

            // Format the text to look like a clean terminal output
            val statusDisplay = if (batteryStatus.isNotEmpty() && batteryStatus != "UNKNOWN") {
                " [$batteryStatus]"
            } else ""

            Text(
                text = "BAT $batteryLevel%$statusDisplay",
                color = Color.DarkGray,
                fontSize = 16.sp,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 2.sp
            )
        }
    }
}