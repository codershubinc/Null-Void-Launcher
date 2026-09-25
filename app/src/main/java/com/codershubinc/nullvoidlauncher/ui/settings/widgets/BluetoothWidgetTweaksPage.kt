package com.codershubinc.nullvoidlauncher.ui.settings.widgets

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bluetooth
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codershubinc.nullvoidlauncher.data.BluetoothStyle
import com.codershubinc.nullvoidlauncher.data.UserManager
import com.codershubinc.nullvoidlauncher.ui.bluetooth.BluetoothDeviceInfo
import com.codershubinc.nullvoidlauncher.ui.bluetooth.BluetoothDeviceType
import com.codershubinc.nullvoidlauncher.ui.bluetooth.BluetoothHelper
import com.codershubinc.nullvoidlauncher.ui.bluetooth.BluetoothInfoState
import com.codershubinc.nullvoidlauncher.ui.bluetooth.BluetoothSettingsScreen
import com.codershubinc.nullvoidlauncher.ui.components.ModernCard
import com.codershubinc.nullvoidlauncher.ui.widgets.BluetoothWidget

@Composable
fun BluetoothWidgetTweaksPage(
    userManager: UserManager,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var showBluetooth by remember { mutableStateOf(userManager.getShowBluetoothWidget()) }
    var bluetoothStyle by remember { mutableStateOf(userManager.getBluetoothStyle()) }
    var bluetoothFont by remember { mutableStateOf(userManager.getBluetoothFont()) }
    var isBluetoothConfigOpen by remember { mutableStateOf(false) }

    var hasBtPermission by remember { mutableStateOf(BluetoothHelper.hasBluetoothPermission(context)) }
    val btPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasBtPermission = granted
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

    WidgetPageScaffold(
        title = "Bluetooth Widget",
        subtitle = "Connected device telemetry, battery & styles",
        onBack = onBack
    ) {
        WidgetVisibilityCard(
            title = "Show Bluetooth Widget",
            description = "Display connected audio device / accessory badge",
            visible = showBluetooth,
            onVisibleChange = {
                showBluetooth = it
                userManager.saveShowBluetoothWidget(it)
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
                BluetoothWidget(
                    style = bluetoothStyle,
                    font = bluetoothFont,
                    showOnlyIfConnected = false,
                    previewInfo = previewBluetooth
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            WidgetStyleSelector(
                title = "Bluetooth Style",
                styles = BluetoothStyle.entries,
                selectedStyle = bluetoothStyle,
                onStyleSelected = {
                    bluetoothStyle = it
                    userManager.saveBluetoothStyle(it)
                },
                getLabel = { it.name }
            )

            WidgetFontSelector(
                selectedFont = bluetoothFont,
                onFontSelected = {
                    bluetoothFont = it
                    userManager.saveBluetoothFont(it)
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

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
