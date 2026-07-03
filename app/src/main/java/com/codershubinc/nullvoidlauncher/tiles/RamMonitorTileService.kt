package com.codershubinc.nullvoidlauncher.tiles

import android.app.ActivityManager
import android.content.Context
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.Locale

class RamMonitorTileService : TileService() {

    private val parentJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.Main + parentJob)
    private var updateJob: Job? = null

    override fun onStartListening() {
        super.onStartListening()
        startPeriodicUpdate()
    }

    override fun onStopListening() {
        stopPeriodicUpdate()
        super.onStopListening()
    }

    override fun onDestroy() {
        parentJob.cancel()
        super.onDestroy()
    }

    private fun startPeriodicUpdate() {
        updateJob?.cancel()
        updateJob = serviceScope.launch {
            while (isActive) {
                updateRamTile()
                delay(1500) // Update every 1.5 seconds while panel is visible
            }
        }
    }

    private fun stopPeriodicUpdate() {
        updateJob?.cancel()
        updateJob = null
    }

    private fun updateRamTile() {
        val tile = qsTile ?: return
        
        val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memoryInfo)
        
        val totalMemGb = memoryInfo.totalMem / 1073741824.0 // 1024^3
        val availMemGb = memoryInfo.availMem / 1073741824.0
        val usedMemGb = totalMemGb - availMemGb
        val pctUsed = ((memoryInfo.totalMem - memoryInfo.availMem).toDouble() / memoryInfo.totalMem * 100).toInt()

        tile.label = String.format(Locale.US, "RAM: %.1fG/%.1fG", usedMemGb, totalMemGb)
        tile.subtitle = String.format(Locale.US, "%d%% Used (%.1fG Free)", pctUsed, availMemGb)
        tile.state = Tile.STATE_ACTIVE
        
        tile.updateTile()
    }
}
