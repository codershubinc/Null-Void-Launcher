package com.codershubinc.nullvoidlauncher.ui.homescreen

import android.content.SharedPreferences
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
import com.codershubinc.nullvoidlauncher.data.model.LauncherThemeConfig
import com.codershubinc.nullvoidlauncher.data.UserManager
import com.codershubinc.nullvoidlauncher.data.prefs.rememberPrefVersion
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
    onOpenWidgetSettings: () -> Unit = {},
    onOpenWidgetTweaks: ((com.codershubinc.nullvoidlauncher.ui.settings.WidgetSubPage) -> Unit)? = null
) {
    val configuration = LocalConfiguration.current
    val isTablet = configuration.screenWidthDp > 600
    val context = LocalContext.current
    val userManager = remember { UserManager(context) }

    val prefVersion = rememberPrefVersion(userManager)

    // Read values keyed on prefVersion so home screen instantly refreshes when tweaked
    val showClock = remember(prefVersion) { userManager.widgets.showClock }
    val showFavorites = remember(prefVersion) { userManager.widgets.showFavorites }
    val showMusic = remember(prefVersion) { userManager.widgets.showMusic }
    val showNetwork = remember(prefVersion) { userManager.widgets.showNetwork }
    val showBluetooth = remember(prefVersion) { userManager.widgets.showBluetooth }
    val showControlDeck = remember(prefVersion) { userManager.controlDeck.showControlDeck }

    val musicStyle = remember(prefVersion) { userManager.widgets.musicStyle }
    val favoritesStyle = remember(prefVersion) { userManager.widgets.favoritesStyle }
    val clockStyle = remember(prefVersion) { userManager.widgets.clockStyle }
    val networkStyle = remember(prefVersion) { userManager.widgets.networkStyle }
    val bluetoothStyle = remember(prefVersion) { userManager.widgets.bluetoothStyle }

    val clockFont = remember(prefVersion) { userManager.widgets.clockFont }
    val networkFont = remember(prefVersion) { userManager.widgets.networkFont }
    val bluetoothFont = remember(prefVersion) { userManager.widgets.bluetoothFont }
    val favoritesFont = remember(prefVersion) { userManager.widgets.favoritesFont }
    val musicFont = remember(prefVersion) { userManager.widgets.musicFont }

    val hideStatusBar = remember(prefVersion) { userManager.theme.hideStatusBar }

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
            .navigationBarsPadding()
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
                            onLongClick = {
                                if (onOpenWidgetTweaks != null) onOpenWidgetTweaks(com.codershubinc.nullvoidlauncher.ui.settings.WidgetSubPage.BLUETOOTH)
                                else onOpenBluetoothSettings()
                            }
                        )
                    }
                    if (showNetwork) {
                        NetworkWidget(
                            style = networkStyle,
                            font = networkFont,
                            onTap = { NetworkHelper.openWifiSettings(context) },
                            onLongClick = {
                                if (onOpenWidgetTweaks != null) onOpenWidgetTweaks(com.codershubinc.nullvoidlauncher.ui.settings.WidgetSubPage.NETWORK)
                                else onOpenNetworkUsage()
                            }
                        )
                    }
                    if (showControlDeck) {
                        val controlDeckStyle = remember(prefVersion) { userManager.controlDeck.style }
                        val controlDeckIconStyle = remember(prefVersion) { userManager.controlDeck.iconStyle }
                        val controlDeckActions = remember(prefVersion) { userManager.controlDeck.actions }
                        val controlDeckCompact = remember(prefVersion) { userManager.controlDeck.compact }

                        com.codershubinc.nullvoidlauncher.ui.widgets.QuickControlDeckWidget(
                            overrideStyle = controlDeckStyle,
                            overrideIconStyle = controlDeckIconStyle,
                            overrideActions = controlDeckActions,
                            overrideCompact = controlDeckCompact,
                            onLongClick = {
                                onOpenWidgetTweaks?.invoke(com.codershubinc.nullvoidlauncher.ui.settings.WidgetSubPage.CONTROL_DECK)
                            }
                        )
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
                    onLongClick = {
                        if (onOpenWidgetTweaks != null) onOpenWidgetTweaks(com.codershubinc.nullvoidlauncher.ui.settings.WidgetSubPage.CLOCK)
                        else onOpenWidgetSettings()
                    },
                    onOpenWidgetTweaks = onOpenWidgetTweaks
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
                    modifier = Modifier.wrapContentSize(Alignment.BottomEnd),
                    onLongClick = {
                        if (onOpenWidgetTweaks != null) onOpenWidgetTweaks(com.codershubinc.nullvoidlauncher.ui.settings.WidgetSubPage.FAVORITES)
                        else onOpenWidgetSettings()
                    }
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
                    modifier = Modifier.padding(bottom = 36.dp, start = 24.dp, end = 24.dp),
                    onLongClick = {
                        if (onOpenWidgetTweaks != null) onOpenWidgetTweaks(com.codershubinc.nullvoidlauncher.ui.settings.WidgetSubPage.MUSIC)
                        else onOpenWidgetSettings()
                    }
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
