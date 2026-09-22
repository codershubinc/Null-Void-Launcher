package com.codershubinc.nullvoidlauncher.ui.widgets

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.codershubinc.nullvoidlauncher.ui.controls.ControlHelper

@Composable
fun QuickControlDeckWidget(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isTorchOn by remember { mutableStateOf(ControlHelper.isTorchActive()) }
    var ringerState by remember { mutableStateOf(ControlHelper.getRingerState(context)) }

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(
                        Color.White.copy(alpha = 0.08f),
                        Color.White.copy(alpha = 0.03f)
                    )
                )
            )
            .border(
                1.dp,
                Brush.horizontalGradient(
                    listOf(
                        Color.White.copy(alpha = 0.22f),
                        Color.White.copy(alpha = 0.05f)
                    )
                ),
                RoundedCornerShape(20.dp)
            )
            .padding(horizontal = 8.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 1. Flashlight / Torch
        QuickControlItem(
            icon = if (isTorchOn) Icons.Rounded.FlashlightOn else Icons.Rounded.FlashlightOff,
            isActive = isTorchOn,
            activeColor = Color(0xFFFFD54F),
            onClick = {
                ControlHelper.toggleTorch(context) { on ->
                    isTorchOn = on
                }
            }
        )

        // 2. Ringer Mode (Ring / Vibrate / Silent)
        val (ringerIcon, ringerActive) = when (ringerState) {
            ControlHelper.RingerState.SILENT -> Icons.Rounded.VolumeOff to false
            ControlHelper.RingerState.VIBRATE -> Icons.Rounded.Vibration to true
            ControlHelper.RingerState.NORMAL -> Icons.Rounded.VolumeUp to true
        }
        QuickControlItem(
            icon = ringerIcon,
            isActive = ringerActive,
            activeColor = Color(0xFF3D5AFE),
            onClick = {
                ringerState = ControlHelper.cycleRingerMode(context)
            }
        )

        // 3. Hotspot Shortcut
        QuickControlItem(
            icon = Icons.Rounded.WifiTethering,
            isActive = false,
            activeColor = Color(0xFF00E5FF),
            onClick = {
                ControlHelper.openHotspotSettings(context)
            }
        )

        // 4. Display / Auto-Rotate Shortcut
        val isAutoRotate = remember { ControlHelper.isAutoRotateEnabled(context) }
        QuickControlItem(
            icon = Icons.Rounded.ScreenRotation,
            isActive = isAutoRotate,
            activeColor = Color(0xFF00E676),
            onClick = {
                ControlHelper.openDisplaySettings(context)
            }
        )
    }
}

@Composable
private fun QuickControlItem(
    icon: ImageVector,
    isActive: Boolean,
    activeColor: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(if (isActive) activeColor.copy(alpha = 0.22f) else Color.White.copy(alpha = 0.05f))
            .border(
                1.dp,
                if (isActive) activeColor.copy(alpha = 0.5f) else Color.White.copy(alpha = 0.08f),
                CircleShape
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isActive) activeColor else Color.White.copy(alpha = 0.65f),
            modifier = Modifier.size(17.dp)
        )
    }
}
