package com.codershubinc.nullvoidlauncher.ui.homescreen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.codershubinc.nullvoidlauncher.data.LauncherThemeConfig
import com.codershubinc.nullvoidlauncher.data.UserManager
import com.codershubinc.nullvoidlauncher.ui.bluetooth.BluetoothHelper
import com.codershubinc.nullvoidlauncher.ui.network.NetworkHelper
import com.codershubinc.nullvoidlauncher.ui.widgets.BluetoothWidget
import com.codershubinc.nullvoidlauncher.ui.widgets.ClockWidget
import com.codershubinc.nullvoidlauncher.ui.widgets.FavoritesWidget
import com.codershubinc.nullvoidlauncher.ui.widgets.MusicWidget
import com.codershubinc.nullvoidlauncher.ui.widgets.NetworkWidget

@Composable
fun ElegantTheme(
    config: LauncherThemeConfig,
    onOpenDrawer: () -> Unit,
    onOpenNetworkUsage: () -> Unit = {},
    onOpenBluetoothSettings: () -> Unit = {},
    onOpenWidgetSettings: () -> Unit = {}
) {
    val configuration = LocalConfiguration.current
    val isTablet = configuration.screenWidthDp > 600
    val context = LocalContext.current
    val userManager = remember { UserManager(context) }

    val showClock = userManager.getShowClockWidget()
    val showFavorites = userManager.getShowFavoritesWidget()
    val showMusic = userManager.getShowMusicWidget()
    val showNetwork = userManager.getShowNetworkWidget()
    val showPower = userManager.getShowPowerWidget()
    val showBluetooth = userManager.getShowBluetoothWidget()
    val showControlDeck = userManager.getShowControlDeck()
    val musicStyle = userManager.getMusicStyle()
    val favoritesStyle = userManager.getFavoritesStyle()
    val clockStyle = userManager.getClockStyle()
    val networkStyle = userManager.getNetworkStyle()
    val powerStyle = userManager.getPowerStyle()
    val bluetoothStyle = userManager.getBluetoothStyle()
    val clockFont = userManager.getClockFont()
    val networkFont = userManager.getNetworkFont()
    val powerFont = userManager.getPowerFont()
    val bluetoothFont = userManager.getBluetoothFont()
    val favoritesFont = userManager.getFavoritesFont()
    val musicFont = userManager.getMusicFont()

    val hideStatusBar = userManager.getHideStatusBar()

    // Theme Content Box
    Box(
        modifier = Modifier
            .fillMaxSize()
            .then(
                if (hideStatusBar) {
                    Modifier.padding(top = 16.dp)
                } else {
                    Modifier.statusBarsPadding()
                }
            )
            .padding(horizontal = if (isTablet) 12.dp else 0.dp)
    ) {
        // TELEMETRY STACK (Bluetooth + Network + Control Deck)
        if (showNetwork || showBluetooth) {
            Box(
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                Column(
                    modifier = Modifier
                        .padding(top = 24.dp, end = 24.dp),
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (showBluetooth) {
                        BluetoothWidget(
                            style = bluetoothStyle,
                            font = bluetoothFont,
                            onTap = { BluetoothHelper.openBluetoothSettings(context) },
                            onLongClick = onOpenBluetoothSettings
                        )
                    }
                    if (showNetwork) {
                        NetworkWidget(
                            style = networkStyle,
                            font = networkFont,
                            onTap = { NetworkHelper.openWifiSettings(context) },
                            onLongClick = onOpenNetworkUsage
                        )
                    }
                    if (showControlDeck) {
                        com.codershubinc.nullvoidlauncher.ui.widgets.QuickControlDeckWidget()
                    }
                }
            }
        }

        // CLOCK WIDGET
        if (showClock) {
            Box(
                modifier = Modifier
            ) {
                ClockWidget(
                    style = clockStyle,
                    font = clockFont,
                    onLongClick = onOpenWidgetSettings
                )
            }
        }

        // FAVORITES WIDGET
        if (showFavorites) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 24.dp, bottom = if (isTablet) 180.dp else 130.dp)
            ) {
                FavoritesWidget(
                    style = favoritesStyle,
                    font = favoritesFont,
                    modifier = Modifier.wrapContentSize(Alignment.BottomEnd)
                )
            }
        }

        // MUSIC WIDGET
        if (showMusic) {
            Box(
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                MusicWidget(
                    style = musicStyle,
                    font = musicFont,
                    modifier = Modifier.padding(bottom = 36.dp, start = 24.dp, end = 24.dp)
                )
            }
        }

        // Gestural Affordance Indicator (Swipe Up to Open Drawer)
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 12.dp)
                .clip(RoundedCornerShape(8.dp))
                .clickable { onOpenDrawer() }
                .padding(horizontal = 16.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .width(36.dp)
                    .height(3.5.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color.White.copy(alpha = 0.25f))
            )
        }
    }
}
