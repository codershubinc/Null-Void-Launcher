/*
 * Copyright (C) 2026- Swapnil Ingle
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.codershubinc.nullvoidlauncher.ui.music

import android.content.ComponentName
import android.graphics.Bitmap
import android.media.MediaMetadata
import android.media.session.MediaController
import android.media.session.MediaSessionManager
import android.media.session.PlaybackState
import android.service.notification.NotificationListenerService
import androidx.core.graphics.scale

data class MusicTrack(
    val title: String?,
    val artist: String?,
    val isPlaying: Boolean,
    val artwork: Bitmap?, // Compressed
    val fullArtwork: Bitmap?, // Original
    val packageName: String
)


class MediaService : NotificationListenerService() {
    companion object {
        private var _instance: MediaService? = null
        val instance: MediaService? get() = _instance
    }

    private lateinit var sessionManager: MediaSessionManager

    override fun onListenerConnected() {
        super.onListenerConnected()
        _instance = this
        sessionManager =
            getSystemService(MEDIA_SESSION_SERVICE) as MediaSessionManager
    }

    override fun onListenerDisconnected() {
        super.onListenerDisconnected()
        _instance = null
    }

    private var lastTrackId: String? = null
    private var lastCompressedArtwork: Bitmap? = null
    private var lastFullArtwork: Bitmap? = null

    fun getMediaSessionInfo(): MusicTrack? {
        if (!::sessionManager.isInitialized) return null

        val controllers =
            sessionManager.getActiveSessions(
                ComponentName(this, MediaService::class.java)
            )

        val activeController = controllers.firstOrNull {
            it.playbackState?.state == PlaybackState.STATE_PLAYING
        } ?: controllers.firstOrNull()

        return activeController?.let { controller ->
            val metadata = controller.metadata
            val playbackState = controller.playbackState

            
            val title = metadata?.getString(MediaMetadata.METADATA_KEY_TITLE) ?: "Unknown Title"
            val artist = metadata?.getString(MediaMetadata.METADATA_KEY_ARTIST) ?: "Unknown Artist"
            val currentTrackId = "$title-$artist-${controller.packageName}"

            val artworks = if (currentTrackId == lastTrackId && lastCompressedArtwork != null) {
                Pair(lastCompressedArtwork, lastFullArtwork)
            } else {
                val rawArtwork = metadata?.getBitmap(MediaMetadata.METADATA_KEY_ALBUM_ART)
                    ?: metadata?.getBitmap(MediaMetadata.METADATA_KEY_ART)
                
                val compressed = rawArtwork?.let {
                    val size = 128
                    it.scale(size, size)
                }
                lastTrackId = currentTrackId
                lastCompressedArtwork = compressed
                lastFullArtwork = rawArtwork
                Pair(compressed, rawArtwork)
            }

            MusicTrack(
                title = title,
                artist = artist,
                isPlaying = playbackState?.state == PlaybackState.STATE_PLAYING,
                artwork = artworks.first,
                fullArtwork = artworks.second,
                packageName = controller.packageName
            )
        }
    }

    // Inside MediaService class
    private fun getActiveController(): MediaController? {
        if (!::sessionManager.isInitialized) return null
        val controllers =
            sessionManager.getActiveSessions(ComponentName(this, MediaService::class.java))
        // Prefer the one that is currently playing
        return controllers.firstOrNull { it.playbackState?.state == PlaybackState.STATE_PLAYING }
            ?: controllers.firstOrNull()
    }

    fun togglePlayPause() {
        val controller = getActiveController()
        if (controller?.playbackState?.state == PlaybackState.STATE_PLAYING) {
            controller.transportControls.pause()
        } else {
            controller?.transportControls?.play()
        }
    }

    fun next() = getActiveController()?.transportControls?.skipToNext()
    fun previous() = getActiveController()?.transportControls?.skipToPrevious()
}
 