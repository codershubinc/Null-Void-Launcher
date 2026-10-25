package com.codershubinc.nullvoidlauncher.ui.settings.widgets.steps

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.DirectionsRun
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codershubinc.nullvoidlauncher.system.steps.StepsHelper
import com.codershubinc.nullvoidlauncher.ui.components.ModernCard
import com.codershubinc.nullvoidlauncher.ui.widgets.steps.GoogleFitBlue
import com.codershubinc.nullvoidlauncher.ui.widgets.steps.GoogleFitGreen

@Composable
fun StepsDebugLogsSection(
    onOpenGoogleFitOrHealth: () -> Unit
) {
    val context = LocalContext.current
    val debugLogs by StepsHelper.syncLogs.collectAsState()
    var logsExpanded by remember { mutableStateOf(false) }

    ModernCard {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { logsExpanded = !logsExpanded },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Sync & Fetch Debug Logs",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Logcat tag: StepsHelper • ${debugLogs.size} log entries",
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 12.sp
                    )
                }

                Text(
                    text = if (logsExpanded) "Collapse" else "Expand",
                    color = GoogleFitBlue,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            if (logsExpanded) {
                Spacer(modifier = Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 280.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Black.copy(alpha = 0.5f))
                        .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                        .padding(10.dp)
                ) {
                    if (debugLogs.isEmpty()) {
                        Text(
                            text = "No logs yet. Tap 'Sync Now' above to trigger fetch.",
                            color = Color.White.copy(alpha = 0.4f),
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            items(debugLogs.size) { idx ->
                                val line = debugLogs[idx]
                                Text(
                                    text = line,
                                    color = if (line.contains("SUCCESS", ignoreCase = true) || line.contains("Found Google Fit", ignoreCase = true)) GoogleFitGreen
                                    else if (line.contains("Error", ignoreCase = true) || line.contains("Warning", ignoreCase = true)) Color(0xFFEA4335)
                                    else Color.White.copy(alpha = 0.8f),
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(20.dp))

    // Google Fit Integration Launch Card
    ModernCard {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onOpenGoogleFitOrHealth() }
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(GoogleFitBlue.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.DirectionsRun,
                    contentDescription = null,
                    tint = GoogleFitBlue,
                    modifier = Modifier.size(20.dp)
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Open Google Fit / Health App",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = if (StepsHelper.isGoogleFitInstalled(context)) "Google Fit installed" else "Tap to open or install Google Fit",
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 12.sp
                )
            }
        }
    }
}
