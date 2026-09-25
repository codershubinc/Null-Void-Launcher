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
import com.codershubinc.nullvoidlauncher.data.DayStyle
import com.codershubinc.nullvoidlauncher.data.UserManager
import com.codershubinc.nullvoidlauncher.ui.components.ModernCard
import com.codershubinc.nullvoidlauncher.ui.widgets.DayWidget

@Composable
fun DayWidgetTweaksPage(
    userManager: UserManager,
    onBack: () -> Unit
) {
    var showDay by remember { mutableStateOf(userManager.getShowDayWidget()) }
    var dayStyle by remember { mutableStateOf(userManager.getDayStyle()) }
    var dayFont by remember { mutableStateOf(userManager.getDayFont()) }

    WidgetPageScaffold(
        title = "Day Widget",
        subtitle = "Brutalist, retro & elegant weekday displays",
        onBack = onBack
    ) {
        WidgetVisibilityCard(
            title = "Show Day Widget",
            description = "Display weekday indicator within the clock stack",
            visible = showDay,
            onVisibleChange = {
                showDay = it
                userManager.saveShowDayWidget(it)
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
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                DayWidget(
                    style = dayStyle,
                    font = dayFont,
                    dayText = "FRIDAY"
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            WidgetStyleSelector(
                title = "Day Style",
                styles = DayStyle.entries,
                selectedStyle = dayStyle,
                onStyleSelected = {
                    dayStyle = it
                    userManager.saveDayStyle(it)
                },
                getLabel = { it.name }
            )

            WidgetFontSelector(
                selectedFont = dayFont,
                onFontSelected = {
                    dayFont = it
                    userManager.saveDayFont(it)
                }
            )
        }
    }
}
