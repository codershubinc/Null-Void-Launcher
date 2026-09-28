package com.codershubinc.nullvoidlauncher.ui.bluetooth

import android.Manifest
import android.content.Context
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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codershubinc.nullvoidlauncher.data.BluetoothStyle
import com.codershubinc.nullvoidlauncher.data.UserManager
import com.codershubinc.nullvoidlauncher.data.WidgetFont
import com.codershubinc.nullvoidlauncher.ui.components.ModernCard
import com.codershubinc.nullvoidlauncher.ui.widgets.BluetoothWidget

/**
 * BluetoothSettingsScreen — Custom configuration page opened on long-pressing the Bluetooth widget.
 * Allows users to choose which device to display, toggle auto-hide when disconnected,
 * and customize widget style and typography.
 */


@Composable
fun BluetoothSettingsScreen(
    userManager: UserManager,
    onClose: () -> Unit
) {
    val context = LocalContext.current
    var showBluetooth by remember { mutableStateOf(userManager.getShowBluetoothWidget()) }
    var onlyConnected by remember { mutableStateOf(userManager.getBluetoothShowOnlyIfConnected()) }
    var preferredAddress by remember { mutableStateOf(userManager.getPreferredBluetoothDevice()) }
    var style by remember { mutableStateOf(userManager.getBluetoothStyle()) }
    var font by remember { mutableStateOf(userManager.getBluetoothFont()) }

    var bluetoothInfo by remember { mutableStateOf(BluetoothHelper.getBluetoothInfo(context)) }

    fun refreshInfo() {
        bluetoothInfo = BluetoothHelper.getBluetoothInfo(context)
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { _ ->
        refreshInfo()
    }

    LaunchedEffect(Unit) {
        if (!BluetoothHelper.hasBluetoothPermission(context) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissionLauncher.launch(Manifest.permission.BLUETOOTH_CONNECT)
        }
    }

    fun saveAndClose() {
        userManager.saveShowBluetoothWidget(showBluetooth)
        userManager.saveBluetoothShowOnlyIfConnected(onlyConnected)
        userManager.savePreferredBluetoothDevice(preferredAddress)
        userManager.saveBluetoothStyle(style)
        userManager.saveBluetoothFont(font)
        onClose()
    }

    BluetoothSettingsContent(
        showBluetooth = showBluetooth,
        onShowBluetoothChange = {
            showBluetooth = it
            userManager.saveShowBluetoothWidget(it)
        },
        onlyConnected = onlyConnected,
        onOnlyConnectedChange = {
            onlyConnected = it
            userManager.saveBluetoothShowOnlyIfConnected(it)
        },
        preferredAddress = preferredAddress,
        onPreferredAddressChange = {
            preferredAddress = it
            userManager.savePreferredBluetoothDevice(it)
        },
        style = style,
        onStyleChange = { style = it },
        font = font,
        onFontChange = { font = it },
        bluetoothInfo = bluetoothInfo,
        onRequestPermission = {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                permissionLauncher.launch(Manifest.permission.BLUETOOTH_CONNECT)
            }
        },
        onRefreshInfo = { refreshInfo() },
        onOpenAppSettings = { BluetoothHelper.openAppSettings(context) },
        onOpenBluetoothSettings = { BluetoothHelper.openBluetoothSettings(context) },
        onClose = { saveAndClose() }
    )
}

@Composable
fun BluetoothSettingsContent(
    showBluetooth: Boolean,
    onShowBluetoothChange: (Boolean) -> Unit,
    onlyConnected: Boolean,
    onOnlyConnectedChange: (Boolean) -> Unit,
    preferredAddress: String,
    onPreferredAddressChange: (String) -> Unit,
    style: BluetoothStyle,
    onStyleChange: (BluetoothStyle) -> Unit,
    font: WidgetFont,
    onFontChange: (WidgetFont) -> Unit,
    bluetoothInfo: BluetoothInfoState,
    onRequestPermission: () -> Unit,
    onRefreshInfo: () -> Unit,
    onOpenAppSettings: () -> Unit,
    onOpenBluetoothSettings: () -> Unit,
    onClose: () -> Unit
) {
    val previewInfo = remember(bluetoothInfo, preferredAddress) {
        if (bluetoothInfo.connectedDevices.isNotEmpty()) {
            bluetoothInfo
        } else {
            // Simulated preview for customization if no actual device is currently connected
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
    }

    val accent = Color(0xFF00E5FF)
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF080808))
            .statusBarsPadding()
            .padding(24.dp)
            .verticalScroll(scrollState)
    ) {
        // Back Button
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.05f))
                .clickable { onClose() },
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back", tint = Color.White)
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text("Bluetooth Widget", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Text("Configure target device, visibility & style", color = Color.White.copy(alpha = 0.5f), fontSize = 14.sp)
        Spacer(modifier = Modifier.height(24.dp))

        // ── Live Preview Card ───────────────────────────────────────────────
        ModernCard {
            Text("Live Preview", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
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
                BluetoothWidget(
                    style = style,
                    font = font,
                    showOnlyIfConnected = false,
                    previewInfo = previewInfo
                )
            }

            Spacer(modifier = Modifier.height(14.dp))
            Text("Style", color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(bottom = 6.dp))
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                BluetoothStyle.entries.forEach { s ->
                    val selected = style == s
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (selected) accent else Color.White.copy(alpha = 0.1f))
                            .clickable { onStyleChange(s) }
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                    ) {
                        Text(
                            s.name,
                            color = if (selected) Color.Black else Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            Text("Typography", color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(bottom = 6.dp))
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                WidgetFont.entries.forEach { f ->
                    val selected = font == f
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (selected) accent else Color.White.copy(alpha = 0.1f))
                            .clickable { onFontChange(f) }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(
                            f.label,
                            color = if (selected) Color.Black else Color.White,
                            fontWeight = FontWeight.Medium,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ── Permission Request Banner (if permission missing) ───────────────
        if (!bluetoothInfo.hasPermission) {
            ModernCard {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFFB300).copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.BluetoothDisabled,
                            contentDescription = null,
                            tint = Color(0xFFFFB300),
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Bluetooth Permission Required",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Null Void Launcher needs Nearby Devices permission to detect connected accessories and battery levels.",
                            color = Color.White.copy(alpha = 0.6f),
                            fontSize = 11.5.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onRequestPermission,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = accent),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Grant Permission", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }

                    OutlinedButton(
                        onClick = onOpenAppSettings,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("App Settings", color = Color.White, fontSize = 12.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        // ── Device Selection Card ───────────────────────────────────────────
        ModernCard {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Device to Display", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text("Select which device the widget should track", color = Color.White.copy(alpha = 0.5f), fontSize = 12.sp)
                }
                IconButton(onClick = onRefreshInfo) {
                    Icon(Icons.Rounded.Refresh, contentDescription = "Refresh", tint = accent)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Option 1: Auto (Active Device)
            val isAutoSelected = preferredAddress.isEmpty()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isAutoSelected) accent.copy(alpha = 0.15f) else Color.White.copy(alpha = 0.04f))
                    .border(
                        1.dp,
                        if (isAutoSelected) accent.copy(alpha = 0.4f) else Color.Transparent,
                        RoundedCornerShape(12.dp)
                    )
                    .clickable { onPreferredAddressChange("") }
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Autorenew,
                    contentDescription = null,
                    tint = if (isAutoSelected) accent else Color.White.copy(alpha = 0.5f),
                    modifier = Modifier.size(22.dp)
                )

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Auto (Active Connected Device)",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Automatically tracks whichever device is currently active",
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 11.sp
                    )
                }

                if (isAutoSelected) {
                    Icon(
                        imageVector = Icons.Rounded.CheckCircle,
                        contentDescription = null,
                        tint = accent,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Paired / Connected Devices List
            if (bluetoothInfo.pairedDevices.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .clickable {
                            if (!bluetoothInfo.hasPermission) {
                                onRequestPermission()
                            } else {
                                onOpenBluetoothSettings()
                            }
                        }
                        .background(if (!bluetoothInfo.hasPermission) accent.copy(alpha = 0.08f) else Color.Transparent)
                        .padding(vertical = 16.dp, horizontal = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (!bluetoothInfo.hasPermission) "Bluetooth permission required — Tap to Grant" else "No paired Bluetooth devices found (Tap to pair in Settings)",
                        color = if (!bluetoothInfo.hasPermission) accent else Color.White.copy(alpha = 0.45f),
                        fontSize = 12.sp,
                        fontWeight = if (!bluetoothInfo.hasPermission) FontWeight.Bold else FontWeight.Normal
                    )
                }
            } else {
                bluetoothInfo.pairedDevices.forEach { dev ->
                    val isDevSelected = preferredAddress.equals(dev.address, ignoreCase = true)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isDevSelected) accent.copy(alpha = 0.15f) else Color.White.copy(alpha = 0.04f))
                            .border(
                                1.dp,
                                if (isDevSelected) accent.copy(alpha = 0.4f) else Color.Transparent,
                                RoundedCornerShape(12.dp)
                            )
                            .clickable { onPreferredAddressChange(dev.address) }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        val devIcon = com.codershubinc.nullvoidlauncher.ui.widgets.bluetooth.resolveBluetoothDeviceIcon(
                            dev.deviceType,
                            dev.isConnected
                        )

                        Icon(
                            imageVector = devIcon,
                            contentDescription = null,
                            tint = if (dev.isConnected) Color(0xFF00E676) else Color.White.copy(alpha = 0.4f),
                            modifier = Modifier.size(20.dp)
                        )

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = dev.name,
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = if (dev.isConnected) "Connected" else "Paired",
                                    color = if (dev.isConnected) Color(0xFF00E676) else Color.White.copy(alpha = 0.4f),
                                    fontSize = 11.sp
                                )
                                if (dev.hasBattery) {
                                    Text(text = "•", color = Color.White.copy(alpha = 0.3f), fontSize = 9.sp)
                                    Text(
                                        text = "${dev.batteryLevel}% Battery",
                                        color = Color(0xFF00E676),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }

                        if (isDevSelected) {
                            Icon(
                                imageVector = Icons.Rounded.CheckCircle,
                                contentDescription = null,
                                tint = accent,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ── Widget Behavior Card ────────────────────────────────────────────
        ModernCard {
            Text("Behavior & Visibility", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(14.dp))

            // Hide when disconnected toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Hide When Disconnected", color = Color.White, fontSize = 14.sp)
                    Text("Only show the widget on homescreen when a device is connected", color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp)
                }
                Switch(
                    checked = onlyConnected,
                    onCheckedChange = onOnlyConnectedChange,
                    colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = accent)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Enable / Disable widget toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Enable Bluetooth Widget", color = Color.White, fontSize = 14.sp)
                    Text("Show widget in top-right telemetry stack", color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp)
                }
                Switch(
                    checked = showBluetooth,
                    onCheckedChange = onShowBluetoothChange,
                    colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = accent)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ── Quick Actions Card ──────────────────────────────────────────────
        ModernCard {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(accent.copy(alpha = 0.12f))
                    .border(1.dp, accent.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                    .clickable { onOpenBluetoothSettings() }
                    .padding(horizontal = 14.dp, vertical = 12.dp),
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
                        tint = accent,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Open System Bluetooth Settings",
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

        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF080808)
@Composable
fun BluetoothSettingsScreenPreview() {
    var showWidget by remember { mutableStateOf(true) }
    var onlyConnected by remember { mutableStateOf(false) }
    var preferredAddress by remember { mutableStateOf("00:11:22:33:44:55") }
    var style by remember { mutableStateOf(BluetoothStyle.GLASS) }
    var font by remember { mutableStateOf(WidgetFont.DEFAULT) }

    val mockBluetoothInfo = remember {
        BluetoothInfoState(
            isBluetoothEnabled = true,
            hasPermission = true,
            connectedDevices = listOf(
                BluetoothDeviceInfo(
                    name = "AirPods Pro",
                    address = "00:11:22:33:44:55",
                    isConnected = true,
                    batteryLevel = 85,
                    deviceType = BluetoothDeviceType.BUDS
                )
            ),
            pairedDevices = listOf(
                BluetoothDeviceInfo(
                    name = "AirPods Pro",
                    address = "00:11:22:33:44:55",
                    isConnected = true,
                    batteryLevel = 85,
                    deviceType = BluetoothDeviceType.BUDS
                ),
                BluetoothDeviceInfo(
                    name = "Sony WH-1000XM5",
                    address = "AA:BB:CC:DD:EE:FF",
                    isConnected = false,
                    batteryLevel = -1,
                    deviceType = BluetoothDeviceType.HEADPHONES
                ),
                BluetoothDeviceInfo(
                    name = "JBL Charge 5",
                    address = "11:22:33:44:55:66",
                    isConnected = false,
                    batteryLevel = -1,
                    deviceType = BluetoothDeviceType.SPEAKER
                )
            ),
            activeDevice = BluetoothDeviceInfo(
                name = "AirPods Pro",
                address = "00:11:22:33:44:55",
                isConnected = true,
                batteryLevel = 85,
                deviceType = BluetoothDeviceType.BUDS
            )
        )
    }

    BluetoothSettingsContent(
        showBluetooth = showWidget,
        onShowBluetoothChange = { showWidget = it },
        onlyConnected = onlyConnected,
        onOnlyConnectedChange = { onlyConnected = it },
        preferredAddress = preferredAddress,
        onPreferredAddressChange = { preferredAddress = it },
        style = style,
        onStyleChange = { style = it },
        font = font,
        onFontChange = { font = it },
        bluetoothInfo = mockBluetoothInfo,
        onRequestPermission = {},
        onRefreshInfo = {},
        onOpenAppSettings = {},
        onOpenBluetoothSettings = {},
        onClose = {}
    )
}
