package com.codershubinc.nullvoidlauncher.ui.settings

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codershubinc.nullvoidlauncher.data.BluetoothStyle
import com.codershubinc.nullvoidlauncher.data.ClockStyle
import com.codershubinc.nullvoidlauncher.data.DayStyle
import com.codershubinc.nullvoidlauncher.data.FavoritesStyle
import com.codershubinc.nullvoidlauncher.data.MusicStyle
import com.codershubinc.nullvoidlauncher.data.NetworkStyle
import com.codershubinc.nullvoidlauncher.data.PowerStyle
import com.codershubinc.nullvoidlauncher.data.StorageStyle
import com.codershubinc.nullvoidlauncher.data.UserManager
import com.codershubinc.nullvoidlauncher.data.WidgetFont
import com.codershubinc.nullvoidlauncher.data.repository.AppInfo
import com.codershubinc.nullvoidlauncher.data.repository.getInstalledApps
import com.codershubinc.nullvoidlauncher.ui.bluetooth.BluetoothDeviceInfo
import com.codershubinc.nullvoidlauncher.ui.bluetooth.BluetoothDeviceType
import com.codershubinc.nullvoidlauncher.ui.bluetooth.BluetoothHelper
import com.codershubinc.nullvoidlauncher.ui.bluetooth.BluetoothInfoState
import com.codershubinc.nullvoidlauncher.ui.bluetooth.BluetoothSettingsScreen
import com.codershubinc.nullvoidlauncher.ui.components.ModernCard
import com.codershubinc.nullvoidlauncher.ui.music.MusicTrack
import com.codershubinc.nullvoidlauncher.ui.network.NetworkInfoState
import com.codershubinc.nullvoidlauncher.ui.network.NetworkUsageScreen
import com.codershubinc.nullvoidlauncher.ui.power.PowerInfoState
import com.codershubinc.nullvoidlauncher.ui.widgets.BluetoothWidget
import com.codershubinc.nullvoidlauncher.ui.widgets.ClockWidget
import com.codershubinc.nullvoidlauncher.ui.widgets.DayWidget
import com.codershubinc.nullvoidlauncher.ui.widgets.FavoritesWidget
import com.codershubinc.nullvoidlauncher.ui.widgets.MusicWidget
import com.codershubinc.nullvoidlauncher.ui.widgets.NetworkWidget
import com.codershubinc.nullvoidlauncher.ui.widgets.PowerWidget
import com.codershubinc.nullvoidlauncher.ui.widgets.StorageWidget
import com.codershubinc.nullvoidlauncher.utils.StorageInfoState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun WidgetSettingsScreen(userManager: UserManager, onClose: () -> Unit) {
    val context = LocalContext.current
    var radius by remember { mutableFloatStateOf(userManager.getWidgetCornerRadius()) }
    var blurIntensity by remember { mutableFloatStateOf(userManager.getWidgetBlurIntensity()) }
    var glassEffect by remember { mutableStateOf(userManager.getWidgetGlassEffect()) }
    var widgetColor by remember { mutableIntStateOf(userManager.getWidgetColor()) }
    var preset by remember { mutableStateOf(userManager.getWidgetPreset()) }

    var showClock by remember { mutableStateOf(userManager.getShowClockWidget()) }
    var showMusic by remember { mutableStateOf(userManager.getShowMusicWidget()) }
    var showFavorites by remember { mutableStateOf(userManager.getShowFavoritesWidget()) }
    var showStorage by remember { mutableStateOf(userManager.getShowStorageWidget()) }
    var showNetwork by remember { mutableStateOf(userManager.getShowNetworkWidget()) }
    var showPower by remember { mutableStateOf(userManager.getShowPowerWidget()) }
    var showBluetooth by remember { mutableStateOf(userManager.getShowBluetoothWidget()) }
    var showControlDeck by remember { mutableStateOf(userManager.getShowControlDeck()) }

    var clockStyle by remember { mutableStateOf(userManager.getClockStyle()) }
    var musicStyle by remember { mutableStateOf(userManager.getMusicStyle()) }
    var favoritesStyle by remember { mutableStateOf(userManager.getFavoritesStyle()) }
    var dayStyle by remember { mutableStateOf(userManager.getDayStyle()) }
    var networkStyle by remember { mutableStateOf(userManager.getNetworkStyle()) }
    var powerStyle by remember { mutableStateOf(userManager.getPowerStyle()) }
    var storageStyle by remember { mutableStateOf(userManager.getStorageStyle()) }
    var bluetoothStyle by remember { mutableStateOf(userManager.getBluetoothStyle()) }

    var clockFont by remember { mutableStateOf(userManager.getClockFont()) }
    var dayFont by remember { mutableStateOf(userManager.getDayFont()) }
    var musicFont by remember { mutableStateOf(userManager.getMusicFont()) }
    var favoritesFont by remember { mutableStateOf(userManager.getFavoritesFont()) }
    var storageFont by remember { mutableStateOf(userManager.getStorageFont()) }
    var networkFont by remember { mutableStateOf(userManager.getNetworkFont()) }
    var powerFont by remember { mutableStateOf(userManager.getPowerFont()) }
    var bluetoothFont by remember { mutableStateOf(userManager.getBluetoothFont()) }
    var showNetworkUsageOnWidget by remember { mutableStateOf(userManager.getShowNetworkUsageOnWidget()) }
    var isDetailedUsageOpen by remember { mutableStateOf(false) }
    var isBluetoothConfigOpen by remember { mutableStateOf(false) }
    var hasBtPermission by remember { mutableStateOf(BluetoothHelper.hasBluetoothPermission(context)) }
    val btPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasBtPermission = granted
    }

    var previewApps by remember { mutableStateOf<List<AppInfo>>(emptyList()) }
    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            previewApps = getInstalledApps(context).take(4)
        }
    }
    val previewTrack = remember {
        MusicTrack(
            title = "Midnight City",
            artist = "M83 • Synthwave",
            isPlaying = true
        )
    }
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
    val previewPower = remember {
        PowerInfoState(
            level = 82,
            isCharging = true,
            status = "Charging",
            temperatureC = 31.4f,
            voltageV = 4.15f,
            health = "Good",
            isPowerSaveMode = false
        )
    }
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
    val previewBluetooth = remember {
        BluetoothInfoState(
            isBluetoothEnabled = true,
            hasPermission = true,
            connectedDevices = listOf(
                BluetoothDeviceInfo(
                    name = "AirPods Pro",
                    address = "00:11:22:33:44:55",
                    isConnected = true,
                    batteryLevel = 85,
                    deviceType = BluetoothDeviceType.HEADPHONES
                )
            ),
            activeDevice = BluetoothDeviceInfo(
                name = "AirPods Pro",
                address = "00:11:22:33:44:55",
                isConnected = true,
                batteryLevel = 85,
                deviceType = BluetoothDeviceType.HEADPHONES
            )
        )
    }

    val scrollState = rememberScrollState()

    val accent = Color(0xFF3D5AFE)

    fun saveAll() {
        userManager.saveWidgetCornerRadius(radius)
        userManager.saveWidgetBlurIntensity(blurIntensity)
        userManager.saveWidgetGlassEffect(glassEffect)
        userManager.saveWidgetColor(widgetColor)
        userManager.saveWidgetPreset(preset)
        userManager.saveShowClockWidget(showClock)
        userManager.saveShowMusicWidget(showMusic)
        userManager.saveShowFavoritesWidget(showFavorites)
        userManager.saveShowStorageWidget(showStorage)
        userManager.saveShowNetworkWidget(showNetwork)
        userManager.saveShowPowerWidget(showPower)
        userManager.saveShowBluetoothWidget(showBluetooth)
        userManager.saveShowControlDeck(showControlDeck)
        userManager.saveClockStyle(clockStyle)
        userManager.saveMusicStyle(musicStyle)
        userManager.saveFavoritesStyle(favoritesStyle)
        userManager.saveDayStyle(dayStyle)
        userManager.saveNetworkStyle(networkStyle)
        userManager.savePowerStyle(powerStyle)
        userManager.saveStorageStyle(storageStyle)
        userManager.saveBluetoothStyle(bluetoothStyle)
        userManager.saveClockFont(clockFont)
        userManager.saveDayFont(dayFont)
        userManager.saveMusicFont(musicFont)
        userManager.saveFavoritesFont(favoritesFont)
        userManager.saveStorageFont(storageFont)
        userManager.saveNetworkFont(networkFont)
        userManager.savePowerFont(powerFont)
        userManager.saveBluetoothFont(bluetoothFont)
        userManager.saveShowNetworkUsageOnWidget(showNetworkUsageOnWidget)
        onClose()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF080808))
            .statusBarsPadding()
            .padding(24.dp)
            .verticalScroll(scrollState)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.05f))
                .clickable { saveAll() },
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back", tint = Color.White)
        }

        Spacer(modifier = Modifier.height(32.dp))
        Text("Widget Tweaks", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Bold)
        Text("Style, visibility & layout", color = Color.White.copy(alpha = 0.5f), fontSize = 16.sp)
        Spacer(modifier = Modifier.height(32.dp))

        // ── Visibility ──────────────────────────────────────────────────────
        ModernCard {
            Text("Visibility", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Clock Widget", color = Color.White)
                Switch(
                    checked = showClock,
                    onCheckedChange = { showClock = it },
                    colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = accent)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Music Widget", color = Color.White)
                Switch(
                    checked = showMusic,
                    onCheckedChange = { showMusic = it },
                    colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = accent)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Favorites Widget", color = Color.White)
                Switch(
                    checked = showFavorites,
                    onCheckedChange = { showFavorites = it },
                    colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = accent)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Storage Widget", color = Color.White)
                Switch(
                    checked = showStorage,
                    onCheckedChange = { showStorage = it },
                    colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = accent)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Network / Wi-Fi Widget", color = Color.White)
                Switch(
                    checked = showNetwork,
                    onCheckedChange = { showNetwork = it },
                    colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = accent)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Power / Battery Widget", color = Color.White)
                Switch(
                    checked = showPower,
                    onCheckedChange = { showPower = it },
                    colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = accent)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Bluetooth Widget", color = Color.White)
                Switch(
                    checked = showBluetooth,
                    onCheckedChange = { showBluetooth = it },
                    colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = accent)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Quick Control Deck (Flashlight, Sound)", color = Color.White)
                Switch(
                    checked = showControlDeck,
                    onCheckedChange = { showControlDeck = it },
                    colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = accent)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ── Clock / Time Widget Style & Font ─────────────────────────────────
        ModernCard {
            Text("Clock / Time Widget", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text("Customize style & typography", color = Color.White.copy(alpha = 0.5f), fontSize = 12.sp)
            Spacer(modifier = Modifier.height(14.dp))

            // Live Preview Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color.White.copy(alpha = 0.03f))
                    .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(14.dp))
                    .padding(14.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                ClockWidget(
                    style = clockStyle,
                    font = clockFont,
                    previewTimeText = "20:45",
                    previewDayText = "FRIDAY",
                    previewMonthName = "september",
                    previewDayOfMonth = "20",
                    previewBatteryLevel = 85,
                    previewBatteryStatus = "DISCHARGING"
                )
            }

            Spacer(modifier = Modifier.height(14.dp))
            Text("Style", color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(bottom = 6.dp))
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ClockStyle.entries.forEach { s ->
                    val selected = clockStyle == s
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (selected) accent else Color.White.copy(alpha = 0.1f))
                            .clickable { clockStyle = s }
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                    ) {
                        Text(s.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }

            FontSelectorRow(selectedFont = clockFont, accent = accent, onFontSelected = { clockFont = it })
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ── Network / Wi-Fi Widget Style & Font ──────────────────────────────
        ModernCard {
            Text("Network / Wi-Fi Widget", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text("Customize style & typography", color = Color.White.copy(alpha = 0.5f), fontSize = 12.sp)
            Spacer(modifier = Modifier.height(14.dp))

            // Live Preview Box
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

            Spacer(modifier = Modifier.height(14.dp))
            Text("Style", color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(bottom = 6.dp))
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                NetworkStyle.entries.forEach { s ->
                    val selected = networkStyle == s
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (selected) accent else Color.White.copy(alpha = 0.1f))
                            .clickable { networkStyle = s }
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                    ) {
                        Text(s.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }

            FontSelectorRow(selectedFont = networkFont, accent = accent, onFontSelected = { networkFont = it })

            Spacer(modifier = Modifier.height(14.dp))

            // Show usage on widget toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White.copy(alpha = 0.04f))
                    .clickable { showNetworkUsageOnWidget = !showNetworkUsageOnWidget }
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
                    onCheckedChange = { showNetworkUsageOnWidget = it },
                    colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = accent)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Button to open dedicated usage tracking page
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(accent.copy(alpha = 0.15f))
                    .border(1.dp, accent.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
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
                        tint = accent,
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

        Spacer(modifier = Modifier.height(24.dp))

        // ── Power / Battery Widget Style & Font ──────────────────────────────
        ModernCard {
            Text("Power / Battery Widget", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text("Integrated clock/day/date telemetry style & typography", color = Color.White.copy(alpha = 0.5f), fontSize = 12.sp)
            Spacer(modifier = Modifier.height(14.dp))

            // Live Preview Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color.White.copy(alpha = 0.03f))
                    .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(14.dp))
                    .padding(14.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                PowerWidget(
                    style = powerStyle,
                    font = powerFont,
                    previewInfo = previewPower
                )
            }

            Spacer(modifier = Modifier.height(14.dp))
            Text("Style", color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(bottom = 6.dp))
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PowerStyle.entries.forEach { s ->
                    val selected = powerStyle == s
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (selected) accent else Color.White.copy(alpha = 0.1f))
                            .clickable { powerStyle = s }
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                    ) {
                        Text(s.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }

            FontSelectorRow(selectedFont = powerFont, accent = accent, onFontSelected = { powerFont = it })
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ── Bluetooth Widget Style & Font ────────────────────────────────────
        ModernCard {
            Text("Bluetooth Widget", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text("Customize connected device telemetry & style", color = Color.White.copy(alpha = 0.5f), fontSize = 12.sp)
            Spacer(modifier = Modifier.height(14.dp))

            // Live Preview Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color.White.copy(alpha = 0.03f))
                    .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(14.dp))
                    .padding(14.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                BluetoothWidget(
                    style = bluetoothStyle,
                    font = bluetoothFont,
                    showOnlyIfConnected = false,
                    previewInfo = previewBluetooth
                )
            }

            Spacer(modifier = Modifier.height(14.dp))
            Text("Style", color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(bottom = 6.dp))
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                BluetoothStyle.entries.forEach { s ->
                    val selected = bluetoothStyle == s
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (selected) accent else Color.White.copy(alpha = 0.1f))
                            .clickable { bluetoothStyle = s }
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                    ) {
                        Text(s.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }

            FontSelectorRow(selectedFont = bluetoothFont, accent = accent, onFontSelected = { bluetoothFont = it })

            Spacer(modifier = Modifier.height(14.dp))

            // Button to open dedicated Bluetooth device selection page
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF00E5FF).copy(alpha = 0.12f))
                    .border(1.dp, Color(0xFF00E5FF).copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                    .clickable { isBluetoothConfigOpen = true }
                    .padding(horizontal = 14.dp, vertical = 11.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Bluetooth,
                        contentDescription = null,
                        tint = Color(0xFF00E5FF),
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Configure Target Device & Auto-Hide",
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

            if (!hasBtPermission && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFFFB300).copy(alpha = 0.12f))
                        .border(1.dp, Color(0xFFFFB300).copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                        .clickable { btPermissionLauncher.launch(Manifest.permission.BLUETOOTH_CONNECT) }
                        .padding(horizontal = 14.dp, vertical = 11.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Lock,
                            contentDescription = null,
                            tint = Color(0xFFFFB300),
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "Grant Bluetooth Permission",
                            color = Color(0xFFFFB300),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Icon(
                        imageVector = Icons.Rounded.ChevronRight,
                        contentDescription = null,
                        tint = Color(0xFFFFB300)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ── Music Widget Style & Font ────────────────────────────────────────
        ModernCard {
            Text("Music Widget", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text("Customize style & typography", color = Color.White.copy(alpha = 0.5f), fontSize = 12.sp)
            Spacer(modifier = Modifier.height(14.dp))

            // Live Preview Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color.White.copy(alpha = 0.03f))
                    .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(14.dp))
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                MusicWidget(style = musicStyle, font = musicFont, previewTrack = previewTrack)
            }

            Spacer(modifier = Modifier.height(14.dp))
            Text("Style", color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(bottom = 6.dp))
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MusicStyle.entries.forEach { s ->
                    val selected = musicStyle == s
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (selected) accent else Color.White.copy(alpha = 0.1f))
                            .clickable { musicStyle = s }
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                    ) {
                        Text(s.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }

            FontSelectorRow(selectedFont = musicFont, accent = accent, onFontSelected = { musicFont = it })
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ── Favorites Widget Style & Font ────────────────────────────────────
        ModernCard {
            Text("Favorites Widget", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text("Customize style & typography", color = Color.White.copy(alpha = 0.5f), fontSize = 12.sp)
            Spacer(modifier = Modifier.height(14.dp))

            // Live Preview Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color.White.copy(alpha = 0.03f))
                    .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(14.dp))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                FavoritesWidget(style = favoritesStyle, font = favoritesFont, previewApps = previewApps)
            }

            Spacer(modifier = Modifier.height(14.dp))
            Text("Style", color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(bottom = 6.dp))
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FavoritesStyle.entries.forEach { s ->
                    val selected = favoritesStyle == s
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (selected) accent else Color.White.copy(alpha = 0.1f))
                            .clickable { favoritesStyle = s }
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                    ) {
                        Text(s.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }

            FontSelectorRow(selectedFont = favoritesFont, accent = accent, onFontSelected = { favoritesFont = it })
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ── Day Widget Style & Font ──────────────────────────────────────────
        ModernCard {
            Text("Day Widget", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text("Customize style & typography", color = Color.White.copy(alpha = 0.5f), fontSize = 12.sp)
            Spacer(modifier = Modifier.height(14.dp))

            // Live Preview Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color.White.copy(alpha = 0.03f))
                    .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(14.dp))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                DayWidget(style = dayStyle, font = dayFont, dayText = "FRIDAY")
            }

            Spacer(modifier = Modifier.height(14.dp))
            Text("Style", color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(bottom = 6.dp))
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                DayStyle.entries.forEach { s ->
                    val selected = dayStyle == s
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (selected) accent else Color.White.copy(alpha = 0.1f))
                            .clickable { dayStyle = s }
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Text(s.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }

            FontSelectorRow(selectedFont = dayFont, accent = accent, onFontSelected = { dayFont = it })
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ── Storage Widget Style & Font ──────────────────────────────────────
        ModernCard {
            Text("Storage Widget", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text("Customize storage telemetry & style", color = Color.White.copy(alpha = 0.5f), fontSize = 12.sp)
            Spacer(modifier = Modifier.height(14.dp))

            // Live Preview Box
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

            Spacer(modifier = Modifier.height(14.dp))
            Text("Style", color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(bottom = 6.dp))
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StorageStyle.entries.forEach { s ->
                    val selected = storageStyle == s
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (selected) accent else Color.White.copy(alpha = 0.1f))
                            .clickable { storageStyle = s }
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                    ) {
                        Text(s.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }

            FontSelectorRow(selectedFont = storageFont, accent = accent, onFontSelected = { storageFont = it })
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ── Appearance ──────────────────────────────────────────────────────
        ModernCard {
            Text("Appearance", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))

            Text("Widget Preset", color = Color.White.copy(alpha = 0.7f))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)) {
                listOf("GLASS", "SOLID", "MINIMAL").forEach { p ->
                    val isSelected = preset == p
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) accent else Color.White.copy(alpha = 0.1f))
                            .clickable { preset = p }
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(p, color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Glass Effect (Blur)", color = Color.White)
                Switch(checked = glassEffect, onCheckedChange = { glassEffect = it }, colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = accent))
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("Corner Radius: ${radius.toInt()}dp", color = Color.White.copy(alpha = 0.7f))
            Slider(value = radius, onValueChange = { radius = it }, valueRange = 0f..50f, colors = SliderDefaults.colors(thumbColor = accent, activeTrackColor = accent))

            Spacer(modifier = Modifier.height(16.dp))
            Text("Blur Intensity: ${blurIntensity.toInt()}px", color = Color.White.copy(alpha = 0.7f))
            Slider(value = blurIntensity, onValueChange = { blurIntensity = it }, valueRange = 0f..100f, colors = SliderDefaults.colors(thumbColor = accent, activeTrackColor = accent))

            Spacer(modifier = Modifier.height(16.dp))
            Text("Base Color", color = Color.White.copy(alpha = 0.7f))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.padding(top = 8.dp)) {
                val colors = listOf(Color(0x1AFFFFFF), Color(0x33000000), Color(0x333D5AFE), Color(0x33FF4081))
                colors.forEach { c ->
                    Box(
                        modifier = Modifier.size(40.dp).clip(CircleShape).background(c)
                            .clickable { widgetColor = c.toArgb() },
                        contentAlignment = Alignment.Center
                    ) {
                        if (widgetColor == c.toArgb()) Icon(Icons.Rounded.Check, contentDescription = null, tint = Color.White)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("Apply Font to All Widgets", color = Color.White.copy(alpha = 0.7f))
            Text("Quickly sync typography across all widgets", color = Color.White.copy(alpha = 0.45f), fontSize = 11.sp)
            Row(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                WidgetFont.entries.forEach { f ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.White.copy(alpha = 0.08f))
                            .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(10.dp))
                            .clickable {
                                clockFont = f
                                dayFont = f
                                musicFont = f
                                favoritesFont = f
                                storageFont = f
                                networkFont = f
                            }
                            .padding(horizontal = 12.dp, vertical = 7.dp)
                    ) {
                        Text(
                            text = f.label,
                            color = Color.White,
                            fontFamily = f.toFontFamily(),
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Save button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(accent)
                .clickable { saveAll() }
                .padding(vertical = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("Save Changes", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(40.dp))
    }

    if (isDetailedUsageOpen) {
        NetworkUsageScreen(onClose = { isDetailedUsageOpen = false })
    }

    if (isBluetoothConfigOpen) {
        BluetoothSettingsScreen(
            userManager = userManager,
            onClose = {
                isBluetoothConfigOpen = false
                showBluetooth = userManager.getShowBluetoothWidget()
                bluetoothStyle = userManager.getBluetoothStyle()
                bluetoothFont = userManager.getBluetoothFont()
            }
        )
    }
}

@Composable
private fun FontSelectorRow(
    selectedFont: WidgetFont,
    accent: Color,
    onFontSelected: (WidgetFont) -> Unit
) {
    Text(
        text = "Font",
        color = Color.White.copy(alpha = 0.6f),
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(top = 12.dp, bottom = 6.dp)
    )
    Row(
        modifier = Modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        WidgetFont.entries.forEach { f ->
            val selected = selectedFont == f
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (selected) accent else Color.White.copy(alpha = 0.08f))
                    .border(
                        1.dp,
                        if (selected) accent else Color.White.copy(alpha = 0.08f),
                        RoundedCornerShape(10.dp)
                    )
                    .clickable { onFontSelected(f) }
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Text(
                    text = f.label,
                    color = Color.White,
                    fontFamily = f.toFontFamily(),
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                    fontSize = 12.sp
                )
            }
        }
    }
}

