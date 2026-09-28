package com.codershubinc.nullvoidlauncher.ui.steps

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.SystemClock
import android.provider.Settings
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.request.AggregateRequest
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

data class StepsInfoState(
    val currentSteps: Int = 0,
    val dailyGoal: Int = 6000,
    val caloriesBurned: Int = 0,
    val distanceKm: Float = 0f,
    val hasSensorPermission: Boolean = true,
    val hasHealthConnectPermission: Boolean = false,
    val isHealthConnectAvailable: Boolean = false,
    val isGoogleFitInstalled: Boolean = false,
    val syncSource: String = "Sensor",
    val rawSensorSteps: Int = 0,
    val healthConnectSteps: Int? = null
) {
    val progress: Float
        get() = if (dailyGoal > 0) (currentSteps.toFloat() / dailyGoal).coerceIn(0f, 1f) else 0f

    val progressPercent: Int
        get() = (progress * 100).roundToInt()

    val formattedSteps: String
        get() = String.format(Locale.US, "%,d", currentSteps)

    val formattedGoal: String
        get() = String.format(Locale.US, "%,d", dailyGoal)

    val formattedDistance: String
        get() = String.format(Locale.US, "%.1f km", distanceKm)

    val formattedCalories: String
        get() = "$caloriesBurned kcal"
}

object StepsHelper {

    private const val TAG = "StepsHelper"
    const val GOOGLE_FIT_PACKAGE = "com.google.android.apps.fitness"
    const val HEALTH_CONNECT_PACKAGE = "com.google.android.apps.healthdata"
    private const val PREFS_STEPS = "null_void_steps_data"
    private const val KEY_DATE = "steps_date_key"
    private const val KEY_START_STEP_COUNT = "steps_initial_sensor_offset"
    private const val KEY_STEPS_TODAY = "steps_today_count"
    private const val KEY_LAST_RAW_COUNT = "steps_last_raw_count"
    private const val KEY_SYNC_SOURCE = "steps_sync_source"
    private const val KEY_CALIBRATED_DATE = "steps_calibrated_date"
    private const val KEY_CALIBRATED_VALUE = "steps_calibrated_value"

    val HEALTH_CONNECT_PERMISSIONS = setOf(
        HealthPermission.getReadPermission(StepsRecord::class)
    )

    private val helperScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private var sensorManager: SensorManager? = null
    private var stepCounterSensor: Sensor? = null
    private var stepDetectorSensor: Sensor? = null
    private var listenerRegistered = false
    private var receiverRegistered = false
    private var appContext: Context? = null

    // Reactive StateFlow for real-time Compose updates on every step or sync
    private val _stepsState = MutableStateFlow(StepsInfoState())
    val stepsState: StateFlow<StepsInfoState> = _stepsState.asStateFlow()

    // Debug log stream for logcat and in-app diagnostics
    private val _syncLogs = MutableStateFlow<List<String>>(emptyList())
    val syncLogs: StateFlow<List<String>> = _syncLogs.asStateFlow()

    fun log(msg: String) {
        Log.d(TAG, msg)
        val time = SimpleDateFormat("HH:mm:ss.SSS", Locale.US).format(Date())
        val line = "[$time] $msg"
        _syncLogs.value = (_syncLogs.value + line).takeLast(80)
    }

    private val screenReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            context?.let { ctx ->
                log("Received broadcast: ${intent?.action} -> triggering auto sync")
                syncSteps(ctx)
            }
        }
    }

    private val stepListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent?) {
            event ?: return
            val context = appContext ?: return

            if (event.sensor.type == Sensor.TYPE_STEP_COUNTER) {
                val totalSinceReboot = event.values.firstOrNull() ?: return
                if (totalSinceReboot <= 0f) return
                handleStepCounterEvent(context, totalSinceReboot)
            } else if (event.sensor.type == Sensor.TYPE_STEP_DETECTOR) {
                handleStepDetectorEvent(context)
            }
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
    }

    private fun handleStepCounterEvent(context: Context, totalSinceReboot: Float) {
        val prefs = context.getSharedPreferences(PREFS_STEPS, Context.MODE_PRIVATE)
        val todayStr = getTodayDateKey()
        val savedDate = prefs.getString(KEY_DATE, "") ?: ""
        val savedStepsToday = prefs.getInt(KEY_STEPS_TODAY, 0)
        val lastRawCount = prefs.getFloat(KEY_LAST_RAW_COUNT, -1f)

        // Determine if phone was rebooted today
        val bootTimeMs = System.currentTimeMillis() - SystemClock.elapsedRealtime()
        val todayMidnightMs = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toEpochSecond() * 1000L
        val rebootedToday = bootTimeMs >= todayMidnightMs

        var baseline = prefs.getFloat(KEY_START_STEP_COUNT, -1f)

        if (savedDate != todayStr) {
            // New day rollover
            baseline = if (rebootedToday) {
                0f
            } else if (lastRawCount > 0f && totalSinceReboot >= lastRawCount) {
                lastRawCount
            } else {
                totalSinceReboot
            }

            val initialToday = (totalSinceReboot - baseline).toInt().coerceAtLeast(0)
            prefs.edit()
                .putString(KEY_DATE, todayStr)
                .putFloat(KEY_START_STEP_COUNT, baseline)
                .putInt(KEY_STEPS_TODAY, initialToday)
                .putFloat(KEY_LAST_RAW_COUNT, totalSinceReboot)
                .putString(KEY_SYNC_SOURCE, "Hardware Sensor")
                .apply()

            log("Hardware day roll: raw=$totalSinceReboot, baseline=$baseline, initialToday=$initialToday, rebootedToday=$rebootedToday")
            publishCurrentState(context, initialToday, "Hardware Sensor", rawSensor = initialToday)
            return
        }

        // Mid-day reboot detection
        if (lastRawCount > 0f && totalSinceReboot < lastRawCount) {
            baseline = totalSinceReboot - savedStepsToday
            prefs.edit().putFloat(KEY_START_STEP_COUNT, baseline).apply()
            log("Mid-day reboot detected! Adjusted baseline=$baseline (savedStepsToday=$savedStepsToday)")
        } else if (baseline < 0f) {
            baseline = if (rebootedToday) {
                0f
            } else if (savedStepsToday > 0 && totalSinceReboot >= savedStepsToday) {
                totalSinceReboot - savedStepsToday
            } else {
                totalSinceReboot
            }
            prefs.edit().putFloat(KEY_START_STEP_COUNT, baseline).apply()
        }

        val calculated = (totalSinceReboot - baseline).toInt().coerceAtLeast(0)
        val stepsToday = maxOf(calculated, savedStepsToday)

        prefs.edit()
            .putInt(KEY_STEPS_TODAY, stepsToday)
            .putFloat(KEY_LAST_RAW_COUNT, totalSinceReboot)
            .putString(KEY_SYNC_SOURCE, "Hardware Sensor")
            .apply()

        publishCurrentState(context, stepsToday, "Hardware Sensor", rawSensor = stepsToday)
    }

    private fun handleStepDetectorEvent(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_STEPS, Context.MODE_PRIVATE)
        val todayStr = getTodayDateKey()
        val savedDate = prefs.getString(KEY_DATE, "") ?: ""

        val current = if (savedDate == todayStr) {
            prefs.getInt(KEY_STEPS_TODAY, 0) + 1
        } else {
            1
        }

        prefs.edit()
            .putString(KEY_DATE, todayStr)
            .putInt(KEY_STEPS_TODAY, current)
            .putString(KEY_SYNC_SOURCE, "Hardware Sensor")
            .apply()

        log("StepDetector tick: current=$current")
        publishCurrentState(context, current, "Hardware Sensor", rawSensor = current)
    }

    fun calibrateHardwareSteps(context: Context, targetSteps: Int) {
        val app = context.applicationContext
        val prefs = app.getSharedPreferences(PREFS_STEPS, Context.MODE_PRIVATE)
        val todayStr = getTodayDateKey()
        val lastRaw = prefs.getFloat(KEY_LAST_RAW_COUNT, -1f)

        log("Calibrating hardware steps to $targetSteps (lastRawCount=$lastRaw)")

        if (lastRaw > 0f) {
            val newBaseline = lastRaw - targetSteps
            prefs.edit()
                .putString(KEY_DATE, todayStr)
                .putFloat(KEY_START_STEP_COUNT, newBaseline)
                .putInt(KEY_STEPS_TODAY, targetSteps)
                .putString(KEY_SYNC_SOURCE, "Manual / Calibrated")
                .putString(KEY_CALIBRATED_DATE, todayStr)
                .putInt(KEY_CALIBRATED_VALUE, targetSteps)
                .apply()
        } else {
            prefs.edit()
                .putString(KEY_DATE, todayStr)
                .putInt(KEY_STEPS_TODAY, targetSteps)
                .putString(KEY_SYNC_SOURCE, "Manual / Calibrated")
                .putString(KEY_CALIBRATED_DATE, todayStr)
                .putInt(KEY_CALIBRATED_VALUE, targetSteps)
                .apply()
        }

        publishCurrentState(app, targetSteps, "Manual / Calibrated", rawSensor = targetSteps)
    }

    private fun publishCurrentState(
        context: Context,
        stepsCount: Int,
        source: String = "Sensor",
        rawSensor: Int? = null,
        healthConnect: Int? = null
    ) {
        val dailyGoal = _stepsState.value.dailyGoal
        val distance = (stepsCount * 0.000762f)
        val calories = (stepsCount * 0.043f).roundToInt()

        _stepsState.value = _stepsState.value.copy(
            currentSteps = stepsCount,
            caloriesBurned = calories,
            distanceKm = distance,
            hasSensorPermission = hasActivityRecognitionPermission(context),
            isGoogleFitInstalled = isGoogleFitInstalled(context),
            isHealthConnectAvailable = isHealthConnectAvailable(context),
            syncSource = source,
            rawSensorSteps = rawSensor ?: _stepsState.value.rawSensorSteps,
            healthConnectSteps = healthConnect ?: _stepsState.value.healthConnectSteps
        )
    }

    private fun getTodayDateKey(): String {
        return SimpleDateFormat("yyyyMMdd", Locale.US).format(Date())
    }

    fun hasActivityRecognitionPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACTIVITY_RECOGNITION
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    fun isHealthConnectAvailable(context: Context): Boolean {
        return try {
            val status = HealthConnectClient.getSdkStatus(context)
            status == HealthConnectClient.SDK_AVAILABLE
        } catch (_: Exception) {
            false
        }
    }

    suspend fun hasHealthConnectPermission(context: Context): Boolean {
        if (!isHealthConnectAvailable(context)) return false
        return try {
            val client = HealthConnectClient.getOrCreate(context)
            val granted = client.permissionController.getGrantedPermissions()
            granted.containsAll(HEALTH_CONNECT_PERMISSIONS)
        } catch (_: Exception) {
            false
        }
    }

    suspend fun readHealthConnectSteps(context: Context): Pair<Long, String>? {
        if (!isHealthConnectAvailable(context)) {
            log("Health Connect SDK is not available on this device")
            return null
        }
        return try {
            val client = HealthConnectClient.getOrCreate(context)
            val granted = client.permissionController.getGrantedPermissions()
            log("Health Connect granted permissions: $granted")
            if (!granted.containsAll(HEALTH_CONNECT_PERMISSIONS)) {
                log("Missing Health Connect permission: $HEALTH_CONNECT_PERMISSIONS")
                return null
            }

            val startOfDay = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()
            val endOfDay = LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()

            log("Querying Health Connect records between $startOfDay and $endOfDay")

            // 1. Inspect raw records to see provider breakdown
            val readRequest = ReadRecordsRequest(
                recordType = StepsRecord::class,
                timeRangeFilter = TimeRangeFilter.between(startOfDay, endOfDay)
            )
            val recordsResponse = client.readRecords(readRequest)
            val records = recordsResponse.records

            log("Health Connect raw records found: ${records.size}")
            records.forEachIndexed { idx, rec ->
                log("  Record #$idx: pkg=${rec.metadata.dataOrigin.packageName}, count=${rec.count}, start=${rec.startTime}, end=${rec.endTime}")
            }

            val fitRecords = records.filter { it.metadata.dataOrigin.packageName == GOOGLE_FIT_PACKAGE }

            if (fitRecords.isNotEmpty()) {
                val fitTotal = fitRecords.sumOf { it.count }
                log("Found Google Fit specific records! Count=${fitRecords.size}, Sum=$fitTotal steps")
                Pair(fitTotal, "Google Fit (Health Connect)")
            } else {
                // If not specifically Google Fit, use official deduplicated aggregate
                val aggregateResponse = client.aggregate(
                    AggregateRequest(
                        metrics = setOf(StepsRecord.COUNT_TOTAL),
                        timeRangeFilter = TimeRangeFilter.between(startOfDay, endOfDay)
                    )
                )
                val aggTotal = aggregateResponse[StepsRecord.COUNT_TOTAL] ?: records.sumOf { it.count }
                log("No Google Fit records; aggregated Health Connect total=$aggTotal steps (all sources)")
                Pair(aggTotal, "Health Connect")
            }
        } catch (e: Exception) {
            log("Error reading Health Connect: ${e.message}")
            e.printStackTrace()
            null
        }
    }

    fun isGoogleFitInstalled(context: Context): Boolean {
        return try {
            context.packageManager.getPackageInfo(GOOGLE_FIT_PACKAGE, 0)
            true
        } catch (_: Exception) {
            false
        }
    }

    fun openGoogleFitOrHealth(context: Context) {
        log("Opening Google Fit or Health Connect app")
        val fitIntent = context.packageManager.getLaunchIntentForPackage(GOOGLE_FIT_PACKAGE)
        if (fitIntent != null) {
            fitIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            try {
                context.startActivity(fitIntent)
                return
            } catch (_: Exception) {}
        }

        val healthConnectIntent = context.packageManager.getLaunchIntentForPackage(HEALTH_CONNECT_PACKAGE)
        if (healthConnectIntent != null) {
            healthConnectIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            try {
                context.startActivity(healthConnectIntent)
                return
            } catch (_: Exception) {}
        }

        val settingsIntent = Intent("androidx.health.ACTION_HEALTH_CONNECT_SETTINGS").apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        try {
            context.startActivity(settingsIntent)
            return
        } catch (_: Exception) {}

        val viewIntent = Intent(Intent.ACTION_VIEW).apply {
            data = android.net.Uri.parse("https://play.google.com/store/apps/details?id=$GOOGLE_FIT_PACKAGE")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        try {
            context.startActivity(viewIntent)
        } catch (_: Exception) {
            val fallback = Intent(Settings.ACTION_SETTINGS).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            try { context.startActivity(fallback) } catch (_: Exception) {}
        }
    }

    fun getGoogleFitWidgets(context: Context): List<android.appwidget.AppWidgetProviderInfo> {
        return try {
            val manager = android.appwidget.AppWidgetManager.getInstance(context)
            val list = manager.getInstalledProvidersForPackage(GOOGLE_FIT_PACKAGE, android.os.Process.myUserHandle()) ?: emptyList()
            log("getGoogleFitWidgets: found ${list.size} widget providers for $GOOGLE_FIT_PACKAGE")
            list
        } catch (e: Exception) {
            log("getGoogleFitWidgets error: ${e.message}")
            emptyList()
        }
    }

    fun requestPinGoogleFitWidget(context: Context, providerInfo: android.appwidget.AppWidgetProviderInfo? = null): Boolean {
        return try {
            val manager = android.appwidget.AppWidgetManager.getInstance(context)
            if (!manager.isRequestPinAppWidgetSupported) {
                log("requestPinAppWidget is NOT supported by the active system launcher")
                return false
            }
            val target = providerInfo ?: getGoogleFitWidgets(context).firstOrNull() ?: return false
            log("Requesting system pin for widget provider: ${target.provider}")
            manager.requestPinAppWidget(target.provider, null, null)
            true
        } catch (e: Exception) {
            log("requestPinGoogleFitWidget error: ${e.message}")
            e.printStackTrace()
            false
        }
    }

    fun registerStepSensor(context: Context) {
        val app = context.applicationContext
        appContext = app

        log("registerStepSensor called")
        syncSteps(app)

        if (!receiverRegistered) {
            try {
                val filter = IntentFilter().apply {
                    addAction(Intent.ACTION_SCREEN_ON)
                    addAction(Intent.ACTION_USER_PRESENT)
                    addAction(Intent.ACTION_TIME_TICK)
                    addAction(Intent.ACTION_TIME_CHANGED)
                    addAction(Intent.ACTION_DATE_CHANGED)
                }
                app.registerReceiver(screenReceiver, filter)
                receiverRegistered = true
                log("Registered screen & time broadcast receiver for step auto-sync")
            } catch (e: Exception) {
                log("Error registering screenReceiver: ${e.message}")
            }
        }

        if (listenerRegistered) return
        if (!hasActivityRecognitionPermission(app)) {
            log("Cannot register hardware sensor: ACTIVITY_RECOGNITION permission not granted")
            return
        }

        try {
            val sm = app.getSystemService(Context.SENSOR_SERVICE) as? SensorManager ?: return
            sensorManager = sm
            stepCounterSensor = sm.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
            stepDetectorSensor = sm.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)

            if (stepCounterSensor != null) {
                sm.registerListener(stepListener, stepCounterSensor, SensorManager.SENSOR_DELAY_UI)
                listenerRegistered = true
                log("Registered hardware TYPE_STEP_COUNTER listener")
            } else if (stepDetectorSensor != null) {
                sm.registerListener(stepListener, stepDetectorSensor, SensorManager.SENSOR_DELAY_UI)
                listenerRegistered = true
                log("Registered hardware TYPE_STEP_DETECTOR listener")
            } else {
                log("No hardware step sensors available on device")
            }
        } catch (e: Exception) {
            log("Error registering sensorListener: ${e.message}")
            e.printStackTrace()
        }
    }

    fun flushSensor() {
        try {
            stepCounterSensor?.let { sensorManager?.flush(stepListener) }
        } catch (_: Exception) {}
    }

    fun unregisterStepSensor() {
        if (!listenerRegistered) return
        try {
            sensorManager?.unregisterListener(stepListener)
            listenerRegistered = false
            log("Unregistered step sensor listener")
        } catch (_: Exception) {}
    }

    fun syncSteps(context: Context) {
        val app = context.applicationContext
        appContext = app
        val userManager = com.codershubinc.nullvoidlauncher.data.UserManager(app)

        helperScope.launch {
            log("--- Syncing Steps ---")
            val syncMode = userManager.getStepSyncMode()
            log("Configured Sync Mode: $syncMode")

            val hcAvailable = isHealthConnectAvailable(app)
            val hcPermitted = if (hcAvailable) hasHealthConnectPermission(app) else false
            log("Health Connect status: available=$hcAvailable, permitted=$hcPermitted")

            if (syncMode != com.codershubinc.nullvoidlauncher.data.StepSyncMode.HARDWARE_SENSOR && hcPermitted) {
                val hcResult = readHealthConnectSteps(app)
                if (hcResult != null && hcResult.first > 0) {
                    val stepsInt = hcResult.first.toInt().coerceAtLeast(0)
                    val sourceLabel = hcResult.second
                    val prefs = app.getSharedPreferences(PREFS_STEPS, Context.MODE_PRIVATE)
                    val todayStr = getTodayDateKey()
                    prefs.edit()
                        .putString(KEY_DATE, todayStr)
                        .putInt(KEY_STEPS_TODAY, stepsInt)
                        .putString(KEY_SYNC_SOURCE, sourceLabel)
                        .apply()

                    log("Sync SUCCESS from Health Connect: $stepsInt steps (Source: $sourceLabel)")

                    withContext(Dispatchers.Main) {
                        val updated = _stepsState.value.copy(
                            currentSteps = stepsInt,
                            caloriesBurned = (stepsInt * 0.043f).roundToInt(),
                            distanceKm = (stepsInt * 0.000762f),
                            hasHealthConnectPermission = true,
                            isHealthConnectAvailable = true,
                            syncSource = sourceLabel,
                            healthConnectSteps = stepsInt
                        )
                        _stepsState.value = updated
                    }
                    return@launch
                } else {
                    log("Health Connect has 0 step records! (Google Fit has not written to Health Connect yet). Falling back to Hardware Sensor so steps are not zeroed out.")
                }
            }

            // Fallback: Read from stored sensor steps and flush pending hardware events
            withContext(Dispatchers.Main) {
                flushSensor()
                val currentInfo = getStepsInfo(app)
                log("Using local hardware pedometer: ${currentInfo.currentSteps} steps (Source: ${currentInfo.syncSource})")
                _stepsState.value = currentInfo.copy(
                    hasHealthConnectPermission = hcPermitted,
                    isHealthConnectAvailable = hcAvailable,
                    healthConnectSteps = 0
                )
            }
        }
    }

    fun getStepsInfo(context: Context, dailyGoal: Int = 6000): StepsInfoState {
        val app = context.applicationContext
        val hasPerm = hasActivityRecognitionPermission(app)
        val fitInstalled = isGoogleFitInstalled(app)
        val hcAvailable = isHealthConnectAvailable(app)

        val prefs = app.getSharedPreferences(PREFS_STEPS, Context.MODE_PRIVATE)
        val todayStr = getTodayDateKey()
        val savedDate = prefs.getString(KEY_DATE, "") ?: ""
        val source = prefs.getString(KEY_SYNC_SOURCE, "Hardware Sensor") ?: "Hardware Sensor"

        val stepsCount = if (savedDate == todayStr) {
            prefs.getInt(KEY_STEPS_TODAY, 0)
        } else {
            0
        }

        val distance = (stepsCount * 0.000762f)
        val calories = (stepsCount * 0.043f).roundToInt()

        val state = StepsInfoState(
            currentSteps = stepsCount,
            dailyGoal = dailyGoal,
            caloriesBurned = calories,
            distanceKm = distance,
            hasSensorPermission = hasPerm,
            isGoogleFitInstalled = fitInstalled,
            isHealthConnectAvailable = hcAvailable,
            syncSource = source,
            rawSensorSteps = stepsCount
        )
        _stepsState.value = state
        return state
    }
}
