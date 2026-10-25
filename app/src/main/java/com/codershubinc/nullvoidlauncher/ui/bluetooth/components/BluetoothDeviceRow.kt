package com.codershubinc.nullvoidlauncher.ui.bluetooth.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codershubinc.nullvoidlauncher.system.bluetooth.BluetoothDeviceInfo
import com.codershubinc.nullvoidlauncher.ui.widgets.bluetooth.resolveBluetoothDeviceIcon

@Composable
fun BluetoothDeviceRow(
    device: BluetoothDeviceInfo,
    isSelected: Boolean,
    accent: Color,
    onSelect: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) accent.copy(alpha = 0.15f) else Color.White.copy(alpha = 0.04f))
            .border(
                1.dp,
                if (isSelected) accent.copy(alpha = 0.4f) else Color.Transparent,
                RoundedCornerShape(12.dp)
            )
            .clickable { onSelect() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        val devIcon = resolveBluetoothDeviceIcon(
            device.deviceType,
            device.isConnected
        )

        Icon(
            imageVector = devIcon,
            contentDescription = null,
            tint = if (device.isConnected) Color(0xFF00E676) else Color.White.copy(alpha = 0.4f),
            modifier = Modifier.size(20.dp)
        )

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = device.name,
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
                    text = if (device.isConnected) "Connected" else "Paired",
                    color = if (device.isConnected) Color(0xFF00E676) else Color.White.copy(alpha = 0.4f),
                    fontSize = 11.sp
                )
                if (device.hasBattery) {
                    Text(text = "•", color = Color.White.copy(alpha = 0.3f), fontSize = 9.sp)
                    Text(
                        text = "${device.batteryLevel}% Battery",
                        color = Color(0xFF00E676),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        if (isSelected) {
            Icon(
                imageVector = Icons.Rounded.CheckCircle,
                contentDescription = null,
                tint = accent,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
