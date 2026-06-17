package com.codershubinc.nullvoidlauncher.ui.widgets

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codershubinc.nullvoidlauncher.data.ClockStyle
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*


@Composable
fun ClockWidget(style: ClockStyle) {
    val context = LocalContext.current
    var timeText by remember { mutableStateOf("") }
    var dayText by remember { mutableStateOf("") }
    var dateText by remember { mutableStateOf("") }
    var batteryLevel by remember { mutableIntStateOf(-1) }
    var batteryStatus by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        val timeFormatter = SimpleDateFormat("HH:mm", Locale.getDefault())
        val dayFormatter = SimpleDateFormat("EEEE", Locale.getDefault())
        val dateFormatter = SimpleDateFormat("dd MMMM, yyyy", Locale.getDefault())
        while (true) {
            val now = Date()
            timeText = timeFormatter.format(now)
            dayText = dayFormatter.format(now).uppercase()
            dateText = dateFormatter.format(now).uppercase()
            delay(1000)
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

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        when (style) {
            ClockStyle.MINIMAL -> {
                Text(
                    text = timeText,
                    color = Color.White,
                    fontSize = 72.sp,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 4.sp,
                )
                if (batteryLevel != -1) {
                    Spacer(modifier = Modifier.height(8.dp))
                    val statusDisplay =
                        if (batteryStatus.isNotEmpty() && batteryStatus != "UNKNOWN") " [$batteryStatus]" else ""
                    Text(
                        text = "BAT $batteryLevel%$statusDisplay",
                        color = Color.DarkGray,
                        fontSize = 16.sp,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 2.sp
                    )
                }
            }

            ClockStyle.TERMINAL -> {
                Text(
                    text = "> TIME: $timeText",
                    color = Color.White,
                    fontSize = 48.sp,
                    fontFamily = FontFamily.Monospace
                )
                if (batteryLevel != -1) {
                    val statusDisplay =
                        if (batteryStatus.isNotEmpty() && batteryStatus != "UNKNOWN") " ($batteryStatus)" else ""
                    Text(
                        text = "> BATT: $batteryLevel%$statusDisplay",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontFamily = FontFamily.Monospace,

                        )
                }
                Text(
                    text = "> STATUS: ONLINE",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontFamily = FontFamily.Monospace,
                    textAlign = TextAlign.Start
                )
            }

            ClockStyle.BOLD -> {
                Text(
                    text = timeText,
                    color = Color.White,
                    fontSize = 110.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.SansSerif
                )
                if (batteryLevel != -1) {
                    Text(
                        text = "$batteryLevel%",
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Light
                    )
                }
            }

            ClockStyle.VERTICAL -> {
                Column(horizontalAlignment = Alignment.Start) {
                    Text(
                        text = timeText,
                        color = Color.White,
                        fontSize = 64.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    )
                    if (batteryLevel != -1) {
                        val statusDisplay = if (batteryStatus.isNotEmpty() && batteryStatus != "UNKNOWN") " $batteryStatus" else ""
                        Text(
                            text = "LVL $batteryLevel%$statusDisplay",
                            color = Color.DarkGray,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 1.sp,
                        )
                    }
                }
            }

            ClockStyle.VOID -> {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = timeText,
                        color = Color.White,
                        fontSize = 120.sp,
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.ExtraLight,
                        letterSpacing = (-8).sp,
                        modifier = Modifier.graphicsLayer { alpha = 0.8f }
                    )
                    Box(
                        modifier = Modifier
                            .width(200.dp)
                            .height(1.dp)
                            .background(Color.White.copy(alpha = 0.2f))
                    )
                    Text(
                        text = "SYSTEM_VOID_ACTIVE",
                        color = Color.White.copy(alpha = 0.4f),
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 4.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }

            ClockStyle.MODERN -> {
                Column(horizontalAlignment = Alignment.Start) {
                    Text(
                        text = "GOOD MOMENT,",
                        color = Color.White.copy(alpha = 0.4f),
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 2.sp
                    )
                    Text(
                        text = "IT'S $dayText",
                        color = Color.White,
                        fontSize = 54.sp,
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Black,
                        letterSpacing = (-2).sp
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = dateText,
                            color = Color.White.copy(alpha = 0.6f),
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "| $timeText",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
