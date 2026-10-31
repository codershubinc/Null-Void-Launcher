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

package com.codershubinc.nullvoidlauncher.ui.github

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.OpenInNew
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codershubinc.nullvoidlauncher.data.model.GithubProfile
import com.codershubinc.nullvoidlauncher.data.UserManager
import com.codershubinc.nullvoidlauncher.ui.components.ModernCard
import com.codershubinc.nullvoidlauncher.ui.components.ProfileDetailRow
import com.codershubinc.nullvoidlauncher.ui.github.components.EditUsernameDialog
import com.codershubinc.nullvoidlauncher.ui.github.components.GithubProfileHeader
import com.codershubinc.nullvoidlauncher.ui.github.components.GithubStatsGrid
import com.codershubinc.nullvoidlauncher.utils.NetworkImage
import kotlinx.coroutines.launch

@Composable
fun GithubProfileScreen(
    username: String,
    userManager: UserManager,
    onUsernameChanged: ((String) -> Unit)? = null,
    onOpenSettings: () -> Unit,
    onOpenAbout: () -> Unit,
    onClose: () -> Unit
) {
    val uriHandler = LocalUriHandler.current
    val scope = rememberCoroutineScope()

    var activeUsername by remember(username) { mutableStateOf(username) }
    var profile by remember { mutableStateOf<GithubProfile?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var isRefreshing by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var syncErrorMessage by remember { mutableStateOf<String?>(null) }

    val scrollState = rememberScrollState()

    fun loadProfile(userToFetch: String, force: Boolean = false) {
        scope.launch {
            if (force) isRefreshing = true else isLoading = true
            syncErrorMessage = null
            val fetched = userManager.github.fetchGithubProfile(userToFetch, force)
            if (fetched != null) {
                profile = fetched
            } else {
                syncErrorMessage = "Unable to fetch profile for @$userToFetch"
            }
            isLoading = false
            isRefreshing = false
        }
    }

    LaunchedEffect(activeUsername) {
        loadProfile(activeUsername, false)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF080808))
    ) {
        // Decorative high-tech background radial glows
        Box(
            modifier = Modifier
                .size(340.dp)
                .align(Alignment.TopEnd)
                .offset(x = 120.dp, y = (-60).dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFF3D5AFE).copy(alpha = 0.15f), Color.Transparent)
                    )
                )
        )
        Box(
            modifier = Modifier
                .size(280.dp)
                .align(Alignment.BottomStart)
                .offset(x = (-80).dp, y = 80.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFF00E676).copy(alpha = 0.05f), Color.Transparent)
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp)
                .verticalScroll(scrollState)
        ) {
            // Header with navigation & quick action buttons
            GithubProfileHeader(
                onClose = onClose,
                onRefresh = { loadProfile(activeUsername, true) },
                isRefreshing = isRefreshing,
                onOpenSettings = onOpenSettings,
                onOpenAbout = onOpenAbout,
                onEditUsername = { showEditDialog = true }
            )

            Spacer(modifier = Modifier.height(28.dp))

            if (isLoading) {
                ModernCard {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(
                            color = Color(0xFF3D5AFE),
                            modifier = Modifier.size(36.dp),
                            strokeWidth = 3.dp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Synchronizing with GitHub...",
                            color = Color.White.copy(alpha = 0.5f),
                            fontSize = 14.sp
                        )
                    }
                }
            } else if (profile != null) {
                val p = profile!!

                // Main Identity Card
                ModernCard(
                    modifier = Modifier.clickable {
                        uriHandler.openUri("https://github.com/${p.login}")
                    }
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        NetworkImage(
                            url = p.avatarUrl,
                            modifier = Modifier
                                .size(76.dp)
                                .clip(RoundedCornerShape(22.dp))
                                .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(22.dp))
                        )
                        Spacer(modifier = Modifier.width(18.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = p.name,
                                color = Color.White,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "@${p.login}",
                                color = Color(0xFF3D5AFE),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.OpenInNew,
                            contentDescription = "View on GitHub",
                            tint = Color.White.copy(alpha = 0.3f),
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    if (p.bio.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = p.bio,
                            color = Color.White.copy(alpha = 0.75f),
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                    HorizontalDivider(color = Color.White.copy(alpha = 0.05f))
                    Spacer(modifier = Modifier.height(16.dp))

                    // Social Counter Matrix (Repos, Followers, Following)
                    GithubStatsGrid(profile = p)
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Metadata Details Card
                ModernCard {
                    Text(
                        text = "Profile Telemetry",
                        color = Color.White.copy(alpha = 0.45f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp,
                        modifier = Modifier.padding(bottom = 14.dp)
                    )

                    ProfileDetailRow(Icons.Rounded.Business, "Company / Org", p.company.ifEmpty { "Independent" })

                    if (p.location.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(14.dp))
                        ProfileDetailRow(Icons.Rounded.LocationOn, "Location", p.location)
                    }

                    if (p.blog.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(14.dp))
                        val cleanBlog = p.blog.trim()
                        val blogUrl = if (cleanBlog.startsWith("http://") || cleanBlog.startsWith("https://")) cleanBlog else "https://$cleanBlog"
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { uriHandler.openUri(blogUrl) },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(modifier = Modifier.weight(1f)) {
                                ProfileDetailRow(Icons.Rounded.Link, "Website / Portfolio", cleanBlog)
                            }
                            Icon(
                                imageVector = Icons.Rounded.ChevronRight,
                                contentDescription = null,
                                tint = Color.White.copy(alpha = 0.3f),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    if (p.hireable) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF00E676).copy(alpha = 0.12f))
                                .border(1.dp, Color(0xFF00E676).copy(alpha = 0.25f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF00E676))
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Available for hire",
                                color = Color(0xFF00E676),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    if (p.publicGists > 0) {
                        Spacer(modifier = Modifier.height(14.dp))
                        ProfileDetailRow(Icons.Rounded.Terminal, "Public Gists", p.publicGists.toString())
                    }

                    if (p.createdAt.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(14.dp))
                        val formattedDate = p.createdAt.take(10)
                        ProfileDetailRow(Icons.Rounded.CalendarToday, "Member Since", formattedDate)
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    ProfileDetailRow(Icons.Rounded.AlternateEmail, "GitHub Handle", "@${p.login}")
                }
            } else {
                // Connection or Not Found Card
                ModernCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFF5252).copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.ErrorOutline,
                                contentDescription = null,
                                tint = Color(0xFFFF5252),
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Unable to Synchronize",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = syncErrorMessage ?: "User @$activeUsername not found or network offline.",
                                color = Color.White.copy(alpha = 0.5f),
                                fontSize = 12.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF3D5AFE))
                            .clickable { showEditDialog = true }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Change GitHub Username",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(36.dp))

            // Footer Info
            Text(
                text = "NullVoid Cloud Telemetry • Cached & Offline Capable",
                color = Color.White.copy(alpha = 0.2f),
                fontSize = 11.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(32.dp))
        }

        // Edit Username Dialog
        if (showEditDialog) {
            EditUsernameDialog(
                initialUsername = activeUsername,
                onDismiss = { showEditDialog = false },
                onSave = { newHandle ->
                    activeUsername = newHandle
                    userManager.github.username = newHandle
                    onUsernameChanged?.invoke(newHandle)
                    showEditDialog = false
                    loadProfile(newHandle, true)
                }
            )
        }
    }
}
