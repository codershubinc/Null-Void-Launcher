package com.codershubinc.nullvoidlauncher.ui.settings.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.DataUsage
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codershubinc.nullvoidlauncher.data.NetworkStyle
import com.codershubinc.nullvoidlauncher.data.UserManager
import com.codershubinc.nullvoidlauncher.ui.components.ModernCard
import com.codershubinc.nullvoidlauncher.ui.network.NetworkInfoState
import com.codershubinc.nullvoidlauncher.ui.network.NetworkUsageScreen
import com.codershubinc.nullvoidlauncher.ui.widgets.NetworkWidget

@Composable
fun NetworkWidgetTweaksPage(
    userManager: UserManager,
    onBack: () -> Unit
) {
    var showNetwork by remember { mutableStateOf(userManager.getShowNetworkWidget()) }
    var networkStyle by remember { mutableStateOf(userManager.getNetworkStyle()) }
    var networkFont by remember { mutableStateOf(userManager.getNetworkFont()) }
    var showNetworkUsageOnWidget by remember { mutableStateOf(userManager.getShowNetworkUsageOnWidget()) }
    var isDetailedUsageOpen by remember { mutableStateOf(false) }

    val previewNetwork = remember {
        NetworkInfoState(
            isConnected = true,
            isWifi = true,
            ssid = "NullVoid_5G",
            ipv4 = "192.168.1.105",
            linkSpeedMbps = 433,
            frequencyMhz = 5180,
            signalLevel = 4,
            upSpeedText = "124 KB/s",
            downSpeedText = "2.4 MB/s",
            todayUsageText = "480 MB",
            monthUsageText = "14.2 GB"
        )
    }

    WidgetPageScaffold(
        title = "Network Widget",
        subtitle = "Customize Wi-Fi telemetry & traffic counters",
        onBack = onBack
    ) {
        WidgetVisibilityCard(
            title = "Show Network Widget",
            description = "Display Wi-Fi / cellular status pill on home screen",
            visible = showNetwork,
            onVisibleChange = {
                showNetwork = it
                userManager.saveShowNetworkWidget(it)
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
                    .padding(14.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                NetworkWidget(
                    style = networkStyle,
                    font = networkFont,
                    showUsage = showNetworkUsageOnWidget,
                    previewInfo = previewNetwork
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            WidgetStyleSelector(
                title = "Network Style",
                styles = NetworkStyle.entries,
                selectedStyle = networkStyle,
                onStyleSelected = {
                    networkStyle = it
                    userManager.saveNetworkStyle(it)
                },
                getLabel = { it.name }
            )

            WidgetFontSelector(
                selectedFont = networkFont,
                onFontSelected = {
                    networkFont = it
                    userManager.saveNetworkFont(it)
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White.copy(alpha = 0.04f))
                    .clickable {
                        val newValue = !showNetworkUsageOnWidget
                        showNetworkUsageOnWidget = newValue
                        userManager.saveShowNetworkUsageOnWidget(newValue)
                    }
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Show Usage at Corner",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Minimal badge displaying today's tracked data",
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 11.sp
                    )
                }
                Switch(
                    checked = showNetworkUsageOnWidget,
                    onCheckedChange = {
                        showNetworkUsageOnWidget = it
                        userManager.saveShowNetworkUsageOnWidget(it)
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = WidgetSettingsAccent
                    )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(WidgetSettingsAccent.copy(alpha = 0.15f))
                    .border(1.dp, WidgetSettingsAccent.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                    .clickable { isDetailedUsageOpen = true }
                    .padding(horizontal = 14.dp, vertical = 11.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.DataUsage,
                        contentDescription = null,
                        tint = WidgetSettingsAccent,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "View Detailed Usage Tracking",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Icon(
                    imageVector = Icons.Rounded.ChevronRight,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.5f)
                )
            }
        }
    }

    if (isDetailedUsageOpen) {
        NetworkUsageScreen(onClose = { isDetailedUsageOpen = false })
    }
}
