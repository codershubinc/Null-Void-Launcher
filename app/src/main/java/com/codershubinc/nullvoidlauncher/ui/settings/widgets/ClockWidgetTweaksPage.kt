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
import com.codershubinc.nullvoidlauncher.data.ClockStyle
import com.codershubinc.nullvoidlauncher.data.UserManager
import com.codershubinc.nullvoidlauncher.ui.components.ModernCard
import com.codershubinc.nullvoidlauncher.ui.widgets.ClockWidget

@Composable
fun ClockWidgetTweaksPage(
    userManager: UserManager,
    onBack: () -> Unit
) {
    var showClock by remember { mutableStateOf(userManager.getShowClockWidget()) }
    var clockStyle by remember { mutableStateOf(userManager.getClockStyle()) }
    var clockFont by remember { mutableStateOf(userManager.getClockFont()) }

    WidgetPageScaffold(
        title = "Clock Widget",
        subtitle = "Customize time, date and typography",
        onBack = onBack
    ) {
        WidgetVisibilityCard(
            title = "Show Clock Widget",
            description = "Toggle clock visibility on your home screen",
            visible = showClock,
            onVisibleChange = {
                showClock = it
                userManager.saveShowClockWidget(it)
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

            Spacer(modifier = Modifier.height(20.dp))

            WidgetStyleSelector(
                title = "Clock Style",
                styles = ClockStyle.entries,
                selectedStyle = clockStyle,
                onStyleSelected = {
                    clockStyle = it
                    userManager.saveClockStyle(it)
                },
                getLabel = { it.name }
            )

            WidgetFontSelector(
                selectedFont = clockFont,
                onFontSelected = {
                    clockFont = it
                    userManager.saveClockFont(it)
                }
            )
        }
    }
}
