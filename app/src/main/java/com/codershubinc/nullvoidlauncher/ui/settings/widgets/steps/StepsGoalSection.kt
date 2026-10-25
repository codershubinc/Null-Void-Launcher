package com.codershubinc.nullvoidlauncher.ui.settings.widgets.steps

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codershubinc.nullvoidlauncher.ui.components.ModernCard
import com.codershubinc.nullvoidlauncher.ui.widgets.steps.GoogleFitBlue

@Composable
fun StepsGoalSection(
    dailyGoal: Int,
    onGoalSelected: (Int) -> Unit
) {
    ModernCard {
        Text(
            text = "Daily Step Goal",
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Select your daily steps target",
            color = Color.White.copy(alpha = 0.5f),
            fontSize = 13.sp
        )
        Spacer(modifier = Modifier.height(14.dp))

        val goals = listOf(4000, 6000, 8000, 10000, 12000)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            goals.forEach { goal ->
                val isSelected = dailyGoal == goal
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            if (isSelected) GoogleFitBlue.copy(alpha = 0.25f)
                            else Color.White.copy(alpha = 0.05f)
                        )
                        .border(
                            1.dp,
                            if (isSelected) GoogleFitBlue else Color.White.copy(alpha = 0.1f),
                            RoundedCornerShape(10.dp)
                        )
                        .clickable { onGoalSelected(goal) }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (goal >= 1000) "${goal / 1000}k" else "$goal",
                        color = if (isSelected) Color.White else Color.White.copy(alpha = 0.7f),
                        fontSize = 13.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
    }
}
