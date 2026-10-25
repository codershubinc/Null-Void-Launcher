package com.codershubinc.nullvoidlauncher.ui.settings.fonts

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.codershubinc.nullvoidlauncher.data.WidgetFont
import com.codershubinc.nullvoidlauncher.data.fonts.FontDownloadState
import com.codershubinc.nullvoidlauncher.data.fonts.FontManager

private val DialogDarkBg = Color(0xFF121214)
private val DialogCardBg = Color(0xFF1B1B1F)
private val DialogBorderColor = Color(0xFF2C2C32)
private val AccentColor = Color(0xFF38BDF8)

private val POPULAR_GOOGLE_FONTS = listOf(
    "Outfit", "JetBrains Mono", "Inter", "Poppins", "Fira Code",
    "Space Grotesk", "Bebas Neue", "Lobster", "Comfortaa", "Roboto Slab",
    "Montserrat", "Oswald", "Raleway", "Lora", "Rubik", "Kanit", "Ubuntu", "Merriweather"
)

@Composable
fun GoogleFontsDialog(
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    var isSearching by remember { mutableStateOf(false) }
    var statusMessage by remember { mutableStateOf<String?>(null) }
    var isDownloadingCurated by remember { mutableStateOf(false) }
    var curatedProgress by remember { mutableStateOf(Pair(0, 0)) }

    val downloadStates by FontManager.downloadStates.collectAsState()
    val downloadVersion by FontManager.downloadVersion.collectAsState()
    val downloadedFonts = remember(downloadVersion) { FontManager.getDownloadedFonts() }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.75f))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .fillMaxHeight(0.90f),
                shape = RoundedCornerShape(28.dp),
                color = DialogDarkBg,
                border = androidx.compose.foundation.BorderStroke(1.dp, DialogBorderColor)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp)
                ) {
                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Rounded.FontDownload,
                                    contentDescription = null,
                                    tint = AccentColor,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Google Fonts Manager",
                                    color = Color.White,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Text(
                                text = "On-demand typography with 0 KB initial APK bloat",
                                color = Color.White.copy(alpha = 0.5f),
                                fontSize = 12.sp,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }

                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .size(36.dp)
                                .background(Color.White.copy(alpha = 0.08f), CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Close,
                                contentDescription = "Close",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Section 1: Curated Recommended Pack
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(DialogCardBg)
                                .border(1.dp, DialogBorderColor, RoundedCornerShape(16.dp))
                                .padding(16.dp)
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "Curated Fonts Pack",
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp
                                        )
                                        Text(
                                            text = "Download Outfit, JetBrains Mono, Inter, Playfair & more in 1 tap (~450 KB total)",
                                            color = Color.White.copy(alpha = 0.6f),
                                            fontSize = 12.sp
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Button(
                                        onClick = {
                                            if (!isDownloadingCurated) {
                                                isDownloadingCurated = true
                                                FontManager.downloadCuratedFonts(
                                                    onProgress = { current, total ->
                                                        curatedProgress = Pair(current, total)
                                                    },
                                                    onComplete = { downloaded, total ->
                                                        isDownloadingCurated = false
                                                        Toast.makeText(
                                                            context,
                                                            "Downloaded $downloaded of $total fonts",
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                    }
                                                )
                                            }
                                        },
                                        enabled = !isDownloadingCurated,
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = AccentColor,
                                            contentColor = Color.Black
                                        ),
                                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                                    ) {
                                        if (isDownloadingCurated) {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(14.dp),
                                                color = Color.Black,
                                                strokeWidth = 2.dp
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "${curatedProgress.first}/${curatedProgress.second}",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        } else {
                                            Icon(
                                                imageVector = Icons.Rounded.CloudDownload,
                                                contentDescription = null,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("Download All", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }

                        // Section 2: Search & Add Any Google Font
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(DialogCardBg)
                                .border(1.dp, DialogBorderColor, RoundedCornerShape(16.dp))
                                .padding(16.dp)
                        ) {
                            Column {
                                Text(
                                    text = "Add Any Google Font",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                                Text(
                                    text = "Type any family name from fonts.google.com to download it",
                                    color = Color.White.copy(alpha = 0.6f),
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(bottom = 12.dp)
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    OutlinedTextField(
                                        value = searchQuery,
                                        onValueChange = { searchQuery = it },
                                        placeholder = {
                                            Text("e.g., Oswald, Montserrat, Lora", color = Color.White.copy(alpha = 0.35f), fontSize = 13.sp)
                                        },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedTextColor = Color.White,
                                            unfocusedTextColor = Color.White,
                                            focusedBorderColor = AccentColor,
                                            unfocusedBorderColor = DialogBorderColor,
                                            cursorColor = AccentColor
                                        ),
                                        singleLine = true
                                    )

                                    Spacer(modifier = Modifier.width(8.dp))

                                    Button(
                                        onClick = {
                                            val query = searchQuery.trim()
                                            if (query.isNotEmpty() && !isSearching) {
                                                isSearching = true
                                                statusMessage = "Searching & downloading '$query'..."
                                                FontManager.downloadCustomFont(query) { success, error ->
                                                    isSearching = false
                                                    if (success) {
                                                        FontManager.setCustomFontFamilyName(query)
                                                        statusMessage = "Successfully downloaded '$query'!"
                                                        searchQuery = ""
                                                        Toast.makeText(context, "Added font '$query'", Toast.LENGTH_SHORT).show()
                                                    } else {
                                                        statusMessage = "Failed: ${error ?: "Font not found"}"
                                                    }
                                                }
                                            }
                                        },
                                        enabled = searchQuery.trim().isNotEmpty() && !isSearching,
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = AccentColor,
                                            contentColor = Color.Black
                                        ),
                                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 14.dp)
                                    ) {
                                        if (isSearching) {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(16.dp),
                                                color = Color.Black,
                                                strokeWidth = 2.dp
                                            )
                                        } else {
                                            Icon(imageVector = Icons.Rounded.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Fetch", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }

                                statusMessage?.let { msg ->
                                    Text(
                                        text = msg,
                                        color = if (msg.startsWith("Failed")) Color(0xFFF87171) else AccentColor,
                                        fontSize = 11.sp,
                                        modifier = Modifier.padding(top = 6.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Text(
                                    text = "Popular Suggestions:",
                                    color = Color.White.copy(alpha = 0.6f),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 6.dp)
                                        .horizontalScroll(rememberScrollState()),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    POPULAR_GOOGLE_FONTS.forEach { fontName ->
                                        val isDownloaded = FontManager.isCustomFontDownloaded(fontName)
                                        val isDownloading = downloadStates[fontName] == FontDownloadState.DOWNLOADING
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(if (isDownloaded) AccentColor.copy(alpha = 0.15f) else Color.White.copy(alpha = 0.05f))
                                                .border(
                                                    1.dp,
                                                    if (isDownloaded) AccentColor.copy(alpha = 0.4f) else Color.White.copy(alpha = 0.08f),
                                                    RoundedCornerShape(8.dp)
                                                )
                                                .clickable(enabled = !isDownloading) {
                                                    if (!isDownloaded) {
                                                        FontManager.downloadCustomFont(fontName) { ok, err ->
                                                            if (ok) {
                                                                Toast.makeText(context, "Downloaded $fontName", Toast.LENGTH_SHORT).show()
                                                            } else {
                                                                Toast.makeText(context, "Failed: $err", Toast.LENGTH_SHORT).show()
                                                            }
                                                        }
                                                    } else {
                                                        FontManager.setCustomFontFamilyName(fontName)
                                                        Toast.makeText(context, "Set active custom font to $fontName", Toast.LENGTH_SHORT).show()
                                                    }
                                                }
                                                .padding(horizontal = 10.dp, vertical = 6.dp)
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                if (isDownloading) {
                                                    CircularProgressIndicator(
                                                        modifier = Modifier.size(10.dp),
                                                        color = AccentColor,
                                                        strokeWidth = 1.5.dp
                                                    )
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                } else if (isDownloaded) {
                                                    Icon(
                                                        imageVector = Icons.Rounded.Check,
                                                        contentDescription = null,
                                                        tint = AccentColor,
                                                        modifier = Modifier.size(12.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                }
                                                Text(
                                                    text = fontName,
                                                    color = if (isDownloaded) AccentColor else Color.White.copy(alpha = 0.8f),
                                                    fontSize = 11.sp
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Section 3: Downloaded Fonts on Device
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Downloaded Fonts (${downloadedFonts.size})",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                                val totalKb = downloadedFonts.sumOf { it.fileSizeKb }
                                Text(
                                    text = "$totalKb KB total",
                                    color = Color.White.copy(alpha = 0.45f),
                                    fontSize = 12.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            if (downloadedFonts.isEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(DialogCardBg)
                                        .padding(24.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(
                                            imageVector = Icons.Rounded.CloudDownload,
                                            contentDescription = null,
                                            tint = Color.White.copy(alpha = 0.3f),
                                            modifier = Modifier.size(36.dp)
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = "No fonts downloaded yet",
                                            color = Color.White.copy(alpha = 0.6f),
                                            fontSize = 13.sp
                                        )
                                        Text(
                                            text = "The launcher is using native system fonts (0 KB). Tap 'Download All' above to fetch curated fonts.",
                                            color = Color.White.copy(alpha = 0.35f),
                                            fontSize = 11.sp,
                                            modifier = Modifier.padding(top = 4.dp)
                                        )
                                    }
                                }
                            } else {
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    downloadedFonts.forEach { fontInfo ->
                                        val sampleFontFamily = remember(fontInfo.name) {
                                            try {
                                                val typeface = android.graphics.Typeface.createFromFile(fontInfo.file)
                                                FontFamily(typeface)
                                            } catch (e: Exception) {
                                                FontFamily.Default
                                            }
                                        }

                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(DialogCardBg)
                                                .border(1.dp, DialogBorderColor, RoundedCornerShape(12.dp))
                                                .padding(12.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                                        Text(
                                                            text = fontInfo.name,
                                                            color = Color.White,
                                                            fontWeight = FontWeight.SemiBold,
                                                            fontSize = 13.sp
                                                        )
                                                        Spacer(modifier = Modifier.width(8.dp))
                                                        Text(
                                                            text = "${fontInfo.fileSizeKb} KB",
                                                            color = Color.White.copy(alpha = 0.4f),
                                                            fontSize = 10.sp
                                                        )
                                                    }
                                                    Text(
                                                        text = "12:45 • Null & Void Launcher Typography Preview",
                                                        fontFamily = sampleFontFamily,
                                                        color = Color.White.copy(alpha = 0.85f),
                                                        fontSize = 13.sp,
                                                        maxLines = 1,
                                                        overflow = TextOverflow.Ellipsis,
                                                        modifier = Modifier.padding(top = 4.dp)
                                                    )
                                                }

                                                IconButton(
                                                    onClick = {
                                                        FontManager.deleteCustomFont(fontInfo.name)
                                                        Toast.makeText(context, "Deleted ${fontInfo.name}", Toast.LENGTH_SHORT).show()
                                                    },
                                                    modifier = Modifier.size(32.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Rounded.DeleteOutline,
                                                        contentDescription = "Delete",
                                                        tint = Color(0xFFF87171).copy(alpha = 0.8f),
                                                        modifier = Modifier.size(18.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
