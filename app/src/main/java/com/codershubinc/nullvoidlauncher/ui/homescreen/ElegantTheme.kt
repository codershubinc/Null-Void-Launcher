package com.codershubinc.nullvoidlauncher.ui.homescreen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codershubinc.nullvoidlauncher.data.LauncherThemeConfig
import com.codershubinc.nullvoidlauncher.data.UserManager
import com.codershubinc.nullvoidlauncher.ui.bluetooth.BluetoothHelper
import com.codershubinc.nullvoidlauncher.ui.network.NetworkHelper
import com.codershubinc.nullvoidlauncher.ui.power.PowerHelper
import com.codershubinc.nullvoidlauncher.ui.widgets.BluetoothWidget
import com.codershubinc.nullvoidlauncher.ui.widgets.ClockWidget
import com.codershubinc.nullvoidlauncher.ui.widgets.FavoritesWidget
import com.codershubinc.nullvoidlauncher.ui.widgets.MusicWidget
import com.codershubinc.nullvoidlauncher.ui.widgets.NetworkWidget
import com.codershubinc.nullvoidlauncher.ui.widgets.PowerWidget
import kotlin.math.roundToInt

@Composable
fun ElegantTheme(
    config: LauncherThemeConfig,
    onOpenDrawer: () -> Unit,
    onOpenNetworkUsage: () -> Unit = {},
    onOpenBluetoothSettings: () -> Unit = {},
    onOpenWidgetSettings: () -> Unit = {},
    isEditMode: Boolean = false,
    onExitEditMode: () -> Unit = {}
) {
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
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

    // 16dp snap grid size in pixels
    val gridSizePx = with(density) { 16.dp.toPx() }

    // Screen dimension limits for clamping (in px)
    val maxHorizontalPx = with(density) { (configuration.screenWidthDp * 0.75f).dp.toPx() }
    val maxVerticalPx = with(density) { (configuration.screenHeightDp * 0.75f).dp.toPx() }

    // State for widget offsets (in pixels)
    var clockOffsetX by remember { mutableFloatStateOf(with(density) { userManager.getWidgetOffsetX("clock").dp.toPx() }) }
    var clockOffsetY by remember { mutableFloatStateOf(with(density) { userManager.getWidgetOffsetY("clock").dp.toPx() }) }

    var telemetryOffsetX by remember { mutableFloatStateOf(with(density) { userManager.getWidgetOffsetX("telemetry").dp.toPx() }) }
    var telemetryOffsetY by remember { mutableFloatStateOf(with(density) { userManager.getWidgetOffsetY("telemetry").dp.toPx() }) }

    var favoritesOffsetX by remember { mutableFloatStateOf(with(density) { userManager.getWidgetOffsetX("favorites").dp.toPx() }) }
    var favoritesOffsetY by remember { mutableFloatStateOf(with(density) { userManager.getWidgetOffsetY("favorites").dp.toPx() }) }

    var musicOffsetX by remember { mutableFloatStateOf(with(density) { userManager.getWidgetOffsetX("music").dp.toPx() }) }
    var musicOffsetY by remember { mutableFloatStateOf(with(density) { userManager.getWidgetOffsetY("music").dp.toPx() }) }

    fun resetAllOffsets() {
        userManager.resetWidgetOffsets()
        clockOffsetX = 0f
        clockOffsetY = 0f
        telemetryOffsetX = 0f
        telemetryOffsetY = 0f
        favoritesOffsetX = 0f
        favoritesOffsetY = 0f
        musicOffsetX = 0f
        musicOffsetY = 0f
    }

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
        // Edit Mode Header Pill
        AnimatedVisibility(
            visible = isEditMode,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 12.dp)
        ) {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color(0xFF161616).copy(alpha = 0.95f))
                    .border(1.dp, Color(0xFF00FFCC).copy(alpha = 0.6f), RoundedCornerShape(24.dp))
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "EDIT MODE",
                    color = Color(0xFF00FFCC),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp
                )

                Box(
                    modifier = Modifier
                        .height(14.dp)
                        .width(1.dp)
                        .background(Color.White.copy(alpha = 0.2f))
                )

                // Reset Button
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { resetAllOffsets() }
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Reset Positions",
                        tint = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "Reset",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 11.sp
                    )
                }

                // Done Button
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF00FFCC))
                        .clickable { onExitEditMode() }
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Done",
                        tint = Color.Black,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "Done",
                        color = Color.Black,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // TELEMETRY STACK (Bluetooth + Network + Control Deck)
        if (showNetwork || showBluetooth) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset { IntOffset(telemetryOffsetX.roundToInt(), telemetryOffsetY.roundToInt()) }
                    .then(
                        if (isEditMode) {
                            Modifier
                                .border(1.dp, Color(0xFF00FFCC).copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                                .background(Color.White.copy(alpha = 0.03f), RoundedCornerShape(12.dp))
                                .pointerInput(Unit) {
                                    detectDragGestures(
                                        onDragEnd = {
                                            with(density) {
                                                userManager.saveWidgetOffset("telemetry", telemetryOffsetX.toDp().value, telemetryOffsetY.toDp().value)
                                            }
                                        }
                                    ) { change, dragAmount ->
                                        change.consume()
                                        val newX = (telemetryOffsetX + dragAmount.x).coerceIn(-maxHorizontalPx, maxHorizontalPx)
                                        val newY = (telemetryOffsetY + dragAmount.y).coerceIn(-maxVerticalPx, maxVerticalPx)
                                        telemetryOffsetX = (newX / gridSizePx).roundToInt() * gridSizePx
                                        telemetryOffsetY = (newY / gridSizePx).roundToInt() * gridSizePx
                                    }
                                }
                        } else Modifier
                    )
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
                    .offset { IntOffset(clockOffsetX.roundToInt(), clockOffsetY.roundToInt()) }
                    .then(
                        if (isEditMode) {
                            Modifier
                                .border(1.dp, Color(0xFF00FFCC).copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                                .background(Color.White.copy(alpha = 0.03f), RoundedCornerShape(12.dp))
                                .pointerInput(Unit) {
                                    detectDragGestures(
                                        onDragEnd = {
                                            with(density) {
                                                userManager.saveWidgetOffset("clock", clockOffsetX.toDp().value, clockOffsetY.toDp().value)
                                            }
                                        }
                                    ) { change, dragAmount ->
                                        change.consume()
                                        val newX = (clockOffsetX + dragAmount.x).coerceIn(-maxHorizontalPx, maxHorizontalPx)
                                        val newY = (clockOffsetY + dragAmount.y).coerceIn(-maxVerticalPx, maxVerticalPx)
                                        clockOffsetX = (newX / gridSizePx).roundToInt() * gridSizePx
                                        clockOffsetY = (newY / gridSizePx).roundToInt() * gridSizePx
                                    }
                                }
                        } else Modifier
                    )
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
                    .offset { IntOffset(favoritesOffsetX.roundToInt(), favoritesOffsetY.roundToInt()) }
                    .then(
                        if (isEditMode) {
                            Modifier
                                .border(1.dp, Color(0xFF00FFCC).copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                                .background(Color.White.copy(alpha = 0.03f), RoundedCornerShape(12.dp))
                                .pointerInput(Unit) {
                                    detectDragGestures(
                                        onDragEnd = {
                                            with(density) {
                                                userManager.saveWidgetOffset("favorites", favoritesOffsetX.toDp().value, favoritesOffsetY.toDp().value)
                                            }
                                        }
                                    ) { change, dragAmount ->
                                        change.consume()
                                        val newX = (favoritesOffsetX + dragAmount.x).coerceIn(-maxHorizontalPx, maxHorizontalPx)
                                        val newY = (favoritesOffsetY + dragAmount.y).coerceIn(-maxVerticalPx, maxVerticalPx)
                                        favoritesOffsetX = (newX / gridSizePx).roundToInt() * gridSizePx
                                        favoritesOffsetY = (newY / gridSizePx).roundToInt() * gridSizePx
                                    }
                                }
                        } else Modifier
                    )
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
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset { IntOffset(musicOffsetX.roundToInt(), musicOffsetY.roundToInt()) }
                    .then(
                        if (isEditMode) {
                            Modifier
                                .border(1.dp, Color(0xFF00FFCC).copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                                .background(Color.White.copy(alpha = 0.03f), RoundedCornerShape(12.dp))
                                .pointerInput(Unit) {
                                    detectDragGestures(
                                        onDragEnd = {
                                            with(density) {
                                                userManager.saveWidgetOffset("music", musicOffsetX.toDp().value, musicOffsetY.toDp().value)
                                            }
                                        }
                                    ) { change, dragAmount ->
                                        change.consume()
                                        val newX = (musicOffsetX + dragAmount.x).coerceIn(-maxHorizontalPx, maxHorizontalPx)
                                        val newY = (musicOffsetY + dragAmount.y).coerceIn(-maxVerticalPx, maxVerticalPx)
                                        musicOffsetX = (newX / gridSizePx).roundToInt() * gridSizePx
                                        musicOffsetY = (newY / gridSizePx).roundToInt() * gridSizePx
                                    }
                                }
                        } else Modifier
                    )
            ) {
                MusicWidget(
                    style = musicStyle,
                    font = musicFont,
                    modifier = Modifier.padding(bottom = 36.dp, start = 24.dp, end = 24.dp)
                )
            }
        }

        // Gestural Affordance Indicator (Swipe Up to Open Drawer)
        if (!isEditMode) {
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
}
