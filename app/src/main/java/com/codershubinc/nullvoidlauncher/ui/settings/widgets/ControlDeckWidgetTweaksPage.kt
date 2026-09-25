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
import com.codershubinc.nullvoidlauncher.data.UserManager
import com.codershubinc.nullvoidlauncher.ui.components.ModernCard
import com.codershubinc.nullvoidlauncher.ui.widgets.QuickControlDeckWidget

@Composable
fun ControlDeckWidgetTweaksPage(
    userManager: UserManager,
    onBack: () -> Unit
) {
    var showControlDeck by remember { mutableStateOf(userManager.getShowControlDeck()) }

    WidgetPageScaffold(
        title = "Quick Control Deck",
        subtitle = "Flashlight, ringer modes and rapid device toggles",
        onBack = onBack
    ) {
        WidgetVisibilityCard(
            title = "Show Quick Control Deck",
            description = "Display quick action toggles right below the telemetry pill",
            visible = showControlDeck,
            onVisibleChange = {
                showControlDeck = it
                userManager.saveShowControlDeck(it)
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
            Text(
                text = "Interactive preview of your flashlight & ringer deck",
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
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                QuickControlDeckWidget()
            }
        }
    }
}
