package com.codershubinc.nullvoidlauncher.ui.settings.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codershubinc.nullvoidlauncher.data.PowerStyle
import com.codershubinc.nullvoidlauncher.data.UserManager
import com.codershubinc.nullvoidlauncher.ui.components.ModernCard
import com.codershubinc.nullvoidlauncher.ui.power.PowerInfoState
import com.codershubinc.nullvoidlauncher.ui.widgets.PowerWidget

@Composable
fun PowerWidgetTweaksPage(
    userManager: UserManager,
    onBack: () -> Unit
) {
    var showPower by remember { mutableStateOf(userManager.getShowPowerWidget()) }
    var powerStyle by remember { mutableStateOf(userManager.getPowerStyle()) }
    var powerFont by remember { mutableStateOf(userManager.getPowerFont()) }

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

    WidgetPageScaffold(
        title = "Battery & Power Widget",
        subtitle = "Telemetry, charging indicators and voltage display",
        onBack = onBack
    ) {
        WidgetVisibilityCard(
            title = "Show Power Widget",
            description = "Display battery telemetry on your home screen",
            visible = showPower,
            onVisibleChange = {
                showPower = it
                userManager.saveShowPowerWidget(it)
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
                PowerWidget(
                    style = powerStyle,
                    font = powerFont,
                    previewInfo = previewPower
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            WidgetStyleSelector(
                title = "Battery Style",
                styles = PowerStyle.entries,
                selectedStyle = powerStyle,
                onStyleSelected = {
                    powerStyle = it
                    userManager.savePowerStyle(it)
                },
                getLabel = { it.name }
            )

            WidgetFontSelector(
                selectedFont = powerFont,
                onFontSelected = {
                    powerFont = it
                    userManager.savePowerFont(it)
                }
            )
        }
    }
}
