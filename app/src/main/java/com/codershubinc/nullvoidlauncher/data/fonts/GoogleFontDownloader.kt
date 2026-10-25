package com.codershubinc.nullvoidlauncher.data.fonts

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.URLEncoder
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern

object GoogleFontDownloader {

    private const val TAG = "GoogleFontDownloader"

    private val httpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .followRedirects(true)
            .build()
    }

    // Direct fallback TTF URLs for curated Google Fonts
    private val KNOWN_FONT_URLS = mapOf(
        "Outfit" to "https://fonts.gstatic.com/s/outfit/v15/QGYyz_MVcBeNP4NjuGObqx1XmO1I4TC1C4E.ttf",
        "JetBrains Mono" to "https://fonts.gstatic.com/s/jetbrainsmono/v24/tDbY2o-flEEny0FZhsfKu5WU4zr3E_BX0PnT8RD8yKxjPQ.ttf",
        "Playfair Display" to "https://fonts.gstatic.com/s/playfairdisplay/v40/nuFvD-vYSZviVYUb_rj3ij__anPXJzDwcbmjWBN2PKdFvUDQ.ttf",
        "Caveat" to "https://fonts.gstatic.com/s/caveat/v23/WnznHAc5bAfYB2QRah7pcpNvOx-pjfJ9SII.ttf",
        "Cinzel" to "https://fonts.gstatic.com/s/cinzel/v26/8vIU7ww63mVu7gtR-kwKxNvkNOjw-tbnTYo.ttf",
        "Pacifico" to "https://fonts.gstatic.com/s/pacifico/v23/FwZY7-Qmy14u9lezJ96A.ttf",
        "Dancing Script" to "https://fonts.gstatic.com/s/dancingscript/v29/If2cXTr6YS-zF4S-kcSWSVi_sxjsohD9F50Ruu7BMSoHTQ.ttf",
        "Inter" to "https://fonts.gstatic.com/s/inter/v20/UcCO3FwrK3iLTeHuS_nVMrMxCp50SjIw2boKoduKmMEVuLyfMZg.ttf",
        "Poppins" to "https://fonts.gstatic.com/s/poppins/v24/pxiEyp8kv8JHgFVrFJA.ttf",
        "Fira Code" to "https://fonts.gstatic.com/s/firacode/v27/uU9eCBsR6Z2vfE9aq3bL0fxyUs4tcw4W_D1sFVc.ttf",
        "Space Grotesk" to "https://fonts.gstatic.com/s/spacegrotesk/v22/V8mQoQDjQSkFtoMM3T6r8E7mF71Q-gOoraIAEj7oUUsj.ttf",
        "Bebas Neue" to "https://fonts.gstatic.com/s/bebasneue/v16/JTUSjIg69CK48gW7PXooxW4.ttf",
        "Lobster" to "https://fonts.gstatic.com/s/lobster/v32/neILzCirqoswsqX9_oU.ttf",
        "Comfortaa" to "https://fonts.gstatic.com/s/comfortaa/v47/1Pt_g8LJRfWJmhDAuUsSQamb1W0lwk4S4WjMPrQ.ttf",
        "Roboto Slab" to "https://fonts.gstatic.com/s/robotoslab/v36/BngbUXZYTXPIvIBgJJSb6s3BzlRRfKOFbvjojISWaA.ttf",
        "Playwrite BE WAL Guides" to "https://fonts.gstatic.com/s/playwritebewalguides/v1/l7gPbiR5yM62ycwevWCt02rrTFoEJvY4kyrrUzHl.ttf",
        "Playwrite BE WAL" to "https://fonts.gstatic.com/s/playwritebewal/v7/DtV1Jwq5QbIzyrA6DHdJ2BksuUmanQtEYjAlv96WFsWCGPug.ttf"
    )

    // Common descriptive website names mapped to official Google Fonts CSS API family names
    private val KNOWN_ALIASES = mapOf(
        "playwrite belgique wallonie bruxelles guides" to "Playwrite BE WAL Guides",
        "playwrite belgique wallonie bruxelles" to "Playwrite BE WAL",
        "playwrite belgique vlaanderen guides" to "Playwrite BE VLG Guides",
        "playwrite belgique vlaanderen" to "Playwrite BE VLG",
        "playwrite france traditionnelle guides" to "Playwrite FR Trad Guides",
        "playwrite france traditionnelle" to "Playwrite FR Trad",
        "playwrite france moderne guides" to "Playwrite FR Moderne Guides",
        "playwrite france moderne" to "Playwrite FR Moderne",
        "playwrite deutschland grundschrift guides" to "Playwrite DE Grund Guides",
        "playwrite deutschland grundschrift" to "Playwrite DE Grund",
        "playwrite deutschland sas guides" to "Playwrite DE SAS Guides",
        "playwrite deutschland la guides" to "Playwrite DE LA Guides",
        "playwrite deutschland va guides" to "Playwrite DE VA Guides",
        "playwrite usa modern guides" to "Playwrite US Modern Guides",
        "playwrite usa modern" to "Playwrite US Modern",
        "playwrite usa traditional guides" to "Playwrite US Trad Guides",
        "playwrite usa traditional" to "Playwrite US Trad",
        "playwrite united kingdom guides" to "Playwrite GB S Guides",
        "playwrite united kingdom" to "Playwrite GB S",
        "playwrite brasil guides" to "Playwrite BR Guides",
        "playwrite brasil" to "Playwrite BR",
        "playwrite espana guides" to "Playwrite ES Guides",
        "playwrite espana" to "Playwrite ES",
        "playwrite italia moderna guides" to "Playwrite IT Moderna Guides",
        "playwrite italia moderna" to "Playwrite IT Moderna",
        "playwrite italia tradizionale guides" to "Playwrite IT Trad Guides",
        "playwrite italia tradizionale" to "Playwrite IT Trad",
        "playwrite mexico guides" to "Playwrite MX Guides",
        "playwrite mexico" to "Playwrite MX"
    )

    private val TTF_URL_PATTERN = Pattern.compile("src:\\s*url\\((https://[^)]+\\.ttf)\\)")

    /**
     * Generates sanitized candidate family names for Google Fonts API lookup.
     */
    fun getCandidateNames(rawName: String): List<String> {
        val candidates = LinkedHashSet<String>()

        // 1. Strip parentheses, e.g. "Dancing Script (Cursive)" -> "Dancing Script"
        val clean = rawName.substringBefore("(").trim()

        // 2. Normalized lowercase version without hyphens/punctuation for alias matching
        val normalized = clean.lowercase()
            .replace("-", " ")
            .replace(Regex("[^a-z0-9 ]"), " ")
            .replace(Regex("\\s+"), " ")
            .trim()

        KNOWN_ALIASES[normalized]?.let { candidates.add(it) }

        // Also check if normalized matches without "guides"
        if (normalized.endsWith(" guides")) {
            val base = normalized.removeSuffix(" guides").trim()
            KNOWN_ALIASES[base]?.let { candidates.add("$it Guides") }
        }

        // 3. Playwrite pattern resolver (e.g. "Playwrite Belgique", "Playwrite Belgium")
        if (normalized.startsWith("playwrite")) {
            val isGuides = normalized.contains("guides")
            val suffix = if (isGuides) " Guides" else ""
            when {
                normalized.contains("belgique") || normalized.contains("belgium") -> {
                    if (normalized.contains("vlaanderen") || normalized.contains("flanders")) {
                        candidates.add("Playwrite BE VLG$suffix")
                    } else {
                        candidates.add("Playwrite BE WAL$suffix")
                    }
                }
                normalized.contains("france") -> {
                    if (normalized.contains("moderne")) candidates.add("Playwrite FR Moderne$suffix")
                    else candidates.add("Playwrite FR Trad$suffix")
                }
                normalized.contains("deutschland") || normalized.contains("germany") -> {
                    candidates.add("Playwrite DE Grund$suffix")
                }
                normalized.contains("usa") || normalized.contains("united states") -> {
                    if (normalized.contains("trad")) candidates.add("Playwrite US Trad$suffix")
                    else candidates.add("Playwrite US Modern$suffix")
                }
                normalized.contains("kingdom") || normalized.contains("uk") || normalized.contains("britain") -> {
                    candidates.add("Playwrite GB S$suffix")
                }
            }
        }

        // 4. Safe alphanumeric name with hyphens replaced by spaces
        val safeAlphanumeric = clean
            .replace("-", " ")
            .replace(Regex("[^a-zA-Z0-9 ]"), " ")
            .replace(Regex("\\s+"), " ")
            .trim()
        if (safeAlphanumeric.isNotEmpty()) candidates.add(safeAlphanumeric)

        // 5. Raw clean name if it doesn't contain forbidden characters
        if (!clean.contains("-") && !clean.contains(":") && clean.matches(Regex("[a-zA-Z0-9 ]+"))) {
            candidates.add(clean)
        }

        return candidates.toList()
    }

    /**
     * Resolves the direct .ttf download URL for any Google Font family name.
     */
    suspend fun resolveTtfUrl(familyName: String): String? = withContext(Dispatchers.IO) {
        val candidates = getCandidateNames(familyName)

        for (candidate in candidates) {
            // Check direct known URLs first
            KNOWN_FONT_URLS[candidate]?.let { return@withContext it }

            // Google Fonts CSS2 API strictly requires alphanumeric and spaces only
            if (candidate.contains("-") || candidate.contains(":") || !candidate.matches(Regex("[a-zA-Z0-9 ]+"))) {
                continue
            }

            val encoded = URLEncoder.encode(candidate.trim(), "UTF-8")
            val cssUrl = "https://fonts.googleapis.com/css2?family=$encoded"

            try {
                val request = Request.Builder()
                    .url(cssUrl)
                    .header("User-Agent", "Mozilla/5.0 (Android; Mobile)")
                    .build()

                httpClient.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        Log.w(TAG, "CSS query attempt failed for candidate '$candidate': code ${response.code}")
                        return@use
                    }
                    val body = response.body?.string() ?: return@use
                    val matcher = TTF_URL_PATTERN.matcher(body)
                    if (matcher.find()) {
                        val url = matcher.group(1)
                        Log.i(TAG, "Successfully resolved font '$familyName' as '$candidate' -> $url")
                        return@withContext url
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error resolving TTF URL for candidate '$candidate'", e)
            }
        }
        null
    }

    /**
     * Downloads a Google Font .ttf directly into the destination file.
     */
    suspend fun downloadTtf(familyName: String, destinationFile: File): Result<File> =
        withContext(Dispatchers.IO) {
            try {
                val ttfUrl = resolveTtfUrl(familyName)
                    ?: return@withContext Result.failure(
                        IOException("Font '$familyName' not found on Google Fonts. Ensure official alphanumeric name without hyphens (e.g. 'Playwrite BE WAL Guides').")
                    )

                val request = Request.Builder()
                    .url(ttfUrl)
                    .build()

                httpClient.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        return@withContext Result.failure(IOException("Failed to download TTF: HTTP ${response.code}"))
                    }

                    val body = response.body
                        ?: return@withContext Result.failure(IOException("Response body is empty"))

                    val tempFile = File(destinationFile.parentFile, "${destinationFile.name}.tmp")
                    FileOutputStream(tempFile).use { output ->
                        body.byteStream().use { input ->
                            input.copyTo(output)
                        }
                    }

                    if (destinationFile.exists()) {
                        destinationFile.delete()
                    }

                    if (!tempFile.renameTo(destinationFile)) {
                        tempFile.copyTo(destinationFile, overwrite = true)
                        tempFile.delete()
                    }

                    Result.success(destinationFile)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to download font '$familyName'", e)
                Result.failure(e)
            }
        }
}
