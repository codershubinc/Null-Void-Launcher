package com.codershubinc.nullvoidlauncher.tiles

import android.content.Intent
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import androidx.core.content.ContextCompat

class KeepAwakeTileService : TileService() {

    override fun onStartListening() {
        super.onStartListening()
        updateTileState()
    }

    override fun onClick() {
        super.onClick()
        val context = applicationContext
        val intent = Intent(context, KeepAwakeService::class.java)

        if (KeepAwakeService.isRunning) {
            context.stopService(intent)
            updateTileState(Tile.STATE_INACTIVE)
        } else {
            ContextCompat.startForegroundService(context, intent)
            updateTileState(Tile.STATE_ACTIVE)
        }
    }

    private fun updateTileState(forcedState: Int? = null) {
        val tile = qsTile ?: return
        val isActive = forcedState?.let { it == Tile.STATE_ACTIVE } ?: KeepAwakeService.isRunning
        tile.state = if (isActive) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
        tile.subtitle = if (isActive) "Always On" else "Off"
        tile.updateTile()
    }
}
