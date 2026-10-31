package com.codershubinc.nullvoidlauncher.ui.settings.widgets

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
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.CloudDownload
import androidx.compose.material.icons.rounded.FontDownload
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codershubinc.nullvoidlauncher.data.WidgetFont
import com.codershubinc.nullvoidlauncher.data.fonts.FontDownloadState
import com.codershubinc.nullvoidlauncher.data.fonts.FontManager
import com.codershubinc.nullvoidlauncher.ui.components.ModernCard
import com.codershubinc.nullvoidlauncher.ui.settings.fonts.GoogleFontsDialog

val WidgetSettingsAccent = Color(0xFF3D5AFE)

@Composable
fun WidgetPageScaffold(
    title: String,
    subtitle: String,
    onBack: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF080808))
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(24.dp)
            .verticalScroll(scrollState)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.05f))
                .clickable { onBack() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                contentDescription = "Back",
                tint = Color.White
            )
        }

        Spacer(modifier = Modifier.height(28.dp))
        Text(
            text = title,
            color = Color.White,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = subtitle,
            color = Color.White.copy(alpha = 0.5f),
            fontSize = 15.sp
        )
        Spacer(modifier = Modifier.height(24.dp))

        content()

        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
fun WidgetVisibilityCard(
    title: String,
    description: String,
    visible: Boolean,
    onVisibleChange: (Boolean) -> Unit
) {
    ModernCard {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f).padding(end = 16.dp)) {
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = description,
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 12.sp
                )
            }
            Switch(
                checked = visible,
                onCheckedChange = onVisibleChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = WidgetSettingsAccent
                )
            )
        }
    }
}

@Composable
fun <T> WidgetStyleSelector(
    title: String = "Style",
    styles: List<T>,
    selectedStyle: T,
    onStyleSelected: (T) -> Unit,
    getLabel: (T) -> String = { it.toString() }
) {
    Text(
        text = title,
        color = Color.White.copy(alpha = 0.7f),
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(bottom = 8.dp)
    )
    Row(
        modifier = Modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        styles.forEach { style ->
            val isSelected = selectedStyle == style
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isSelected) WidgetSettingsAccent else Color.White.copy(alpha = 0.08f))
                    .clickable { onStyleSelected(style) }
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Text(
                    text = getLabel(style),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
fun WidgetFontSelector(
    selectedFont: WidgetFont,
    onFontSelected: (WidgetFont) -> Unit
) {
    val context = LocalContext.current
    var showGoogleFontsDialog by remember { mutableStateOf(false) }
    val downloadStates by FontManager.downloadStates.collectAsState()
    val downloadVersion by FontManager.downloadVersion.collectAsState()

    Text(
        text = "Font",
        color = Color.White.copy(alpha = 0.7f),
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
    )
    Row(
        modifier = Modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        WidgetFont.entries.forEach { f ->
            val isSelected = selectedFont == f
            val isGoogleFont = f.isGoogleFont
            val familyName = f.googleFontFamily
            val isDownloaded = remember(f, downloadVersion) { FontManager.isFontDownloaded(f) }
            val isDownloading = familyName != null && downloadStates[familyName] == FontDownloadState.DOWNLOADING

            val label = if (f == WidgetFont.CUSTOM) {
                "Custom (${FontManager.getCustomFontFamilyName()})"
            } else {
                f.label
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isSelected) WidgetSettingsAccent else Color.White.copy(alpha = 0.08f))
                    .border(
                        1.dp,
                        if (isSelected) WidgetSettingsAccent else Color.White.copy(alpha = 0.08f),
                        RoundedCornerShape(10.dp)
                    )
                    .clickable {
                        if (isGoogleFont && familyName != null && !isDownloaded && !isDownloading) {
                            Toast.makeText(context, "Downloading font '${f.label}'...", Toast.LENGTH_SHORT).show()
                            FontManager.downloadFont(f)
                        }
                        onFontSelected(f)
                    }
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (isDownloading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(10.dp),
                            color = Color.White,
                            strokeWidth = 1.5.dp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                    } else if (isGoogleFont && familyName != null && !isDownloaded) {
                        Icon(
                            imageVector = Icons.Rounded.CloudDownload,
                            contentDescription = "Download font",
                            tint = Color.White.copy(alpha = 0.6f),
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                    }

                    Text(
                        text = label,
                        color = Color.White,
                        fontFamily = f.toFontFamily(),
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 12.sp
                    )
                }
            }
        }

        // + Google Fonts Button
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(Color.White.copy(alpha = 0.12f))
                .border(1.dp, WidgetSettingsAccent.copy(alpha = 0.6f), RoundedCornerShape(10.dp))
                .clickable { showGoogleFontsDialog = true }
                .padding(horizontal = 14.dp, vertical = 8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Rounded.Add,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Google Fonts",
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp
                )
            }
        }
    }

    if (showGoogleFontsDialog) {
        GoogleFontsDialog(onDismiss = { showGoogleFontsDialog = false })
    }
}
