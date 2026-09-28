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
    onOpenWidgetSettings: () -> Unit = {},
    onOpenWidgetTweaks: ((com.codershubinc.nullvoidlauncher.ui.settings.WidgetSubPage) -> Unit)? = null
) {
    val configuration = LocalConfiguration.current
    val isTablet = configuration.screenWidthDp > 600
    val context = LocalContext.current
    val userManager = remember { UserManager(context) }

    // Reactive preference version trigger for instant recomposition on tweaks change
    var prefVersion by remember { mutableIntStateOf(0) }
    DisposableEffect(userManager) {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, _ ->
            prefVersion++
        }
        userManager.prefs.registerOnSharedPreferenceChangeListener(listener)
        onDispose {
            userManager.prefs.unregisterOnSharedPreferenceChangeListener(listener)
        }
    }

    // Read values keyed on prefVersion so home screen instantly refreshes when tweaked
    val showClock = remember(prefVersion) { userManager.getShowClockWidget() }
    val showFavorites = remember(prefVersion) { userManager.getShowFavoritesWidget() }
    val showMusic = remember(prefVersion) { userManager.getShowMusicWidget() }
    val showNetwork = remember(prefVersion) { userManager.getShowNetworkWidget() }
    val showBluetooth = remember(prefVersion) { userManager.getShowBluetoothWidget() }
    val showControlDeck = remember(prefVersion) { userManager.getShowControlDeck() }

    val musicStyle = remember(prefVersion) { userManager.getMusicStyle() }
    val favoritesStyle = remember(prefVersion) { userManager.getFavoritesStyle() }
    val clockStyle = remember(prefVersion) { userManager.getClockStyle() }
    val networkStyle = remember(prefVersion) { userManager.getNetworkStyle() }
    val bluetoothStyle = remember(prefVersion) { userManager.getBluetoothStyle() }

    val clockFont = remember(prefVersion) { userManager.getClockFont() }
    val networkFont = remember(prefVersion) { userManager.getNetworkFont() }
    val bluetoothFont = remember(prefVersion) { userManager.getBluetoothFont() }
    val favoritesFont = remember(prefVersion) { userManager.getFavoritesFont() }
    val musicFont = remember(prefVersion) { userManager.getMusicFont() }

    val hideStatusBar = remember(prefVersion) { userManager.getHideStatusBar() }

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
                        val controlDeckStyle = remember(prefVersion) { userManager.getControlDeckStyle() }
                        val controlDeckIconStyle = remember(prefVersion) { userManager.getControlDeckIconStyle() }
                        val controlDeckActions = remember(prefVersion) { userManager.getControlDeckActions() }
                        val controlDeckCompact = remember(prefVersion) { userManager.getControlDeckCompact() }

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
