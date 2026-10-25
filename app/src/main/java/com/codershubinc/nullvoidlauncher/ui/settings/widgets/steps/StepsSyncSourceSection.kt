package com.codershubinc.nullvoidlauncher.ui.settings.widgets.steps

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Sync
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codershubinc.nullvoidlauncher.data.model.StepSyncMode
import com.codershubinc.nullvoidlauncher.system.steps.StepsInfoState
import com.codershubinc.nullvoidlauncher.ui.components.ModernCard
import com.codershubinc.nullvoidlauncher.ui.widgets.steps.GoogleFitBlue
import com.codershubinc.nullvoidlauncher.ui.widgets.steps.GoogleFitGreen

@Composable
fun StepsSyncSourceSection(
    liveSteps: StepsInfoState,
    syncMode: StepSyncMode,
    onSyncModeChange: (StepSyncMode) -> Unit,
    onSyncNow: () -> Unit,
    onOpenCalibration: () -> Unit
) {
    ModernCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Step Tracking & Sync",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Source: ${liveSteps.syncSource} • ${liveSteps.formattedSteps} steps today",
                    color = GoogleFitGreen,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            // Sync Now Button
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(GoogleFitBlue.copy(alpha = 0.2f))
                    .border(1.dp, GoogleFitBlue.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                    .clickable { onSyncNow() }
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Sync,
                        contentDescription = "Sync",
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "Sync Now",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Sync Source Mode Selector
        Spacer(modifier = Modifier.height(14.dp))
        Text(
            text = "Sync Source Mode",
            color = Color.White.copy(alpha = 0.7f),
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(6.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StepSyncMode.entries.forEach { mode ->
                val isSelected = syncMode == mode
                val label = when (mode) {
                    StepSyncMode.AUTO -> "Auto"
                    StepSyncMode.HARDWARE_SENSOR -> "Sensor Only"
                    StepSyncMode.HEALTH_CONNECT -> "Google Fit Only"
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) GoogleFitBlue.copy(alpha = 0.25f) else Color.White.copy(alpha = 0.05f))
                        .border(1.dp, if (isSelected) GoogleFitBlue else Color.White.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                        .clickable { onSyncModeChange(mode) }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        color = if (isSelected) Color.White else Color.White.copy(alpha = 0.6f),
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }

        // Calibrate Hardware Pedometer Button
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(Color.White.copy(alpha = 0.04f))
                .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(10.dp))
                .clickable { onOpenCalibration() }
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.Edit,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.7f),
                modifier = Modifier.size(18.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Calibrate / Align Step Count",
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Manually set today's baseline to match Google Fit or your tracker",
                    color = Color.White.copy(alpha = 0.45f),
                    fontSize = 11.sp
                )
            }
            Text(
                text = "Calibrate",
                color = GoogleFitBlue,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
