package com.codershubinc.nullvoidlauncher.ui.settings.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codershubinc.nullvoidlauncher.data.StorageStyle
import com.codershubinc.nullvoidlauncher.data.UserManager
import com.codershubinc.nullvoidlauncher.ui.components.ModernCard
import com.codershubinc.nullvoidlauncher.ui.widgets.StorageWidget
import com.codershubinc.nullvoidlauncher.utils.StorageInfoState

@Composable
fun StorageWidgetTweaksPage(
    userManager: UserManager,
    onBack: () -> Unit
) {
    var showStorage by remember { mutableStateOf(userManager.getShowStorageWidget()) }
    var storageStyle by remember { mutableStateOf(userManager.getStorageStyle()) }
    var storageFont by remember { mutableStateOf(userManager.getStorageFont()) }

    val previewStorage = remember {
        StorageInfoState(
            totalBytes = 128_000_000_000L,
            availableBytes = 74_200_000_000L,
            usedBytes = 53_800_000_000L,
            usedPercentage = 42,
            totalText = "128 GB",
            availableText = "74.2 GB",
            usedText = "53.8 GB"
        )
    }

    WidgetPageScaffold(
        title = "Storage Widget",
        subtitle = "Gauge bars, circular rings and disk telemetry",
        onBack = onBack
    ) {
        WidgetVisibilityCard(
            title = "Show Storage Widget",
            description = "Display disk usage meter within the clock stack",
            visible = showStorage,
            onVisibleChange = {
                showStorage = it
                userManager.saveShowStorageWidget(it)
            }
        )

        Spacer(modifier = Modifier.height(20.dp))

        ModernCard {
            Text(
                text = "Live Preview",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(14.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color.White.copy(alpha = 0.03f))
                    .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(14.dp))
                    .padding(16.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                StorageWidget(
                    style = storageStyle,
                    font = storageFont,
                    previewInfo = previewStorage
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            WidgetStyleSelector(
                title = "Storage Style",
                styles = StorageStyle.entries,
                selectedStyle = storageStyle,
                onStyleSelected = {
                    storageStyle = it
                    userManager.saveStorageStyle(it)
                },
                getLabel = { it.name }
            )

            WidgetFontSelector(
                selectedFont = storageFont,
                onFontSelected = {
                    storageFont = it
                    userManager.saveStorageFont(it)
                }
            )
        }
    }
}
