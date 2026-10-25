package com.codershubinc.nullvoidlauncher.data.fonts

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Typeface
import android.util.Log
import androidx.compose.ui.text.font.FontFamily
import com.codershubinc.nullvoidlauncher.data.WidgetFont
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.File
import java.util.concurrent.ConcurrentHashMap

enum class FontDownloadState {
    IDLE,
    DOWNLOADING,
    DOWNLOADED,
    ERROR
}

data class DownloadedFontInfo(
    val name: String,
    val fileSizeKb: Long,
    val file: File
)

object FontManager {

    private const val TAG = "FontManager"
    private const val PREFS_NAME = "null_void_fonts"
    private const val KEY_CUSTOM_FONT = "active_custom_google_font"

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private val mutex = Mutex()

    private var appContext: Context? = null
    private val fontCache = ConcurrentHashMap<String, FontFamily>()

    private val _downloadStates = MutableStateFlow<Map<String, FontDownloadState>>(emptyMap())
    val downloadStates: StateFlow<Map<String, FontDownloadState>> = _downloadStates.asStateFlow()

    private val _downloadVersion = MutableStateFlow(0)
    val downloadVersion: StateFlow<Int> = _downloadVersion.asStateFlow()

    fun init(context: Context) {
        if (appContext == null) {
            appContext = context.applicationContext
            getFontsDir()?.mkdirs()
            refreshDownloadStates()
        }
    }

    private fun getFontsDir(): File? {
        val context = appContext ?: return null
        return File(context.filesDir, "fonts").also {
            if (!it.exists()) it.mkdirs()
        }
    }

    private fun getPrefs(): SharedPreferences? {
        return appContext?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun getCustomFontFamilyName(): String {
        return getPrefs()?.getString(KEY_CUSTOM_FONT, "Outfit") ?: "Outfit"
    }

    fun setCustomFontFamilyName(name: String) {
        getPrefs()?.edit()?.putString(KEY_CUSTOM_FONT, name)?.apply()
        _downloadVersion.value++
    }

    fun sanitizeFontFileName(familyName: String): String {
        val clean = familyName.substringBefore("(").trim().lowercase()
        return clean.replace(Regex("[^a-z0-9]"), "_").replace(Regex("_+"), "_").trim('_') + ".ttf"
    }

    fun getFontFile(familyName: String): File? {
        val fontsDir = getFontsDir() ?: return null
        return File(fontsDir, sanitizeFontFileName(familyName))
    }

    fun isFontDownloaded(font: WidgetFont): Boolean {
        if (!font.isGoogleFont) return true
        val familyName = font.googleFontFamily ?: return false
        val file = getFontFile(familyName) ?: return false
        return file.exists() && file.length() > 0
    }

    fun isCustomFontDownloaded(familyName: String): Boolean {
        val file = getFontFile(familyName) ?: return false
        return file.exists() && file.length() > 0
    }

    fun getFontFamily(font: WidgetFont, triggerDownloadIfMissing: Boolean = true): FontFamily {
        if (!font.isGoogleFont) {
            return when (font) {
                WidgetFont.DEFAULT -> FontFamily.Default
                WidgetFont.SANS_SERIF -> FontFamily.SansSerif
                WidgetFont.MONOSPACE -> FontFamily.Monospace
                WidgetFont.SERIF -> FontFamily.Serif
                WidgetFont.CONDENSED -> try {
                    FontFamily(Typeface.create("sans-serif-condensed", Typeface.NORMAL))
                } catch (e: Exception) {
                    FontFamily.SansSerif
                }
                else -> FontFamily.Default
            }
        }

        val familyName = font.googleFontFamily ?: return FontFamily.Default
        val cached = fontCache[familyName]
        if (cached != null) return cached

        val file = getFontFile(familyName)
        if (file != null && file.exists() && file.length() > 0) {
            try {
                val typeface = Typeface.createFromFile(file)
                val family = FontFamily(typeface)
                fontCache[familyName] = family
                return family
            } catch (e: Exception) {
                Log.e(TAG, "Error loading font file for '$familyName'", e)
            }
        }

        // Font is not downloaded yet - auto-download if requested
        if (triggerDownloadIfMissing && appContext != null) {
            val currentState = _downloadStates.value[familyName]
            if (currentState != FontDownloadState.DOWNLOADING && currentState != FontDownloadState.ERROR) {
                downloadFont(font)
            }
        }

        // Return pleasant fallback while downloading
        return getFallbackFontFamily(font)
    }

    fun getCustomFontFamily(): FontFamily {
        val customName = getCustomFontFamilyName()
        val cached = fontCache[customName]
        if (cached != null) return cached

        val file = getFontFile(customName)
        if (file != null && file.exists() && file.length() > 0) {
            try {
                val typeface = Typeface.createFromFile(file)
                val family = FontFamily(typeface)
                fontCache[customName] = family
                return family
            } catch (e: Exception) {
                Log.e(TAG, "Error loading custom font file for '$customName'", e)
            }
        }

        return FontFamily.SansSerif
    }

    private fun getFallbackFontFamily(font: WidgetFont): FontFamily {
        return when (font) {
            WidgetFont.JETBRAINS_MONO, WidgetFont.FIRA_CODE -> FontFamily.Monospace
            WidgetFont.CINZEL, WidgetFont.PLAYFAIR, WidgetFont.ROBOTO_SLAB -> FontFamily.Serif
            WidgetFont.DANCING_SCRIPT, WidgetFont.CAVEAT, WidgetFont.PACIFICO, WidgetFont.LOBSTER -> FontFamily.Cursive
            else -> FontFamily.SansSerif
        }
    }

    fun downloadFont(font: WidgetFont, onResult: ((Boolean) -> Unit)? = null) {
        val familyName = font.googleFontFamily ?: run {
            onResult?.invoke(false)
            return
        }
        downloadCustomFont(familyName) { success, _ ->
            onResult?.invoke(success)
        }
    }

    fun downloadCustomFont(familyName: String, onResult: ((Boolean, String?) -> Unit)? = null) {
        val fontsDir = getFontsDir() ?: run {
            onResult?.invoke(false, "Context not initialized")
            return
        }

        val destFile = File(fontsDir, sanitizeFontFileName(familyName))

        scope.launch {
            updateDownloadState(familyName, FontDownloadState.DOWNLOADING)

            val result = GoogleFontDownloader.downloadTtf(familyName, destFile)
            if (result.isSuccess) {
                try {
                    val typeface = Typeface.createFromFile(destFile)
                    fontCache[familyName] = FontFamily(typeface)
                    updateDownloadState(familyName, FontDownloadState.DOWNLOADED)
                    _downloadVersion.value++
                    onResult?.invoke(true, null)
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to load downloaded font '$familyName'", e)
                    updateDownloadState(familyName, FontDownloadState.ERROR)
                    onResult?.invoke(false, "Corrupted font file: ${e.message}")
                }
            } else {
                val err = result.exceptionOrNull()?.message ?: "Unknown error"
                updateDownloadState(familyName, FontDownloadState.ERROR)
                onResult?.invoke(false, err)
            }
        }
    }

    /**
     * Downloads curated fonts in one go (for the "Download Recommended Fonts" action).
     */
    fun downloadCuratedFonts(
        onProgress: (current: Int, total: Int) -> Unit,
        onComplete: (downloaded: Int, total: Int) -> Unit
    ) {
        val curatedFonts = WidgetFont.entries.filter { it.isGoogleFont && it.googleFontFamily != null }
        val total = curatedFonts.size

        scope.launch(Dispatchers.IO) {
            var downloadedCount = 0
            curatedFonts.forEachIndexed { index, font ->
                val name = font.googleFontFamily ?: return@forEachIndexed
                val destFile = getFontFile(name)
                if (destFile != null && (!destFile.exists() || destFile.length() == 0L)) {
                    updateDownloadState(name, FontDownloadState.DOWNLOADING)
                    val result = GoogleFontDownloader.downloadTtf(name, destFile)
                    if (result.isSuccess) {
                        try {
                            val typeface = Typeface.createFromFile(destFile)
                            fontCache[name] = FontFamily(typeface)
                            updateDownloadState(name, FontDownloadState.DOWNLOADED)
                            downloadedCount++
                        } catch (e: Exception) {
                            updateDownloadState(name, FontDownloadState.ERROR)
                        }
                    } else {
                        updateDownloadState(name, FontDownloadState.ERROR)
                    }
                } else {
                    downloadedCount++
                }

                launch(Dispatchers.Main) {
                    onProgress(index + 1, total)
                }
            }

            launch(Dispatchers.Main) {
                _downloadVersion.value++
                onComplete(downloadedCount, total)
            }
        }
    }

    fun deleteFont(font: WidgetFont): Boolean {
        val name = font.googleFontFamily ?: return false
        return deleteCustomFont(name)
    }

    fun deleteCustomFont(familyName: String): Boolean {
        val file = getFontFile(familyName) ?: return false
        fontCache.remove(familyName)
        val deleted = if (file.exists()) file.delete() else false
        updateDownloadState(familyName, FontDownloadState.IDLE)
        _downloadVersion.value++
        return deleted
    }

    fun getDownloadedFonts(): List<DownloadedFontInfo> {
        val fontsDir = getFontsDir() ?: return emptyList()
        val files = fontsDir.listFiles { file -> file.isFile && file.extension.lowercase() == "ttf" }
            ?: return emptyList()

        return files.map { file ->
            val cleanName = file.nameWithoutExtension
                .split("_")
                .joinToString(" ") { it.replaceFirstChar { char -> char.uppercase() } }
            DownloadedFontInfo(
                name = cleanName,
                fileSizeKb = file.length() / 1024,
                file = file
            )
        }.sortedBy { it.name }
    }

    private fun updateDownloadState(familyName: String, state: FontDownloadState) {
        val current = _downloadStates.value.toMutableMap()
        current[familyName] = state
        _downloadStates.value = current
    }

    private fun refreshDownloadStates() {
        val fontsDir = getFontsDir() ?: return
        val current = mutableMapOf<String, FontDownloadState>()

        WidgetFont.entries.forEach { font ->
            font.googleFontFamily?.let { name ->
                val file = File(fontsDir, sanitizeFontFileName(name))
                if (file.exists() && file.length() > 0) {
                    current[name] = FontDownloadState.DOWNLOADED
                } else {
                    current[name] = FontDownloadState.IDLE
                }
            }
        }
        _downloadStates.value = current
    }
}
