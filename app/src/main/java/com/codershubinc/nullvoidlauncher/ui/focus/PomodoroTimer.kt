package com.codershubinc.nullvoidlauncher.ui.focus

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

@Composable
fun PomodoroTimer(modifier: Modifier = Modifier) {
    var presetMinutes by remember { mutableIntStateOf(25) }
    var timerSeconds by remember { mutableIntStateOf(presetMinutes * 60) }
    var isTimerRunning by remember { mutableStateOf(value = false) }
    var showCustomTimeDialog by remember { mutableStateOf(value = false) }

    LaunchedEffect(isTimerRunning) {
        if (isTimerRunning) {
            while ((timerSeconds > 0) && isTimerRunning) {
                delay(1.seconds)
                timerSeconds--
            }
            if (timerSeconds == 0) {
                isTimerRunning = false
            }
        }
    }

    val minutes = timerSeconds / 60
    val seconds = timerSeconds % 60
    val timerDisplay = "%02d:%02d".format(minutes, seconds)

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = timerDisplay,
            color = Color.White,
            fontSize = 120.sp,
            fontWeight = FontWeight.ExtraLight,
            fontFamily = FontFamily.Monospace,
            letterSpacing = (-8).sp,
            modifier = Modifier.clickable { showCustomTimeDialog = true },
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
    }

    if (showCustomTimeDialog) {
        CustomTimeDialog(
            presetMinutes = presetMinutes,
            onDismiss = { showCustomTimeDialog = false }
        ) { mins, secs ->
            presetMinutes = mins.coerceAtLeast(1)
            timerSeconds = (mins * 60) + secs
            isTimerRunning = false
            showCustomTimeDialog = false
        }
    }
}

@Composable
fun CustomTimeDialog(
    presetMinutes: Int,
    onDismiss: () -> Unit,
    onApply: (Int, Int) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
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
                    TimeInputBox(
                        label = "MINUTES",
                        value = inputMinutes,
                    ) { newValue ->
                        if (newValue.length <= 2 && newValue.all { it.isDigit() }) {
                            inputMinutes = newValue
                        }
                    }

                    Text(
                        text = ":",
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 32.sp,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(start = 12.dp, end = 12.dp, top = 16.dp)
                    )

                    TimeInputBox(
                        label = "SECONDS",
                        value = inputSeconds,
                    ) { newValue ->
                        if (newValue.length <= 2 && newValue.all { it.isDigit() }) {
                            inputSeconds = newValue
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(bottom = 24.dp)
                ) {
                    listOf(10, 25, 45, 60).forEach { mins ->
                        PresetChip(mins) {
                            inputMinutes = mins.toString()
                            inputSeconds = "00"
                        }
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    DialogButton(
                        text = "CANCEL",
                        isPrimary = false,
                        modifier = Modifier.weight(1f),
                        onClick = onDismiss
                    )
                    DialogButton(
                        text = "APPLY",
                        isPrimary = true,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            val mins = inputMinutes.toIntOrNull() ?: 0
                            val secs = inputSeconds.toIntOrNull() ?: 0
                            onApply(mins, secs)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun TimeInputBox(label: String, value: String, onValueChange: (String) -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
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
                value = value,
                onValueChange = onValueChange,
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
                        if (value.isEmpty()) {
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

@Composable
fun PresetChip(minutes: Int, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .background(Color(0xFF1C1C22), shape = RoundedCornerShape(6.dp))
            .border(1.dp, Color.White.copy(alpha = 0.05f), shape = RoundedCornerShape(6.dp))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = "${minutes}m",
            color = Color.Gray,
            fontSize = 12.sp,
            fontFamily = FontFamily.Monospace
        )
    }
}

@Composable
fun DialogButton(text: String, isPrimary: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .background(if (isPrimary) Color.White else Color.Transparent, shape = RoundedCornerShape(8.dp))
            .then(
                if (!isPrimary) Modifier.border(1.dp, Color.White.copy(alpha = 0.1f), shape = RoundedCornerShape(8.dp))
                else Modifier
            )
            .clickable { onClick() }
            .padding(vertical = 12.dp)
    ) {
        Text(
            text = text,
            color = if (isPrimary) Color.Black else Color.Gray,
            fontSize = 12.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold
        )
    }
}
