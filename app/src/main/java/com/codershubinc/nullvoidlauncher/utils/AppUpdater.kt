package com.codershubinc.nullvoidlauncher.utils

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.File
import java.net.HttpURLConnection
import java.net.URL

data class AppVersion(
    val major: Int,
    val minor: Int,
    val patch: Int,
    val preRelease: Int = 1 // 0 = pre-release (beta/alpha/rc), 1 = stable
) : Comparable<AppVersion> {
    override fun compareTo(other: AppVersion): Int {
        if (major != other.major) return major.compareTo(other.major)
        if (minor != other.minor) return minor.compareTo(other.minor)
        if (patch != other.patch) return patch.compareTo(other.patch)
        return preRelease.compareTo(other.preRelease)
    }

    companion object {
        fun parse(versionStr: String): AppVersion {
            val clean = versionStr.trim().removePrefix("v").removePrefix("V")
            val isPre = clean.contains("beta", ignoreCase = true) ||
                    clean.contains("alpha", ignoreCase = true) ||
                    clean.contains("rc", ignoreCase = true)
            // Extract numeric groups
            val numbers = Regex("\\d+").findAll(clean).map { it.value.toInt() }.toList()
            val major = numbers.getOrNull(0) ?: 0
            val minor = numbers.getOrNull(1) ?: 0
            val patch = numbers.getOrNull(2) ?: 0
            val preRelease = if (isPre) 0 else 1
            return AppVersion(major, minor, patch, preRelease)
        }
    }
}

data class UpdateInfo(
    val isUpdateAvailable: Boolean,
    val currentVersion: String,
    val latestVersion: String,
    val releaseTitle: String,
    val releaseNotes: String,
    val releaseUrl: String,
    val apkDownloadUrl: String?,
    val apkName: String?,
    val apkSize: Long = 0L
)

object AppUpdater {

    fun getCurrentVersionName(context: Context): String {
        return try {
            val pInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                context.packageManager.getPackageInfo(context.packageName, PackageManager.PackageInfoFlags.of(0))
            } else {
                @Suppress("DEPRECATION")
                context.packageManager.getPackageInfo(context.packageName, 0)
            }
            pInfo.versionName ?: Constants.App.VERSION
        } catch (_: Exception) {
            Constants.App.VERSION
        }
    }

    suspend fun checkForUpdates(context: Context): Result<UpdateInfo> = withContext(Dispatchers.IO) {
        try {
            val currentVersionStr = getCurrentVersionName(context)
            val currentVersion = AppVersion.parse(currentVersionStr)

            val url = URL(Constants.Github.LATEST_RELEASE_API_URL)
            val connection = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                setRequestProperty("User-Agent", Constants.Github.USER_AGENT)
                connectTimeout = 10000
                readTimeout = 10000
            }

            if (connection.responseCode == 200) {
                val response = connection.inputStream.bufferedReader().use { it.readText() }
                val json = JSONObject(response)
                val latestTag = json.optString("tag_name", "")
                val releaseTitle = json.optString("name", latestTag)
                val releaseNotes = json.optString("body", "")
                val releaseUrl = json.optString("html_url", Constants.Github.REPO_URL)

                val latestVersion = AppVersion.parse(latestTag)
                val isNewer = latestVersion > currentVersion

                var apkDownloadUrl: String? = null
                var apkName: String? = null
                var apkSize = 0L

                val assets = json.optJSONArray("assets")
                if (assets != null) {
                    for (i in 0 until assets.length()) {
                        val asset = assets.optJSONObject(i) ?: continue
                        val name = asset.optString("name", "")
                        val downloadUrl = asset.optString("browser_download_url", "")
                        val contentType = asset.optString("content_type", "")
                        if (name.endsWith(".apk", ignoreCase = true) || contentType.contains("package-archive")) {
                            apkDownloadUrl = downloadUrl
                            apkName = name
                            apkSize = asset.optLong("size", 0L)
                            break
                        }
                    }
                }

                Result.success(
                    UpdateInfo(
                        isUpdateAvailable = isNewer,
                        currentVersion = currentVersionStr,
                        latestVersion = latestTag,
                        releaseTitle = releaseTitle,
                        releaseNotes = releaseNotes,
                        releaseUrl = releaseUrl,
                        apkDownloadUrl = apkDownloadUrl,
                        apkName = apkName,
                        apkSize = apkSize
                    )
                )
            } else {
                Result.failure(Exception("Server returned HTTP ${connection.responseCode}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun downloadApk(
        context: Context,
        downloadUrl: String,
        onProgress: (progress: Float, downloadedBytes: Long, totalBytes: Long) -> Unit
    ): Result<File> = withContext(Dispatchers.IO) {
        try {
            val client = OkHttpClient.Builder()
                .followRedirects(true)
                .followSslRedirects(true)
                .build()

            val request = Request.Builder()
                .url(downloadUrl)
                .header("User-Agent", Constants.Github.USER_AGENT)
                .build()

            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                return@withContext Result.failure(Exception("HTTP error: ${response.code}"))
            }

            val body = response.body ?: return@withContext Result.failure(Exception("Empty response body"))
            val contentLength = body.contentLength()

            val updateDir = File(context.cacheDir, "updates").apply { mkdirs() }
            val apkFile = File(updateDir, "NullVoid-update.apk")
            if (apkFile.exists()) apkFile.delete()

            body.byteStream().use { input ->
                apkFile.outputStream().use { output ->
                    val buffer = ByteArray(8192)
                    var bytesRead: Int
                    var totalRead = 0L
                    while (input.read(buffer).also { bytesRead = it } != -1) {
                        output.write(buffer, 0, bytesRead)
                        totalRead += bytesRead
                        val progress = if (contentLength > 0) totalRead.toFloat() / contentLength.toFloat() else -1f
                        onProgress(progress, totalRead, contentLength)
                    }
                    output.flush()
                }
            }
            Result.success(apkFile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun canInstallPackages(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.packageManager.canRequestPackageInstalls()
        } else {
            true
        }
    }

    fun openInstallPermissionSettings(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES).apply {
                data = Uri.parse("package:${context.packageName}")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            try {
                context.startActivity(intent)
            } catch (_: Exception) {
                val fallback = Intent(Settings.ACTION_SECURITY_SETTINGS).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                try { context.startActivity(fallback) } catch (_: Exception) {}
            }
        }
    }

    fun installApk(context: Context, apkFile: File): Boolean {
        return try {
            val fileUri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                apkFile
            )
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(fileUri, "application/vnd.android.package-archive")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(intent)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
