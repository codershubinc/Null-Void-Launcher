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
    val artwork: Bitmap? = null,
    val fullArtwork: Bitmap? = null,
    val packageName: String = ""
)


class MediaService : NotificationListenerService() {
    companion object {
        private var _instance: MediaService? = null
        val instance: MediaService? get() = _instance

        fun isPermissionGranted(context: android.content.Context): Boolean {
            val enabledListeners = androidx.core.app.NotificationManagerCompat.getEnabledListenerPackages(context)
            return enabledListeners.contains(context.packageName)
        }
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

        val controllers = try {
            sessionManager.getActiveSessions(
                ComponentName(this, MediaService::class.java)
            )
        } catch (e: Exception) {
            emptyList()
        }

        val activeController = controllers.firstOrNull {
            it.playbackState?.state == PlaybackState.STATE_PLAYING
        } ?: controllers.firstOrNull()

        return activeController?.let { controller ->
            val metadata = controller.metadata
            val playbackState = controller.playbackState

            
            val title = metadata?.getString(MediaMetadata.METADATA_KEY_TITLE)
                ?: metadata?.getString(MediaMetadata.METADATA_KEY_DISPLAY_TITLE)
                ?: "Playing"

            val rawArtist = metadata?.getString(MediaMetadata.METADATA_KEY_ARTIST)
                ?: metadata?.getString(MediaMetadata.METADATA_KEY_ALBUM_ARTIST)
                ?: metadata?.getString(MediaMetadata.METADATA_KEY_AUTHOR)
                ?: metadata?.getString(MediaMetadata.METADATA_KEY_DISPLAY_SUBTITLE)

            val artist = if (rawArtist.isNullOrBlank() ||
                rawArtist.equals("Unknown Artist", ignoreCase = true) ||
                rawArtist.equals("Unknown", ignoreCase = true) ||
                rawArtist.equals("Unknown author", ignoreCase = true)
            ) {
                null
            } else {
                rawArtist
            }

            val currentTrackId = "$title-${artist ?: ""}-${controller.packageName}"

            val artworks = if (currentTrackId == lastTrackId && lastCompressedArtwork != null) {
                Pair(lastCompressedArtwork, lastFullArtwork)
            } else {
                var rawArtwork = metadata?.getBitmap(MediaMetadata.METADATA_KEY_ALBUM_ART)
                    ?: metadata?.getBitmap(MediaMetadata.METADATA_KEY_ART)
                    ?: metadata?.getBitmap(MediaMetadata.METADATA_KEY_DISPLAY_ICON)

                if (rawArtwork == null) {
                    val artUriStr = metadata?.getString(MediaMetadata.METADATA_KEY_ART_URI)
                        ?: metadata?.getString(MediaMetadata.METADATA_KEY_ALBUM_ART_URI)
                        ?: metadata?.getString(MediaMetadata.METADATA_KEY_DISPLAY_ICON_URI)
                    if (!artUriStr.isNullOrBlank()) {
                        try {
                            val uri = android.net.Uri.parse(artUriStr)
                            contentResolver.openInputStream(uri)?.use { stream ->
                                rawArtwork = android.graphics.BitmapFactory.decodeStream(stream)
                            }
                        } catch (_: Exception) {}
                    }
                }

                // If still null, extract app icon so video streams (VLC, etc.) have a high-res graphic
                if (rawArtwork == null) {
                    try {
                        val appIconDrawable = packageManager.getApplicationIcon(controller.packageName)
                        val w = appIconDrawable.intrinsicWidth.coerceAtLeast(128)
                        val h = appIconDrawable.intrinsicHeight.coerceAtLeast(128)
                        val bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
                        val canvas = android.graphics.Canvas(bmp)
                        appIconDrawable.setBounds(0, 0, canvas.width, canvas.height)
                        appIconDrawable.draw(canvas)
                        rawArtwork = bmp
                    } catch (_: Exception) {}
                }
                
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
        val controllers = try {
            sessionManager.getActiveSessions(ComponentName(this, MediaService::class.java))
        } catch (e: Exception) {
            emptyList()
        }
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
 