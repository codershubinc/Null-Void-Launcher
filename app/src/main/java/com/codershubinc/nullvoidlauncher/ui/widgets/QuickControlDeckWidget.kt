package com.codershubinc.nullvoidlauncher.ui.widgets

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codershubinc.nullvoidlauncher.data.ControlDeckAction
import com.codershubinc.nullvoidlauncher.data.ControlDeckStyle
import com.codershubinc.nullvoidlauncher.data.UserManager
import com.codershubinc.nullvoidlauncher.ui.controls.ControlHelper

@Composable
fun QuickControlDeckWidget(
    modifier: Modifier = Modifier,
    overrideStyle: ControlDeckStyle? = null,
    overrideActions: Set<ControlDeckAction>? = null,
    overrideCompact: Boolean? = null,
    onLongClick: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val userManager = remember { UserManager(context) }

    // Ensure listeners & observers are registered
    DisposableEffect(Unit) {
        ControlHelper.register(context)
        onDispose {}
    }

    val deckState by ControlHelper.deckState.collectAsState()

    val style = overrideStyle ?: remember { userManager.getControlDeckStyle() }
    val actions = overrideActions ?: remember { userManager.getControlDeckActions() }
    val isCompact = overrideCompact ?: remember { userManager.getControlDeckCompact() }

    val containerModifier = when (style) {
        ControlDeckStyle.GLASS -> {
            modifier
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
        }
        ControlDeckStyle.MINIMAL -> {
            modifier
                .clip(RoundedCornerShape(12.dp))
                .background(Color.Transparent)
        }
        ControlDeckStyle.OUTLINE -> {
            modifier
                .clip(RoundedCornerShape(16.dp))
                .border(1.dp, Color.White.copy(alpha = 0.25f), RoundedCornerShape(16.dp))
                .background(Color.Black.copy(alpha = 0.4f))
        }
        ControlDeckStyle.SOLID -> {
            modifier
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFF1E1E24))
                .border(1.dp, Color(0xFF2C2C36), RoundedCornerShape(20.dp))
        }
        ControlDeckStyle.CHIP -> {
            modifier
                .clip(RoundedCornerShape(30.dp))
                .background(Color.White.copy(alpha = 0.12f))
                .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(30.dp))
        }
    }

    val paddingValues = if (isCompact) {
        PaddingValues(horizontal = 6.dp, vertical = 4.dp)
    } else {
        PaddingValues(horizontal = 8.dp, vertical = 6.dp)
    }

    val itemSize = if (isCompact) 30.dp else 36.dp
    val iconSize = if (isCompact) 14.dp else 17.dp
    val spacing = if (isCompact) 6.dp else 8.dp

    Row(
        modifier = containerModifier.padding(paddingValues),
        horizontalArrangement = Arrangement.spacedBy(spacing),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Render buttons in standard ordered sequence if enabled
        if (actions.contains(ControlDeckAction.TORCH)) {
            val isTorch = deckState.isTorchOn
            QuickControlButton(
                icon = if (isTorch) Icons.Rounded.FlashlightOn else Icons.Rounded.FlashlightOff,
                label = if (style == ControlDeckStyle.CHIP) "Torch" else null,
                isActive = isTorch,
                activeColor = Color(0xFFFFD54F),
                size = itemSize,
                iconSize = iconSize,
                deckStyle = style,
                onClick = { ControlHelper.toggleTorch(context) },
                onLongClick = onLongClick
            )
        }

        if (actions.contains(ControlDeckAction.RINGER)) {
            val (ringerIcon, ringerActive, ringerText) = when (deckState.ringerState) {
                ControlHelper.RingerState.SILENT -> Triple(Icons.Rounded.VolumeOff, false, "Silent")
                ControlHelper.RingerState.VIBRATE -> Triple(Icons.Rounded.Vibration, true, "Vibrate")
                ControlHelper.RingerState.NORMAL -> Triple(Icons.Rounded.VolumeUp, true, "Ring")
            }
            QuickControlButton(
                icon = ringerIcon,
                label = if (style == ControlDeckStyle.CHIP) ringerText else null,
                isActive = ringerActive,
                activeColor = Color(0xFF3D5AFE),
                size = itemSize,
                iconSize = iconSize,
                deckStyle = style,
                onClick = { ControlHelper.cycleRingerMode(context) },
                onLongClick = {
                    ControlHelper.openSoundSettings(context)
                }
            )
        }

        if (actions.contains(ControlDeckAction.ROTATION)) {
            val isAuto = deckState.isAutoRotate
            QuickControlButton(
                icon = if (isAuto) Icons.Rounded.ScreenRotation else Icons.Rounded.ScreenLockRotation,
                label = if (style == ControlDeckStyle.CHIP) "Rotate" else null,
                isActive = isAuto,
                activeColor = Color(0xFF00E676),
                size = itemSize,
                iconSize = iconSize,
                deckStyle = style,
                onClick = { ControlHelper.toggleAutoRotate(context) },
                onLongClick = {
                    ControlHelper.openDisplaySettings(context)
                }
            )
        }

        if (actions.contains(ControlDeckAction.DND)) {
            val isDnd = deckState.isDndActive
            QuickControlButton(
                icon = if (isDnd) Icons.Rounded.DoNotDisturbOn else Icons.Rounded.DoNotDisturbOff,
                label = if (style == ControlDeckStyle.CHIP) "DND" else null,
                isActive = isDnd,
                activeColor = Color(0xFFFF5252),
                size = itemSize,
                iconSize = iconSize,
                deckStyle = style,
                onClick = { ControlHelper.toggleDnd(context) },
                onLongClick = {
                    ControlHelper.openSoundSettings(context)
                }
            )
        }

        if (actions.contains(ControlDeckAction.HOTSPOT)) {
            QuickControlButton(
                icon = Icons.Rounded.WifiTethering,
                label = if (style == ControlDeckStyle.CHIP) "Hotspot" else null,
                isActive = false,
                activeColor = Color(0xFF00E5FF),
                size = itemSize,
                iconSize = iconSize,
                deckStyle = style,
                onClick = { ControlHelper.openHotspotSettings(context) },
                onLongClick = onLongClick
            )
        }

        if (actions.contains(ControlDeckAction.BLUETOOTH)) {
            val isBt = deckState.isBluetoothEnabled
            QuickControlButton(
                icon = if (isBt) Icons.Rounded.Bluetooth else Icons.Rounded.BluetoothDisabled,
                label = if (style == ControlDeckStyle.CHIP) "BT" else null,
                isActive = isBt,
                activeColor = Color(0xFF448AFF),
                size = itemSize,
                iconSize = iconSize,
                deckStyle = style,
                onClick = { ControlHelper.openBluetoothSettings(context) },
                onLongClick = onLongClick
            )
        }

        if (actions.contains(ControlDeckAction.WIFI)) {
            val isWifi = deckState.isWifiEnabled
            QuickControlButton(
                icon = if (isWifi) Icons.Rounded.Wifi else Icons.Rounded.WifiOff,
                label = if (style == ControlDeckStyle.CHIP) "Wi-Fi" else null,
                isActive = isWifi,
                activeColor = Color(0xFF69F0AE),
                size = itemSize,
                iconSize = iconSize,
                deckStyle = style,
                onClick = { ControlHelper.openWifiSettings(context) },
                onLongClick = onLongClick
            )
        }
    }
}

@Composable
private fun QuickControlButton(
    icon: ImageVector,
    label: String? = null,
    isActive: Boolean,
    activeColor: Color,
    size: androidx.compose.ui.unit.Dp,
    iconSize: androidx.compose.ui.unit.Dp,
    deckStyle: ControlDeckStyle,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null
) {
    val haptic = LocalHapticFeedback.current
    val animatedBg by animateColorAsState(
        targetValue = when {
            isActive && deckStyle == ControlDeckStyle.SOLID -> activeColor.copy(alpha = 0.35f)
            isActive -> activeColor.copy(alpha = 0.22f)
            deckStyle == ControlDeckStyle.SOLID -> Color.White.copy(alpha = 0.08f)
            deckStyle == ControlDeckStyle.MINIMAL -> Color.Transparent
            else -> Color.White.copy(alpha = 0.05f)
        },
        animationSpec = tween(200),
        label = "buttonBg"
    )

    val animatedBorder by animateColorAsState(
        targetValue = when {
            isActive -> activeColor.copy(alpha = 0.6f)
            deckStyle == ControlDeckStyle.OUTLINE -> Color.White.copy(alpha = 0.2f)
            deckStyle == ControlDeckStyle.MINIMAL -> Color.Transparent
            else -> Color.White.copy(alpha = 0.08f)
        },
        animationSpec = tween(200),
        label = "buttonBorder"
    )

    val animatedTint by animateColorAsState(
        targetValue = if (isActive) activeColor else Color.White.copy(alpha = 0.65f),
        animationSpec = tween(200),
        label = "buttonTint"
    )

    if (label != null) {
        // Chip mode with text
        Row(
            modifier = Modifier
                .height(size)
                .clip(CircleShape)
                .background(animatedBg)
                .border(1.dp, animatedBorder, CircleShape)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onClick()
                        },
                        onLongPress = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onLongClick?.invoke()
                        }
                    )
                }
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = animatedTint,
                modifier = Modifier.size(iconSize)
            )
            Text(
                text = label,
                color = animatedTint,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
        }
    } else {
        // Standard circle/pill icon button
        Box(
            modifier = Modifier
                .size(size)
                .clip(CircleShape)
                .background(animatedBg)
                .border(1.dp, animatedBorder, CircleShape)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onClick()
                        },
                        onLongPress = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onLongClick?.invoke()
                        }
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = animatedTint,
                modifier = Modifier.size(iconSize)
            )
        }
    }
}
