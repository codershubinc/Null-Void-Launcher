package com.codershubinc.nullvoidlauncher.utils

import android.app.usage.StorageStatsManager
import android.content.Context
import android.content.Intent
import android.os.Environment
import android.os.StatFs
import android.os.storage.StorageManager
import android.provider.Settings
import java.io.File
import java.text.DecimalFormat
import java.util.UUID
import kotlin.math.log10
import kotlin.math.pow

data class StorageInfoState(
    val totalBytes: Long = 0L,
    val availableBytes: Long = 0L,
    val usedBytes: Long = 0L,
    val usedPercentage: Int = 0,
    val totalText: String = "0 B",
    val availableText: String = "0 B",
    val usedText: String = "0 B"
)

object StorageUtils {

    /**
     * Returns total physical storage in bytes using StorageStatsManager.
     * This includes system partitions.
     */
    fun getTotalStorage(context: Context): Long {
        return try {
            val storageStatsManager = context.getSystemService(Context.STORAGE_STATS_SERVICE) as StorageStatsManager
            storageStatsManager.getTotalBytes(StorageManager.UUID_DEFAULT)
        } catch (e: Throwable) {
            // Fallback to old method if there's an error
            try {
                val path: File = Environment.getDataDirectory()
                val stat = StatFs(path.path)
                stat.blockSizeLong * stat.blockCountLong
            } catch (ex: Throwable) {
                0L
            }
        }
    }

    /**
     * Returns available storage in bytes.
     */
    fun getAvailableStorage(context: Context): Long {
        return try {
            val storageStatsManager = context.getSystemService(Context.STORAGE_STATS_SERVICE) as StorageStatsManager
            storageStatsManager.getFreeBytes(StorageManager.UUID_DEFAULT)
        } catch (e: Throwable) {
            try {
                val path: File = Environment.getDataDirectory()
                val stat = StatFs(path.path)
                stat.blockSizeLong * stat.availableBlocksLong
            } catch (ex: Throwable) {
                0L
            }
        }
    }

    /**
     * Returns total internal storage in bytes (User partition only).
     */
    @Deprecated("Use getTotalStorage(context)", ReplaceWith("getTotalStorage(context)"))
    fun getTotalInternalStorage(): Long {
        val path: File = Environment.getDataDirectory()
        val stat = StatFs(path.path)
        val blockSize = stat.blockSizeLong
        val totalBlocks = stat.blockCountLong
        return totalBlocks * blockSize
    }

    /**
     * Returns available internal storage in bytes (User partition only).
     */
    @Deprecated("Use getAvailableStorage(context)", ReplaceWith("getAvailableStorage(context)"))
    fun getAvailableInternalStorage(): Long {
        val path: File = Environment.getDataDirectory()
        val stat = StatFs(path.path)
        val blockSize = stat.blockSizeLong
        val availableBlocks = stat.availableBlocksLong
        return availableBlocks * blockSize
    }

    /**
     * Returns used storage percentage as an Integer (0-100).
     */
    fun getUsedStoragePercentage(context: Context): Int {
        val total = getTotalStorage(context)
        val available = getAvailableStorage(context)
        if (total <= 0) return 0
        val used = total - available
        return ((used.toDouble() / total.toDouble()) * 100).toInt()
    }

    /**
     * Formats bytes into a human-readable string using decimal (1000) units.
     * This matches how manufacturers market storage (e.g., 128 GB).
     */
    fun formatSize(size: Long , ): String {
        if (size <= 0) return "0 B"
        val units = arrayOf("B", "KB", "MB", "GB", "TB")
        val digitGroups = (log10(size.toDouble()) / log10(1000.0)).toInt()
        return DecimalFormat("#,##0.#").format(size / 1000.0.pow(digitGroups.toDouble())) + " " + units[digitGroups]
    }

    /**
     * Aggregates and returns a complete StorageInfoState snapshot.
     */
    fun getStorageInfo(context: Context): StorageInfoState {
        val total = getTotalStorage(context)
        val available = getAvailableStorage(context)
        val used = (total - available).coerceAtLeast(0L)
        val percent = if (total > 0) ((used.toDouble() / total.toDouble()) * 100).toInt().coerceIn(0, 100) else 0
        return StorageInfoState(
            totalBytes = total,
            availableBytes = available,
            usedBytes = used,
            usedPercentage = percent,
            totalText = formatSize(total),
            availableText = formatSize(available),
            usedText = formatSize(used)
        )
    }

    /**
     * Opens device storage settings.
     */
    @Suppress("DEPRECATION")
    fun openStorageSettings(context: Context) {
        val intent = Intent(Settings.ACTION_INTERNAL_STORAGE_SETTINGS).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        try {
            context.startActivity(intent)
        } catch (_: Exception) {
            val fallback = Intent(Settings.ACTION_STORAGE_VOLUME_ACCESS_SETTINGS).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            try {
                context.startActivity(fallback)
            } catch (_: Exception) {
                val sys = Intent(Settings.ACTION_SETTINGS).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                try { context.startActivity(sys) } catch (_: Exception) {}
            }
        }
    }
}
