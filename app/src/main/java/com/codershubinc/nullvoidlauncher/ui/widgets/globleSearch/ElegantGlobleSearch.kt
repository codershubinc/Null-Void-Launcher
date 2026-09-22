package com.codershubinc.nullvoidlauncher.ui.widgets.globleSearch

import android.content.Context
import android.content.Intent
import android.content.pm.LauncherApps
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codershubinc.nullvoidlauncher.data.UserManager
import com.codershubinc.nullvoidlauncher.data.repository.AppInfo
import com.codershubinc.nullvoidlauncher.data.repository.LazyAppIcon
import com.codershubinc.nullvoidlauncher.utils.MathEvaluator
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ElegantSearchScreen(
    allApps: List<AppInfo>,
    userManager: UserManager,
    onClose: () -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var searchQuery by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    val listState = rememberLazyListState()

    var selectedAppForMenu by remember { mutableStateOf<AppInfo?>(null) }
    var hiddenApps by remember { mutableStateOf(userManager.getHiddenApps()) }
    var favoritesList by remember { mutableStateOf(userManager.getFavorites()) }
    var showHiddenAppsOnly by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(150)
        focusRequester.requestFocus()
        keyboardController?.show()
    }

    // Calculator evaluation
    val mathResult = remember(searchQuery) {
        MathEvaluator.evaluate(searchQuery)
    }

    // Filter apps, respecting hidden apps
    val visibleApps = remember(allApps, hiddenApps, showHiddenAppsOnly) {
        allApps.filter { app ->
            val key = app.componentName.flattenToString()
            if (showHiddenAppsOnly) {
                hiddenApps.contains(key)
            } else {
                !hiddenApps.contains(key)
            }
        }.sortedBy { it.label.lowercase() }
    }

    val filteredApps = remember(searchQuery, visibleApps) {
        if (searchQuery.trim().isEmpty()) {
            visibleApps
        } else {
            val query = searchQuery.trim().lowercase()
            visibleApps.filter { it.label.lowercase().contains(query) }
        }
    }

    // Generate Alphabet Fast Scroller indices
    val alphabet = remember(filteredApps, searchQuery) {
        if (searchQuery.isEmpty()) {
            filteredApps.mapNotNull { it.label.firstOrNull()?.uppercaseChar() }
                .distinct()
                .filter { it in 'A'..'Z' || it == '#' }
                .sorted()
        } else {
            emptyList()
        }
    }

    val firstLetterIndices = remember(filteredApps, alphabet) {
        val map = mutableMapOf<Char, Int>()
        filteredApps.forEachIndexed { index, app ->
            val first = app.label.firstOrNull()?.uppercaseChar() ?: '#'
            val letter = if (first in 'A'..'Z') first else '#'
            if (!map.containsKey(letter)) {
                map[letter] = index
            }
        }
        map
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF070709))
            .statusBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // Search Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.08f),
                                Color.White.copy(alpha = 0.03f)
                            )
                        ),
                        shape = RoundedCornerShape(24.dp)
                    )
                    .border(
                        width = 1.dp,
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.18f),
                                Color.White.copy(alpha = 0.04f)
                            )
                        ),
                        shape = RoundedCornerShape(24.dp)
                    )
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Search,
                    contentDescription = null,
                    tint = Color(0xFFC5A35E),
                    modifier = Modifier.size(24.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                BasicTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    textStyle = TextStyle(
                        color = Color.White,
                        fontSize = 17.sp,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Medium
                    ),
                    cursorBrush = SolidColor(Color(0xFFC5A35E)),
                    modifier = Modifier
                        .weight(1f)
                        .focusRequester(focusRequester),
                    decorationBox = { innerTextField ->
                        if (searchQuery.isEmpty()) {
                            Text(
                                text = if (showHiddenAppsOnly) "Search hidden protocols..." else "Search or calculate (e.g. 45*12)...",
                                color = Color.White.copy(alpha = 0.3f),
                                fontFamily = FontFamily.Serif,
                                fontSize = 16.sp
                            )
                        }
                        innerTextField()
                    }
                )

                if (searchQuery.isNotEmpty()) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = "Clear",
                        tint = Color.White.copy(alpha = 0.5f),
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .clickable { searchQuery = "" }
                    )
                } else {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = "Close",
                        tint = Color.White.copy(alpha = 0.4f),
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .clickable {
                                keyboardController?.hide()
                                onClose()
                            }
                    )
                }
            }

            // Math evaluation banner if expression parsed
            if (mathResult != null) {
                Spacer(modifier = Modifier.height(14.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF3D5AFE).copy(alpha = 0.12f))
                        .border(1.dp, Color(0xFF3D5AFE).copy(alpha = 0.35f), RoundedCornerShape(16.dp))
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Rounded.Calculate,
                            contentDescription = "Math",
                            tint = Color(0xFF3D5AFE),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "= $mathResult",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White.copy(alpha = 0.08f))
                            .clickable {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                                val clip = android.content.ClipData.newPlainText("Result", mathResult)
                                clipboard.setPrimaryClip(clip)
                            }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "Copy",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Section Filter & Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = when {
                        searchQuery.isNotEmpty() -> "RESULTS FOR: ${searchQuery.uppercase()}"
                        showHiddenAppsOnly -> "HIDDEN APPLICATIONS (${filteredApps.size})"
                        else -> "ALL APPLICATIONS (${filteredApps.size})"
                    },
                    color = Color(0xFFC5A35E).copy(alpha = 0.7f),
                    fontSize = 11.sp,
                    fontFamily = FontFamily.SansSerif,
                    letterSpacing = 3.sp,
                    fontWeight = FontWeight.Bold
                )

                if (hiddenApps.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (showHiddenAppsOnly) Color(0xFF3D5AFE).copy(alpha = 0.2f) else Color.White.copy(alpha = 0.05f))
                            .border(1.dp, if (showHiddenAppsOnly) Color(0xFF3D5AFE) else Color.White.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                            .clickable { showHiddenAppsOnly = !showHiddenAppsOnly }
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (showHiddenAppsOnly) Icons.Rounded.VisibilityOff else Icons.Rounded.Visibility,
                                contentDescription = null,
                                tint = if (showHiddenAppsOnly) Color(0xFF3D5AFE) else Color.White.copy(alpha = 0.6f),
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (showHiddenAppsOnly) "Show All" else "Hidden (${hiddenApps.size})",
                                color = if (showHiddenAppsOnly) Color.White else Color.White.copy(alpha = 0.6f),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // App List with Alphabet Scroller
            Box(modifier = Modifier.fillMaxSize()) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(
                        bottom = 32.dp,
                        end = if (alphabet.isNotEmpty()) 28.dp else 0.dp
                    )
                ) {
                    itemsIndexed(
                        items = filteredApps,
                        key = { _, app -> app.componentName.flattenToString() + app.userHandle.hashCode() }
                    ) { _, app ->
                        ElegantAppRow(
                            app = app,
                            context = context,
                            isFavorite = favoritesList.contains(app.componentName.flattenToString()),
                            isHidden = hiddenApps.contains(app.componentName.flattenToString()),
                            onAppClick = {
                                val launcherApps = context.getSystemService(Context.LAUNCHER_APPS_SERVICE) as LauncherApps
                                launcherApps.startMainActivity(app.componentName, app.userHandle, null, null)
                                keyboardController?.hide()
                                onClose()
                            },
                            onLongClick = {
                                selectedAppForMenu = app
                            }
                        )
                    }
                }

                // Fast Alpha Scroller on Right Edge
                if (alphabet.isNotEmpty()) {
                    Column(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(vertical = 12.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White.copy(alpha = 0.03f))
                            .padding(horizontal = 4.dp, vertical = 6.dp)
                            .pointerInput(Unit) {
                                detectVerticalDragGestures { change, _ ->
                                    val totalHeight = size.height
                                    val itemHeight = totalHeight / alphabet.size.toFloat()
                                    val index = (change.position.y / itemHeight).toInt().coerceIn(0, alphabet.size - 1)
                                    val letter = alphabet[index]
                                    val targetIndex = firstLetterIndices[letter]
                                    if (targetIndex != null) {
                                        scope.launch {
                                            listState.scrollToItem(targetIndex)
                                        }
                                    }
                                }
                            },
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        alphabet.forEach { letter ->
                            Text(
                                text = letter.toString(),
                                color = Color.White.copy(alpha = 0.45f),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier
                                    .clickable {
                                        val target = firstLetterIndices[letter]
                                        if (target != null) {
                                            scope.launch {
                                                listState.scrollToItem(target)
                                            }
                                        }
                                    }
                                    .padding(vertical = 1.dp)
                            )
                        }
                    }
                }
            }
        }

        // App Long-Press Action Modal BottomSheet
        if (selectedAppForMenu != null) {
            val app = selectedAppForMenu!!
            val appKey = app.componentName.flattenToString()
            val isFav = favoritesList.contains(appKey)
            val isHid = hiddenApps.contains(appKey)

            AlertDialog(
                onDismissRequest = { selectedAppForMenu = null },
                containerColor = Color(0xFF141418),
                shape = RoundedCornerShape(24.dp),
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(10.dp))
                                .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            LazyAppIcon(app, context)
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(
                                text = app.label,
                                color = Color.White,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = app.packageName,
                                color = Color.White.copy(alpha = 0.4f),
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                },
                text = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // App Info
                        AppMenuActionItem(
                            icon = Icons.Rounded.Info,
                            title = "App Info",
                            subtitle = "System permissions, storage & battery"
                        ) {
                            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                data = Uri.parse("package:${app.packageName}")
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            }
                            context.startActivity(intent)
                            selectedAppForMenu = null
                        }

                        // Toggle Favorites
                        AppMenuActionItem(
                            icon = if (isFav) Icons.Rounded.StarBorder else Icons.Rounded.Star,
                            title = if (isFav) "Remove from Favorites" else "Add to Favorites",
                            subtitle = if (isFav) "Remove from quick launcher dock" else "Pin to quick launcher dock"
                        ) {
                            val current = favoritesList.toMutableList()
                            if (isFav) {
                                current.remove(appKey)
                            } else {
                                if (!current.contains(appKey)) current.add(appKey)
                            }
                            userManager.saveFavorites(current)
                            favoritesList = current
                            selectedAppForMenu = null
                        }

                        // Toggle Hide App
                        AppMenuActionItem(
                            icon = if (isHid) Icons.Rounded.Visibility else Icons.Rounded.VisibilityOff,
                            title = if (isHid) "Unhide Application" else "Hide Application",
                            subtitle = if (isHid) "Restore app to main drawer" else "Hide app from drawer search"
                        ) {
                            userManager.toggleHiddenApp(appKey)
                            hiddenApps = userManager.getHiddenApps()
                            selectedAppForMenu = null
                        }

                        // Uninstall App
                        AppMenuActionItem(
                            icon = Icons.Rounded.DeleteOutline,
                            title = "Uninstall",
                            subtitle = "Remove app from device",
                            tint = Color(0xFFFF5252)
                        ) {
                            val uninstallIntent = Intent(Intent.ACTION_DELETE).apply {
                                data = Uri.parse("package:${app.packageName}")
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            }
                            context.startActivity(uninstallIntent)
                            selectedAppForMenu = null
                        }
                    }
                },
                confirmButton = {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { selectedAppForMenu = null }
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "Cancel",
                            color = Color.White.copy(alpha = 0.6f),
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp
                        )
                    }
                }
            )
        }
    }
}

@Composable
fun AppMenuActionItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    tint: Color = Color.White,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(tint.copy(alpha = 0.08f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = title,
                color = tint,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = subtitle,
                color = Color.White.copy(alpha = 0.4f),
                fontSize = 11.sp
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ElegantAppRow(
    app: AppInfo,
    context: Context,
    isFavorite: Boolean = false,
    isHidden: Boolean = false,
    onAppClick: () -> Unit,
    onLongClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .combinedClickable(
                onClick = onAppClick,
                onLongClick = onLongClick
            )
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(12.dp))
                .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            LazyAppIcon(app, context)
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = app.label.uppercase(),
                color = Color.White,
                fontFamily = FontFamily.Serif,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 1.sp
            )
            Text(
                text = "SYSTEM.PROTOCOL.LAUNCH",
                color = Color(0xFFC5A35E).copy(alpha = 0.4f),
                fontFamily = FontFamily.Monospace,
                fontSize = 8.sp,
                letterSpacing = 1.sp
            )
        }

        if (isFavorite) {
            Icon(
                imageVector = Icons.Rounded.Star,
                contentDescription = "Favorite",
                tint = Color(0xFFC5A35E),
                modifier = Modifier
                    .size(16.dp)
                    .padding(end = 4.dp)
            )
        }

        if (isHidden) {
            Icon(
                imageVector = Icons.Rounded.VisibilityOff,
                contentDescription = "Hidden",
                tint = Color.White.copy(alpha = 0.3f),
                modifier = Modifier
                    .size(16.dp)
                    .padding(end = 4.dp)
            )
        }
    }
}
