package com.codershubinc.nullvoidlauncher.ui.about

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codershubinc.nullvoidlauncher.data.GithubProfile
import com.codershubinc.nullvoidlauncher.data.UserManager
import com.codershubinc.nullvoidlauncher.ui.components.InfoRow
import com.codershubinc.nullvoidlauncher.ui.components.ModernCard
import com.codershubinc.nullvoidlauncher.ui.components.ProfileDetailRow
import com.codershubinc.nullvoidlauncher.utils.Constants
import com.codershubinc.nullvoidlauncher.utils.NetworkImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun AboutScreen(userManager: UserManager, onClose: () -> Unit) {
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()
    val uriHandler = LocalUriHandler.current
    val configuration = LocalConfiguration.current
    val isTablet = configuration.screenWidthDp >= 600
    
    val context = androidx.compose.ui.platform.LocalContext.current
    var updateInfo by remember { mutableStateOf<com.codershubinc.nullvoidlauncher.utils.UpdateInfo?>(null) }
    var updateStatus by remember { mutableStateOf("Check for updates") }
    var isChecking by remember { mutableStateOf(false) }
    var isDownloading by remember { mutableStateOf(false) }
    var downloadProgress by remember { mutableStateOf(0f) }
    var downloadedBytes by remember { mutableStateOf(0L) }
    var totalBytes by remember { mutableStateOf(0L) }
    var downloadedApkFile by remember { mutableStateOf<java.io.File?>(null) }
    var showPermissionDialog by remember { mutableStateOf(false) }

    val currentVersion = remember {
        val base = Constants.App.VERSION
        val suffix = Constants.App.VERSION_SUFFIX
        if (suffix.isNotEmpty()) "$base-$suffix" else base
    }

    var devProfile by remember { mutableStateOf<GithubProfile?>(null) }
    var isDevLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        isDevLoading = true
        devProfile = userManager.fetchGithubProfile(Constants.Github.USERNAME, false)
        isDevLoading = false
    }

    fun triggerInstall(file: java.io.File) {
        if (com.codershubinc.nullvoidlauncher.utils.AppUpdater.canInstallPackages(context)) {
            val installed = com.codershubinc.nullvoidlauncher.utils.AppUpdater.installApk(context, file)
            if (!installed) {
                updateStatus = "Error launching installer"
            }
        } else {
            showPermissionDialog = true
        }
    }

    fun startDownload(info: com.codershubinc.nullvoidlauncher.utils.UpdateInfo) {
        val downloadUrl = info.apkDownloadUrl
        if (downloadUrl.isNullOrEmpty()) {
            uriHandler.openUri(info.releaseUrl)
            return
        }

        scope.launch {
            isDownloading = true
            downloadProgress = 0f
            updateStatus = "Downloading update..."
            val result = com.codershubinc.nullvoidlauncher.utils.AppUpdater.downloadApk(
                context = context,
                downloadUrl = downloadUrl,
                onProgress = { progress, downloaded, total ->
                    downloadProgress = progress
                    downloadedBytes = downloaded
                    totalBytes = total
                }
            )
            isDownloading = false
            result.onSuccess { apkFile ->
                downloadedApkFile = apkFile
                updateStatus = "Download complete"
                triggerInstall(apkFile)
            }.onFailure { error ->
                updateStatus = "Download failed: ${error.localizedMessage ?: "Unknown error"}"
            }
        }
    }

    suspend fun checkForUpdates() {
        isChecking = true
        updateStatus = "Searching servers..."
        downloadedApkFile = null
        val result = com.codershubinc.nullvoidlauncher.utils.AppUpdater.checkForUpdates(context)
        isChecking = false
        result.onSuccess { info ->
            updateInfo = info
            updateStatus = if (info.isUpdateAvailable) {
                "Update Available: ${info.latestVersion}"
            } else {
                "System is up to date (${info.latestVersion})"
            }
        }.onFailure { error ->
            updateStatus = error.localizedMessage ?: "Failed to check for updates"
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF080808)) // Deep black-grey
    ) {
        // Decorative background glow
        Box(
            modifier = Modifier
                .size(300.dp)
                .align(Alignment.TopEnd)
                .offset(x = 100.dp, y = (-50).dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFF3D5AFE).copy(alpha = 0.15f), Color.Transparent)
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = if (isTablet) 64.dp else 24.dp)
                .then(
                    if (isTablet) Modifier.widthIn(max = 800.dp).align(Alignment.TopCenter)
                    else Modifier
                )
                .verticalScroll(scrollState)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            
            // Back Button
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.05f))
                    .clickable { onClose() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Header
            Text(
                text = "About",
                color = Color.White,
                fontSize = 40.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = FontFamily.SansSerif
            )
            Text(
                text = "NullVoid Launcher Protocol",
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 16.sp,
                fontFamily = FontFamily.SansSerif
            )

            Spacer(modifier = Modifier.height(40.dp))

            if (isTablet) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        // AppVersionCard
                        AppVersionCard(
                            currentVersion = currentVersion,
                            isChecking = isChecking,
                            isDownloading = isDownloading,
                            downloadProgress = downloadProgress,
                            downloadedBytes = downloadedBytes,
                            totalBytes = totalBytes,
                            updateStatus = updateStatus,
                            updateInfo = updateInfo,
                            downloadedApkFile = downloadedApkFile,
                            onCheckClick = { scope.launch { checkForUpdates() } },
                            onDownloadClick = { info -> startDownload(info) },
                            onInstallClick = { file -> triggerInstall(file) },
                            onViewReleaseClick = { url -> uriHandler.openUri(url) }
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Developer Section
                        Text(
                            text = "Lead Developer",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(start = 4.dp, bottom = 12.dp)
                        )
                        DeveloperCard(isDevLoading, devProfile, uriHandler)
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        // Info Sections
                        Text(
                            text = "System Details",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(start = 4.dp, bottom = 12.dp)
                        )
                        SystemDetailsCard()

                        Spacer(modifier = Modifier.height(24.dp))

                        // Social/Links Section
                        LinksCard(uriHandler)
                    }
                }
            } else {
                // Mobile Layout
                // App Version Card
                AppVersionCard(
                    currentVersion = currentVersion,
                    isChecking = isChecking,
                    isDownloading = isDownloading,
                    downloadProgress = downloadProgress,
                    downloadedBytes = downloadedBytes,
                    totalBytes = totalBytes,
                    updateStatus = updateStatus,
                    updateInfo = updateInfo,
                    downloadedApkFile = downloadedApkFile,
                    onCheckClick = { scope.launch { checkForUpdates() } },
                    onDownloadClick = { info -> startDownload(info) },
                    onInstallClick = { file -> triggerInstall(file) },
                    onViewReleaseClick = { url -> uriHandler.openUri(url) }
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Developer Section
                Text(
                    text = "Lead Developer",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 4.dp, bottom = 12.dp)
                )
                DeveloperCard(isDevLoading, devProfile, uriHandler)

                Spacer(modifier = Modifier.height(24.dp))

                // Info Sections
                Text(
                    text = "System Details",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 4.dp, bottom = 12.dp)
                )
                SystemDetailsCard()

                Spacer(modifier = Modifier.height(24.dp))

                // Social/Links Section
                LinksCard(uriHandler)
            }

            Spacer(modifier = Modifier.height(48.dp))
            
            // Footer
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "© 2026- Swapnil Ingle",
                    color = Color.White.copy(alpha = 0.3f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "All Rights Reserved",
                    color = Color.White.copy(alpha = 0.2f),
                    fontSize = 10.sp
                )
            }
            
            Spacer(modifier = Modifier.height(40.dp))
        }

        if (showPermissionDialog) {
            androidx.compose.material3.AlertDialog(
                onDismissRequest = { showPermissionDialog = false },
                title = {
                    Text(
                        text = "Permission Required",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Text(
                        text = "NullVoid Launcher needs permission to install unknown apps to perform this in-app update. Please enable it in system settings.",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 14.sp
                    )
                },
                confirmButton = {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF3D5AFE))
                            .clickable {
                                showPermissionDialog = false
                                com.codershubinc.nullvoidlauncher.utils.AppUpdater.openInstallPermissionSettings(context)
                            }
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "Settings",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                },
                dismissButton = {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { showPermissionDialog = false }
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "Cancel",
                            color = Color.White.copy(alpha = 0.6f),
                            fontSize = 14.sp
                        )
                    }
                },
                containerColor = Color(0xFF141416),
                shape = RoundedCornerShape(16.dp)
            )
        }
    }
}

@Composable
fun AppVersionCard(
    currentVersion: String,
    isChecking: Boolean,
    isDownloading: Boolean,
    downloadProgress: Float,
    downloadedBytes: Long,
    totalBytes: Long,
    updateStatus: String,
    updateInfo: com.codershubinc.nullvoidlauncher.utils.UpdateInfo?,
    downloadedApkFile: java.io.File?,
    onCheckClick: () -> Unit,
    onDownloadClick: (com.codershubinc.nullvoidlauncher.utils.UpdateInfo) -> Unit,
    onInstallClick: (java.io.File) -> Unit,
    onViewReleaseClick: (String) -> Unit
) {
    ModernCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Current Version",
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = currentVersion,
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Action button based on update state
            val (buttonText, isEnabled, action) = when {
                isChecking -> Triple("Checking...", false, {})
                isDownloading -> Triple("Downloading...", false, {})
                downloadedApkFile != null && downloadedApkFile.exists() -> Triple("Install", true, { onInstallClick(downloadedApkFile) })
                updateInfo != null && updateInfo.isUpdateAvailable -> {
                    if (updateInfo.apkDownloadUrl != null) {
                        Triple("Download", true, { onDownloadClick(updateInfo) })
                    } else {
                        Triple("View", true, { onViewReleaseClick(updateInfo.releaseUrl) })
                    }
                }
                else -> Triple("Check", true, onCheckClick)
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isEnabled) Color(0xFF3D5AFE) else Color(0xFF3D5AFE).copy(alpha = 0.4f))
                    .clickable(enabled = isEnabled) { action() }
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Text(
                    text = buttonText,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Downloading progress bar
        if (isDownloading) {
            Spacer(modifier = Modifier.height(16.dp))
            if (downloadProgress >= 0f) {
                androidx.compose.material3.LinearProgressIndicator(
                    progress = { downloadProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = Color(0xFF3D5AFE),
                    trackColor = Color.White.copy(alpha = 0.1f)
                )
            } else {
                androidx.compose.material3.LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = Color(0xFF3D5AFE),
                    trackColor = Color.White.copy(alpha = 0.1f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            val progressText = if (totalBytes > 0) {
                "${com.codershubinc.nullvoidlauncher.utils.StorageUtils.formatSize(downloadedBytes)} / ${com.codershubinc.nullvoidlauncher.utils.StorageUtils.formatSize(totalBytes)} (${(downloadProgress * 100).toInt()}%)"
            } else {
                com.codershubinc.nullvoidlauncher.utils.StorageUtils.formatSize(downloadedBytes)
            }
            Text(
                text = progressText,
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 12.sp
            )
        }

        // Status text
        if (updateStatus != "Check for updates") {
            Spacer(modifier = Modifier.height(14.dp))
            val isSuccess = updateStatus.contains("Available") || updateStatus.contains("complete")
            val isError = updateStatus.contains("failed", ignoreCase = true) || updateStatus.contains("error", ignoreCase = true)

            val statusColor = when {
                isSuccess -> Color(0xFF00E676)
                isError -> Color(0xFFFF5252)
                else -> Color.White.copy(alpha = 0.7f)
            }

            Text(
                text = updateStatus,
                color = statusColor,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }

        // If an update is available and notes exist, show release info
        if (updateInfo != null && updateInfo.isUpdateAvailable && !isDownloading) {
            if (updateInfo.apkName != null && updateInfo.apkSize > 0) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Package: ${updateInfo.apkName} (${com.codershubinc.nullvoidlauncher.utils.StorageUtils.formatSize(updateInfo.apkSize)})",
                    color = Color.White.copy(alpha = 0.4f),
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
fun DeveloperCard(isDevLoading: Boolean, devProfile: GithubProfile?, uriHandler: androidx.compose.ui.platform.UriHandler) {
    if (isDevLoading) {
        ModernCard {
            Text(
                text = "Fetching developer profile...",
                color = Color.White.copy(alpha = 0.4f),
                fontSize = 14.sp
            )
        }
    } else if (devProfile != null) {
        ModernCard(
            modifier = Modifier.clickable {
                uriHandler.openUri(Constants.Github.BASE_URL)
            }
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                NetworkImage(
                    url = devProfile.avatarUrl,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = devProfile.name,
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "@${devProfile.login}",
                        color = Color(0xFF3D5AFE),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = Color.White.copy(alpha = 0.05f))
            Spacer(modifier = Modifier.height(16.dp))

            ProfileDetailRow(Icons.Rounded.Description, "Bio", devProfile.bio)
            Spacer(modifier = Modifier.height(16.dp))
            ProfileDetailRow(Icons.Rounded.Business, "Company", devProfile.company)
            Spacer(modifier = Modifier.height(16.dp))
            ProfileDetailRow(Icons.Rounded.Code, "Public Repositories", devProfile.publicRepos.toString())
        }
    }
}

@Composable
fun SystemDetailsCard() {
    ModernCard {
        InfoRow(Icons.Rounded.Info, "Codename", Constants.System.CODENAME)
        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color.White.copy(alpha = 0.05f))
        InfoRow(Icons.Rounded.Business, "Organization", Constants.System.ORGANIZATION)
        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color.White.copy(alpha = 0.05f))
        InfoRow(Icons.Rounded.Build, "Build Type", Constants.System.BUILD_TYPE)
        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color.White.copy(alpha = 0.05f))
        InfoRow(Icons.Rounded.Code, "UI Engine", Constants.System.UI_ENGINE)
    }
}

@Composable
fun LinksCard(uriHandler: androidx.compose.ui.platform.UriHandler) {
    ModernCard {
        InfoRow(
            icon = Icons.Rounded.Public,
            label = "Source Code",
            value = "GitHub/${Constants.Github.REPO_SLUG}",
            onClick = { uriHandler.openUri(Constants.Github.REPO_URL) }
        )
        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color.White.copy(alpha = 0.05f))
        InfoRow(
            icon = Icons.Rounded.Description,
            label = "License",
            value = "GNU GPL v3",
            onClick = { uriHandler.openUri(Constants.Github.LICENSE_URL) }
        )
    }
}
