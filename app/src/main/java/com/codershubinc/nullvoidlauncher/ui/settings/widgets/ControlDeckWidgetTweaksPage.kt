package com.codershubinc.nullvoidlauncher.ui.settings.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codershubinc.nullvoidlauncher.data.ControlDeckAction
import com.codershubinc.nullvoidlauncher.data.ControlDeckStyle
import com.codershubinc.nullvoidlauncher.data.UserManager
import com.codershubinc.nullvoidlauncher.ui.components.ModernCard
import com.codershubinc.nullvoidlauncher.ui.controls.ControlHelper
import com.codershubinc.nullvoidlauncher.ui.widgets.QuickControlDeckWidget

@Composable
fun ControlDeckWidgetTweaksPage(
    userManager: UserManager,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var showControlDeck by remember { mutableStateOf(userManager.getShowControlDeck()) }
    var deckStyle by remember { mutableStateOf(userManager.getControlDeckStyle()) }
    var isCompact by remember { mutableStateOf(userManager.getControlDeckCompact()) }
    var enabledActions by remember { mutableStateOf(userManager.getControlDeckActions()) }

    LaunchedEffect(Unit) {
        ControlHelper.register(context)
        ControlHelper.refreshState(context)
    }

    WidgetPageScaffold(
        title = "Quick Control Deck",
        subtitle = "Aesthetic, reactive hardware toggles & system shortcuts",
        onBack = onBack
    ) {
        // 1. Visibility toggle
        WidgetVisibilityCard(
            title = "Show Quick Control Deck",
            description = "Display quick action toggles right below the telemetry pill on home screen",
            visible = showControlDeck,
            onVisibleChange = {
                showControlDeck = it
                userManager.saveShowControlDeck(it)
            }
        )

        Spacer(modifier = Modifier.height(20.dp))

        // 2. Interactive Live Preview
        ModernCard {
            Text(
                text = "Live Interactive Preview",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Tap buttons to test real-time hardware sync with your phone",
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 12.sp
            )
            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color.White.copy(alpha = 0.03f))
                    .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(14.dp))
                    .padding(vertical = 24.dp, horizontal = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                QuickControlDeckWidget(
                    overrideStyle = deckStyle,
                    overrideActions = enabledActions,
                    overrideCompact = isCompact
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 3. Deck Style Selector
        ModernCard {
            Text(
                text = "Deck Aesthetic Style",
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Choose the container aesthetic and button style",
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 12.sp
            )
            Spacer(modifier = Modifier.height(14.dp))

            WidgetStyleSelector(
                title = "Visual Style",
                styles = ControlDeckStyle.entries,
                selectedStyle = deckStyle,
                onStyleSelected = {
                    deckStyle = it
                    userManager.saveControlDeckStyle(it)
                },
                getLabel = { style ->
                    when (style) {
                        ControlDeckStyle.GLASS -> "Glass (Frosted)"
                        ControlDeckStyle.MINIMAL -> "Minimal (Floating)"
                        ControlDeckStyle.OUTLINE -> "Outline (Border)"
                        ControlDeckStyle.SOLID -> "Solid (Dark)"
                        ControlDeckStyle.CHIP -> "Chip (With Text)"
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 4. Compact Mode
        ModernCard {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Compact Size",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Reduce icon and padding size for a sleek minimal footprint",
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 12.sp
                    )
                }
                Switch(
                    checked = isCompact,
                    onCheckedChange = {
                        isCompact = it
                        userManager.saveControlDeckCompact(it)
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = Color(0xFF3D5AFE),
                        uncheckedThumbColor = Color.Gray,
                        uncheckedTrackColor = Color.White.copy(alpha = 0.1f)
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 5. Toggle Actions Customization
        ModernCard {
            Text(
                text = "Toggle Buttons & Actions",
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Enable or disable individual quick controls in your bar",
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 12.sp
            )
            Spacer(modifier = Modifier.height(12.dp))

            ControlActionRow(
                icon = Icons.Rounded.FlashlightOn,
                title = "Flashlight / Torch",
                subtitle = "Hardware synchronized camera LED switch",
                accentColor = Color(0xFFFFD54F),
                isEnabled = enabledActions.contains(ControlDeckAction.TORCH),
                onToggle = { active ->
                    val updated = enabledActions.toMutableSet()
                    if (active) updated.add(ControlDeckAction.TORCH) else updated.remove(ControlDeckAction.TORCH)
                    enabledActions = updated
                    userManager.saveControlDeckActions(updated)
                }
            )

            HorizontalDivider(color = Color.White.copy(alpha = 0.05f), modifier = Modifier.padding(vertical = 8.dp))

            ControlActionRow(
                icon = Icons.Rounded.VolumeUp,
                title = "Ringer Mode Switch",
                subtitle = "Cycle Ring ➔ Vibrate ➔ Silent modes (Long-press opens Sound)",
                accentColor = Color(0xFF3D5AFE),
                isEnabled = enabledActions.contains(ControlDeckAction.RINGER),
                onToggle = { active ->
                    val updated = enabledActions.toMutableSet()
                    if (active) updated.add(ControlDeckAction.RINGER) else updated.remove(ControlDeckAction.RINGER)
                    enabledActions = updated
                    userManager.saveControlDeckActions(updated)
                }
            )

            HorizontalDivider(color = Color.White.copy(alpha = 0.05f), modifier = Modifier.padding(vertical = 8.dp))

            ControlActionRow(
                icon = Icons.Rounded.ScreenRotation,
                title = "Auto-Rotate",
                subtitle = "Syncs with system rotation orientation lock",
                accentColor = Color(0xFF00E676),
                isEnabled = enabledActions.contains(ControlDeckAction.ROTATION),
                onToggle = { active ->
                    val updated = enabledActions.toMutableSet()
                    if (active) updated.add(ControlDeckAction.ROTATION) else updated.remove(ControlDeckAction.ROTATION)
                    enabledActions = updated
                    userManager.saveControlDeckActions(updated)
                }
            )

            HorizontalDivider(color = Color.White.copy(alpha = 0.05f), modifier = Modifier.padding(vertical = 8.dp))

            ControlActionRow(
                icon = Icons.Rounded.DoNotDisturbOn,
                title = "Do Not Disturb (DND)",
                subtitle = "Quickly mute notifications and interruptions",
                accentColor = Color(0xFFFF5252),
                isEnabled = enabledActions.contains(ControlDeckAction.DND),
                onToggle = { active ->
                    val updated = enabledActions.toMutableSet()
                    if (active) updated.add(ControlDeckAction.DND) else updated.remove(ControlDeckAction.DND)
                    enabledActions = updated
                    userManager.saveControlDeckActions(updated)
                }
            )

            HorizontalDivider(color = Color.White.copy(alpha = 0.05f), modifier = Modifier.padding(vertical = 8.dp))

            ControlActionRow(
                icon = Icons.Rounded.WifiTethering,
                title = "Hotspot / Tethering",
                subtitle = "Direct shortcut to Wi-Fi hotspot configuration",
                accentColor = Color(0xFF00E5FF),
                isEnabled = enabledActions.contains(ControlDeckAction.HOTSPOT),
                onToggle = { active ->
                    val updated = enabledActions.toMutableSet()
                    if (active) updated.add(ControlDeckAction.HOTSPOT) else updated.remove(ControlDeckAction.HOTSPOT)
                    enabledActions = updated
                    userManager.saveControlDeckActions(updated)
                }
            )

            HorizontalDivider(color = Color.White.copy(alpha = 0.05f), modifier = Modifier.padding(vertical = 8.dp))

            ControlActionRow(
                icon = Icons.Rounded.Bluetooth,
                title = "Bluetooth Panel",
                subtitle = "State indicator and rapid Bluetooth shortcut",
                accentColor = Color(0xFF448AFF),
                isEnabled = enabledActions.contains(ControlDeckAction.BLUETOOTH),
                onToggle = { active ->
                    val updated = enabledActions.toMutableSet()
                    if (active) updated.add(ControlDeckAction.BLUETOOTH) else updated.remove(ControlDeckAction.BLUETOOTH)
                    enabledActions = updated
                    userManager.saveControlDeckActions(updated)
                }
            )

            HorizontalDivider(color = Color.White.copy(alpha = 0.05f), modifier = Modifier.padding(vertical = 8.dp))

            ControlActionRow(
                icon = Icons.Rounded.Wifi,
                title = "Wi-Fi Panel",
                subtitle = "State indicator and rapid Wi-Fi selector shortcut",
                accentColor = Color(0xFF69F0AE),
                isEnabled = enabledActions.contains(ControlDeckAction.WIFI),
                onToggle = { active ->
                    val updated = enabledActions.toMutableSet()
                    if (active) updated.add(ControlDeckAction.WIFI) else updated.remove(ControlDeckAction.WIFI)
                    enabledActions = updated
                    userManager.saveControlDeckActions(updated)
                }
            )
        }
    }
}

@Composable
private fun ControlActionRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    accentColor: Color,
    isEnabled: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle(!isEnabled) }
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(if (isEnabled) accentColor.copy(alpha = 0.15f) else Color.White.copy(alpha = 0.05f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isEnabled) accentColor else Color.White.copy(alpha = 0.4f),
                    modifier = Modifier.size(18.dp)
                )
            }
            Column {
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = subtitle,
                    color = Color.White.copy(alpha = 0.45f),
                    fontSize = 11.sp
                )
            }
        }

        Switch(
            checked = isEnabled,
            onCheckedChange = onToggle,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = accentColor,
                uncheckedThumbColor = Color.Gray,
                uncheckedTrackColor = Color.White.copy(alpha = 0.1f)
            )
        )
    }
}
