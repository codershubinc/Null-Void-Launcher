package com.codershubinc.nullvoidlauncher.ui.focus

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.codershubinc.nullvoidlauncher.ui.music.MediaService
import com.codershubinc.nullvoidlauncher.ui.music.MusicTrack
import com.codershubinc.nullvoidlauncher.ui.widgets.music.ElegantMusicWidget
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

@Composable
fun FocusModeScreen(onClose: () -> Unit) {
    val context = LocalContext.current
    var timeText by remember { mutableStateOf("") }
    var dateText by remember { mutableStateOf("") }
    var batteryLevel by remember { mutableIntStateOf(-1) }
    var isCharging by remember { mutableStateOf(false) }

    var presetMinutes by remember { mutableIntStateOf(25) }
    var timerSeconds by remember { mutableIntStateOf(presetMinutes * 60) }
    var isTimerRunning by remember { mutableStateOf(false) }
    var showCustomTimeDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val timeFormatter = SimpleDateFormat("HH:mm", Locale.getDefault())
        val dateFormatter = SimpleDateFormat("EEEE, MMMM dd", Locale.getDefault())
        
        while (true) {
            val now = Date()
            timeText = timeFormatter.format(now)
            dateText = dateFormatter.format(now).uppercase()
            
            delay(1000.milliseconds)
        }
    }

    LaunchedEffect(isTimerRunning) {
        if (isTimerRunning) {
            while (timerSeconds > 0 && isTimerRunning) {
                delay(1.seconds)
                timerSeconds--
            }
            if (timerSeconds == 0) {
                isTimerRunning = false
            }
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
                isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING || 
                             status == BatteryManager.BATTERY_STATUS_FULL
            }
        }
        context.registerReceiver(batteryReceiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        onDispose { context.unregisterReceiver(batteryReceiver) }
    }

    val quotes = remember {
        listOf(
            "STAY FOCUSED.",
            "DEEP WORK ONLY.",
            "MINIMALISM IS KEY.",
            "EYES ON THE PRIZE.",
            "LESS IS MORE.",
            "KEEP PUSHING.",
            "SILENCE IS POWER."
        )
    }
    val quote = remember { quotes.random() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(24.dp)
    ) {
        // Battery info (Top Start)
        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
                .statusBarsPadding()
                .padding(top = 8.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (isCharging) {
                    Icon(
                        imageVector = Icons.Rounded.BatteryChargingFull,
                        contentDescription = null,
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                }
                Text(
                    text = "$batteryLevel%",
                    color = if (batteryLevel < 20) Color.Red else Color.DarkGray,
                    fontSize = 14.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
            Text(
                text = "SYSTEM_READY",
                color = Color.DarkGray.copy(alpha = 0.5f),
                fontSize = 8.sp,
                fontFamily = FontFamily.Monospace
            )
        }

        // Close Button
        Icon(
            imageVector = Icons.Rounded.Close,
            contentDescription = "Exit Focus Mode",
            tint = Color.DarkGray,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .statusBarsPadding()
                .size(32.dp)
                .clickable { onClose() }
        )

        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Big Pomodoro Timer
            val minutes = timerSeconds / 60
            val seconds = timerSeconds % 60
            val timerDisplay = "%02d:%02d".format(minutes, seconds)

            Text(
                text = timerDisplay,
                color = Color.White,
                fontSize = 140.sp,
                fontWeight = FontWeight.ExtraLight,
                fontFamily = FontFamily.Monospace,
                letterSpacing = (-8).sp,
                modifier = Modifier.clickable {
                    showCustomTimeDialog = true
                }
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                if (!isTimerRunning) {
                    Icon(
                        imageVector = Icons.Rounded.Remove,
                        contentDescription = "Decrease Time",
                        tint = Color.White.copy(alpha = 0.2f),
                        modifier = Modifier
                            .size(24.dp)
                            .clickable {
                                if (presetMinutes > 5) {
                                    presetMinutes -= 5
                                    timerSeconds = presetMinutes * 60
                                }
                            }
                    )
                }

                Icon(
                    imageVector = if (isTimerRunning) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                    contentDescription = "Start/Pause Timer",
                    tint = Color.White.copy(alpha = 0.4f),
                    modifier = Modifier
                        .size(32.dp)
                        .clickable { isTimerRunning = !isTimerRunning }
                )

                if (!isTimerRunning) {
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = "Increase Time",
                        tint = Color.White.copy(alpha = 0.2f),
                        modifier = Modifier
                            .size(24.dp)
                            .clickable {
                                if (presetMinutes < 120) {
                                    presetMinutes += 5
                                    timerSeconds = presetMinutes * 60
                                }
                            }
                    )
                }

                Icon(
                    imageVector = Icons.Rounded.Refresh,
                    contentDescription = "Reset Timer",
                    tint = Color.White.copy(alpha = 0.4f),
                    modifier = Modifier
                        .size(32.dp)
                        .clickable {
                            isTimerRunning = false
                            timerSeconds = presetMinutes * 60
                        }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "[ +1 MIN ]",
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .clickable {
                            if (isTimerRunning) {
                                timerSeconds += 60
                            } else {
                                presetMinutes += 1
                                timerSeconds = presetMinutes * 60
                            }
                        }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )

                Text(
                    text = "[ CUSTOM ]",
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .clickable {
                            showCustomTimeDialog = true
                        }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Current Time and Date (Smaller)
            Text(
                text = timeText,
                color = Color.Gray,
                fontSize = 32.sp,
                fontWeight = FontWeight.Light,
                fontFamily = FontFamily.Monospace
            )
            Text(
                text = dateText,
                color = Color.DarkGray,
                fontSize = 14.sp,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.sp
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Text(
                text = quote,
                color = Color.DarkGray.copy(alpha = 0.3f),
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 4.sp
            )
        }

        // Elegant Music Widget (Bottom)
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
        ) {
            ElegantMusicWidget()
        }

        // Custom Time Input Dialog
        if (showCustomTimeDialog) {
            Dialog(onDismissRequest = { showCustomTimeDialog = false }) {
                var inputMinutes by remember { mutableStateOf(presetMinutes.toString()) }
                var inputSeconds by remember { mutableStateOf("00") }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .background(Color(0xFF0F0F12), shape = RoundedCornerShape(16.dp))
                        .border(1.dp, Color.White.copy(alpha = 0.15f), shape = RoundedCornerShape(16.dp))
                        .padding(24.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "> SET FOCUS TIME",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // Minutes Input Box
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "MINUTES",
                                    color = Color.Gray,
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace,
                                    modifier = Modifier.padding(bottom = 6.dp)
                                )
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .width(80.dp)
                                        .background(Color(0xFF16161A), shape = RoundedCornerShape(8.dp))
                                        .border(1.dp, Color.White.copy(alpha = 0.1f), shape = RoundedCornerShape(8.dp))
                                        .padding(vertical = 12.dp)
                                ) {
                                    BasicTextField(
                                        value = inputMinutes,
                                        onValueChange = { newValue ->
                                            if (newValue.length <= 2 && newValue.all { it.isDigit() }) {
                                                inputMinutes = newValue
                                            }
                                        },
                                        textStyle = TextStyle(
                                            color = Color.White,
                                            fontSize = 28.sp,
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Medium,
                                            textAlign = TextAlign.Center
                                        ),
                                        cursorBrush = SolidColor(Color.White),
                                        singleLine = true,
                                        decorationBox = { innerTextField ->
                                            Box(
                                                contentAlignment = Alignment.Center,
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                if (inputMinutes.isEmpty()) {
                                                    Text(
                                                        text = "00",
                                                        color = Color.DarkGray,
                                                        fontSize = 28.sp,
                                                        fontFamily = FontFamily.Monospace,
                                                        fontWeight = FontWeight.Medium,
                                                        textAlign = TextAlign.Center
                                                    )
                                                }
                                                innerTextField()
                                            }
                                        }
                                    )
                                }
                            }

                            Text(
                                text = ":",
                                color = Color.White.copy(alpha = 0.5f),
                                fontSize = 32.sp,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.padding(start = 12.dp, end = 12.dp, top = 16.dp)
                            )

                            // Seconds Input Box
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "SECONDS",
                                    color = Color.Gray,
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace,
                                    modifier = Modifier.padding(bottom = 6.dp)
                                )
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .width(80.dp)
                                        .background(Color(0xFF16161A), shape = RoundedCornerShape(8.dp))
                                        .border(1.dp, Color.White.copy(alpha = 0.1f), shape = RoundedCornerShape(8.dp))
                                        .padding(vertical = 12.dp)
                                ) {
                                    BasicTextField(
                                        value = inputSeconds,
                                        onValueChange = { newValue ->
                                            if (newValue.length <= 2 && newValue.all { it.isDigit() }) {
                                                inputSeconds = newValue
                                            }
                                        },
                                        textStyle = TextStyle(
                                            color = Color.White,
                                            fontSize = 28.sp,
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Medium,
                                            textAlign = TextAlign.Center
                                        ),
                                        cursorBrush = SolidColor(Color.White),
                                        singleLine = true,
                                        decorationBox = { innerTextField ->
                                            Box(
                                                contentAlignment = Alignment.Center,
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                if (inputSeconds.isEmpty()) {
                                                    Text(
                                                        text = "00",
                                                        color = Color.DarkGray,
                                                        fontSize = 28.sp,
                                                        fontFamily = FontFamily.Monospace,
                                                        fontWeight = FontWeight.Medium,
                                                        textAlign = TextAlign.Center
                                                    )
                                                }
                                                innerTextField()
                                            }
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Preset Chips
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(bottom = 24.dp)
                        ) {
                            listOf(10, 25, 45, 60).forEach { mins ->
                                Box(
                                    modifier = Modifier
                                        .background(Color(0xFF1C1C22), shape = RoundedCornerShape(6.dp))
                                        .border(1.dp, Color.White.copy(alpha = 0.05f), shape = RoundedCornerShape(6.dp))
                                        .clickable {
                                            inputMinutes = mins.toString()
                                            inputSeconds = "00"
                                        }
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = "${mins}m",
                                        color = Color.Gray,
                                        fontSize = 12.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                        }

                        // Action buttons
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // Cancel Button
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .weight(1f)
                                    .background(Color.Transparent, shape = RoundedCornerShape(8.dp))
                                    .border(1.dp, Color.White.copy(alpha = 0.1f), shape = RoundedCornerShape(8.dp))
                                    .clickable {
                                        showCustomTimeDialog = false
                                    }
                                    .padding(vertical = 12.dp)
                            ) {
                                Text(
                                    text = "CANCEL",
                                    color = Color.Gray,
                                    fontSize = 12.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            // Apply Button
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .weight(1f)
                                    .background(Color.White, shape = RoundedCornerShape(8.dp))
                                    .clickable {
                                        val mins = inputMinutes.toIntOrNull() ?: 0
                                        val secs = inputSeconds.toIntOrNull() ?: 0
                                        val total = mins * 60 + secs
                                        if (total > 0) {
                                            presetMinutes = mins.coerceAtLeast(1)
                                            timerSeconds = total
                                            isTimerRunning = false
                                        }
                                        showCustomTimeDialog = false
                                    }
                                    .padding(vertical = 12.dp)
                            ) {
                                Text(
                                    text = "APPLY",
                                    color = Color.Black,
                                    fontSize = 12.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
