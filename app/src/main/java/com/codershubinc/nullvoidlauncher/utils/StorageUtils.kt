package com.codershubinc.nullvoidlauncher.utils

import android.app.usage.StorageStatsManager
import android.content.Context
import android.os.Environment
import android.os.StatFs
import android.os.storage.StorageManager
import java.io.File
import java.text.DecimalFormat
import java.util.UUID
import kotlin.math.log10
import kotlin.math.pow

object StorageUtils {

    /**
     * Returns total physical storage in bytes using StorageStatsManager.
     * This includes system partitions.
     */
    fun getTotalStorage(context: Context): Long {
        return try {
            val storageStatsManager = context.getSystemService(Context.STORAGE_STATS_SERVICE) as StorageStatsManager
            storageStatsManager.getTotalBytes(StorageManager.UUID_DEFAULT)
        } catch (e: Exception) {
            // Fallback to old method if there's an error
            val path: File = Environment.getDataDirectory()
            val stat = StatFs(path.path)
            stat.blockSizeLong * stat.blockCountLong
        }
    }

    /**
     * Returns available storage in bytes.
     */
    fun getAvailableStorage(context: Context): Long {
        return try {
            val storageStatsManager = context.getSystemService(Context.STORAGE_STATS_SERVICE) as StorageStatsManager
            storageStatsManager.getFreeBytes(StorageManager.UUID_DEFAULT)
        } catch (e: Exception) {
            val path: File = Environment.getDataDirectory()
            val stat = StatFs(path.path)
            stat.blockSizeLong * stat.availableBlocksLong
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
}
