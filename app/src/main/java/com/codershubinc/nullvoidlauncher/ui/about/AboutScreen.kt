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
    
    var updateStatus by remember { mutableStateOf("Check for updates") }
    var isChecking by remember { mutableStateOf(false) }
    val currentVersion = Constants.App.VERSION+"-"+ Constants.App.VERSION_SUFFIX

    var devProfile by remember { mutableStateOf<GithubProfile?>(null) }
    var isDevLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        isDevLoading = true
        devProfile = userManager.fetchGithubProfile(Constants.Github.USERNAME, false)
        isDevLoading = false
    }

    suspend fun checkForUpdates() {
        isChecking = true
        updateStatus = "Searching servers..."
        try {
            val result = withContext(Dispatchers.IO) {
                val url = URL(Constants.Github.LATEST_RELEASE_API_URL)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.setRequestProperty("User-Agent", Constants.Github.USER_AGENT)
                
                if (connection.responseCode == 200) {
                    val response = connection.inputStream.bufferedReader().use { it.readText() }
                    val json = JSONObject(response)
                    val latestTag = json.getString("tag_name")
                    if (latestTag != currentVersion) {
                        "Update Available: $latestTag"
                    } else {
                        "System is up to date"
                    }
                } else {
                    "Server error: ${connection.responseCode}"
                }
            }
            updateStatus = result
        } catch (e: Exception) {
            updateStatus = "Link failed"
        } finally {
            isChecking = false
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
                        // App Version Card
                        AppVersionCard(currentVersion, isChecking, updateStatus) {
                            scope.launch { checkForUpdates() }
                        }

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
                AppVersionCard(currentVersion, isChecking, updateStatus) {
                    scope.launch { checkForUpdates() }
                }

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
    }
}

@Composable
fun AppVersionCard(currentVersion: String, isChecking: Boolean, updateStatus: String, onUpdateClick: () -> Unit) {
    ModernCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
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

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF3D5AFE))
                    .clickable(enabled = !isChecking) { onUpdateClick() }
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Text(
                    text = if (isChecking) "Checking..." else "Update",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        if (updateStatus != "Check for updates") {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = updateStatus,
                color = if (updateStatus.contains("Available")) Color(0xFF00E676) else Color.White.copy(alpha = 0.7f),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
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
