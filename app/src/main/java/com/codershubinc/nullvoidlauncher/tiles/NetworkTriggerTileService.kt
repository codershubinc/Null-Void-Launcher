package com.codershubinc.nullvoidlauncher.tiles

import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class NetworkTriggerTileService : TileService() {

    private val parentJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.Main + parentJob)
    private var resetJob: Job? = null
    
    companion object {
        private var isRequesting = false
    }

    override fun onStartListening() {
        super.onStartListening()
        val tile = qsTile ?: return
        if (!isRequesting) {
            tile.state = Tile.STATE_INACTIVE
            tile.subtitle = "Ready"
            tile.updateTile()
        }
    }

    override fun onClick() {
        super.onClick()
        val tile = qsTile ?: return

        if (isRequesting) {
            Toast.makeText(this, "Request already in progress", Toast.LENGTH_SHORT).show()
            return
        }

        isRequesting = true
        tile.state = Tile.STATE_ACTIVE
        tile.subtitle = "Sending..."
        tile.updateTile()

        resetJob?.cancel()

        serviceScope.launch {
            val result = executeNetworkRequest()
            
            tile.state = Tile.STATE_INACTIVE
            if (result.isSuccess) {
                val message = result.getOrNull() ?: "Success"
                tile.subtitle = "Success"
                Toast.makeText(this@NetworkTriggerTileService, "Network trigger success: $message", Toast.LENGTH_SHORT).show()
            } else {
                val errorMessage = result.exceptionOrNull()?.message ?: "Request failed"
                tile.subtitle = "Failed"
                Toast.makeText(this@NetworkTriggerTileService, "Network trigger error: $errorMessage", Toast.LENGTH_LONG).show()
            }
            tile.updateTile()
            isRequesting = false

            // Reset tile subtitle to "Ready" after 3 seconds
            resetJob = launch {
                delay(3000)
                tile.subtitle = "Ready"
                tile.updateTile()
            }
        }
    }

    private suspend fun executeNetworkRequest(): Result<String> = withContext(Dispatchers.IO) {
        var connection: HttpURLConnection? = null
        try {
            // Replace with your local environment endpoint as needed
            val url = URL("http://192.168.1.50/command")
            connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.connectTimeout = 3000
            connection.readTimeout = 3000
            connection.doOutput = true
            connection.setRequestProperty("Content-Type", "application/json; utf-8")
            connection.setRequestProperty("Accept", "application/json")

            val jsonInputString = "{\"command\": \"trigger_adb\", \"timestamp\": ${System.currentTimeMillis()}}"
            
            connection.outputStream.use { os ->
                OutputStreamWriter(os, "UTF-8").use { writer ->
                    writer.write(jsonInputString)
                    writer.flush()
                }
            }

            val responseCode = connection.responseCode
            if (responseCode in 200..299) {
                Result.success("HTTP $responseCode")
            } else {
                Result.failure(Exception("HTTP $responseCode"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        } finally {
            connection?.disconnect()
        }
    }

    override fun onDestroy() {
        resetJob?.cancel()
        parentJob.cancel()
        super.onDestroy()
    }
}
