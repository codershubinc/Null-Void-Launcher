package com.codershubinc.nullvoidlauncher.ui.settings.widgets

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Security
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
import com.codershubinc.nullvoidlauncher.data.UserManager
import com.codershubinc.nullvoidlauncher.data.model.StepSyncMode
import com.codershubinc.nullvoidlauncher.data.model.StepsStyle
import com.codershubinc.nullvoidlauncher.system.steps.StepsHelper
import com.codershubinc.nullvoidlauncher.system.steps.StepsInfoState
import com.codershubinc.nullvoidlauncher.ui.components.ModernCard
import com.codershubinc.nullvoidlauncher.ui.settings.widgets.steps.StepsDebugLogsSection
import com.codershubinc.nullvoidlauncher.ui.settings.widgets.steps.StepsGoalSection
import com.codershubinc.nullvoidlauncher.ui.settings.widgets.steps.StepsHealthConnectSection
import com.codershubinc.nullvoidlauncher.ui.settings.widgets.steps.StepsSyncSourceSection
import com.codershubinc.nullvoidlauncher.ui.widgets.StepsWidget
import com.codershubinc.nullvoidlauncher.ui.widgets.steps.GoogleFitBlue

@Composable
fun StepsWidgetTweaksPage(
    userManager: UserManager,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var showSteps by remember { mutableStateOf(userManager.widgets.showSteps) }
    var stepsStyle by remember { mutableStateOf(userManager.widgets.stepsStyle) }
    var stepsFont by remember { mutableStateOf(userManager.widgets.stepsFont) }
    var dailyGoal by remember { mutableIntStateOf(userManager.widgets.stepsDailyGoal) }
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
                userManager.widgets.showSteps = it
            }
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Step Sync Status & Quick Sync Card
        var syncMode by remember { mutableStateOf(userManager.widgets.stepSyncMode) }
        StepsSyncSourceSection(
            liveSteps = liveSteps,
            syncMode = syncMode,
            onSyncModeChange = { mode ->
                syncMode = mode
                userManager.widgets.stepSyncMode = mode
                StepsHelper.syncSteps(context)
            },
            onSyncNow = { StepsHelper.syncSteps(context) },
            onOpenCalibration = {
                calibrationInput = liveSteps.currentSteps.toString()
                showCalibrationDialog = true
            }
        )

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
                    userManager.widgets.stepsStyle = it
                },
                getLabel = { it.name }
            )

            WidgetFontSelector(
                selectedFont = stepsFont,
                onFontSelected = {
                    stepsFont = it
                    userManager.widgets.stepsFont = it
                }
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Daily Goal Selector
        StepsGoalSection(
            dailyGoal = dailyGoal,
            onGoalSelected = { goal ->
                dailyGoal = goal
                userManager.widgets.stepsDailyGoal = goal
            }
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Health Connect & Google Fit App Widget Section
        StepsHealthConnectSection(
            liveSteps = liveSteps,
            onRequestHealthConnectPermission = {
                healthConnectLauncher.launch(StepsHelper.HEALTH_CONNECT_PERMISSIONS)
            },
            onOpenGoogleFitOrHealth = {
                StepsHelper.openGoogleFitOrHealth(context)
            }
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Debug Logs & Launch Google Fit Section
        StepsDebugLogsSection(
            onOpenGoogleFitOrHealth = {
                StepsHelper.openGoogleFitOrHealth(context)
            }
        )
    }
}
