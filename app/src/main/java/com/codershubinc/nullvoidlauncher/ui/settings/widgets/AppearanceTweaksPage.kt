package com.codershubinc.nullvoidlauncher.ui.settings.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codershubinc.nullvoidlauncher.data.UserManager
import com.codershubinc.nullvoidlauncher.data.WidgetFont
import com.codershubinc.nullvoidlauncher.ui.components.ModernCard

@Composable
fun AppearanceTweaksPage(
    userManager: UserManager,
    onBack: () -> Unit
) {
    var radius by remember { mutableFloatStateOf(userManager.getWidgetCornerRadius()) }
    var blurIntensity by remember { mutableFloatStateOf(userManager.getWidgetBlurIntensity()) }
    var glassEffect by remember { mutableStateOf(userManager.getWidgetGlassEffect()) }
    var widgetColor by remember { mutableIntStateOf(userManager.getWidgetColor()) }
    var preset by remember { mutableStateOf(userManager.getWidgetPreset()) }

    WidgetPageScaffold(
        title = "Widget Appearance",
        subtitle = "Glassmorphism, blur, corners & global typography",
        onBack = onBack
    ) {
        ModernCard {
            Text(
                text = "Preset Styles",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                listOf("GLASS", "SOLID", "MINIMAL").forEach { p ->
                    val isSelected = preset == p
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) WidgetSettingsAccent else Color.White.copy(alpha = 0.08f))
                            .clickable {
                                preset = p
                                userManager.saveWidgetPreset(p)
                            }
                            .padding(horizontal = 16.dp, vertical = 9.dp)
                    ) {
                        Text(
                            text = p,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
                    Text("Glass Effect (Frosted Blur)", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    Text("Applies backdrop blur and glass lighting", color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp)
                }
                Switch(
                    checked = glassEffect,
                    onCheckedChange = {
                        glassEffect = it
                        userManager.saveWidgetGlassEffect(it)
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = WidgetSettingsAccent
                    )
                )
            }

            Spacer(modifier = Modifier.height(18.dp))
            Text("Corner Radius: ${radius.toInt()}dp", color = Color.White.copy(alpha = 0.7f), fontSize = 13.sp)
            Slider(
                value = radius,
                onValueChange = {
                    radius = it
                    userManager.saveWidgetCornerRadius(it)
                },
                valueRange = 0f..50f,
                colors = SliderDefaults.colors(thumbColor = WidgetSettingsAccent, activeTrackColor = WidgetSettingsAccent)
            )

            Spacer(modifier = Modifier.height(14.dp))
            Text("Blur Intensity: ${blurIntensity.toInt()}px", color = Color.White.copy(alpha = 0.7f), fontSize = 13.sp)
            Slider(
                value = blurIntensity,
                onValueChange = {
                    blurIntensity = it
                    userManager.saveWidgetBlurIntensity(it)
                },
                valueRange = 0f..100f,
                colors = SliderDefaults.colors(thumbColor = WidgetSettingsAccent, activeTrackColor = WidgetSettingsAccent)
            )

            Spacer(modifier = Modifier.height(14.dp))
            Text("Base Color Tint", color = Color.White.copy(alpha = 0.7f), fontSize = 13.sp)
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(top = 10.dp, bottom = 4.dp)
            ) {
                val colors = listOf(Color(0x1AFFFFFF), Color(0x33000000), Color(0x333D5AFE), Color(0x33FF4081))
                colors.forEach { c ->
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(c)
                            .clickable {
                                widgetColor = c.toArgb()
                                userManager.saveWidgetColor(c.toArgb())
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (widgetColor == c.toArgb()) {
                            Icon(Icons.Rounded.Check, contentDescription = null, tint = Color.White)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            Text("Apply Font to All Widgets", color = Color.White.copy(alpha = 0.8f), fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            Text("Synchronize typography across all widgets simultaneously", color = Color.White.copy(alpha = 0.45f), fontSize = 11.sp)
            Row(
                modifier = Modifier
                    .padding(top = 10.dp)
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
                                userManager.saveClockFont(f)
                                userManager.saveDayFont(f)
                                userManager.saveMusicFont(f)
                                userManager.saveFavoritesFont(f)
                                userManager.saveStorageFont(f)
                                userManager.saveNetworkFont(f)
                                userManager.savePowerFont(f)
                                userManager.saveBluetoothFont(f)
                            }
                            .padding(horizontal = 14.dp, vertical = 9.dp)
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
    }
}
