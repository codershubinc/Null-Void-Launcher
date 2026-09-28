package com.codershubinc.nullvoidlauncher.ui.settings.widgets

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.DirectionsRun
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material.icons.rounded.Sync
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.health.connect.client.PermissionController
import com.codershubinc.nullvoidlauncher.data.StepsStyle
import com.codershubinc.nullvoidlauncher.data.UserManager
import com.codershubinc.nullvoidlauncher.ui.components.ModernCard
import com.codershubinc.nullvoidlauncher.ui.steps.StepsHelper
import com.codershubinc.nullvoidlauncher.ui.steps.StepsInfoState
import com.codershubinc.nullvoidlauncher.ui.widgets.StepsWidget
import com.codershubinc.nullvoidlauncher.ui.widgets.steps.GoogleFitBlue
import com.codershubinc.nullvoidlauncher.ui.widgets.steps.GoogleFitGreen

@Composable
fun StepsWidgetTweaksPage(
    userManager: UserManager,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var showSteps by remember { mutableStateOf(userManager.getShowStepsWidget()) }
    var stepsStyle by remember { mutableStateOf(userManager.getStepsStyle()) }
    var stepsFont by remember { mutableStateOf(userManager.getStepsFont()) }
    var dailyGoal by remember { mutableIntStateOf(userManager.getStepsDailyGoal()) }
    var hasSensorPermission by remember { mutableStateOf(StepsHelper.hasActivityRecognitionPermission(context)) }

    var showCalibrationDialog by remember { mutableStateOf(false) }
    var calibrationInput by remember { mutableStateOf("") }

    val liveSteps by StepsHelper.stepsState.collectAsState()

    DisposableEffect(Unit) {
        StepsHelper.registerStepSensor(context)
        StepsHelper.syncSteps(context)
        onDispose {}
    }

    val sensorPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasSensorPermission = granted
        if (granted) {
            StepsHelper.registerStepSensor(context)
            StepsHelper.syncSteps(context)
        }
    }

    val healthConnectLauncher = rememberLauncherForActivityResult(
        PermissionController.createRequestPermissionResultContract()
    ) {
        StepsHelper.syncSteps(context)
    }

    val previewSteps = remember(liveSteps, dailyGoal) {
        if (liveSteps.currentSteps > 0) {
            liveSteps.copy(dailyGoal = dailyGoal)
        } else {
            StepsInfoState(
                currentSteps = 4520,
                dailyGoal = dailyGoal,
                caloriesBurned = 194,
                distanceKm = 3.4f,
                hasSensorPermission = hasSensorPermission,
                isGoogleFitInstalled = StepsHelper.isGoogleFitInstalled(context)
            )
        }
    }

    if (showCalibrationDialog) {
        AlertDialog(
            onDismissRequest = { showCalibrationDialog = false },
            title = {
                Text("Calibrate Today's Steps", color = Color.White, fontWeight = FontWeight.Bold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Enter your current step count from Google Fit or your fitness tracker. The pedometer will continue counting forward from this value.",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 13.sp
                    )
                    OutlinedTextField(
                        value = calibrationInput,
                        onValueChange = { calibrationInput = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Today's Total Steps") },
                        placeholder = { Text("e.g. 5400") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoogleFitBlue,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val num = calibrationInput.toIntOrNull()
                        if (num != null && num >= 0) {
                            StepsHelper.calibrateHardwareSteps(context, num)
                        }
                        showCalibrationDialog = false
                    }
                ) {
                    Text("Save & Calibrate", color = GoogleFitBlue, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCalibrationDialog = false }) {
                    Text("Cancel", color = Color.White.copy(alpha = 0.6f))
                }
            },
            containerColor = Color(0xFF1E1E1E),
            shape = RoundedCornerShape(16.dp)
        )
    }

    WidgetPageScaffold(
        title = "Steps & Fitness Widget",
        subtitle = "Google Steps / Fit pedometer telemetry, goals & styles",
        onBack = onBack
    ) {
        WidgetVisibilityCard(
            title = "Show Steps Widget",
            description = "Display daily step count, goal progress and distance",
            visible = showSteps,
            onVisibleChange = {
                showSteps = it
                userManager.saveShowStepsWidget(it)
            }
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Step Sync Status & Quick Sync Card
        ModernCard {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Step Tracking & Sync",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Source: ${liveSteps.syncSource} • ${liveSteps.formattedSteps} steps today",
                        color = GoogleFitGreen,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Sync Now Button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(GoogleFitBlue.copy(alpha = 0.2f))
                        .border(1.dp, GoogleFitBlue.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                        .clickable {
                            StepsHelper.syncSteps(context)
                        }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Sync,
                            contentDescription = "Sync",
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "Sync Now",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Sync Source Mode Selector
            var syncMode by remember { mutableStateOf(userManager.getStepSyncMode()) }
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = "Sync Source Mode",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                com.codershubinc.nullvoidlauncher.data.StepSyncMode.entries.forEach { mode ->
                    val isSelected = syncMode == mode
                    val label = when (mode) {
                        com.codershubinc.nullvoidlauncher.data.StepSyncMode.AUTO -> "Auto"
                        com.codershubinc.nullvoidlauncher.data.StepSyncMode.HARDWARE_SENSOR -> "Sensor Only"
                        com.codershubinc.nullvoidlauncher.data.StepSyncMode.HEALTH_CONNECT -> "Google Fit Only"
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) GoogleFitBlue.copy(alpha = 0.25f) else Color.White.copy(alpha = 0.05f))
                            .border(1.dp, if (isSelected) GoogleFitBlue else Color.White.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                            .clickable {
                                syncMode = mode
                                userManager.saveStepSyncMode(mode)
                                StepsHelper.syncSteps(context)
                            }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            color = if (isSelected) Color.White else Color.White.copy(alpha = 0.6f),
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }

            // Zero records notice if Health Connect has no records from Google Fit
            if (liveSteps.hasHealthConnectPermission && (liveSteps.healthConnectSteps == null || liveSteps.healthConnectSteps == 0)) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFFBBC05).copy(alpha = 0.12f))
                        .border(1.dp, Color(0xFFFBBC05).copy(alpha = 0.25f), RoundedCornerShape(8.dp))
                        .clickable { StepsHelper.openGoogleFitOrHealth(context) }
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Info,
                        contentDescription = null,
                        tint = Color(0xFFFBBC05),
                        modifier = Modifier.size(18.dp)
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Google Fit has 0 records in Health Connect",
                            color = Color(0xFFFBBC05),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "To enable sync: Open Google Fit > Profile > Settings ⚙️ > Turn ON 'Sync Fit with Health Connect'. Falling back to hardware sensor in the meantime.",
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 11.sp
                        )
                    }
                }
            }

            // Health Connect integration CTA
            if (liveSteps.isHealthConnectAvailable) {
                Spacer(modifier = Modifier.height(14.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            if (liveSteps.hasHealthConnectPermission) GoogleFitGreen.copy(alpha = 0.12f)
                            else GoogleFitBlue.copy(alpha = 0.12f)
                        )
                        .border(
                            1.dp,
                            if (liveSteps.hasHealthConnectPermission) GoogleFitGreen.copy(alpha = 0.3f)
                            else GoogleFitBlue.copy(alpha = 0.3f),
                            RoundedCornerShape(10.dp)
                        )
                        .clickable {
                            if (!liveSteps.hasHealthConnectPermission) {
                                healthConnectLauncher.launch(StepsHelper.HEALTH_CONNECT_PERMISSIONS)
                            } else {
                                StepsHelper.openGoogleFitOrHealth(context)
                            }
                        }
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = if (liveSteps.hasHealthConnectPermission) Icons.Rounded.CheckCircle else Icons.Rounded.FitnessCenter,
                        contentDescription = null,
                        tint = if (liveSteps.hasHealthConnectPermission) GoogleFitGreen else GoogleFitBlue,
                        modifier = Modifier.size(20.dp)
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (liveSteps.hasHealthConnectPermission) "Google Fit / Health Connect Synced" else "Sync with Google Fit (Health Connect)",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = if (liveSteps.hasHealthConnectPermission) "Importing official daily steps directly from Google Fit & Health Connect" else "Tap Connect to link. Make sure 'Sync Fit with Health Connect' is on in Google Fit settings.",
                            color = Color.White.copy(alpha = 0.5f),
                            fontSize = 11.sp
                        )
                    }
                    if (!liveSteps.hasHealthConnectPermission) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(GoogleFitBlue)
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "Connect",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Calibrate Hardware Pedometer Button
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.White.copy(alpha = 0.04f))
                    .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(10.dp))
                    .clickable {
                        calibrationInput = liveSteps.currentSteps.toString()
                        showCalibrationDialog = true
                    }
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Edit,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.7f),
                    modifier = Modifier.size(18.dp)
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Calibrate / Align Step Count",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Manually set today's baseline to match Google Fit or your tracker",
                        color = Color.White.copy(alpha = 0.45f),
                        fontSize = 11.sp
                    )
                }
                Text(
                    text = "Calibrate",
                    color = GoogleFitBlue,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Hardware Activity Recognition Permission Card if needed
        if (!hasSensorPermission && Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ModernCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFFBBC05).copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Security,
                            contentDescription = null,
                            tint = Color(0xFFFBBC05),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Activity Recognition Permission",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Required for hardware step counting sensors",
                            color = Color.White.copy(alpha = 0.5f),
                            fontSize = 12.sp
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(GoogleFitBlue)
                            .clickable {
                                sensorPermissionLauncher.launch(Manifest.permission.ACTIVITY_RECOGNITION)
                            }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "Grant",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        // Live Preview Card
        ModernCard {
            Text(
                text = "Live Preview",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(14.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color.White.copy(alpha = 0.03f))
                    .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(14.dp))
                    .padding(16.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                StepsWidget(
                    style = stepsStyle,
                    font = stepsFont,
                    previewInfo = previewSteps
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            WidgetStyleSelector(
                title = "Steps Style",
                styles = StepsStyle.entries,
                selectedStyle = stepsStyle,
                onStyleSelected = {
                    stepsStyle = it
                    userManager.saveStepsStyle(it)
                },
                getLabel = { it.name }
            )

            WidgetFontSelector(
                selectedFont = stepsFont,
                onFontSelected = {
                    stepsFont = it
                    userManager.saveStepsFont(it)
                }
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Daily Goal Selector
        ModernCard {
            Text(
                text = "Daily Step Goal",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Select your daily steps target",
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 13.sp
            )
            Spacer(modifier = Modifier.height(14.dp))

            val goals = listOf(4000, 6000, 8000, 10000, 12000)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                goals.forEach { goal ->
                    val isSelected = dailyGoal == goal
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if (isSelected) GoogleFitBlue.copy(alpha = 0.25f)
                                else Color.White.copy(alpha = 0.05f)
                            )
                            .border(
                                1.dp,
                                if (isSelected) GoogleFitBlue else Color.White.copy(alpha = 0.1f),
                                RoundedCornerShape(10.dp)
                            )
                            .clickable {
                                dailyGoal = goal
                                userManager.saveStepsDailyGoal(goal)
                            }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (goal >= 1000) "${goal / 1000}k" else "$goal",
                            color = if (isSelected) Color.White else Color.White.copy(alpha = 0.7f),
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Direct Google Fit App Widget Section
        ModernCard {
            val fitWidgets = remember { StepsHelper.getGoogleFitWidgets(context) }
            val isFitInstalled = remember { StepsHelper.isGoogleFitInstalled(context) }
            var pinMessage by remember { mutableStateOf<String?>(null) }

            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(GoogleFitBlue.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.FitnessCenter,
                            contentDescription = null,
                            tint = GoogleFitBlue,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Add Direct Google Fit App Widget",
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Add official widgets provided by the Google Fit application",
                            color = Color.White.copy(alpha = 0.5f),
                            fontSize = 12.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                if (!isFitInstalled) {
                    Text(
                        text = "Google Fit is not installed on this device. Install Google Fit to sync step telemetry.",
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(GoogleFitBlue)
                            .clickable { StepsHelper.openGoogleFitOrHealth(context) }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "Get Google Fit",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(GoogleFitBlue.copy(alpha = 0.12f))
                            .border(1.dp, GoogleFitBlue.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                            .padding(14.dp)
                    ) {
                        Text(
                            text = "Native Google Fit Integration Active",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "NullVoid renders Google Fit data directly on your home screen via the native Steps Widget above. Android minimalist launchers do not host traditional OS app widget panels.",
                            color = Color.White.copy(alpha = 0.65f),
                            fontSize = 11.sp,
                            lineHeight = 16.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(GoogleFitBlue)
                                    .clickable { StepsHelper.openGoogleFitOrHealth(context) }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "Open Google Fit App",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            if (fitWidgets.isNotEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color.White.copy(alpha = 0.1f))
                                        .clickable {
                                            val success = StepsHelper.requestPinGoogleFitWidget(context, fitWidgets.firstOrNull())
                                            pinMessage = if (success) "Widget pin requested!" else "Launcher widget pin not supported by current system"
                                        }
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = "Request OS Pin",
                                        color = Color.White,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }

                if (pinMessage != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = pinMessage ?: "",
                        color = GoogleFitGreen,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Live Debug & Fetch Logs Card
        val debugLogs by StepsHelper.syncLogs.collectAsState()
        var logsExpanded by remember { mutableStateOf(false) }

        ModernCard {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { logsExpanded = !logsExpanded },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Sync & Fetch Debug Logs",
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Logcat tag: StepsHelper • ${debugLogs.size} log entries",
                            color = Color.White.copy(alpha = 0.5f),
                            fontSize = 12.sp
                        )
                    }

                    Text(
                        text = if (logsExpanded) "Collapse" else "Expand",
                        color = GoogleFitBlue,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (logsExpanded) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 280.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.Black.copy(alpha = 0.5f))
                            .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    ) {
                        if (debugLogs.isEmpty()) {
                            Text(
                                text = "No logs yet. Tap 'Sync Now' above to trigger fetch.",
                                color = Color.White.copy(alpha = 0.4f),
                                fontSize = 11.sp,
                                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                            )
                        } else {
                            androidx.compose.foundation.lazy.LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                items(debugLogs.size) { idx ->
                                    val line = debugLogs[idx]
                                    Text(
                                        text = line,
                                        color = if (line.contains("SUCCESS", ignoreCase = true) || line.contains("Found Google Fit", ignoreCase = true)) GoogleFitGreen
                                        else if (line.contains("Error", ignoreCase = true) || line.contains("Warning", ignoreCase = true)) Color(0xFFEA4335)
                                        else Color.White.copy(alpha = 0.8f),
                                        fontSize = 11.sp,
                                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Google Fit Integration Launch Card
        ModernCard {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        StepsHelper.openGoogleFitOrHealth(context)
                    }
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(GoogleFitBlue.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.DirectionsRun,
                        contentDescription = null,
                        tint = GoogleFitBlue,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Open Google Fit / Health App",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = if (StepsHelper.isGoogleFitInstalled(context)) "Google Fit installed" else "Tap to open or install Google Fit",
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}
