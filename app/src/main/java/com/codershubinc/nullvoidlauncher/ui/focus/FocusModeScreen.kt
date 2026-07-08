package com.codershubinc.nullvoidlauncher.ui.focus

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.tv.TvContract
import android.os.BatteryManager
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codershubinc.nullvoidlauncher.ui.widgets.music.ElegantMusicWidget
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun FocusModeScreen(onClose: () -> Unit) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val isTablet = configuration.screenWidthDp > 600
    
    var timeText by remember { mutableStateOf("") }
    var dateText by remember { mutableStateOf("") }
    var batteryLevel by remember { mutableIntStateOf(-1) }
    var isCharging by remember { mutableStateOf(value = false) }

    var activeEvent by remember { mutableStateOf<TimelineEvent?>(null) }
    var activeProgress by remember { mutableIntStateOf(0) }
    var activeMinutesLeft by remember { mutableIntStateOf(0) }
    var serviceStatus by remember { mutableStateOf(ServiceStatus.CONNECTING) }

    val scrollState = rememberScrollState()

    val activePhaseService = remember {
        ActivePhaseService(
            serverUrl = "http://10.141.206.44:3000",
            userId = "ingleswapnil2004@gmail.com",
            onStatusChanged = { status -> serviceStatus = status }
        ) { event, progress, minutesLeft ->
            activeEvent = event
            activeProgress = progress
            activeMinutesLeft = minutesLeft
        }
    }

    DisposableEffect(Unit) {
        activePhaseService.startListening()
        onDispose { activePhaseService.stopListening() }
    }

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

    DisposableEffect(context) {
        val batteryReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val level = intent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
                val scale = intent?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
                if ((level != -1) && (scale != -1)) {
                    batteryLevel = ((level * 100) / scale.toFloat()).toInt()
                }

                val status = intent?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
                isCharging = status in listOf(
                    BatteryManager.BATTERY_STATUS_CHARGING,
                    BatteryManager.BATTERY_STATUS_FULL,
                )
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
            "SILENCE IS POWER.",
        )
    }
    val quote = remember { quotes.random() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Top Dashboard Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Battery Section (Glassy Chip)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(12.dp))
                    .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                if (isCharging) {
                    Icon(
                        imageVector = Icons.Rounded.BatteryChargingFull,
                        contentDescription = null,
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                }
                Text(
                    text = "$batteryLevel%",
                    color = if (batteryLevel < 20) Color.Red else Color.White.copy(alpha = 0.7f),
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
                
                // Status Indicator
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(
                            when (serviceStatus) {
                                ServiceStatus.CONNECTED -> Color(0xFF4CAF50)
                                ServiceStatus.CONNECTING -> Color.Yellow
                                ServiceStatus.FALLBACK -> Color.Cyan
                                else -> Color.Red
                            }
                        )
                )
            }

            // Reconnect Button (Shown on error or disconnect)
            if (serviceStatus == ServiceStatus.ERROR || serviceStatus == ServiceStatus.DISCONNECTED || serviceStatus == ServiceStatus.FALLBACK) {
                Box(
                    modifier = Modifier
                        .padding(end = 16.dp)
                        .background(Color.Red.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                        .border(1.dp, Color.Red.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                        .clickable { activePhaseService.startListening() }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Rounded.Refresh,
                            contentDescription = "Retry",
                            tint = Color.Red.copy(alpha = 0.7f),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "RECONNECT",
                            color = Color.Red.copy(alpha = 0.7f),
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Close Button
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White.copy(alpha = 0.05f))
                    .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                    .clickable { onClose() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.Close,
                    contentDescription = "Exit",
                    tint = Color.White.copy(alpha = 0.7f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // Main Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(100.dp)) // Safe space for top bar

            if (activeEvent != null) {
                // FORCE LANDSCAPE LAYOUT (Side-by-Side)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 48.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    // LEFT: BIG TIMER
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1.2f)
                    ) {
                        Text(
                            text = "● ${activeEvent?.title?.uppercase() ?: "ACTIVE PHASE"}",
                            color = Color(0xFF4CAF50),
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 3.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "%02d".format(activeMinutesLeft),
                            color = Color.White,
                            fontSize = if (isTablet) 200.sp else 160.sp,
                            fontWeight = FontWeight.ExtraLight,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = (-12).sp
                        )
                        Text(
                            text = "MINUTES REMAINING",
                            color = Color.White.copy(alpha = 0.4f),
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 4.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(48.dp))

                    // RIGHT: DETAILS & PROTOCOLS
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .background(
                                brush = Brush.verticalGradient(
                                    listOf(Color.White.copy(alpha = 0.07f), Color.Transparent)
                                ),
                                shape = RoundedCornerShape(32.dp)
                            )
                            .border(
                                1.dp,
                                Color.White.copy(alpha = 0.15f),
                                RoundedCornerShape(32.dp)
                            )
                            .padding(32.dp)
                    ) {
                        Text(
                            text = activeEvent?.coreAction ?: "",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Light,
                            lineHeight = 26.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = buildAnnotatedString {
                                append(activeEvent?.category?.uppercase() ?: "" )
                                append(" • ")
                                withStyle(style = SpanStyle(color = Color.White, fontSize = 20.sp))  {
                                    append(activeEvent?.startTime ?: "")
                                }
                                append(" - ")

                                withStyle(style = SpanStyle(color = Color.White, fontSize = 20.sp)){
                                    append(activeEvent?.endTime ?: "")
                                }
                            },
                            color = Color.White.copy(alpha = 0.3f),
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace

                        )
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        // Progress Bar
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(2.dp)
                                .background(Color.White.copy(alpha = 0.1f))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .fillMaxWidth(activeProgress / 100f)
                                    .background(Color.White)
                            )
                        }

                        val tasks = activeEvent?.tasks ?: emptyList()
                        if (tasks.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(32.dp))
                            Text(
                                text = "PROTOCOLS",
                                color = Color.White.copy(alpha = 0.2f),
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            tasks.take(3).forEach { task ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(vertical = 4.dp)
                                ) {
                                    Icon(
                                        imageVector = if (task.completed) Icons.Rounded.CheckBox else Icons.Rounded.CheckBoxOutlineBlank,
                                        contentDescription = null,
                                        tint = if (task.completed) Color(0xFF4CAF50) else Color.White.copy(alpha = 0.2f),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = task.title,
                                        color = if (task.completed) Color.White.copy(alpha = 0.3f) else Color.White.copy(alpha = 0.8f),
                                        fontSize = 13.sp,
                                        fontFamily = FontFamily.Monospace,
                                        maxLines = 1,
                                        textDecoration = if (task.completed) androidx.compose.ui.text.style.TextDecoration.LineThrough else null
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                Text(
                    text = "INITIALIZING FOCUS PROTOCOLS...",
                    color = Color.White.copy(alpha = 0.3f),
                    fontSize = 14.sp,
                    fontFamily = FontFamily.Monospace
                )
            }

            Spacer(modifier = Modifier.height(64.dp))

            // Footer Info (Clock & Quote)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 48.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column(horizontalAlignment = Alignment.Start) {
                    Text(
                        text = timeText,
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraLight,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = dateText,
                        color = Color.White.copy(alpha = 0.2f),
                        fontSize = 14.sp,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 2.sp
                    )
                }

                Text(
                    text = quote,
                    color = Color.White.copy(alpha = 0.3f),
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 5.sp,
                    textAlign = TextAlign.End,
                    modifier = Modifier.widthIn(max = 300.dp)
                )
            }

            Spacer(modifier = Modifier.height(160.dp)) // Music widget space
        }

        // Elegant Music Widget
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 24.dp)
        ) {
            ElegantMusicWidget()
        }
    }
}
