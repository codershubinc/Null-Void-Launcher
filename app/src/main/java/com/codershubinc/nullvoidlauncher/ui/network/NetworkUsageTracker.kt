package com.codershubinc.nullvoidlauncher.ui.network

import android.content.Context
import android.content.SharedPreferences
import android.net.TrafficStats
import android.os.SystemClock
import androidx.core.content.edit
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class UsageLogEntry(
    val key: String,
    val displayLabel: String,
    val rxBytes: Long,
    val txBytes: Long,
    val totalBytes: Long,
    val formattedTotal: String,
    val formattedRx: String,
    val formattedTx: String
)

data class NetworkSpeedSample(
    val upSpeedBytesPerSec: Long = 0L,
    val downSpeedBytesPerSec: Long = 0L,
    val upSpeedText: String = "0 B/s",
    val downSpeedText: String = "0 B/s",
    val todayUsageBytes: Long = 0L,
    val todayUsageText: String = "0 B",
    val monthUsageBytes: Long = 0L,
    val monthUsageText: String = "0 B"
)

object NetworkUsageTracker {

    private const val PREFS_NAME = "network_usage_tracker_logs"
    private const val KEY_DAILY_LOGS = "daily_logs_json"
    private const val KEY_MONTHLY_LOGS = "monthly_logs_json"
    private const val KEY_LAST_TOTAL_RX = "last_total_rx"
    private const val KEY_LAST_TOTAL_TX = "last_total_tx"
    private const val KEY_LAST_SAMPLE_TIME = "last_sample_time"

    @Volatile
    private var lastSampleTimeMs: Long = 0L
    @Volatile
    private var lastTotalRxBytes: Long = -1L
    @Volatile
    private var lastTotalTxBytes: Long = -1L

    @Volatile
    private var currentUpSpeedBps: Long = 0L
    @Volatile
    private var currentDownSpeedBps: Long = 0L

    private val dayFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    private val monthFormat = SimpleDateFormat("yyyy-MM", Locale.US)
    private val displayDayFormat = SimpleDateFormat("EEE, MMM d", Locale.US)
    private val displayMonthFormat = SimpleDateFormat("MMMM yyyy", Locale.US)

    private fun getPrefs(context: Context): SharedPreferences {
        return context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    /**
     * Samples TrafficStats, computes upload/download speeds, and accumulates
     * delta bytes into persistent daily and monthly usage records.
     */
    @Synchronized
    fun sample(context: Context): NetworkSpeedSample {
        val prefs = getPrefs(context)
        val now = SystemClock.elapsedRealtime()

        val curRx = TrafficStats.getTotalRxBytes()
        val curTx = TrafficStats.getTotalTxBytes()

        val isSupported = curRx != TrafficStats.UNSUPPORTED.toLong() && curTx != TrafficStats.UNSUPPORTED.toLong()

        if (isSupported) {
            if (lastTotalRxBytes < 0 || lastTotalTxBytes < 0) {
                // Initialize from memory or persistent store
                lastTotalRxBytes = prefs.getLong(KEY_LAST_TOTAL_RX, curRx)
                lastTotalTxBytes = prefs.getLong(KEY_LAST_TOTAL_TX, curTx)
                lastSampleTimeMs = now
            } else {
                val dtMs = now - lastSampleTimeMs
                if (dtMs in 500..10000) {
                    val rxDelta = if (curRx >= lastTotalRxBytes) curRx - lastTotalRxBytes else curRx
                    val txDelta = if (curTx >= lastTotalTxBytes) curTx - lastTotalTxBytes else curTx

                    currentDownSpeedBps = (rxDelta * 1000L) / dtMs
                    currentUpSpeedBps = (txDelta * 1000L) / dtMs

                    if (rxDelta > 0 || txDelta > 0) {
                        recordDeltas(prefs, rxDelta, txDelta)
                    }
                } else if (dtMs > 10000) {
                    // Time elapsed too large (e.g. device was sleeping or app paused)
                    val rxDelta = if (curRx >= lastTotalRxBytes) curRx - lastTotalRxBytes else curRx
                    val txDelta = if (curTx >= lastTotalTxBytes) curTx - lastTotalTxBytes else curTx

                    currentDownSpeedBps = 0L
                    currentUpSpeedBps = 0L

                    if (rxDelta > 0 || txDelta > 0) {
                        recordDeltas(prefs, rxDelta, txDelta)
                    }
                }

                lastSampleTimeMs = now
                lastTotalRxBytes = curRx
                lastTotalTxBytes = curTx

                prefs.edit {
                    putLong(KEY_LAST_TOTAL_RX, curRx)
                    putLong(KEY_LAST_TOTAL_TX, curTx)
                    putLong(KEY_LAST_SAMPLE_TIME, now)
                }
            }
        } else {
            currentUpSpeedBps = 0L
            currentDownSpeedBps = 0L
        }

        val todayUsage = getTodayUsage(context)
        val monthUsage = getMonthUsage(context)

        return NetworkSpeedSample(
            upSpeedBytesPerSec = currentUpSpeedBps,
            downSpeedBytesPerSec = currentDownSpeedBps,
            upSpeedText = formatSpeed(currentUpSpeedBps),
            downSpeedText = formatSpeed(currentDownSpeedBps),
            todayUsageBytes = todayUsage.totalBytes,
            todayUsageText = todayUsage.formattedTotal,
            monthUsageBytes = monthUsage.totalBytes,
            monthUsageText = monthUsage.formattedTotal
        )
    }

    private fun recordDeltas(prefs: SharedPreferences, rxDelta: Long, txDelta: Long) {
        val now = Date()
        val todayKey = dayFormat.format(now)
        val monthKey = monthFormat.format(now)

        // Update daily logs
        val dailyRaw = prefs.getString(KEY_DAILY_LOGS, "{}") ?: "{}"
        val dailyJson = try { JSONObject(dailyRaw) } catch (_: Exception) { JSONObject() }
        val todayObj = if (dailyJson.has(todayKey)) dailyJson.getJSONObject(todayKey) else JSONObject()
        val curDayRx = todayObj.optLong("rx", 0L) + rxDelta
        val curDayTx = todayObj.optLong("tx", 0L) + txDelta
        todayObj.put("rx", curDayRx)
        todayObj.put("tx", curDayTx)
        dailyJson.put(todayKey, todayObj)

        // Update monthly logs
        val monthlyRaw = prefs.getString(KEY_MONTHLY_LOGS, "{}") ?: "{}"
        val monthlyJson = try { JSONObject(monthlyRaw) } catch (_: Exception) { JSONObject() }
        val monthObj = if (monthlyJson.has(monthKey)) monthlyJson.getJSONObject(monthKey) else JSONObject()
        val curMonthRx = monthObj.optLong("rx", 0L) + rxDelta
        val curMonthTx = monthObj.optLong("tx", 0L) + txDelta
        monthObj.put("rx", curMonthRx)
        monthObj.put("tx", curMonthTx)
        monthlyJson.put(monthKey, monthObj)

        // Prune older daily logs (> 90 days) to preserve storage
        if (dailyJson.length() > 90) {
            val keys = dailyJson.keys()
            val sortedKeys = mutableListOf<String>()
            while (keys.hasNext()) sortedKeys.add(keys.next())
            sortedKeys.sort()
            while (sortedKeys.size > 90) {
                val oldest = sortedKeys.removeAt(0)
                dailyJson.remove(oldest)
            }
        }

        prefs.edit {
            putString(KEY_DAILY_LOGS, dailyJson.toString())
            putString(KEY_MONTHLY_LOGS, monthlyJson.toString())
        }
    }

    fun getTodayUsage(context: Context): UsageLogEntry {
        val todayKey = dayFormat.format(Date())
        val prefs = getPrefs(context)
        val dailyRaw = prefs.getString(KEY_DAILY_LOGS, "{}") ?: "{}"
        val dailyJson = try { JSONObject(dailyRaw) } catch (_: Exception) { JSONObject() }
        val obj = dailyJson.optJSONObject(todayKey)
        val rx = obj?.optLong("rx", 0L) ?: 0L
        val tx = obj?.optLong("tx", 0L) ?: 0L
        val total = rx + tx

        return UsageLogEntry(
            key = todayKey,
            displayLabel = "Today",
            rxBytes = rx,
            txBytes = tx,
            totalBytes = total,
            formattedTotal = formatBytes(total),
            formattedRx = formatBytes(rx),
            formattedTx = formatBytes(tx)
        )
    }

    fun getMonthUsage(context: Context): UsageLogEntry {
        val monthKey = monthFormat.format(Date())
        val prefs = getPrefs(context)
        val monthlyRaw = prefs.getString(KEY_MONTHLY_LOGS, "{}") ?: "{}"
        val monthlyJson = try { JSONObject(monthlyRaw) } catch (_: Exception) { JSONObject() }
        val obj = monthlyJson.optJSONObject(monthKey)
        val rx = obj?.optLong("rx", 0L) ?: 0L
        val tx = obj?.optLong("tx", 0L) ?: 0L
        val total = rx + tx

        return UsageLogEntry(
            key = monthKey,
            displayLabel = formatDisplayMonth(monthKey),
            rxBytes = rx,
            txBytes = tx,
            totalBytes = total,
            formattedTotal = formatBytes(total),
            formattedRx = formatBytes(rx),
            formattedTx = formatBytes(tx)
        )
    }

    fun getDailyLogs(context: Context): List<UsageLogEntry> {
        val prefs = getPrefs(context)
        val raw = prefs.getString(KEY_DAILY_LOGS, "{}") ?: "{}"
        val json = try { JSONObject(raw) } catch (_: Exception) { JSONObject() }
        val todayKey = dayFormat.format(Date())
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, -1)
        val yesterdayKey = dayFormat.format(cal.time)

        val list = mutableListOf<UsageLogEntry>()
        val keys = json.keys()
        val sortedKeys = mutableListOf<String>()
        while (keys.hasNext()) sortedKeys.add(keys.next())
        sortedKeys.sortDescending()

        for (key in sortedKeys) {
            val obj = json.optJSONObject(key) ?: continue
            val rx = obj.optLong("rx", 0L)
            val tx = obj.optLong("tx", 0L)
            val total = rx + tx
            val label = when (key) {
                todayKey -> "Today"
                yesterdayKey -> "Yesterday"
                else -> formatDisplayDate(key)
            }
            list.add(
                UsageLogEntry(
                    key = key,
                    displayLabel = label,
                    rxBytes = rx,
                    txBytes = tx,
                    totalBytes = total,
                    formattedTotal = formatBytes(total),
                    formattedRx = formatBytes(rx),
                    formattedTx = formatBytes(tx)
                )
            )
        }
        return list
    }

    fun getMonthlyLogs(context: Context): List<UsageLogEntry> {
        val prefs = getPrefs(context)
        val raw = prefs.getString(KEY_MONTHLY_LOGS, "{}") ?: "{}"
        val json = try { JSONObject(raw) } catch (_: Exception) { JSONObject() }
        val thisMonthKey = monthFormat.format(Date())

        val list = mutableListOf<UsageLogEntry>()
        val keys = json.keys()
        val sortedKeys = mutableListOf<String>()
        while (keys.hasNext()) sortedKeys.add(keys.next())
        sortedKeys.sortDescending()

        for (key in sortedKeys) {
            val obj = json.optJSONObject(key) ?: continue
            val rx = obj.optLong("rx", 0L)
            val tx = obj.optLong("tx", 0L)
            val total = rx + tx
            val label = if (key == thisMonthKey) "This Month (${formatDisplayMonth(key)})" else formatDisplayMonth(key)
            list.add(
                UsageLogEntry(
                    key = key,
                    displayLabel = label,
                    rxBytes = rx,
                    txBytes = tx,
                    totalBytes = total,
                    formattedTotal = formatBytes(total),
                    formattedRx = formatBytes(rx),
                    formattedTx = formatBytes(tx)
                )
            )
        }
        return list
    }

    fun clearAllLogs(context: Context) {
        getPrefs(context).edit {
            remove(KEY_DAILY_LOGS)
            remove(KEY_MONTHLY_LOGS)
        }
    }

    private fun formatDisplayDate(key: String): String {
        return try {
            val date = dayFormat.parse(key)
            if (date != null) displayDayFormat.format(date) else key
        } catch (_: Exception) {
            key
        }
    }

    private fun formatDisplayMonth(key: String): String {
        return try {
            val date = monthFormat.parse(key)
            if (date != null) displayMonthFormat.format(date) else key
        } catch (_: Exception) {
            key
        }
    }

    fun formatBytes(bytes: Long): String {
        if (bytes <= 0L) return "0 B"
        val kb = bytes / 1024.0
        val mb = kb / 1024.0
        val gb = mb / 1024.0
        return when {
            gb >= 1.0 -> String.format(Locale.US, "%.2f GB", gb)
            mb >= 1.0 -> String.format(Locale.US, "%.1f MB", mb)
            kb >= 1.0 -> String.format(Locale.US, "%.1f KB", kb)
            else -> "$bytes B"
        }
    }

    fun formatSpeed(bytesPerSec: Long): String {
        if (bytesPerSec <= 0L) return "0 B/s"
        val kb = bytesPerSec / 1024.0
        val mb = kb / 1024.0
        val gb = mb / 1024.0
        return when {
            gb >= 1.0 -> String.format(Locale.US, "%.2f GB/s", gb)
            mb >= 1.0 -> String.format(Locale.US, "%.1f MB/s", mb)
            kb >= 1.0 -> String.format(Locale.US, "%.1f KB/s", kb)
            else -> "$bytesPerSec B/s"
        }
    }
}
